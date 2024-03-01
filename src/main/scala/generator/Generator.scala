package generator

import zio._

import scala.language.postfixOps

object Generator {
  val live: ZLayer[GeneratedState, Nothing, Generator] = ZLayer.fromFunction(Generator(_))

  def start: ZIO[Generator, Throwable, Long] = ZIO.serviceWithZIO[Generator](_.start)
}

case class Generator(state: GeneratedState) {
  def start: Task[Long] = state.update
    .tap(stocks => ZIO.log(stocks.toString))
    .schedule(Schedule.spaced(1 seconds))
}
