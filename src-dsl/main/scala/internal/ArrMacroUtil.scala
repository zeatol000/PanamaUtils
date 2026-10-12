package zea.panama.dsl.internal

import scala.language.implicitConversions
import scala.language.unsafeNulls
import scala.compiletime.constValue
import scala.compiletime.erasedValue
import scala.quoted.*

import java.lang.foreign.*

import zea.panama.dsl.*
import zea.panama.dsl.internal.*

object ArrMacroUtil {
  def dimensions(using q: Quotes)(tpe: q.reflect.TypeRepr): Long = {
    import quotes.reflect.*
    tpe match {
      case AppliedType(tycon, List(tType, cType))
        if tycon.typeSymbol == TypeRepr.of[Arr].typeSymbol =>
          1l + dimensions(tType)
      case _ => 0l
    }
  }

  def tycon(using q: Quotes)(tpe: q.reflect.TypeRepr): q.reflect.TypeRepr = {
    import quotes.reflect.*
    tpe match {
      case AppliedType(tycon, List(tType, cType))
        if tycon.typeSymbol == TypeRepr.of[Arr].typeSymbol =>
        tType
      case _ => tpe
    }
  }

  def nestedLength(using q: Quotes)(tpe: q.reflect.TypeRepr, n: Long): Long = {
    import quotes.reflect.*
    tpe match {
      case AppliedType(tycon, List(tType, cType))
        if tycon.typeSymbol == TypeRepr.of[Arr].typeSymbol =>

        if (n <= 0) lengthOf(tpe)
        else nestedLength(tType, n - 1)
      case _ => 0l
    }
  }

  def multiplyNestedLengths(using q: Quotes)(tpe: q.reflect.TypeRepr, max: Long): Long = {
    import quotes.reflect.*
    tpe match {
      case AppliedType(tycon, List(tType, cType))
        if tycon.typeSymbol == TypeRepr.of[Arr].typeSymbol =>
        if (max == 0) return 1l
        //println(tpe.show)
        val a =
        lengthOf(tpe) * multiplyNestedLengths(tType, max - 1)
        //println(a)
        a
      case _ =>
        1l
    }
  }

  def multiLengthOf(using q: Quotes)(tpe: q.reflect.TypeRepr): Long = {
    import quotes.reflect.*
    tpe match {
      case AppliedType(tycon, List(tType, cType))
        if tycon.typeSymbol == TypeRepr.of[Arr].typeSymbol =>

        cType match {
          case ConstantType(LongConstant(value)) =>
            value + multiLengthOf(tType)
          case _ => 0l
        }
      case _ => 0l
    }
  }

  def lengthOf(using q: Quotes)(tpe: q.reflect.TypeRepr): Long = {
    import quotes.reflect.*
    tpe match {
      case AppliedType(tycon, List(tType, cType))
        if tycon.typeSymbol == TypeRepr.of[Arr].typeSymbol =>
        
        cType match {
          case ConstantType(LongConstant(value)) =>
            value
          case _ => 0
        }
      case _ => 0
    }
  } 

  def typeOf(using q: Quotes)(tpe: q.reflect.TypeRepr):
    q.reflect.TypeRepr = {
    import quotes.reflect.*
    tpe match {
      case AppliedType(tycon, List(tType, cType))
        if tycon.typeSymbol == TypeRepr.of[Arr].typeSymbol =>
        
        typeOf(tType)
      case _ => tpe
    }
  }

  def makeMultiType(using q: Quotes)
  (dimensions: Long, tpe: q.reflect.TypeRepr): q.reflect.TypeRepr = {
    import quotes.reflect.*

    if (dimensions <= 0) typeOf(tpe)
    else {
      val len =  ConstantType(LongConstant(lengthOf(tpe)))
      AppliedType(
        TypeRepr.of[Arr],
        List(
          makeMultiType(dimensions - 1, tycon(tpe)),
          len
        )
      )
    }
  }
}
