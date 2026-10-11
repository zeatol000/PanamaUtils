import org.scalatest.funsuite.AnyFunSuite

import java.lang.foreign.*
import zea.panama.dsl.*

class Arrays extends AnyFunSuite {


  test("Dereferencing past a pointer's allocated size "
     + "should throw ArrayIndexOutOfBoundsException") {

    withConfined {
      val ptr = Arr.allocate[Byte, 1]

      assertThrows[IndexOutOfBoundsException] {
        ptr(1)
      }
    }
  }
}
