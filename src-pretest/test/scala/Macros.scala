package zea.panama.test

import scala.quoted.*

import java.lang.foreign.*

import zea.panama.dsl.*
import internal.*
import ArrMacroImpl.*
import ArrMacroUtil.*

object Macros {
  inline def dimensionsOf[T]: Long =
    ${ dimensionsOfImpl[T] }

  def dimensionsOfImpl[T: Type](using Quotes): Expr[Long] = {
    import quotes.reflect.*
    val tpe = TypeRepr.of[T]
    Expr(dimensions(tpe))
  }
}
