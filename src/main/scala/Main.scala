import generator.{GeneratedState, Generator}
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import stock.StockApp.StockRoutes
import stock.StockDao
import zio._
import zio.http.Server

object Main extends ZIOAppDefault {
  private val App = StockRoutes

  private final val program = for {
    _         <- ZIO.log("Starting the application..")
    generator <- Generator.start
    server    <- Server.serve(App)
  } yield server

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    program.provide(
      GeneratedState.live,
      Generator.live,
      StockDao.live,
      Server.default,
      Quill.Postgres.fromNamingStrategy(SnakeCase),
      Quill.DataSource.fromPrefix("database-configuration")
    )
}
