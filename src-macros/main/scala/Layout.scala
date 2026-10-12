package zea.panama.macros

import scala.language.unsafeNulls
import scala.annotation.experimental
import scala.quoted.*

import java.lang.foreign.*

import zea.panama.dsl.*
import zea.panama.dsl.internal.*

private object Layout {
  def sym(using q: Quotes)(
    moduleCls: q.reflect.Symbol
  ): q.reflect.Symbol = {
    import q.reflect.*

    Symbol.newVal(
      parent        = moduleCls,
      name          = "LAYOUT",
      tpe           = TypeRepr.of[StructLayout],
      flags         = Flags.EmptyFlags,
      privateWithin = Symbol.noSymbol
    )
  }


  def tree(using q: Quotes)(
    sym: q.reflect.Symbol,
    fields: List[q.reflect.ValDef]
  ): q.reflect.ValDef = {
    import q.reflect.*

    val all: List[Expr[MemoryLayout]] = fields.map { f =>
      val vl   = ttvle(f.tpt.tpe)
      val name = Expr(f.name)
      '{
        $vl.withName($name)
      }
    }

    val rhs: Term = '{
      MemoryLayout.structLayout(
        ${Expr.ofList(all)}*
      )
    }.asTerm

    ValDef(
      symbol = sym,
      rhs    = Some(rhs)
    )
  }
}
