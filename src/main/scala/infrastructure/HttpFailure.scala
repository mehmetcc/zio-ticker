package infrastructure

import zio.http._
import zio.json._

case class HttpFailure private (message: String)

object HttpFailure {
  implicit val httpFailureEncoder: JsonEncoder[HttpFailure] = DeriveJsonEncoder.gen[HttpFailure]
  implicit val httpFailureDecoder: JsonDecoder[HttpFailure] = DeriveJsonDecoder.gen[HttpFailure]

  def failureResponse(message: String, status: Status): Response =
    Response.json(HttpFailure(message).toJson).status(status)
}

object CommonFailures {
  case object BadRequestFailure extends Throwable("Bad request")
}
