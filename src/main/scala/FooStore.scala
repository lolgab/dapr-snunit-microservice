package app

import scala.concurrent.Future

trait FooStore {
  def getFoo(): Future[Either[String, Foo]]
  def setFoo(foo: Foo): Future[Either[String, Unit]]
}
