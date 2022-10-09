scalaVersion := "3.2.0"

enablePlugins(ScalaNativePlugin)
enablePlugins(SNUnitPlugin)

val snunitVersion = "0.1.0"

libraryDependencies ++= Seq(
  "com.softwaremill.sttp.tapir" %%% "tapir-json-upickle" % "1.1.2",
  "com.lihaoyi" %%% "upickle" % "2.0.0",
  "com.github.lolgab" %%% "snunit-tapir" % snunitVersion,
  "com.github.lolgab" %%% "snunit-async" % snunitVersion,
  "com.github.lolgab" %%% "httpclient" % "0.0.1"
)
