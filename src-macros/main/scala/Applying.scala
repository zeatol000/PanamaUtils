package zea.panama.macros

import scala.language.unsafeNulls
import scala.annotation.experimental
import scala.quoted.*

import java.lang.foreign.*
import java.lang.invoke.*

private object Applying {
  def sym(using q: Quotes)(
    moduleCls: q.reflect.Symbol,
    classSym: q.reflect.Symbol,
    fields: List[q.reflect.ValDef]
  ): q.reflect.Symbol = {
    import q.reflect.*

    Symbol.newMethod(
      parent        = moduleCls,
      name          = "apply",
      tpe           = MethodType(fields.map(_.name))(
        paramInfosExp = _ => fields.map(_.tpt.tpe),
        resultTypeExp = _ =>
          MethodType(MethodTypeKind.Contextual)("a" :: Nil)(
            paramInfosExp = _ => TypeRepr.of[Arena] :: Nil,
            resultTypeExp = _ => classSym.typeRef
          )
      ),
      flags         = Flags.EmptyFlags,
      privateWithin = Symbol.noSymbol
    )
  }

  def tree(using q: Quotes)(
    sym: q.reflect.Symbol,
    classSym: q.reflect.Symbol,
    vhs: List[q.reflect.ValDef],
    layout: Expr[StructLayout]
  ): q.reflect.DefDef = {
    import q.reflect.*

    DefDef(
      symbol = sym,
      rhsFn  = {
        case (args0: List[Tree]) :: ((arena: Term) :: Nil) :: Nil =>
          val args: List[Term] = args0.asInstanceOf[List[Term]]

          val segSym = Symbol.newVal(
            parent        = sym,
            name          = "seg",
            tpe           = TypeRepr.of[MemorySegment],
            flags         = Flags.EmptyFlags,
            privateWithin = Symbol.noSymbol
          )

          val allocateExpr: Expr[MemorySegment] = '{ ${arena.asExprOf[Arena]}.allocate($layout) }
          val segVal = ValDef(segSym, Some(allocateExpr.asTerm))

          val setStats: List[Statement] = vhs.zip(args).map { (vh, arg) =>
            val vhe: Expr[VarHandle] = Ref(vh.symbol).asExprOf[VarHandle]
            val fe:  Expr[Any]       = arg.asExpr
            val segExpr: Expr[MemorySegment] = Ref(segSym).asExprOf[MemorySegment]

            '{
              $vhe.set($segExpr, 0L, $fe)
            }.asTerm
          }

          val constructPoint: Term = Apply(
            Select(New(TypeIdent(classSym)), classSym.primaryConstructor),
            List(Ref(segSym))
          )

          val body = Block(segVal :: setStats, constructPoint)
          Some(body.changeOwner(sym))
        case _ =>
          None
      }
    )
  }
}
