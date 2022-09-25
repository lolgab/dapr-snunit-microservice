scalaVersion := "3.2.0"

enablePlugins(ScalaNativePlugin)

val snunitVersion = "0.0.25"

libraryDependencies ++= Seq(
  "com.softwaremill.sttp.tapir" %%% "tapir-json-upickle" % "1.1.1",
  "com.lihaoyi" %%% "upickle" % "2.0.0",
  "com.github.lolgab" %%% "snunit-async" % snunitVersion,
  "com.github.lolgab" %%% "snunit-tapir" % snunitVersion,
  "com.github.lolgab" %%% "httpclient" % "0.0.1"
)

lazy val deploy = taskKey[Unit]("deploy to NGINX Unit")
deploy := {
  import sys.process._
  // `unitd --help` prints the default unix socket
  val unixSocketPath = Seq("unitd", "--help").!!.linesIterator
    .find(_.contains("unix:"))
    .get
    .replaceAll(".+unix:", "")
    .stripSuffix("\"")

  val nativeLinkResult = (Compile / nativeLink).value

  def sendConfig() = {
    val config = s"""{
      "applications": {
        "app": {
          "type": "external",
          "executable": "$nativeLinkResult"
        }
      },
      "listeners": {
        "*:8080": {
          "pass": "applications/app"
        }
      }
    }"""
    val result = Seq(
      "curl",
      "--unix-socket",
      unixSocketPath,
      "-X",
      "PUT",
      "-d",
      config,
      "http://localhost/config"
    ).!!

    println(result)
  }
  def sendRestart() = {
    val result = Seq(
      "curl",
      "--unix-socket",
      unixSocketPath,
      "http://localhost/control/applications/app/restart"
    ).!!

    println(result)
  }

  sendConfig()
  sendRestart()
}
