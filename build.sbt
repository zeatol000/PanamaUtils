name := "PanamaUtils"
version := "0.1.0"
organization := "com.github.zeatol000"

scalaVersion := "3.9.0"
scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-experimental",
  "-language:"+
    "implicitConversions,"+
    "unsafeNulls",
  "-opt",
  "-Wconf:msg=.*is not declared infix.*:s",
  "-Wconf:msg=.*is out of sync with its TASTy file.*:s",
  "-Xmax-inlines:512",
  "-Yexplicit-nulls",
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
)

javaOptions ++= Seq(
  "--add-opens=java.base/java.nio=ALL-UNNAMED",
  "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
  "--enable-native-access=ALL-UNNAMED"
)
fork := true


Compile / scalaSource := baseDirectory.value / "main" / "scala"
Test    / scalaSource := baseDirectory.value / "test" / "scala"
def projectOf(id: String): Project =
  Project(id, file("src-" + id))
    .settings(name := name.value + "-" + id)



// base dsl library
lazy val dsl = projectOf("dsl")


// macro library
lazy val macros = projectOf("macros")
  .dependsOn(dsl)
  .settings(
    libraryDependencies += "org.scala-lang" %% "scala3-compiler" % scalaVersion.value
  )


// runs before test suproject. Needed for the proper TASTy manipulation
// also where most tests are
lazy val preTest = projectOf("pretest")
  .dependsOn(dsl, macros)
  .settings(
    publish / skip := true
  )


// playground, basically
lazy val test = projectOf("test")
  .dependsOn(dsl, macros, preTest)
  .settings(
    publish / skip := true
  )
