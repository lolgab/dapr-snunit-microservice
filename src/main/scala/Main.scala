package app

import snunit.tapir.SNUnitFutureServerInterpreter.*
import scala.concurrent.ExecutionContext.Implicits.global

val store = new DaprFooStore()
val api = new Api(store)

@main
def main() =
  snunit.AsyncServerBuilder
    .build(toHandler(api.routes))
