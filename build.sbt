name := "local_yellowfin"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= {
  Seq(
    "axis" % "axis-wsdl4j" % "1.2" exclude("log4j", "log4j"),
    "org.apache.axis" % "axis" % "1.4" exclude("log4j", "log4j"),
    "javax.xml.rpc" % "javax.xml.rpc-api" % "1.1" exclude("log4j", "log4j"),
    "commons-discovery" % "commons-discovery" % "0.5" exclude("log4j", "log4j"),
    "log4j" % "log4j" % "1.2.14"
  )
}