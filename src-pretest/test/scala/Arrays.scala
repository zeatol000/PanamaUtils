package zea.panama.test

import scala.quoted.*

import org.scalatest.funsuite.AnyFunSuite

import java.lang.foreign.*
import zea.panama.dsl.*

import Macros.*

class Arrays extends AnyFunSuite {
  test("Dereferencing past a pointer's allocated size "
     + "should throw IndexOutOfBoundsException") {

    withConfined {
      val ptr: Arr[Byte, 1L] = Arr.allocate

      assertThrows[IndexOutOfBoundsException] {
        ptr(1L)
      }
    }
  }

  test("Arrays should be readable and writable") {
    withConfined {
      val arr: Arr[Int, 32L] = Arr.allocate
      for (i <- 0 until 32) {
        arr(i) = i
      }

      for (i <- 0 until 32) {
        assert(arr(i) == i)
      }
    }
  }

  test("Multidimensional arrays should be in one block in memory") {
    withConfined {
      val arr: Arr[Arr[Int, 4L], 4L] = Arr.allocate

      assert(arr.raw.byteSize == ttvl[Int].byteSize * 4 * 4)

      for (i <- 0 until 4)
        for (j <- 0 until 4)
          arr(i)(j) = i * j


      val ptr: Ptr[Int] = arr.toPointer.reinterpret
      
      for (i <- 0 until 4)
        for (j <- 0 until 4) {
          val off = (i * 4 + j) * ttvl[Int].byteSize

          assert(ptr(off) == i * j)
        }
    }
  }

  test("dimensions of non array should be 0") {
    assert(dimensionsOf[Int] == 0)
  }
  test("dimensions of array should be 1") {
    assert(dimensionsOf[Arr[Int, 2L]] == 1)
  }
  test("dimensions of array of arrays should be 2") {
    assert(dimensionsOf[Arr[Arr[Int, 2L], 1L]] == 2)
  }
}
