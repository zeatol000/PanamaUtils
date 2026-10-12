package zea.panama.test

import org.scalatest.funsuite.AnyFunSuite

import java.lang.foreign.*
import zea.panama.dsl.*

class Pointers extends AnyFunSuite {

  test("Dereferencing past a pointer's allocated size"
     + "should throw IndexOutOfBoundsException") {
    
    withConfined {
      val ptr = Ptr.allocate[Byte](0)

      assertThrows[IndexOutOfBoundsException] {
        !ptr
      }
    }
  }

  test("Dereferencing NULL should throw IndexOutOfBoundsException") {
    assertThrows[IndexOutOfBoundsException] {
      !Ptr.NULL
    }
  }
}
