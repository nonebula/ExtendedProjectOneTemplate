package connectors

import play.api.libs.json.OFormat
import play.api.libs.ws.WSClient

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LibraryConnector @Inject()(ws: WSClient) {
  def get[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): Future[Response] = {
    val request = ws.url(url)
    val response = request.get()
    response.map {
      result =>
        result.json.as[Response]
    }
  }
}
//error handling here for extension task in get
//Note: We are making many assumptions here including expecting a json body in the response, that the json body can be parsed into our model and that our request made was a success. All of this will need error handling, this will be an extension task.