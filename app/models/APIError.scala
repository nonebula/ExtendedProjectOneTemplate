package models

import play.api.http.Status

sealed abstract class APIError(
                                val httpResponseStatus: Int,
                                val reason: String
                              )

object APIError {

  final case class BadAPIResponse(upstreamStatus: Int, upstreamMessage: String)
    extends APIError(
      Status.INTERNAL_SERVER_ERROR,
      s"Bad response from upstream; got status: $upstreamStatus, and got reason $upstreamMessage"
    )

  final case class DatabaseError(message: String, cause: Option[Throwable] = None)
    extends APIError(
      Status.INTERNAL_SERVER_ERROR,
      s"Database error occurred: $message"
    )

  final case class NotFoundError(message: String)
    extends APIError(
      Status.NOT_FOUND,
      s"Resource not found: $message"
    )

}
