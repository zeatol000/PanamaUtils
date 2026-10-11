package zea.panama.macros

import scala.language.unsafeNulls
import scala.annotation.experimental
import scala.quoted.*
import scala.quoted.runtime.impl.QuotesImpl

import dotty.tools.dotc.core.Symbols => DSymbols
import dotty.tools.dotc.transform.PanamaMacroHelper

import java.lang.foreign.*

private object Classes {
  def asValueClass(using q: Quotes)(
    old: q.reflect.ClassDef,
    obj: q.reflect.ClassDef,
    name: String,
    fields: List[q.reflect.ValDef]
  ): (q.reflect.ClassDef, q.reflect.Symbol) = {
    import q.reflect.*

    /* Symbol of the parameter / unbox field */
    val segParam = Symbol.newVal(
      parent        = old.symbol,
      name          = "segment",
      tpe           = TypeRepr.of[MemorySegment],
      flags         = Flags.ParamAccessor,
      privateWithin = Symbol.noSymbol
    )

    val qi = q.asInstanceOf[QuotesImpl]
    PanamaMacroHelper.prepareValueClass(
      old.symbol.asInstanceOf[DSymbols.ClassSymbol],
      obj.symbol.asInstanceOf[DSymbols.Symbol],
      fields.map(_.symbol.asInstanceOf[DSymbols.Symbol]),
      segParam.asInstanceOf[DSymbols.Symbol]
    )(using qi.ctx)

    /* New classdef */
    val newClassDef = ClassDef(
      cls     = old.symbol,
      parents = TypeTree.of[AnyVal] :: Nil,
      body    = Nil
    )

    (newClassDef, segParam)
  }
}
