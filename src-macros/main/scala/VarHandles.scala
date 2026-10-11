package zea.panama.macros

import scala.language.unsafeNulls
import scala.annotation.experimental
import scala.quoted.*

import java.lang.foreign.*
import java.lang.invoke.*

private object VarHandles {

  def syms(using q: Quotes)(
    moduleCls: q.reflect.Symbol,
    fields: List[q.reflect.ValDef]
  ): List[q.reflect.Symbol] = {
    import q.reflect.*

    fields.map { f =>
      Symbol.newVal(
        parent        = moduleCls,
        name          = f.name + "$handle",
        tpe           = TypeRepr.of[VarHandle],
        flags         = Flags.EmptyFlags,
        privateWithin = moduleCls
      )
    }
  }


  def trees(using q: Quotes)(
    vhSyms: List[q.reflect.Symbol],
    fields: List[q.reflect.ValDef],
    layout: Expr[StructLayout]
  ): List[q.reflect.ValDef] = {
    import q.reflect.*

    val rhss: List[Expr[VarHandle]] = fields.map { f => 
      val name = Expr(f.name)
      '{
        $layout.varHandle(
          MemoryLayout.PathElement.groupElement($name)
        )
      }
    }

    rhss.zip(vhSyms).map { (rhs, sym) =>
      ValDef(
        symbol = sym,
        rhs    = Some(rhs.asTerm)
      )
    }
  }
}
