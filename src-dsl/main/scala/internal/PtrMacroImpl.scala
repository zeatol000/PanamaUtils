package zea.panama.dsl.internal

import scala.language.implicitConversions
import scala.language.unsafeNulls
import scala.compiletime.constValue
import scala.compiletime.erasedValue
import scala.quoted.*

import java.lang.foreign.*
import java.lang.foreign.ValueLayout.*

import zea.panama.dsl.*
import zea.panama.dsl.internal.*
import zea.panama.dsl.internal.ArrMacroUtil.*

object PtrMacroImpl {
  inline def ptrGet[T](ptr: MemorySegment, offset: Long): T =
    ${ ptrGetImpl[T]('ptr, 'offset) }
 

  def ptrGetImpl(using q: Quotes)
  (ptr: Expr[MemorySegment], tpe: q.reflect.TypeRepr, offset: Expr[Long]): Expr[Any] = {
    import q.reflect.*
    tpe match {
      case t if t =:= TypeRepr.of[Byte]     => '{ $ptr.get(JAVA_BYTE,     $offset) }
      case t if t =:= TypeRepr.of[Char]     => '{ $ptr.get(JAVA_CHAR,     $offset) }
      case t if t =:= TypeRepr.of[Float]    => '{ $ptr.get(JAVA_FLOAT,    $offset) }
      case t if t =:= TypeRepr.of[Int]      => '{ $ptr.get(JAVA_INT,      $offset) }
      case t if t =:= TypeRepr.of[Long]     => '{ $ptr.get(JAVA_LONG,     $offset) }
      case t if t =:= TypeRepr.of[Short]    => '{ $ptr.get(JAVA_SHORT,    $offset) }
      case t if t =:= TypeRepr.of[Boolean]  => '{ $ptr.get(JAVA_BOOLEAN,  $offset) }
      case _                                => '{ $ptr.get(ADDRESS,       $offset) }
    }
  }


  def ptrGetImpl[T: Type](using q: Quotes)(
    ptr: Expr[MemorySegment],
    offset: Expr[Long]
  ): Expr[T] = {
    import q.reflect.*
  
    ptrGetImpl(using q)(ptr, TypeRepr.of[T], offset).asExprOf[T]
  }


  inline def ptrSet[T](
    ptr: MemorySegment, offset: Long, value: T
  ): Unit =
    ptrSetImpl(ttvl[T], ptr, offset, value)

  def ptrSetImpl[T](
    vl: ValueLayout,
    ptr: MemorySegment,
    offset: Long,
    value: T
  ): Unit = vl match {
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
