package stock

import generator.GeneratedState
import infrastructure.HttpFailure
import stock.StockApp.Failures._
import stock.StockApp.Models.GetSymbolResponse._
import stock.StockApp.Models._
import zio._
import zio.http._
import zio.json._

import java.time.ZonedDateTime

object StockApp {
  val StockRoutes: HttpApp[GeneratedState] = Routes(getSymbol, getSymbols).toHttpApp

  private def getSymbol: Route[GeneratedState, Nothing] =
    Method.GET / "api" / "v1" / "symbol" / string("symbol") -> handler { (symbol: String, request: Request) =>
      extractSymbol(symbol).fold(success = responseOnceOnSuccess, failure = responseOnFailure)
    }

  private def getSymbols: Route[GeneratedState, Nothing] = Method.GET / "api" / "v1" / "symbol" -> handler {
    extractSymbols.fold(success = responseMultipleOnSuccess, failure = responseOnFailure)
  }

  private def extractSymbols: ZIO[GeneratedState, Throwable, GetSymbolsResponse] = for {
    lookup <- GeneratedState.lookup
    processed = lookup.map(current =>
                  GetSymbolResponse(symbol = current.symbol, price = current.price, issuedAt = current.issuedAt)
                )
    symbols = GetSymbolsResponse(processed)
  } yield symbols

  private def extractSymbol(symbol: Symbol): ZIO[GeneratedState, Throwable, GetSymbolResponse] =
    matchLookup(symbol).map(current => GetSymbolResponse(current.symbol, current.price, current.issuedAt))

  private def matchLookup(symbol: Symbol): ZIO[GeneratedState, Throwable, Stock] = for {
    lookup <- GeneratedState.lookup
    found <- lookup.find(_.symbol == symbol) match {
               case Some(value) => ZIO.succeed(value)
               case None        => ZIO.fail(SymbolNotFoundFailure)
             }
  } yield found

  private def responseOnceOnSuccess(response: GetSymbolResponse): Response = Response.json(response.toJson)

  private def responseMultipleOnSuccess(response: GetSymbolsResponse): Response = Response.json(response.toJson)

  private def responseOnFailure(throwable: Throwable): Response = throwable match {
    case SymbolNotFoundFailure => HttpFailure.failureResponse(SymbolNotFoundFailure.getMessage, Status.NotFound)
    case other: Throwable      => HttpFailure.failureResponse(other.getMessage, Status.InternalServerError)
  }

  object Models {
    case class GetSymbolResponse(symbol: Symbol, price: Price, issuedAt: ZonedDateTime)

    case class GetSymbolsResponse(symbols: List[GetSymbolResponse])

    object GetSymbolResponse {
      implicit val getSymbolEncoder: JsonEncoder[GetSymbolResponse] = DeriveJsonEncoder.gen[GetSymbolResponse]
      implicit val getSymbolDecoder: JsonDecoder[GetSymbolResponse] = DeriveJsonDecoder.gen[GetSymbolResponse]
    }

    object GetSymbolsResponse {
      implicit val getSymbolsEncoder: JsonEncoder[GetSymbolsResponse] = DeriveJsonEncoder.gen[GetSymbolsResponse]
      implicit val getSymbolsDecoder: JsonDecoder[GetSymbolsResponse] = DeriveJsonDecoder.gen[GetSymbolsResponse]
    }
  }

  object Failures {
    case object SymbolNotFoundFailure extends Throwable("Symbol not found")
  }

}
