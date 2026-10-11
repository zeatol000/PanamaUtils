package dotty.tools.dotc.transform

import dotty.tools.dotc.util.*
import dotty.tools.dotc.core.Contexts.*
import dotty.tools.dotc.core.Symbols.*
import dotty.tools.dotc.core.Types.*
import dotty.tools.dotc.core.Names.*
import dotty.tools.dotc.core.{SymDenotations, Flags as DotcFlags}
import dotty.tools.dotc.core.Scopes.MutableScope
import dotty.tools.dotc.ast.tpd.*
import dotty.tools.dotc.core.tasty.*
import dotty.tools.dotc.report
import dotty.tools.tasty.TastyBuffer
import dotty.tools.dotc.reporting.Message

import scala.collection.mutable

object PanamaMacroHelper {
  /** Setup the value class.  
   */
  def prepareValueClass(
      classSymbol: ClassSymbol,
      moduleSymbol: Symbol,
      fieldSymbols: List[Symbol],
      segParam: Symbol
  )(using ctx: Context): Unit = {
    // Unlink old field symbols from class
    val decls = classSymbol.classInfo.decls.asInstanceOf[MutableScope]
    for (f <- fieldSymbols) {
      decls.unlink(f)
    }
    decls.enter(segParam)

    // Unlink proxy apply methods from companion object
    val modCls =
      if (moduleSymbol.is(DotcFlags.Module))
      then moduleSymbol.moduleClass.asClass
      else moduleSymbol.asClass
    val modDecls = modCls.classInfo.decls.asInstanceOf[MutableScope]
    val oldApplies = modDecls.lookupAll(termName("apply")).toList
    for (a <- oldApplies) {
      modDecls.unlink(a)
    }

    // Link a new constructor
    val ctor = classSymbol.primaryConstructor
    val memSegType = requiredClass("java.lang.foreign.MemorySegment").typeRef
    val ctorParam = newSymbol(ctor, termName("segment"), DotcFlags.Param, memSegType)
    ctor.info = MethodType(List(termName("segment")))(_ => List(memSegType), _ => classSymbol.typeRef)
    ctor.setParamss(List(List(ctorParam)))
    
    // Setup AnyVal inheritance
    setToAnyVal(classSymbol)
  }

  /** Set a class's parent to AnyVal */
  def setToAnyVal(classSymbol: ClassSymbol)(using ctx: Context): Unit = {
    val denot: SymDenotations.ClassDenotation = classSymbol.classDenot
    denot.info match {
      case info: ClassInfo =>
        info.parents.foreach { parent =>
          if (parent.typeSymbol != defn.AnyClass)
          if (parent.typeSymbol != defn.ObjectClass)
            report.error("@struct marked class cannot extend anything"
              + "\nfound: " + parent.typeSymbol,
              classSymbol.sourcePos)
        }

        denot.info = info.derivedClassInfo(
          //prefix =
          declaredParents = defn.AnyValType :: Nil
          //decls =
          //selfInfo =
        )
        //denot.setFlag(DotcFlags.ValueTypeClass)

      case other =>
        report.error("Unexpected class info", classSymbol.sourcePos)
    }
  }

  
  
  def repickle(classSym: ClassSymbol, classDef: Tree, companionDef: Tree)(using ctx: Context): Unit = {
    val unit = ctx.compilationUnit
    val sourceRelativePath =
      val reference = ctx.settings.sourceroot.value
      dotty.tools.dotc.util.SourceFile.relativePath(unit.source, reference)
    val attributes = Attributes(
      sourceFile = sourceRelativePath,
      scala2StandardLibrary = dotty.tools.dotc.config.Feature.shouldBehaveAsScala2,
      explicitNulls = ctx.settings.YexplicitNulls.value,
      captureChecked = dotty.tools.dotc.config.Feature.ccEnabled,
      withPureFuns = dotty.tools.dotc.config.Feature.pureFunsEnabled,
      isJava = false,
      isOutline = false
    )

    val pickler = new TastyPickler(classSym, isBestEffortTasty = false)
    val treePkl = new TreePickler(pickler, attributes)

    val pkgTree = unit.tpdTree match {
      case p: PackageDef =>
        val newStats = p.stats.map {
          case t: MemberDef if t.symbol == classSym => classDef
          case t: MemberDef if t.symbol == classSym.companionModule.moduleClass => companionDef
          case other => other
        }
        cpy.PackageDef(p)(p.pid, newStats)
      case _ =>
        classDef
    }

    val trees = sliceTopLevel(pkgTree, classSym)
    val tree = trees.headOption.getOrElse(classDef)
    treePkl.pickle(tree :: Nil)

    val positionWarnings = new mutable.ListBuffer[Message]()
    def computePickled(): Array[Byte] = inContext(ctx.fresh) {
      val scratch = new ScratchData
      //treePkl.compactify(scratch)
      if tree.span.exists then
        val reference = ctx.settings.sourceroot.value
        PositionPickler.picklePositions(
          pickler, treePkl.buf.addrOfTree, treePkl.treeAnnots, treePkl.typeAnnots, reference,
          unit.source, tree :: Nil, positionWarnings,
          scratch.positionBuffer, scratch.pickledIndices)

      if !ctx.settings.XdropComments.value then
        CommentPickler.pickleComments(
          pickler, treePkl.buf.addrOfTree, treePkl.docString, tree,
          scratch.commentBuffer)

      AttributePickler.pickleAttributes(attributes, pickler, scratch.attributeBuffer)
      pickler.assembleParts()
    }

    unit.pickled += (classSym -> (() => computePickled()))
  }
}
