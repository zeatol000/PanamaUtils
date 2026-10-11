package zea.panama.dsl

import scala.language.unsafeNulls
import scala.quoted.*

import java.lang.foreign.*
import java.lang.foreign.ValueLayout.*

/** Represents a *T.
 * 
 * Pointers represent a raw memory segment. They do not apply offset
 * manipulation when dereferencing, so given a pointer to 4 integers in memory,
 * and accessing offset 3, you will be dereferencing a byte index 3. This
 * will cause an illegal argument exception as the underlying MemorySegment
 * is expecting an argument that conforms to the byte offset.
 */
opaque type Ptr[T] = MemorySegment


object Ptr {
  val NULL: Ptr[Nothing] = MemorySegment.NULL

  inline def allocate[T](size: Long)(using a: Arena): Ptr[T] =
    a.allocate(ttvl[T], size)

  inline def allocate[T](layout: MemoryLayout, size: Long)(using a: Arena): Ptr[T] =
    a.allocate(layout, size)

  inline def allocate[T](layout: MemoryLayout)(using a: Arena): Ptr[T] =
    a.allocate(layout)




  inline def apply[T](ptr: MemorySegment): Ptr[T] =
    ptr

  inline def apply[T](addr: Long): Ptr[T] =
    MemorySegment.ofAddress(addr)


  extension [T](ptr: Ptr[T]) {
    inline def raw: MemorySegment = ptr
    inline def addr: Long = ptr.address

    inline def unary_! : T = ptrGet[T](ptr, 0)

    inline def apply(off: Long): T = ptrGet[T](ptr, off)
    inline def update(off: Long, value: T): Unit = ptrSet[T](ptr, off, value)

    inline def reinterpret(size: Long): Ptr[T] =
      ptr.raw.reinterpret(size)
  }


  inline def ptrGet[T](ptr: MemorySegment, offset: Long): T =
    ${ ptrGetImpl[T]('ptr, 'offset) }
  

  def ptrGetImpl[T: Type](using q: Quotes)(
    ptr: Expr[MemorySegment],
    offset: Expr[Long]
  ): Expr[T] = {
    import q.reflect.*

    TypeRepr.of[T] match {
      case t if t =:= TypeRepr.of[Byte]     => '{ $ptr.get(JAVA_BYTE,     $offset) }.asInstanceOf[Expr[T]]
      case t if t =:= TypeRepr.of[Char]     => '{ $ptr.get(JAVA_CHAR,     $offset) }.asInstanceOf[Expr[T]]
      case t if t =:= TypeRepr.of[Float]    => '{ $ptr.get(JAVA_FLOAT,    $offset) }.asInstanceOf[Expr[T]]
      case t if t =:= TypeRepr.of[Int]      => '{ $ptr.get(JAVA_INT,      $offset) }.asInstanceOf[Expr[T]]
      case t if t =:= TypeRepr.of[Long]     => '{ $ptr.get(JAVA_LONG,     $offset) }.asInstanceOf[Expr[T]]
      case t if t =:= TypeRepr.of[Short]    => '{ $ptr.get(JAVA_SHORT,    $offset) }.asInstanceOf[Expr[T]]
      case t if t =:= TypeRepr.of[Boolean]  => '{ $ptr.get(JAVA_BOOLEAN,  $offset) }.asInstanceOf[Expr[T]]
      case _                                => '{ $ptr.get(ADDRESS,       $offset) }.asInstanceOf[Expr[T]]
    }
  }


  inline def ptrSet[T](ptr: MemorySegment, offset: Long, value: T): Unit =
    ptrSetImpl(ttvl[T], ptr, offset, value)

  def ptrSetImpl[T](vl: ValueLayout, ptr: MemorySegment, offset: Long, value: T): Unit = vl match {
    case vl: OfByte    => ptr.set(vl, offset, value.asInstanceOf[Byte])
    case vl: OfChar    => ptr.set(vl, offset, value.asInstanceOf[Char])
    case vl: OfFloat   => ptr.set(vl, offset, value.asInstanceOf[Float])
    case vl: OfInt     => ptr.set(vl, offset, value.asInstanceOf[Int])
    case vl: OfLong    => ptr.set(vl, offset, value.asInstanceOf[Long])
    case vl: OfShort   => ptr.set(vl, offset, value.asInstanceOf[Short])
    case vl: OfBoolean => ptr.set(vl, offset, value.asInstanceOf[Boolean])
    case _             => ptr.set(ADDRESS, offset, value.asInstanceOf[MemorySegment])
  }
}
