package generator

import stock.{StockDao, StockDto}
import zio._

import scala.language.postfixOps

object Generator {
  val live: ZLayer[GeneratedState, Nothing, Generator] = ZLayer.fromFunction(Generator(_))

  def start: ZIO[StockDao with Generator, Throwable, Long] = ZIO.serviceWithZIO[Generator](_.start)
}

case class Generator(state: GeneratedState) {
  def start: ZIO[StockDao, Throwable, Long] = state.update
    .map(stocks => stocks.map(StockDto.fromStock))
    .tap(StockDao.create)
    .schedule(Schedule.spaced(1 seconds))
}
