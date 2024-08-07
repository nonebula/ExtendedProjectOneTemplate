package connectors

import cats.data.EitherT
import models.APIError
import play.api.libs.json.OFormat
import play.api.libs.ws.{WSClient, WSResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LibraryConnector @Inject()(ws: WSClient) {
  def get[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): EitherT[Future, APIError, Response] = {
    val request = ws.url(url)
    val response = request.get()
    EitherT {
      response
        .map {
          result =>
            Right(result.json.as[Response])
        }
        .recover { case _: WSResponse =>
          Left(APIError.BadAPIResponse(500, "Could not connect"))
        }
    }
  }


}
//error handling here for extension task in get
//Note: We are making many assumptions here including expecting a json body in the response, that the json body can be parsed into our model and that our request made was a success. All of this will need error handling, this will be an extension task.

// ERROR HANDLING USING EITHERT - example:

//def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] =
//  collection.find().toFuture().map {
//    case books: Seq[DataModel] => Right(books)
//    case _ => Left(APIError.BadAPIResponse(404, "Books cannot be found"))
//  }