package stock

import com.github.javafaker.Faker

import java.time.ZonedDateTime
import scala.annotation.tailrec

case class StockDto(symbol: Symbol, price: Price, issuedAt: ZonedDateTime = ZonedDateTime.now())

object StockDto {
  def fromStock(stock: Stock): StockDto =
    new StockDto(symbol = stock.symbol, price = stock.price, issuedAt = stock.issuedAt)
}

case class Stock private (stockId: Int, symbol: Symbol, price: Price, issuedAt: ZonedDateTime = ZonedDateTime.now()) {
  def next: Stock = Stock(stockId = stockId, symbol = symbol, price = price + scala.util.Random.between(-0.5, 0.5))
}

object Stock {
  private val faker: Faker = new Faker()

  @tailrec
  def random: Stock = {
    val generated = generate

    if (generated.symbol.forall(_.isLetter)) generated else random
  }

  private def generate: Stock = {
    val stock = faker.stock()
    Stock(0, stock.nyseSymbol(), scala.util.Random.between(0.0, 40.0))
  }
}
