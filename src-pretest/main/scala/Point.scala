package test

import zea.panama.dsl.*
import zea.panama.macros.*

@struct class Points {
  val x: Ptr[Arr[Int, 32]] = ???
  val y: Ptr[Arr[Int, 32]] = ???
}
object Points
