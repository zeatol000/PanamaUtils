package zea.panama

import scala.language.unsafeNulls
import scala.annotation.experimental
import scala.annotation.publicInBinary
import scala.quoted.*

import java.lang.foreign.*
import java.lang.foreign.ValueLayout.*

import zea.panama.dsl.ttvle
import zea.panama.dsl.Arr

package object macros {

  /** Type to MemoryLayout Expr. */
  private[panama] def ttmle(using q: Quotes)
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
  





  private inline def ohNo(what: String, caller: String): Nothing =
    throw new Error(
s"""|Panama macros failed to erase this tree!
    |You likely had a $what call $caller from zea.panama.macros.
    |This was supposed to be erased into the proper Project Panama implementation, but it wasn't.
    |This means something really bad happened and the macro didn't properly execute.
    |Please report this and change Panama Macro version.
    |If that doesn't work, ignore the macros and write using the base DSL."""
    .stripMargin)


  /** Right hand side of a struct or union field.
   *
   * This call actually doesn't need to be on the rhs of a field. It only needs
   * to be an expression that satisfies the typer. Once it has, the rhs is 
   * completely elimated after the macro execution.
   *
   * {{{
   * @struct class S {
   *   val x: Int = field
   * }
   * object S
   * }}}
   * erases to
   * {{{
   * class S(val segment: MemorySegment) extends MemorySegment
   * object S {
   *    val LAYOUT: StructLayout = ...
   *    def apply(x: Int)(using a: Arena): S = ...
   *    private val x$handle: VarHandle = ...
   *    extension (self: S) {
   *      def x: Int = ...
   *    }
   * }
   * }}}
   */
  inline def field: Nothing = fieldImpl
  @publicInBinary private[macros] def fieldImpl: Nothing =
    ohNo("struct or union", "field")



  // These two fields are to identify function and variable defintions for
  // the @library macro annotation


  /** Right hand side of a dynamic library's function.
   *
   * This is needed to be on the rhs of a native function definition as it
   * would otherwise be impossible to distinguish JVM functions in a @library
   * annotated object.
   *
   * {{{
   * def add(a: Int, b: Int): Int = function("add")
   * }}}
   * erases to
   * {{{
   * private val add$handle: MethodHandle = ...
   * def add(a: Int, b: Int): Int = add$handle.invoke(a, b).asInstanceOf[Int]
   * }}}
   *
   * @param name the name of the function in the library.
   *   Separate from the JVM function name to allow the user to change naming
   *   conventions.
   */
  inline def function(inline name: String = "", opts: Linker.Option*): Nothing = functionImpl
  @publicInBinary private[macros] def functionImpl: Nothing =
    ohNo("dynamic library's function definition", "function")


  /** Right hand side of a dynamic library's variable.
   * Erased tree depends on the mutability of the field
   *
   * This is needed to be on the rhs of a native variable definition as it
   * would otherwise be impossible to distinguish JVM variables in a @library
   * annotated object.
   *
   * {{{
   * var data: Int = variable("data")
   * }}}
   * erases to
   * {{{
   * private val data$handle: VarHandle = ...
   * private val data$address: MemorySegment = ...
   * def data: Int = data$handle.get(data$address).asInstanceOf[Int]
   * def data_=(data: Int): Unit = data$handle.set(data$address, data)
   * }}}
   *
   * @param name the name of the variable in the library.
   *   Separate from the JVM function name to allow the user to change naming
   *   conventions.
   */
  inline def variable(inline name: String = ""): Nothing = variableImpl
  @publicInBinary private[macros] def variableImpl: Nothing =
    ohNo("dynamic library's variable definition", "variable")
}
