package app

import scala.concurrent.*
import upickle.default.*
import com.github.lolgab.httpclient

class DaprFooStore()(using ExecutionContext) extends FooStore {
  private val daprUrl = "http://localhost:3500/v1.0"
  private val daprStateStore = s"$daprUrl/state/statestore"

  def getFoo(): concurrent.Future[Either[String, Foo]] = get()
    .url(s"$daprStateStore/foo_1")
    .future()
    .map { res =>
      if (is2xx(res.code)) Right(read[Foo](res.body))
      else Left(s"got ${res.body}")
    }

  def setFoo(foo: Foo): concurrent.Future[Either[String, Unit]] = post()
    .url(daprStateStore)
    .body(s"""[{"key": "foo_1", "value": ${write(foo)}}]""")
    .future()
    .map { res =>
      if (is2xx(res.code)) Right(())
      else {
        Left(s"got ${res.body}")
      }
    }

  private def is2xx(code: Int) = code >= 200 && code < 300

  private def baseReq(method: httpclient.Method) =
    httpclient
      .Request()
      .method(method)
      .header("dapr-app-id: app")

  private def post() =
    baseReq(httpclient.Method.POST)
      .header("Content-Type: application/json")

  private def get() =
    baseReq(httpclient.Method.GET)

}
