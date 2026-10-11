package zea.panama.dsl.types

import scala.language.unsafeNulls
import scala.compiletime.erasedValue

import java.lang.foreign.*
import java.lang.foreign.ValueLayout.*
import java.lang.invoke.*

import zea.panama.dsl.DynamicLibrary.*

/** A utility for creating structs.
 * Intended to be extended by the companion object.
 *
 * @tparam A the type of your data, such as an opaque type.
 * @param vls the name of the fields and the value layout they represent.
 *
 * E.g.,
 * {{{
 * opaque type Point = MemorySegment
 *
 * object Point extends Struct[Point](
 *    "x" -> JAVA_INT,
 *    "y" -> JAVA_INT
 * ) {
 *    // var handles
 *   private val _x: Field[Int] = field("x")
 *   private val _y: Field[Int] = field("y")
 *
 *   def apply(x: Int, y: Int)(using a: Arena): Point = {
 *      val seg = a.allocate(LAYOUT)
 *      _x.set(seg, x)
 *      _y.set(seg, y)
 *      seg
 *   }
 *
 *   extension (p: Point) {
 *      def x: Int = _x.get(p)
 *      def y: Int = _y.get(p)
 *   }
 * }
 * }}}
 */
transparent trait Struct[A](
  private val vls: (String, ValueLayout)*
) {
  val LAYOUT: StructLayout = MemoryLayout.structLayout(
    vls.map{ (n, vl) => vl.withName(n) } *
  )

  protected def element(name: String): MemoryLayout.PathElement =
    MemoryLayout.PathElement.groupElement(name)


  def fieldOffset(name: String) =
    LAYOUT.byteOffset(element(name))


  protected inline def field[B](
    inline name: String):
    Field[B] = {
    LAYOUT.varHandle(element(name)).asInstanceOf
  }



  opaque type Field[B] = VarHandle

  extension [B](f: Field[B]) {
    inline def get(self: Any): B = f
      .asInstanceOf[VarHandle]
      .get(self, 0L)
      .asInstanceOf[B]

    inline def set(self: Any, a: B): Unit = f
      .asInstanceOf[VarHandle]
      .set(self, 0L, a)
  }
}
