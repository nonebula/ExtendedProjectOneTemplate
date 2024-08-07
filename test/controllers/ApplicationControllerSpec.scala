package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.test.Helpers._
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, Result}
import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val TestApplicationController = new ApplicationController(

    component,
    service,
    repoService
  )(executionContext)

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  "ApplicationController .create" should {

    "create a book in the database" in {
      beforeEach()

      val request: FakeRequest[JsValue] = buildPost("/api").withBody(Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED

      afterEach()
    }

    "return a BadRequest for invalid JSON" in {
      val invalidRequest: FakeRequest[JsValue] = buildPost("/api").withBody(Json.obj("invalid" -> "data"))
      val result: Future[Result] = TestApplicationController.create()(invalidRequest)

      status(result) shouldBe Status.BAD_REQUEST
    }
  }


  "ApplicationController .read" should {

    "find a book in the database by id" in {
      beforeEach()

      val request: FakeRequest[JsValue] = buildGet(s"/api/${dataModel._id}").withBody(Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read(dataModel._id)(FakeRequest())

      status(readResult) shouldBe OK
      contentAsJson(readResult) shouldBe Json.toJson(dataModel)

      afterEach()
    }
    "return NotFound if the book is not found" in {
      val readResult: Future[Result] = TestApplicationController.read("nonexistent_id")(FakeRequest())

      status(readResult) shouldBe Status.NOT_FOUND
    }
  }

  "ApplicationController .readName" should {

    "find a book in the database by name" in {
      beforeEach()

      val request: FakeRequest[JsValue] = buildGet(s"/api/${dataModel._id}").withBody(Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.readName(dataModel.name)(FakeRequest())

      status(readResult) shouldBe OK
      contentAsJson(readResult) shouldBe Json.toJson(dataModel)

      afterEach()
    }

    "return NotFound if the book by name is not found" in {
      val readResult: Future[Result] = TestApplicationController.readName("Nonexistent Name")(FakeRequest())

      status(readResult) shouldBe Status.NOT_FOUND
    }
  }

  "ApplicationController .update" should {
    "update a book in the database" in {
      beforeEach()

      val request: FakeRequest[JsValue] = buildPost("/api").withBody(Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val updatedDataModel: DataModel = dataModel.copy(name = "Updated Name")
      val updatedRequest: FakeRequest[JsValue] = buildPut(s"/api/${dataModel._id}").withBody(Json.toJson(updatedDataModel))
      val updatedResult: Future[Result] = TestApplicationController.update(dataModel._id)(updatedRequest)

      status(updatedResult) shouldBe ACCEPTED

      val readResult: Future[Result] = TestApplicationController.read(dataModel._id)(FakeRequest())
      status(readResult) shouldBe OK
      contentAsJson(readResult) shouldBe Json.toJson(updatedDataModel)

      afterEach()
    }
    "return NotFound if the book to update is not found" in {
      val updatedDataModel: DataModel = dataModel.copy(name = "Updated Name")
      val updatedRequest: FakeRequest[JsValue] = buildPut(s"/api/nonexistent_id").withBody(Json.toJson(updatedDataModel))
      val updatedResult: Future[Result] = TestApplicationController.update("nonexistent_id")(updatedRequest)

      status(updatedResult) shouldBe Status.NOT_FOUND
    }

    "return BadRequest for invalid JSON" in {
      val invalidRequest: FakeRequest[JsValue] = buildPut(s"/api/${dataModel._id}").withBody(Json.obj("invalid" -> "data"))
      val result: Future[Result] = TestApplicationController.update(dataModel._id)(invalidRequest)

      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "ApplicationController .delete" should {
    "delete a book in the database" in {
      beforeEach()
      val request: FakeRequest[JsValue] = buildPost("/api").withBody(Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe CREATED

      val deleteRequest: FakeRequest[AnyContent] = buildDelete(s"/api/${dataModel._id}")
      val deletedResult: Future[Result] = TestApplicationController.delete(dataModel._id)(deleteRequest)

      status(deletedResult) shouldBe ACCEPTED

      afterEach()
    }
    "return NotFound if the book to delete is not found" in {
      val deleteRequest: FakeRequest[AnyContent] = buildDelete("/api/nonexistent_id")
      val deletedResult: Future[Result] = TestApplicationController.delete("nonexistent_id")(deleteRequest)

      status(deletedResult) shouldBe Status.NOT_FOUND
    }
  }

  override def beforeEach(): Unit = await(repository.deleteAll())

  override def afterEach(): Unit = await(repository.deleteAll())
}
