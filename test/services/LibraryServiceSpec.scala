package services

import baseSpec.BaseSpec
import cats.data.EitherT
import connectors.LibraryConnector
import models.{APIError, DataModel, GoogleBook}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json, OFormat}

import scala.concurrent.{ExecutionContext, Future}


class LibraryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite {

  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val mockConnector: LibraryConnector = mock[LibraryConnector]
  val testService = new LibraryService(mockConnector)

  implicit val dataModelFormat: OFormat[DataModel] = Json.format[DataModel]

  val gameOfThrones: JsValue = Json.obj(
    "_id" -> "someId",
    "name" -> "A Game of Thrones",
    "description" -> "The best book!!!",
    "pageCount" -> 100
  )


  //  Okay, back to the testing, go ahead and make the "return a book" and "return an error" tests pass in LibraryServiceSpec, this should only require small changes).

  "getGoogleBook" should {
    val url: String = "testUrl"

    "return a book" in {
      (mockConnector.get[DataModel](_: String)(_: OFormat[DataModel], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.right[APIError](Future.successful(gameOfThrones.as[DataModel])))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe Right(GoogleBook("someId", "A Game of Thrones", "The best book!!!", 100))
      }
    }

    "return an error" in {
      (mockConnector.get[DataModel](_: String)(_: OFormat[DataModel], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.left[DataModel](Future.successful(APIError.BadAPIResponse(500, "Test exception"))))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe Left(APIError.BadAPIResponse(500, "Test exception"))
      }
    }
  }
}