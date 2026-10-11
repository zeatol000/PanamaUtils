# PanamaUtils

A bunch of Project Panama utilities for making lightweight, typesafe Scala code.

## Installation

For the base DSL library, add this to your build.sbt:
```scala
libraryDependencies += "com.github.zeatol000" %% "panamautils-dsl" % "0.1.0"
```

And for the experimental macro library, add this:
```scala
libraryDependencies += "com.github.zeatol000" %% "panamautils-macros" % "0.1.0"

scalacOptions += "-Wconf:msg=.*is out of sync with its TASTy file.*:s"
```

The imports are `zea.panama.dsl.*` and `zea.panama.macros.*` respectively.

The compiler option will be explained below. See [Macro Library](#macro-library)

---
# Base DSL
The DSL contains many utilities for dealing with Project Panama, downcalls, 
opaque types for simple types, and builder utilities for complex types such as structs.



Simple utilities are often just to clear the clutter of a method, e.g.,
```scala
def withConfined[T](fn: Arena ?=> T): T
def withAuto[T](fn: Arena ?=> T): T
def withShared[T](fn: Arena ?=> T): T
```
all create an implicit arena of the respective variety, try to run the implicit
function with it, close the arena, and return the return value of the function.



Downcalls primarily revolve around the DynamicLibrary class, which uses the
opaque types (see below) to make typesafe function and variable definitions.

The C library:
```c
long add(int a, int b);
int variable;
```
can be represented as:
```scala
object Library extends DynamicLibrary(path, Arena.global) {

    val add = function("add")[Long, Int, Int]

    private val (handle, ptr) = variable("variable")[Int]
    def variable: Int = handle.get(ptr)
    def variable_=(x: Int): Unit = handle.set(ptr, x)
}
```

Functions are nice and compact because the underlying MethodHandle is being
bound to the address of the symbol. Variables need 2 values since VarHandles
cannot be bound to the address.



Opaque types are where the DSL really shines. There are 4 primary types:
- `A &=> R`
- `A *=> R`
- `Ptr[T]`
- `Arr[T, C <: Int & Singleton]`

First, `&=>` is a MethodHandle. It represents downcalls created by the
DynamicLibrary's function method.
`*=>` is the opposite of `&=>`. It is a MemorySegment that represents a function
pointer or an upcall.
Both `&=>` and `*=>` are easy to use. To run the function, just use the apply
syntax sugar. They also understand tuples being placed as the argument and will
unbox them to get the arguments.

`Ptr[T]` isn't all too special. It represents a MemorySegment and really only
exists to add a type to the MemorySegment.
`Arr[T, C <: Int & Singleton]` is an array of a compiletime-determined length.
It auromatically calculates MemoryLayout offsets, while a pointer does not.

The DSL uses the type parameters to get the proper ValueLayout and MemoryLayout
at compiletime, so instead of getting a `Ptr[Int]` of length 2 with
`Ptr(a.allocate(JAVA_INT, 2))`, you can get it with `Ptr.allocate[Int](2)`.
The same thing applies to `Arr[Int, 2]`, just the length is supplied via type.



Complex types such as structs and unions are tricky to make utilities for.
The struct:
```c
struct Point {
    const int x;
    const int y;
}
```
can be represented as:
```scala
import zea.panama.dsl.types.Struct

opaque type Point = MemorySegment

object Point extends Struct[Point](
    "x" -> JAVA_INT,
    "y" -> JAVA_INT
) {
        // opaque VarHandles.
    private val _x: Field[Int] = field("x")
    private val _y: Field[Int] = field("y")

    def apply(x: Int, y: Int)(using a: Arena): Point = {
        val seg = a.allocate(LAYOUT)
        _x.set(seg, x)
        _y.set(seg, y)
        seg
    }

    extension (p: Point) {
        def x: Int = _x.get(p)
        def y: Int = _y.get(p)
    }
}
```

As you can see, there isn't much that a DSL can do to help with defining
complex data types. We can only provide utility methods to aid.


---
# Macro Library
The base DSL already uses a lot of inline methods, so why a dedicated macro
library?
Simple, this macro library adds a lot of really complicated macros that the
base DSL has struggles with. Such as making structs nice and clean.

NOTE: the macro library is very experimental and does some things that the
compiler does not like. It is also very picky. Use at your own risk.

Using the Point struct from above, the macro library can instead define it as:
```scala
import zea.panama.macros.struct

@struct class Point {
    val x: Int = ???
    val y: Int = ???
}
object Point
```

It then takes that and generates the following:
```scala
class Point(val segment: MemorySegment) extends AnyVal

object Point {
  val LAYOUT: StructLayout = MemoryLayout.structLayout(
    ValueLayout.JAVA_INT.withName("x"),
    ValueLayout.JAVA_INT.withName("y")
  )
 
  private val x$handle: VarHandle = LAYOUT.varHandle(
    MemoryLayout.PathElement.groupElement("x")
  )
  private val y$handle: VarHandle = LAYOUT.varHandle(
    MemoryLayout.PathElement.groupElement("y")
  )
 
  def apply(using a: Arena)(x: Int, y: Int): Point = {
    val segment = a.allocate(Point.LAYOUT)
    Point.x$handle.set(segment, x)
    Point.y$handle.set(segment, y)
    new Point(segment)
  }
 
  extension (self: Point) {
    def x: Int = x$handle.get(self.segment, 0L).asInstanceOf[Int]
    def y: Int = y$handle.get(self.segment, 0L).asInstanceOf[Int]
  }
}
```

Woah! even AnyVal optimizations? hell yea.
The right hand side of all the fields can really be anything that satisfies the
typer. The entire rhs is eliminated after the macro executes.

The @struct annotation is a MacroAnnotation, which allows for the inline
transformation of the abstract syntax tree. Although, it pushes it a bit too
far.

First, MacroAnnotation does not allow the creation of new top level types,
meaning the class and object must both be user made.

Second, it technically isn't supposed to be able to rewrite the Point's
superclass to AnyVal, but does anyway.

Third, in order to get these to properly work, the macro sends a callback to
the compiler that rewrites the TASTy file. This means you get the amazing
warning "warn: <file>.class is out of sync with its TASTy file ...". For this
reason, I HIGHLY recommend you add the following to your scala compiler options:
`-Wconf:msg=.*is out of sync with its TASTy file.*:s`


Now the really bad news about the macro annotations:
You need to have it in another project.
If you have a struct defined in project A, and project A references the struct,
then the Scala compiler will see "oh hey, this class is an AnyRef and takes no
parameters!", so when you try to run `Point(32, 32)` it will error that `Point`
takes no parameters.
If you somehow get it to compile, and decompile the class files you will notice
that any and all references to `Point` will treat it like an ordinary class,
even though the `Point` class itself applies the AnyVal optimizations.
In other words, a struct "Foo" with no parameters, generates the apply method
`public java/lang/foreign/MemorySegment apply(java/lang/foreign/Arena)`, while
the external references will try to run the method
`public com/example/Foo apply(java/lang/foreign/Arena)`. This throws a
`NoSuchMethodError`. 

On the other hand, with it in another project, the compiler reads the TASTy
file (and the class file) and sees that it "extends" AnyVal, and correctly
references the methods with the underlying MemorySegment.
