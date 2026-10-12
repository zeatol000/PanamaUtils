package zea.panama.dsl.internal

import scala.language.implicitConversions
import scala.language.unsafeNulls
import scala.compiletime.constValue
import scala.compiletime.erasedValue
import scala.quoted.*

import java.lang.foreign.*

import zea.panama.dsl.*
import zea.panama.dsl.internal.*
import zea.panama.dsl.internal.ArrMacroUtil.*
import zea.panama.dsl.internal.PtrMacroImpl => Ptr

object ArrMacroImpl { 
  def applyImpl[T: Type, C <: Long & Singleton: Type]
  (using Quotes, Type[Arr[T, C]])
  (arr: Expr[Arr[T, C]], off: Expr[Long]): Expr[T] = {
    import quotes.reflect.*
   
    val tpe = TypeRepr.of[Arr[T, C]]
    val d = dimensions(tpe)

    // WARN: for some reason, a memorysegment describing a byte, with
    // length 1 is validating a qword. I.e., Arr[Byte, 1L](1L) returns
    // garbage data instead of throwing.
    // We need to manually check for every dereference... ugh.
    if (d == 1) {
      val len = Expr(lengthOf(tpe))
      '{
        val off2 = $off
        if (off2 < 0 || off2 >= $len)
          throw new IndexOutOfBoundsException(
            s"Accessing index $off2 in Arr of length ${$len}")
        Ptr.ptrGet[T]($arr.raw, off2 * ttml[T].byteSize)
      }
    }
    else {
      val length: Expr[Long] = Expr(lengthOf(TypeRepr.of[T]))
      val vl = ttvle(typeOf(tpe))
      val dif = Expr(multiplyNestedLengths(tpe, -1) / lengthOf(tpe))
      '{
        val bs = $vl.byteSize
        MemorySegment.ofAddress(
          $arr.raw.address + $off * $dif * bs
        ).reinterpret($length * bs).asInstanceOf[T]
      }
    }
  }



  def updateImpl[T: Type, C <: Long & Singleton: Type]
  (using Quotes)
  (arr: Expr[Arr[T, C]], off: Expr[Long], value: Expr[T]): Expr[Unit] = {
    import quotes.reflect.*
 
    val tpe = TypeRepr.of[Arr[T, C]]
    val d = dimensions(tpe)

    // QWord nonsense
    if (d == 1) {
      val len = Expr(lengthOf(tpe))
      '{
        val off2 = $off
        if (off2 < 0 || off2 >= $len)
          throw new IndexOutOfBoundsException(
            s"Accessing index $off2 in Arr of length ${$len}")
        Ptr.ptrSet[T]($arr.raw, off2 * ttml[T].byteSize, $value)
      }
    }
    else {
      report.error("attempt to update multidimensional array.\n"
        + "Help: consider running apply first to get the first array dimension.\n"
        + "Eg: arr(i)(j) = v")
      '{ () }
    }
  }



  def reinterpretImpl[T: Type, U: Type,
    C <: Long & Singleton: Type, K <: Long & Singleton: Type
  ](using Quotes)(arr: Expr[Arr[T, C]]): Expr[Arr[U, K]] = {
    import quotes.reflect.*
    val t = TypeRepr.of[Arr[U, K]]
    val len = Expr(multiplyNestedLengths(t, -1))
    val tpe = ttmle(typeOf(t))

    '{
      $arr.raw.reinterpret($len * $tpe.byteSize)
        .asInstanceOf[Arr[U, K]]
    }
  }



  def allocateImpl[T: Type, C <: Long & Singleton: Type](using Quotes)
  (a: Expr[Arena]): Expr[Arr[T, C]] = {
    import quotes.reflect.*
    val r = TypeRepr.of[Arr[T, C]]

    val tpe = ttmle(typeOf(r))
    val len = Expr(multiplyNestedLengths(r, -1))

    '{
      Arr[T, C](
        $a.allocate($tpe, $len)
      )
    }
  }
  


  def getImpl[T: Type, C <: Long & Singleton: Type]
  (using q: Quotes)(arr: Expr[Arr[T, C]], i: Expr[Long], is: Expr[Seq[Long]]):
    Expr[Any] = {
    import q.reflect.*
    val typeInitial = TypeRepr.of[Arr[T, C]]
    var t = typeInitial
    val d = dimensions(t)

    val mul = TypeRepr.of[Int].typeSymbol.methodMember("*").head
    val add = TypeRepr.of[Int].typeSymbol.methodMember("+").head

    is match {
      case Varargs(args) => // args: Seq[Expr[Long]]
        if (args.length == 0) {
          return applyImpl[T, C](arr, i)
        }
        else if (d < args.length + 1) {
          report.error("arguments provided to Arr.get(...) exceeds the number "
                      +"of dimensions in the array.")
          return '{()}.asExprOf[Nothing]
        }

        // d >= args.length + 1
        // off = i*N2*N3*N4.. + j*N2*N3.. + k*N2.. + l..
        val len1 = lengthOf(t)

        var n = multiplyNestedLengths(t, -1) / len1
        var tree = Apply(Select(i.asTerm, mul), Literal(LongConstant(n)) :: Nil)

        var argss = args

        while ({
          t = tycon(t)
          n = multiplyNestedLengths(t, -1) / len1

          val rhs = Apply(Select(argss.head.asTerm, mul), Literal(LongConstant(n)) :: Nil)

          tree = Apply(Select(tree, add), rhs :: Nil)
          argss = argss.tail
          argss.length > 0
        }) ()
          
        val off = tree.asExprOf[Long]
        val layout = ttmle(typeOf(typeInitial))

        if (d == args.length + 1) {
          val tpe = typeOf(typeInitial)

          Ptr.ptrGetImpl(using q)(
            '{$arr.raw},
            tpe,
            '{ $off * ${layout}.byteSize }
          )
        }
        else {
          val diff = d - (args.length + 1)
          val tpe = makeMultiType(diff, typeInitial)

          val length = Expr(lengthOf(TypeRepr.of[T]))
          '{
            val bs = $layout.byteSize
            MemorySegment.ofAddress(
              $arr.raw.address + $off * bs
            ).reinterpret($length * bs)
          }
        }
      case _ =>
        applyImpl[T, C](arr, i)
    }
  }

}
