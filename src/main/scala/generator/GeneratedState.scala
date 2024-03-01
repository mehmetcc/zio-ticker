package generator

import stock.Stock
import zio._
import zio.stm._

object GeneratedState {
  private def initialize: List[Stock] = Set.fill(scala.util.Random.between(30, 50))(Stock.random).toList

  val live: ZLayer[Any, Nothing, GeneratedState] = ZLayer {
    TRef.make(initialize).commit.map(GeneratedState(_))
  }

  def update: ZIO[GeneratedState, Throwable, List[Stock]] = ZIO.serviceWithZIO[GeneratedState](_.update)

  def lookup: ZIO[GeneratedState, Throwable, List[Stock]] = ZIO.serviceWithZIO[GeneratedState](_.lookup)
}

case class GeneratedState(ref: TRef[List[Stock]]) {
  def update: Task[List[Stock]] = ref.updateAndGet(_.map(_.next)).commit

  def lookup: Task[List[Stock]] = ref.get.commit
}
