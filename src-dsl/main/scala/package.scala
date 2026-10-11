package zea.panama

import scala.language.unsafeNulls
import scala.compiletime.erasedValue
import scala.quoted.*

import java.lang.foreign.*
import java.lang.foreign.ValueLayout.*
import java.lang.invoke.*

/** The dsl package contains definitions for type-safe representation of native
 * types, functions, and variables.
 */
package object dsl {
  val LINKER: Linker = Linker.nativeLinker()


  def withConfined[T](fn: Arena ?=> T): T = {
    val a = Arena.ofConfined()
    try fn(using a)
    finally a.close()
  }

  def withAuto[T](fn: Arena ?=> T): T = {
    val a = Arena.ofAuto()
    try fn(using a)
    finally a.close()
  }

  def withShared[T](fn: Arena ?=> T): T = {
    val a = Arena.ofShared()
    try fn(using a)
    finally a.close()
  }


  inline def ttvl[T]: ValueLayout =
    inline erasedValue[T] match {
      case _: Byte    => JAVA_BYTE
      case _: Char    => JAVA_CHAR
      case _: Double  => JAVA_DOUBLE
      case _: Float   => JAVA_FLOAT
      case _: Int     => JAVA_INT
      case _: Long    => JAVA_LONG
      case _: Short   => JAVA_SHORT
      case _: Boolean => JAVA_BOOLEAN
      case _ => ADDRESS
    }


  inline def ttml[T]: MemoryLayout =
    ttvl[T] 

  /** Type to ValueLayout Expr. */
  private[panama] def ttvle(using q: Quotes)
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

  private[panama] def ttmle(using q: Quotes)
  (tpe: q.reflect.TypeRepr): Expr[MemoryLayout] =
    ttvle(tpe)
}
