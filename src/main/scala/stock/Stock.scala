package stock

import com.github.javafaker.Faker

import java.time.Instant

case class Stock private (tickId: Int, symbol: Symbol, price: Price, issuedAt: Instant = Instant.now) {
  def next: Stock = Stock(tickId = tickId, symbol = symbol, price = price + scala.util.Random.between(-0.5, 0.5))
}

object Stock {
  private val faker: Faker = new Faker()

  def random: Stock = {
    val stock = faker.stock()
    Stock(0, stock.nyseSymbol(), scala.util.Random.between(0.0, 40.0))
  }
}
