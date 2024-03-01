package stock

import zio.Scope
import zio.test.Assertion.isNonEmptyString
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assert, assertTrue}

object StockSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("Tick should")(
    test("create random ticker entry") {
      val created = Stock.random
      assert(created.symbol)(isNonEmptyString) && assertTrue(created.price > 0, created.price <= 40.0)
    },
    test("advance ticker") {
      val created = Stock.random
      val updated = created.next
      assertTrue(
        created.symbol == updated.symbol,
        scala.math.abs(created.price - updated.price) <= 0.5
      )
    }
  )
}
