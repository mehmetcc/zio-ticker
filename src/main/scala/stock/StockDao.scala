package stock

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio._

object StockDao {
  val live: ZLayer[Quill.Postgres[SnakeCase], Nothing, StockDao] = ZLayer.fromFunction(StockDao.apply _)

  def create(stock: StockDto): ZIO[StockDao, Throwable, RuntimeFlags] = ZIO.serviceWithZIO[StockDao](_.create(stock))

  def create(stocks: List[StockDto]): ZIO[StockDao, Throwable, List[RuntimeFlags]] =
    ZIO.serviceWithZIO[StockDao](_.create(stocks))
}

case class StockDao(quill: Quill.Postgres[SnakeCase]) {
  import quill._

  def create(stock: StockDto): Task[Int] = run {
    query[Stock]
      .insertValue(lift(Stock(stockId = 0, symbol = stock.symbol, price = stock.price, issuedAt = stock.issuedAt)))
      .returningGenerated(generated => generated.stockId)
  }

  def create(stocks: List[StockDto]): Task[List[Int]] = run {
    quote {
      liftQuery(stocks).foreach { stock =>
        query[Stock]
          .insertValue(Stock(stockId = 0, symbol = stock.symbol, price = stock.price, issuedAt = stock.issuedAt))
          .returningGenerated(generated => generated.stockId)
      }
    }
  }
}
