import generator.{GeneratedState, Generator}
import stock.StockApp.StockRoutes
import zio._
import zio.http.Server

object Main extends ZIOAppDefault {
  private val App = StockRoutes

  private final val program = for {
    _         <- ZIO.log("Starting the application..")
    generator <- Generator.start.fork
    server    <- Server.serve(App)
  } yield server

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    program.provide(GeneratedState.live, Generator.live, Server.default)
}
