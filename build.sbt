organization  := "tintoy"
name          := "slick-db-demo"
version       := "1.0"

scalaVersion  := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-async" % "0.9.5",
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.postgresql" % "postgresql" % "9.4-1204-jdbc4"
)
