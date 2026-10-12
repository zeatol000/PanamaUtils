package zea.panama.dsl

import scala.language.implicitConversions
import scala.language.unsafeNulls
import scala.compiletime.constValue
import scala.compiletime.erasedValue
import scala.quoted.*

import java.lang.foreign.*

import zea.panama.dsl.internal.ArrMacroImpl.*

/** Represents a T[c].
 *
 * Example:
 * {{{
 * val a: Arr[Int, 32L] = Arr.allocate(using arena)
 * }}}
 *
 * Arrays are adjusted to apply offsets properly, so given an array int[4],
 * accessing i[2] will index the 3rd element, instead of the 3rd byte.
 *
 * An Arr is considered a pointer to the actual array in memory. HOWEVER, an
 * Arr[Arr[?, ?], ?] is not a pointer to an array of pointers to arrays.
 * Instead an Arr of Arr is a fully opaque shorthand to one Arr of two lengths.
 * Extension methods change implementation to reflect this. 
 *
 * Keep in mind that the base Arr is a pointer to the array. Meaning a struct
 * of Arr will not fully line up in memory. In other words, the C struct:
 * {{{
 * struct Points {
 *    int xs[4];
 *    int ys[4];
 * }
 * }}}
 * Appears like this in memory:
 * 0x8000:    int       xs[0]
 * 0x8004:    int       xs[1]
 * 0x8008:    int       xs[2]
 * 0x800c:    int       xs[3]
 * 0x8010:    int       ys[0]
 * 0x8014:    int       ys[1]
 * 0x8018:    int       ys[2]
 * 0x801c:    int       ys[3]
 * However in Panama, the same struct would be laid out like this in memory:
 * 0x8000:    Ptr[Arr[Int, 4]]    xs
 * 0x8008:    Ptr[Arr[Int, 4]]    ys
 * This is a flaw that I intend to fix, but it requires some complications as
 * the Project Panama way of defining structs (MemoryLayout.structLayout)
 * cannot have arrays (MemoryLayout.sequenceLayout) inside. Instead, a padding
 * will need to be generated and the accessor methods will need to be
 * customized for this.
 */
opaque type Arr[T, C <: Long & Singleton] = MemorySegment

object Arr {
  /** Allocate memory in an arena and return the MemorySegment as an Arr.
   *
   * Multidimensional array compatible. Allocating Arr[Arr[Int, 4], 4] will
   * allocate the 16 times byte size of an Int.
   */
  inline def allocate[T, C <: Long & Singleton](using a: Arena): Arr[T, C] =
    ${ allocateImpl[T, C]('a) }

  /** Reinterpret a pointer as an Arr */
  inline def apply[T, C <: Long & Singleton](ptr: MemorySegment): Arr[T, C] =
    ptr

  /** Reinterpret a pointer as an Arr */
  @annotation.targetName("applyptr")
  inline def apply[T, C <: Long & Singleton](ptr: Ptr[T]): Arr[T, C] =
    ptr.raw



  extension [T, C <: Long & Singleton](arr: Arr[T, C]) {
    /** Get the raw MemorySegment */
    inline def raw: MemorySegment = arr

    /** Get the length of the array */
    inline def length: Long =
      constValue[C]



    /** Get a value at an index, or a nested array. */
    inline def apply(off: Long): T = 
      ${ applyImpl[T, C]('arr, 'off) }
    
    /** Set a value at an index. Not multidimensional array compatible. */
    inline def update(off: Long, value: T): Unit = 
      ${ updateImpl[T, C]('arr, 'off, 'value)}
    
    /** Optimized method of getting values from multidimensional arrays.
     * This method is kinda stupid as it can't provide a proper return type as
     * the return type depends on the inputs. Just use an .asInstanceOf[]
     */
    transparent inline def get(i: Long, is: Long*) =
      ${ getImpl[T, C]('arr, 'i, 'is) }



    /** Reinterpret this array. */
    inline def reinterpret[U, K <: Long & Singleton]: Arr[U, K] =
      ${ reinterpretImpl[T, U, C, K]('arr) }

    /** Reinterpret the underlying MemorySegment */
    inline def reinterpret(size: Long): Arr[T, C] =
      arr.raw.reinterpret(size)



    /** Turn this array into a pointer. Not multidimensional array compatible. */
    inline def toPointer: Ptr[T] = Ptr(arr.raw)
    


    /** Get the address of the memory */
    inline def addr: Long = arr.address

    /** Get the size of the memory */
    inline def size: Long = arr.byteSize



    /** Check if the pointer is null or NULL */
    inline def isNull: Boolean =
      arr == null || arr.addr == 0

    /** Check if the pointer is null, NULL, or has a length of 0 */
    inline def isEmpty: Boolean =
      arr == null || arr.addr == 0 || arr.size == 0
  }
}
