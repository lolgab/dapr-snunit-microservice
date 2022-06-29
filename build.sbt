scalaVersion := "3.1.2"

enablePlugins(ScalaNativePlugin)

libraryDependencies ++= Seq(
  "com.softwaremill.sttp.client3" %%% "core" % "3.6.2",
  "com.softwaremill.sttp.tapir" %%% "tapir-json-upickle" % "1.0.0",
  "com.lihaoyi" %%% "upickle" % "2.0.0",
  "com.github.lolgab" %%% "snunit-async" % "0.0.20",
  "com.github.lolgab" %%% "snunit-tapir" % "0.0.21-4-1721a5",
  "com.github.lolgab" %%% "httpclient" % "0.0.1-SNAPSHOT"
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
