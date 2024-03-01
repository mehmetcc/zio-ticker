package generator

import zio._
import zio.test._

object GeneratedStateSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("Generated spec should")(
    test("should update") {
      for {
        current <- GeneratedState.lookup
        updated <- GeneratedState.update
      } yield assertTrue(current != updated, current.size == updated.size)
    },
    test("should lookup") {
      for {
        current <- GeneratedState.lookup
      } yield assertTrue(current.size >= 30, current.size <= 50)
    }
  ).provide(GeneratedState.live)
}
