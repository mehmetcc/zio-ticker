import generator.{GeneratedState, Generator}
import zio._
import zio.Console.printLine

object Main extends ZIOAppDefault {
  private final val program = for {
    _         <- ZIO.log("Starting the application..")
    generator <- Generator.start
  } yield generator

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    program.provide(GeneratedState.live, Generator.live)
}
