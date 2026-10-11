package zea.panama.dsl

import scala.language.unsafeNulls
import scala.compiletime.constValue

import java.lang.foreign.*

/** Represents a T[c].
 *
 * Arrays are adjusted to apply offsets properly, so given an array int[4],
 * accessing i[2] will index the 3rd element, instead of the 3rd byte.
 */
opaque type Arr[T, C <: Int & Singleton] = MemorySegment

object Arr {

  inline def allocate[T, C <: Int & Singleton](using a: Arena): Arr[T, C] =
    a.allocate(ttml[T], constValue[C])

  inline def apply[T, C <: Int & Singleton](ptr: MemorySegment): Arr[T, C] =
    ptr

  @annotation.targetName("applyptr")
  inline def apply[T, C <: Int & Singleton](ptr: Ptr[T]): Arr[T, C] =
    ptr.raw


  extension [T, C <: Int & Singleton](arr: Arr[T, C]) {
    inline def raw: MemorySegment = arr
    inline def toPointer: Ptr[T] = Ptr(arr.raw)
    
    inline def apply(off: Int): T = {
      if (off < 0 || off >= constValue[C])
        ArrAioobe(off, constValue[C])

      Ptr.ptrGet[T](arr, off * ttvl[T].byteSize)
    }
    
    inline def update(off: Int, value: T): Unit = {
      if (off < 0 || off >= constValue[C])
        ArrAioobe(off, constValue[C])

      Ptr.ptrSet[T](arr, off * ttvl[T].byteSize, value)
    }
    
    inline def reinterpret[U, K <: Int & Singleton]: Arr[U, K] =
      arr

    inline def reinterpret(size: Long): Arr[T, C] =
      arr.raw.reinterpret(size)
  }

  def ArrAioobe(off: Int, length: Int): Nothing =
    throw new ArrayIndexOutOfBoundsException(
      s"index $off exceeds native array length of $length")
}
