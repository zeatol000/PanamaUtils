package zea.panama.dsl

import scala.language.unsafeNulls
import scala.quoted.*

import java.lang.foreign.*
import java.lang.foreign.ValueLayout.*

import zea.panama.dsl.internal.PtrMacroImpl.*

/** Represents a *T.
 * 
 * Pointers represent a raw memory segment. They do not apply offset
 * manipulation when dereferencing, so given a pointer to 4 integers in memory,
 * and accessing offset 3, you will be dereferencing a byte index 3. This
 * will cause an illegal argument exception as the underlying MemorySegment
 * is expecting an argument that conforms to the byte offset.
 *
 * Capable of slight pointer arithmetic
 */
opaque type Ptr[T] = MemorySegment


object Ptr {
  private val _NULL: Ptr[Nothing] = MemorySegment.NULL

  /** Get a NULL pointer of a given type */
  def NULL[T]: Ptr[T] = _NULL

  /** Allocate in an arena and return a pointer */
  inline def allocate[T](size: Long)(using a: Arena): Ptr[T] =
    a.allocate(ttvl[T], size)

  /** Allocate in an arena and return a pointer */
  inline def allocate[T](layout: MemoryLayout, size: Long)(using a: Arena): Ptr[T] =
    a.allocate(layout, size)

  /** Allocate in an arena and return a pointer */
  inline def allocate[T](layout: MemoryLayout)(using a: Arena): Ptr[T] =
    a.allocate(layout)



  /** Reinterpret a MemorySegment as a pointer */
  inline def apply[T](ptr: MemorySegment): Ptr[T] =
    ptr

  /** Get a pointer from an address */
  inline def apply[T](addr: Long): Ptr[T] =
    MemorySegment.ofAddress(addr)


  extension [T](ptr: Ptr[T]) {
    /** Get the raw MemorySegment */
    inline def raw: MemorySegment = ptr

    /** Get the address of the memory */
    inline def addr: Long = ptr.address

    /** Get the size of the memory */
    inline def size: Long = ptr.byteSize



    /** Check if the pointer is null or NULL */
    inline def isNull: Boolean =
      ptr == null || ptr.addr == 0

    /** Check if the pointer is null, NULL, or has a length of 0 */
    inline def isEmpty: Boolean =
      ptr == null || ptr.addr == 0 || ptr.size == 0



    /** Dereference */
    inline def unary_! : T = ptrGet[T](ptr, 0)

    /** Dereference with an offset */
    inline def apply(off: Long): T = ptrGet[T](ptr, off)

    /** Dereference (update) at an offset */
    inline def update(off: Long, value: T): Unit = ptrSet[T](ptr, off, value)

    /** Clear the contents of this pointer from memory */
    inline def clear: Unit = ptr.unload



    /** Reinterpret the underlying MemorySegment's length */
    inline def reinterpret(size: Long): Ptr[T] =
      ptr.raw.reinterpret(size)

    /** Reinterpret the type */
    inline def reinterpret[U]: Ptr[U] =
      ptr



    /** Pointer arithmetic */
    inline def + (off: Long): Ptr[T] =
      MemorySegment.ofAddress(ptr.addr + off)

    /** Pointer arithmetic */
    inline def - (off: Long): Ptr[T] =
      MemorySegment.ofAddress(ptr.addr - off)



      /** Are the contents of the memory resident in physical memory */
    inline def loaded: Boolean =
      ptr.isLoaded

    /** Is this pointer a mapped memory segment */
    inline def mapped: Boolean =
      ptr.isMapped

    /** Is this pointer in native memory (offheap) */
    inline def native: Boolean =
      ptr.isNative

    /** Is this pointer read-only */
    inline def readOnly: Boolean =
      ptr.isReadOnly
  }
}
