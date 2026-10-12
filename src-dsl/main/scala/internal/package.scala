package zea.panama.dsl

import scala.language.unsafeNulls
import scala.compiletime.erasedValue
import scala.quoted.*

import java.lang.foreign.*
import java.lang.foreign.ValueLayout.*
import java.lang.invoke.*

package object internal {

  /** Type to ValueLayout Expr. */
  def ttvle(using q: Quotes)
  (tpe: q.reflect.TypeRepr): Expr[ValueLayout] = {
    import q.reflect.*
    tpe match {
      case t if t =:= TypeRepr.of[Byte]    => '{JAVA_BYTE}
      case t if t =:= TypeRepr.of[Char]    => '{JAVA_CHAR}
      case t if t =:= TypeRepr.of[Double]  => '{JAVA_DOUBLE}
      case t if t =:= TypeRepr.of[Float]   => '{JAVA_FLOAT}
      case t if t =:= TypeRepr.of[Int]     => '{JAVA_INT}
      case t if t =:= TypeRepr.of[Long]    => '{JAVA_LONG}
      case t if t =:= TypeRepr.of[Short]   => '{JAVA_SHORT}
      case t if t =:= TypeRepr.of[Boolean] => '{JAVA_BOOLEAN}
      case _                               => '{ADDRESS}
    }
  }



  /** Type to MemoryLayout Expr. */
  def ttmle(using q: Quotes)
  (tpe: q.reflect.TypeRepr): Expr[MemoryLayout] = {
    import q.reflect.*
    tpe match {
      case t if t =:= TypeRepr.of[Byte]    => '{JAVA_BYTE}
      case t if t =:= TypeRepr.of[Char]    => '{JAVA_CHAR}
      case t if t =:= TypeRepr.of[Double]  => '{JAVA_DOUBLE}
      case t if t =:= TypeRepr.of[Float]   => '{JAVA_FLOAT}
      case t if t =:= TypeRepr.of[Int]     => '{JAVA_INT}
      case t if t =:= TypeRepr.of[Long]    => '{JAVA_LONG}
      case t if t =:= TypeRepr.of[Short]   => '{JAVA_SHORT}
      case t if t =:= TypeRepr.of[Boolean] => '{JAVA_BOOLEAN}
      case AppliedType(tycon, List(tType, cType))
        if tycon.typeSymbol == TypeRepr.of[Arr].typeSymbol =>

        cType match {
          case ConstantType(IntConstant(value)) =>
            val vl = ttmle(tType)
            '{ MemoryLayout.sequenceLayout(${Expr(value)}, $vl) }
          case _ =>
            report.error("invalid integer literal")
            '{ADDRESS}
        }
      case _ => '{ADDRESS}
    }
  }
}
