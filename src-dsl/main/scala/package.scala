package zea.panama

import scala.language.unsafeNulls
import scala.compiletime.erasedValue
import scala.quoted.*

import java.lang.foreign.*
import java.lang.foreign.ValueLayout.*
import java.lang.invoke.*

import zea.panama.dsl.internal.*

/** The dsl package contains definitions for type-safe representation of native
 * types, functions, and variables.
 */
package object dsl {
  val LINKER: Linker = Linker.nativeLinker()


  /** Run a function with a confined arena.
   * The arena is intended to be short lived. Do not use this for running
   * an entire project.
   */
  def withConfined[T](fn: Arena ?=> T): T = {
    val a = Arena.ofConfined()
    try fn(using a)
    finally a.close()
  }

  /** Run a function with an auto arena (garbage collected shared).
   * The arena is intended to be short lived. Do not use this for running
   * an entire project.
   */
  def withAuto[T](fn: Arena ?=> T): T = {
    val a = Arena.ofAuto()
    try fn(using a)
    finally a.close()
  }

  /** Run a function with a shared arena.
   * The arena is intended to be short lived. Do not use this for running
   * an entire project.
   */
  def withShared[T](fn: Arena ?=> T): T = {
    val a = Arena.ofShared()
    try fn(using a)
    finally a.close()
  }


  /** Type to ValueLayout */
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


  /** Type to MemoryLayout. Just ttvl[T] but will soon be adjusted to handle
   * Arr[T, C].
   */
  inline def ttml[T]: MemoryLayout =
    ttvl[T] 
}
