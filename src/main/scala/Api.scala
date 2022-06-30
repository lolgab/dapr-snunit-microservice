package app

class Api(store: FooStore) {
  private val setFoo = endpoints.setFoo
    .serverLogic(foo => store.setFoo(foo))

  private val getFoo = endpoints.getFoo
    .serverLogic(_ => store.getFoo())

  val routes = setFoo :: getFoo :: Nil
}
