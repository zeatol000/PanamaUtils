package zea.panama.dsl

import scala.language.unsafeNulls
import scala.compiletime.erasedValue

import java.lang.foreign.*
import java.lang.foreign.ValueLayout.*
import java.lang.invoke.*
import java.nio.file.Path


/** A utility for creating downcalls to native functions and variables.
 *
 * E.g.,
 * {{{
 * object Libc extends DynamicLibrary(
 *    Paths.get("/usr/lib/libc.so.6"),
 *    Arena.global()
 * ) {
 *      // basic function declaration
 *    val malloc: Long &=> MemorySegment = function("malloc")
 *
 *      // functions can also take Linker.Option arguments
 *    val free: MemorySegment &=> Unit = function("free", Linker.Option.critical(false))
 *
 *      // variables are more annoying since VarHandles cannot be bound to a
 *      // specific memory segment
 *    private val (someVar_handle, someVar_pointer) = variable("some_var")[Int]
 *    def someVar: Int = someVar_handle.get(someVar_pointer)
 *    def someVar_=(x: Int): Unit = someVar_handle.set(someVar_pointer, x)
 * }
 * }}}
 *
 * @param path the path to the library
 * @param arena the arena to allocate in
 */
class DynamicLibrary(
  val path: Path,
  val arena: Arena = Arena.global
) {
  import DynamicLibrary.*
  private val lookup: SymbolLookup = SymbolLookup.libraryLookup(path, arena)
  
  final def find(name: String): MemorySegment = lookup.findOrThrow(name)


  private inline
  def call[T](name: String, opts: Seq[Linker.Option], args: MemoryLayout*)[R]: T =
    inline erasedValue[R] match {
      case _: Unit => voidcall(find(name), opts,          args*).asInstanceOf[T]
      case _       => downcall(find(name), opts, ttml[R], args*).asInstanceOf[T]
    }


  inline def variable(name: String)[A]: (VarHandle, MemorySegment) = {
    (ttml[A].varHandle(), find(name)) // vh.get(ptr) ; vh.set(ptr, val)
  }


  inline def function(name: String, opts: Linker.Option*)
  [R]: Unit &=> R =
    call(name, opts)


  inline def function(name: String, opts: Linker.Option*)
  [R, A1]: A1 &=> R =
    call(name, opts,
      ttml[A1]
    )[R]
 

  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2]: (A1, A2) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3]: (A1, A2, A3) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4]: (A1, A2, A3, A4) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5]: (A1, A2, A3, A4, A5) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6]: (A1, A2, A3, A4, A5, A6) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7]: (A1, A2, A3, A4, A5, A6, A7) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8]: (A1, A2, A3, A4, A5, A6, A7, A8) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9]: (A1, A2, A3, A4, A5, A6, A7, A8, A9) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10]: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
      ttml[A10],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11]: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
      ttml[A10],
      ttml[A11],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12]: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
      ttml[A10],
      ttml[A11],
      ttml[A12],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13]: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
      ttml[A10],
      ttml[A11],
      ttml[A12],
      ttml[A13],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14]: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
      ttml[A10],
      ttml[A11],
      ttml[A12],
      ttml[A13],
      ttml[A14],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15]: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
      ttml[A10],
      ttml[A11],
      ttml[A12],
      ttml[A13],
      ttml[A14],
      ttml[A15],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16]: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
      ttml[A10],
      ttml[A11],
      ttml[A12],
      ttml[A13],
      ttml[A14],
      ttml[A15],
      ttml[A16],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17]: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
      ttml[A10],
      ttml[A11],
      ttml[A12],
      ttml[A13],
      ttml[A14],
      ttml[A15],
      ttml[A16],
      ttml[A17],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18]: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
      ttml[A10],
      ttml[A11],
      ttml[A12],
      ttml[A13],
      ttml[A14],
      ttml[A15],
      ttml[A16],
      ttml[A17],
      ttml[A18],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19]: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
      ttml[A10],
      ttml[A11],
      ttml[A12],
      ttml[A13],
      ttml[A14],
      ttml[A15],
      ttml[A16],
      ttml[A17],
      ttml[A18],
      ttml[A19],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20]: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
      ttml[A10],
      ttml[A11],
      ttml[A12],
      ttml[A13],
      ttml[A14],
      ttml[A15],
      ttml[A16],
      ttml[A17],
      ttml[A18],
      ttml[A19],
      ttml[A20],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21]: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
      ttml[A10],
      ttml[A11],
      ttml[A12],
      ttml[A13],
      ttml[A14],
      ttml[A15],
      ttml[A16],
      ttml[A17],
      ttml[A18],
      ttml[A19],
      ttml[A20],
      ttml[A21],
    )[R]


  inline def function(name: String, opts: Linker.Option*)
  [R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22]: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22) &=> R =
    call(name, opts,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
      ttml[A10],
      ttml[A11],
      ttml[A12],
      ttml[A13],
      ttml[A14],
      ttml[A15],
      ttml[A16],
      ttml[A17],
      ttml[A18],
      ttml[A19],
      ttml[A20],
      ttml[A21],
      ttml[A22],
    )[R]
}


object DynamicLibrary {
  private[dsl] def downcall(
    pointer: MemorySegment,
    opts: Seq[Linker.Option],
    res: MemoryLayout,
    args: MemoryLayout*
  ): MethodHandle =
    LINKER.downcallHandle(
      pointer,
      FunctionDescriptor.of(res, args*),
      opts*
    )

  private[dsl] def voidcall(
    pointer: MemorySegment,
    opts: Seq[Linker.Option],
    args: MemoryLayout*
  ): MethodHandle =
    LINKER.downcallHandle(
      pointer,
      FunctionDescriptor.ofVoid(args*),
      opts*
    )
}
