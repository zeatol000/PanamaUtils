package zea.panama.dsl

import scala.language.unsafeNulls
import scala.compiletime.erasedValue

import java.lang.foreign.*
import java.lang.invoke.MethodHandle

/** Represents a function pointer.
 * Used for upwards calls (JVM methods called by native code).
 */
infix opaque type *=> [A, R] = MemorySegment

object *=> {

  extension[R](
    fn: Unit *=> R
  ) transparent inline def apply() = {
    val handle = getHandleImpl[R](fn)
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(): Unit
      case _ => handle.invoke().asInstanceOf[R]
    }
  }


  extension[R,
  A1](
    fn: (
    A1) *=> R
  ) transparent inline def apply( 
    A1:  A1
  ) = {
    val handle = getHandleImpl[R](fn,
      ttml[A1],
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1
      ): Unit
      case _ => handle.invoke(
        A1
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2](
    fn: (
    A1, A2) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2
  ) = {
    val handle = getHandleImpl[R](fn,
      ttml[A1],
      ttml[A2],
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2
      ): Unit
      case _ => handle.invoke(
        A1,  A2
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3](
    fn: (
    A1, A2, A3) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3
  ) = {
    val handle = getHandleImpl[R](fn,
      ttml[A1],
      ttml[A2],
      ttml[A3],
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4](
    fn: (
    A1, A2, A3, A4) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4
  ) = {
    val handle = getHandleImpl[R](fn,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4]
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5](
    fn: (
    A1, A2, A3, A4, A5) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5
  ) = {
    val handle = getHandleImpl[R](fn,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6](
    fn: (
    A1, A2, A3, A4, A5, A6) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6
  ) = {
    val handle = getHandleImpl[R](fn,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7](
    fn: (
    A1, A2, A3, A4, A5, A6, A7) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7
  ) = {
    val handle = getHandleImpl[R](fn,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8
  ) = {
    val handle = getHandleImpl[R](fn,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9
  ) = {
    val handle = getHandleImpl[R](fn,
      ttml[A1],
      ttml[A2],
      ttml[A3],
      ttml[A4],
      ttml[A5],
      ttml[A6],
      ttml[A7],
      ttml[A8],
      ttml[A9],
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9, A10](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9,  A10: A10
  ) = {
    val handle = getHandleImpl[R](fn,
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
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
  A11](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    A11) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9,  A10: A10,
    A11: A11
  ) = {
    val handle = getHandleImpl[R](fn,
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
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
  A11, A12](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    A11, A12) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9,  A10: A10,
    A11: A11, A12: A12
  ) = {
    val handle = getHandleImpl[R](fn,
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
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
  A11, A12, A13](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    A11, A12, A13) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9,  A10: A10,
    A11: A11, A12: A12, A13: A13
  ) = {
    val handle = getHandleImpl[R](fn,
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
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
  A11, A12, A13, A14](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    A11, A12, A13, A14) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9,  A10: A10,
    A11: A11, A12: A12, A13: A13, A14: A14
  ) = {
    val handle = getHandleImpl[R](fn,
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
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
  A11, A12, A13, A14, A15](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    A11, A12, A13, A14, A15) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9,  A10: A10,
    A11: A11, A12: A12, A13: A13, A14: A14, A15: A15
  ) = {
    val handle = getHandleImpl[R](fn,
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
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
  A11, A12, A13, A14, A15, A16](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    A11, A12, A13, A14, A15, A16) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9,  A10: A10,
    A11: A11, A12: A12, A13: A13, A14: A14, A15: A15,
    A16: A16
  ) = {
    val handle = getHandleImpl[R](fn,
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
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
  A11, A12, A13, A14, A15, A16, A17](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    A11, A12, A13, A14, A15, A16, A17) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9,  A10: A10,
    A11: A11, A12: A12, A13: A13, A14: A14, A15: A15,
    A16: A16, A17: A17
  ) = {
    val handle = getHandleImpl[R](fn,
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
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16, A17
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16, A17
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
  A11, A12, A13, A14, A15, A16, A17, A18](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    A11, A12, A13, A14, A15, A16, A17, A18) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9,  A10: A10,
    A11: A11, A12: A12, A13: A13, A14: A14, A15: A15,
    A16: A16, A17: A17, A18: A18
  ) = {
    val handle = getHandleImpl[R](fn,
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
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16, A17, A18
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16, A17, A18
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
  A11, A12, A13, A14, A15, A16, A17, A18, A19](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    A11, A12, A13, A14, A15, A16, A17, A18, A19) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9,  A10: A10,
    A11: A11, A12: A12, A13: A13, A14: A14, A15: A15,
    A16: A16, A17: A17, A18: A18, A19: A19
  ) = {
    val handle = getHandleImpl[R](fn,
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
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16, A17, A18, A19
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16, A17, A18, A19
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
  A11, A12, A13, A14, A15, A16, A17, A18, A19, A20](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    A11, A12, A13, A14, A15, A16, A17, A18, A19, A20) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9,  A10: A10,
    A11: A11, A12: A12, A13: A13, A14: A14, A15: A15,
    A16: A16, A17: A17, A18: A18, A19: A19, A20: A20
  ) = {
    val handle = getHandleImpl[R](fn,
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
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
  A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
  A21](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
    A21) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9,  A10: A10,
    A11: A11, A12: A12, A13: A13, A14: A14, A15: A15,
    A16: A16, A17: A17, A18: A18, A19: A19, A20: A20,
    A21: A21
  ) = {
    val handle = getHandleImpl[R](fn,
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
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
        A21
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
        A21
      ).asInstanceOf[R]
    }
  }



  extension[R,
  A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
  A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
  A21, A22](
    fn: (
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
    A21, A22) *=> R
  ) transparent inline def apply(
    A1:  A1,  A2:  A2,  A3:  A3,  A4:  A4,  A5:  A5,
    A6:  A6,  A7:  A7,  A8:  A8,  A9:  A9,  A10: A10,
    A11: A11, A12: A12, A13: A13, A14: A14, A15: A15,
    A16: A16, A17: A17, A18: A18, A19: A19, A20: A20,
    A21: A21, A22: A22
  ) = {
    val handle = getHandleImpl[R](fn,
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
    )
    inline erasedValue[R] match {
      case _: Unit => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
        A21, A22
      ): Unit
      case _ => handle.invoke(
        A1,  A2,  A3,  A4,  A5,  A6,  A7,  A8,  A9,  A10,
        A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
        A21, A22
      ).asInstanceOf[R]
    }
  }

  private inline def getHandleImpl[R](fn: MemorySegment, vls: MemoryLayout*): MethodHandle = {
    val descriptor: FunctionDescriptor = inline erasedValue[R] match {
      case _: Unit => FunctionDescriptor.ofVoid(vls*)
      case _       => FunctionDescriptor.of(vls.head, vls.tail*)
    }
    LINKER.downcallHandle(fn, descriptor)
  }
}
