package zea.panama.dsl

import scala.language.unsafeNulls
import scala.annotation.targetName

import java.lang.foreign.*

import zea.panama.dsl.internal.PtrMacroImpl.*

/** Added to other memory types to describe that the type runs on the GPU in
 * VRAM.
 * Example:
 * {{{
 * val cpuPtr: Ptr[Int] = Ptr.allocate(1)
 *
 * val gpuPtr1: Ptr[Int] device true = Cuda.mallocManaged(1)
 * val gpuPtr2: Ptr[Int] device false = Cuda.malloc(1)
 * }}}
 * Where 'Cuda' is a user-defined library linking CUDA downcalls.
 *
 * The second part of the type, the boolean literal, describes if that memory
 * is managed and can be accessed. If its true, then dereferencing becomes 
 * available.
 */
opaque infix type device[T, M <: Boolean & Singleton] = T

object device {
  /** Reinterpret another memory type */
  inline def apply[T, M <: Boolean & Singleton](memory: T): T device M =
    memory

  /** Reinterpret an address */
  inline def apply[T, M <: Boolean & Singleton](address: Long): Ptr[T] device M =
    Ptr(address)


  extension [T](mem: T device true) {
    /** Get the underlying type */
    inline def raw: T = mem
  }

  extension [T](mem: T device false) {
    /** Get the underlying type */
    @targetName("rawunmanaged")
    inline def raw: T = mem
  }
}
