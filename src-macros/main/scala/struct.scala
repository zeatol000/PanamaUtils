package zea.panama.macros

import scala.language.unsafeNulls
import scala.annotation.experimental
import scala.annotation.MacroAnnotation
import scala.quoted.*

import java.lang.foreign.*

@experimental
class struct extends MacroAnnotation {

        // opaque type $name
        // vars -> var handles
        // companion object apply (using Arena)
        // extension (self: $name) methods

  def transform(using q: Quotes)
  (tree: q.reflect.Definition, companion: Option[q.reflect.Definition]):
    List[q.reflect.Definition] = {
    import q.reflect.*

    tree match {
      case cd @ ClassDef(name, constr, parents, selfOpt, body) =>
        companion match {
          case Some(c: ClassDef) =>
              create(cd, name, body, c)
          case Some(_) =>
            report.error("companion object for @struct is not an object", tree.pos)
            List(tree)
          case None =>
            report.error("@struct requires a defined companion object", tree.pos)
            List(tree)
        }
        
      case _ =>
        report.error("@struct can only be applied to classes", tree.pos)
        List(tree)
    }
  }

  private def create(using q: Quotes)
  (cd: q.reflect.ClassDef, name: String, body: List[q.reflect.Statement],
    obj: q.reflect.ClassDef):
    List[q.reflect.Definition] = {
    import q.reflect.*

    val fields: List[ValDef] = body.collect {
      case v: ValDef
        if !v.symbol.flags.is(Flags.Param)
        => v
    }

    val methods: List[DefDef] = body.collect {
      case d @ DefDef(name, _, _, Some(_))
        if name != "<init>"
        => d
    }

    val (classDef, segParam) = Classes.asValueClass(cd, obj, name, fields)
    val o = remakeCompanion(obj, cd.symbol, fields, methods, segParam)

    val qi = q.asInstanceOf[scala.quoted.runtime.impl.QuotesImpl]
    dotty.tools.dotc.transform.PanamaMacroHelper.repickle(
      cd.symbol.asInstanceOf[dotty.tools.dotc.core.Symbols.ClassSymbol],
      classDef.asInstanceOf[dotty.tools.dotc.ast.tpd.Tree],
      o.asInstanceOf[dotty.tools.dotc.ast.tpd.Tree]
    )(using qi.ctx)

    classDef ::
    o ::
    Nil
  }
  
  
  private def remakeCompanion(using q: Quotes)
  (obj: q.reflect.ClassDef, classSym: q.reflect.Symbol,
    fields: List[q.reflect.ValDef],
    methods: List[q.reflect.DefDef],
    segParam: q.reflect.Symbol
  ): q.reflect.Definition = {
    import q.reflect.*

    val moduleCls = obj.symbol

    val layoutSym = Layout.sym(moduleCls)
    val applySym  = Applying.sym(moduleCls, classSym, fields)
    val vhSyms    = VarHandles.syms(moduleCls, fields)
    
    val extSyms1: List[Symbol] = fields.flatMap { f =>
      Getters.sym(moduleCls, classSym, f) :: (
        if (f.symbol.flags.is(Flags.Mutable))
          Setters.sym(moduleCls, classSym, f) ::
          Nil
        else
          Nil
      )
    }

    val layoutVal:  ValDef             = Layout.tree(layoutSym, fields)
    val layoutExpr: Expr[StructLayout] = Ref(layoutSym).asExprOf[StructLayout]

    val varHandles: List[ValDef] = VarHandles.trees(vhSyms, fields, layoutExpr)
    val apply:      DefDef       = Applying.tree(applySym, classSym, varHandles, layoutExpr)
    val extensions: List[DefDef] = makeExtensions(
      moduleCls, classSym, fields, methods, varHandles, extSyms1, segParam)

    ClassDef.copy(obj)(
      name    = obj.name,
      constr  = obj.constructor,
      parents = obj.parents,
      selfOpt = obj.self,
      body    = obj.body ::: (layoutVal :: apply :: (varHandles ::: extensions))
    )
  }


  private def makeExtensions(using q: Quotes)(
    moduleSym: q.reflect.Symbol,
    classSym: q.reflect.Symbol,
    fields: List[q.reflect.ValDef],
    methods: List[q.reflect.DefDef],
    vhs: List[q.reflect.ValDef],
    decls: List[q.reflect.Symbol],
    segParam: q.reflect.Symbol
  ): List[q.reflect.DefDef] = {
    import q.reflect.*

    fields.zip(vhs).flatMap { (f, vh) => 
      val getterSym = decls.find(_.name == f.name).get

      Getters.tree(getterSym, vh, f, segParam) :: (
        if (f.symbol.flags.is(Flags.Mutable)) {
          val setterSym = decls.find(_.name == f.name + "_=").get
          Setters.tree(setterSym, vh, segParam) ::
          Nil
        }
        else Nil
      )
    }

    // IGNORING OTHER METHODS FOR NOW
  }
}
