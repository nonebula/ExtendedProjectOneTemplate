package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.test.Helpers._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Result}
import repositories._
import services.LibraryService

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val TestApplicationController = new ApplicationController(
    repository,
    component,
    service
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
  }

  //Make a bad request too


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
  }


  //Make a bad request too


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
  }

  //  "ApplicationController .update" should {
  //    "update a book in the database" in {
  //      beforeEach()
  //
  //      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
  //      val createdResult: Future[Result] = TestApplicationController.create()(request)
  //      status(createdResult) shouldBe Status.CREATED
  //
  //      val updatedDataModel: DataModel = dataModel.copy(name = "Updated Name")
  //      val updatedRequest: FakeRequest[JsValue] = buildPut("/api/${dataModel._id}").withBody[JsValue](Json.toJson(updatedDataModel))
  //      val updatedResult: Future[Result] = TestApplicationController.update(dataModel._id)(updatedRequest)

  //      status(updatedResult) shouldBe ACCEPTED
  //
  //      val readResult: Future[Result] = TestApplicationController.read(dataModel._id)(FakeRequest())
  //      status(readResult) shouldBe OK
  //      contentAsJson(readResult).as[JsValue] shouldBe Json.toJson(updatedDataModel)
  //
  //      afterEach()
  //    }
  //  }

  //Make a bad request too


  //Need to finish the test here but can't seem to work it.
  //  "ApplicationController .updateField" should {
  //    "update a book's field in the database" in {
  //      beforeEach()
  //
  //      val request: FakeRequest[JsValue] = buildPost("/api").withBody(Json.toJson(dataModel))
  //      val createdResult: Future[Result] = TestApplicationController.create()(request)
  //      status(createdResult) shouldBe Status.CREATED
  //
  //      // Update the book's name field
  //      val updatedFieldValue: JsValue = Json.toJson("Updated Name")
  //      val updatedRequest: FakeRequest[JsValue] = buildPut(s"/api/${dataModel._id}/field/name").withBody(updatedFieldValue)
  //      val updatedResult: Future[Result] = TestApplicationController.updateField(dataModel._id, "name", updatedFieldValue)(updatedRequest)
  //
  //      status(updatedResult) shouldBe ACCEPTED
  //
  //      // Read the updated book and verify the updated field
  //      val readResult: Future[Result] = TestApplicationController.read(dataModel._id)(FakeRequest())
  //      status(readResult) shouldBe OK
  //
  //      val expectedDataModel: DataModel = dataModel.copy(name = "Updated Name")
  //      contentAsJson(readResult) shouldBe Json.toJson(expectedDataModel)
  //
  //      contentAsJson(readResult).as[JsValue] shouldBe Json.toJson(expectedDataModel)
  //
  //      afterEach()
  //    }
  //  }


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
  }

  //  "ApplicationController .delete" should {
  //    "delete a book in the database" in {
  //      beforeEach()
  //      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
  //      val createdResult: Future[Result] = TestApplicationController.create()(request)
  //
  //      status(createdResult) shouldBe CREATED
  //
  //      val deleteRequest: FakeRequest[AnyContent] = buildDelete("/api/${dataModel._id}")
  //      val deletedResult: Future[Result] = TestApplicationController.delete(dataModel._id)(deleteRequest)
  //
  //      status(deletedResult) shouldBe ACCEPTED
  //
  //      afterEach()
  //    }
  //  }

  //Make a bad request too


  override def beforeEach(): Unit = await(repository.deleteAll())

  override def afterEach(): Unit = await(repository.deleteAll())
}
