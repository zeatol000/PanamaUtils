package zea.panama.macros

import scala.language.unsafeNulls
import scala.annotation.experimental
import scala.quoted.*

import java.lang.invoke.*

private object Setters {
  def sym(using q: Quotes)(
    mod: q.reflect.Symbol,
    classSym: q.reflect.Symbol,
    field: q.reflect.ValDef
  ): q.reflect.Symbol = {
    import q.reflect.*

    Symbol.newMethod(
      parent        = mod,
      name          = field.name + "_=",
      tpe           = MethodType("self" :: field.name :: Nil)( // extension (self: Point) def x_=(x: Int): Unit
        paramInfosExp = _ => classSym.typeRef :: field.tpt.tpe :: Nil,
        resultTypeExp = _ => TypeRepr.of[Unit]
      ),
      flags         = Flags.Method | Flags.ExtensionMethod,
      privateWithin = Symbol.noSymbol
    )
  }

  def tree(using q: Quotes)(
    sym: q.reflect.Symbol,
    vh: q.reflect.ValDef,
    segParam: q.reflect.Symbol
  ): q.reflect.DefDef = {
    import q.reflect.*

    DefDef(
      symbol = sym,
      rhsFn  = {
        case ((self0: Term) :: (arg0: Term) :: Nil) :: Nil =>
          val segTerm = Select(self0, segParam)
          val seg:  Expr[Any]       = segTerm.asExpr
          val arg:  Expr[Any]       = arg0.asExpr
          val vhe:  Expr[VarHandle] = Ref(vh.symbol).asExprOf[VarHandle]

          val body = '{
            $vhe.set($seg, 0L, $arg)
          }

          Some(body.asTerm.changeOwner(sym))
        case _ =>
          None
      }
    )
  }
}
