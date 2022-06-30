import scala.concurrent.Future
import snunit.tapir.SNUnitInterpreterFuture._
import sttp.tapir._
import sttp.tapir.json.upickle._
import sttp.tapir.generic.auto._
import upickle.default._
import com.github.lolgab.httpclient
import concurrent.ExecutionContext.Implicits.global

val daprUrl = "http://localhost:3500/v1.0"
val daprStateStore = s"$daprUrl/state/statestore"

case class Foo(bar: String) derives ReadWriter

def is2xx(code: Int) = code >= 200 && code < 300

def baseReq(method: httpclient.Method) =
  httpclient.Request()
    .method(method)
    .header("dapr-app-id: app")

def post() =
  baseReq(httpclient.Method.POST)
    .header("Content-Type: application/json")

def get() =
  baseReq(httpclient.Method.GET)

def addFooToStore(foo: Foo) =
  post()
    .url(daprStateStore)
    .body(s"""[{"key": "foo_1", "value": ${write(foo)}}]""")
    .future()
    .map { res =>
      if(is2xx(res.code)) Right(())
      else {
        Left(s"got ${res.body}")
      }
    }

def getFoosFromStore(): Future[Either[String, Foo]] =
  get()
    .url(s"$daprStateStore/foo_1")
    .future()
    .map { res =>
      if(is2xx(res.code)) Right(read[Foo](res.body))
      else Left(s"got ${res.body}")
    }

val setFoo = endpoint
  .post
  .in("foo")
  .in(jsonBody[Foo])
  .errorOut(stringBody)
  .serverLogic[Future](addFooToStore)

val getFoo = endpoint
  .get
  .in("foo")
  .out(jsonBody[Foo])
  .errorOut(stringBody)
  .serverLogic[Future](_ => getFoosFromStore())

@main
def main() =
  snunit.AsyncServerBuilder
    .build(toHandler(getFoo :: setFoo :: Nil))
