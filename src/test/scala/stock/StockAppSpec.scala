package stock

import generator.GeneratedState
import infrastructure.HttpFailure
import stock.StockApp.Failures.SymbolNotFoundFailure
import stock.StockApp.Models.GetSymbolResponse._
import stock.StockApp.Models._
import zio._
import zio.http._
import zio.json._
import zio.test._

object StockAppSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("StockApp should")(
    test("should return a symbol") {
      for {
        lookup   <- GeneratedState.lookup
        symbol    = lookup.head.symbol
        request   = Request.get(s"api/v1/symbol/${symbol}")
        found    <- ZIO.fromOption(lookup.find(_.symbol == symbol))
        expected  = GetSymbolResponse(symbol = found.symbol, price = found.price, issuedAt = found.issuedAt)
        json      = expected.toJson
        response <- StockApp.StockRoutes.runZIO(request)
      } yield assertTrue(response == Response.json(json))
    },
    test("should fail when symbol is not there") {
      for {
        request  <- ZIO.succeed(Request.get(s"api/v1/symbol/ananbacin"))
        expected  = HttpFailure.failureResponse(SymbolNotFoundFailure.getMessage, Status.NotFound)
        response <- StockApp.StockRoutes.runZIO(request)
      } yield assertTrue(response == expected)
    },
    test("should return whole bunch of symbols") {
      for {
        lookup <- GeneratedState.lookup
        request = Request.get(s"api/v1/symbol/")
        expected = GetSymbolsResponse(
                     lookup.map(current => GetSymbolResponse(current.symbol, current.price, current.issuedAt))
                   ).toJson
        response <- StockApp.StockRoutes.runZIO(request)
      } yield assertTrue(response == Response.json(expected))
    }
  ).provide(GeneratedState.live)
}
