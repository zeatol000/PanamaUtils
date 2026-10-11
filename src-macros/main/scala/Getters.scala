package zea.panama.macros

import scala.language.unsafeNulls
import scala.annotation.experimental
import scala.quoted.*

import java.lang.invoke.*

private object Getters {
  def sym(using q: Quotes)(
    mod: q.reflect.Symbol,
    classSym: q.reflect.Symbol,
    field: q.reflect.ValDef
  ): q.reflect.Symbol = {
    import q.reflect.*

    Symbol.newMethod(
      parent        = mod,
      name          = field.name,
      tpe           = MethodType("self" :: Nil)( // extension (self: Point) def x: Int
        paramInfosExp = _ => classSym.typeRef :: Nil,
        resultTypeExp = _ => field.tpt.tpe
      ),
      flags         = Flags.Method | Flags.ExtensionMethod,
      privateWithin = Symbol.noSymbol
    )
  }

  def tree(using q: Quotes)(
    sym: q.reflect.Symbol,
    vh: q.reflect.ValDef,
    field: q.reflect.ValDef,
    segParam: q.reflect.Symbol
  ): q.reflect.DefDef = {
    import q.reflect.*

    DefDef(
      symbol = sym,
      rhsFn  = {
        case ((self0: Term) :: Nil) :: Nil =>
          val segTerm = Select(self0, segParam)
          val seg: Expr[Any] = segTerm.asExpr
          val vhe: Expr[VarHandle] = Ref(vh.symbol).asExprOf[VarHandle]

          val getExpr: Expr[Object] = '{ $vhe.get($seg, 0L).asInstanceOf[Object] }
          val res = TypeApply(
            Select.unique(getExpr.asTerm, "asInstanceOf"),
            List(field.tpt)
          )
          // NOTE: When the get statement returns an opaque type that results
          // in a MemorySegment, such as Ptr[?] or Arr[?, ?], it creates a new
          // MemorySegment that has 0 bytes of length. For these cases, it
          // should reinterpret the byte size since the MemoryLayout is just
          // ADDRESS. ttvl[T].byteSize

          Some(res.changeOwner(sym))
        case _ =>
          None
      }
    )
  }
}
