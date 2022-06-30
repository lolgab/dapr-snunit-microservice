package app

import sttp.tapir.*
import sttp.tapir.json.upickle.*
import sttp.tapir.generic.auto.*

object endpoints {
  val setFoo = endpoint
    .post
    .in("foo")
    .in(jsonBody[Foo])
    .errorOut(stringBody)
  
  val getFoo = endpoint
    .get
    .in("foo")
    .out(jsonBody[Foo])
    .errorOut(stringBody)
}
