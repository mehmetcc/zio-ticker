package stock

import zio._
import zio.test._

object StockDtoSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("StockDto should")(
    test("should create a dto from stock object") {
      val stock = Stock.random
      val dto   = StockDto.fromStock(stock)

      assertTrue(stock.symbol == dto.symbol, stock.price == dto.price, stock.issuedAt == dto.issuedAt)
    }
  )
}
