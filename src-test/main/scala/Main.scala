package test

import java.lang.foreign.*

import zea.panama.dsl.*

object Main {
  def main(args: Array[String]): Unit = {
    given arena: Arena = Arena.ofConfined()
    try {
      println("Hello, world!")

      run
    }
    finally {
      arena.close()
    }
  }

  
  def run(using a: Arena): Unit = {
    val p = Points(newArr, newArr)

    val x: Arr[Int, 32] = (!p.x.reinterpret(8)).reinterpret(32 * 4)
    val y: Arr[Int, 32] = (!p.y.reinterpret(8)).reinterpret(32 * 4)

    val sb = new StringBuilder()
    
    for (i <- 0 until 32) {
      val value: Int = x(i)
      sb.append(value).append('\t')
    }

    sb.append("\n\n")

    for (i <- 0 until 32) {
      val value: Int = y(i)
      sb.append(value).append('\t')
    }

    println(sb)
  }


  def newArr(using a: Arena): Ptr[Arr[Int, 32]] = {
    val ptr: Ptr[Arr[Int, 32]] =
      Ptr.allocate(1)

    val ms: Arr[Int, 32] =
      Arr.allocate

    for (i <- 0 until 32) {
      ms(i) = i
    }

    ptr(0) = ms
    ptr
  }
}



/*
 * The C struct:
 * ```c
 * struct Point {
 *   const int x;
 *   const int y;
 * };
 * ```
 *
 * Can be written as:
 * ```scala
 * @struct class Point {
 *   val x: Int = ???
 *   val y: Int = ???
 * }
 * object Point
 * ```
 *
 * Where '???' is a placeholder for any term that satisfies the
 * typer. It doesn't have to actually work in production since the
 * rhs of the definition is fully erased.
 * The generated code will then be:
 * ```scala
 * class Point(val segment: MemorySegment) extends AnyVal
 * 
 * object Point {
 *   val LAYOUT: StructLayout = MemoryLayout.structLayout(
 *     ValueLayout.JAVA_INT.withName("x"),
 *     ValueLayout.JAVA_INT.withName("y")
 *   )
 *
 *   private val x$handle: VarHandle = LAYOUT.varHandle(
 *     MemoryLayout.PathElement.groupElement("x")
 *   )
 *   private val y$handle: VarHandle = LAYOUT.varHandle(
 *     MemoryLayout.PathElement.groupElement("y")
 *   )
 *
 *   def apply(using a: Arena)(x: Int, y: Int): Point = {
 *     val segment = a.allocate(Point.LAYOUT)
 *     Point.x$handle.set(segment, x)
 *     Point.y$handle.set(segment, y)
 *     new Point(segment)
 *   }
 *
 *   extension (self: Point) {
 *     def x: Int = x$handle.get(self.segment, 0L).asInstanceOf[Int]
 *     def y: Int = y$handle.get(self.segment, 0L).asInstanceOf[Int]
 *   }
 * }
 * ```
 *
 * This constructs a fully typesafe api for the C struct with minimal
 * boilerplate.
 * Similarly to the example, if the struct did not have 'const', then
 * the Scala representation would use 'var', and the macro annotation
 * would also generate setter methods.
 * 
 * Note that the VarHandles have 0L passed as the second paramater.
 * This always happens. When setting the value, you would use:
 *   handle.set(segment, 0L, value)
 */
