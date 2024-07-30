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
import play.api.mvc.Result
import repositories.DataRepository

import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val TestApplicationController = new ApplicationController(
    repository,
    component
  )

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  "ApplicationController .create" should {
    "create a book in the database" in {
      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.???
    }
  }

  //Make a bad request too


  "ApplicationController .read" should {
    "find a book in the database by id" in {
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      //Hint: You could use status(createdResult) shouldBe Status.CREATED to check this has worked again
      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())
      status(readResult) shouldBe ???
      contentAsJson(readResult).as[???] shouldBe ???
    }
  }

  //Make a bad request too


  "ApplicationController .update" should {
    "update a book in the database" in {
      // Create the initial data
      // Check the creation status
      // Update the data
      // Check the update status
      // Verify the updated data
    }
  }

  //Make a bad request too


  "ApplicationController .delete" should {
    "delete a book in the database" in {
      // Create the initial data
      // Check the creation status
      // Delete the data
      // Check the delete status
      // Verify the data is deleted
    }
  }

  //Make a bad request too


  //  "test name" should {
  //    "do something" in {
  //      beforeEach
  //    ...
  //      afterEach
  //    }
  //  }
  //  ...
  //  override def beforeEach(): Unit = await(repository.deleteAll())
  //  override def afterEach(): Unit = await(repository.deleteAll())


}
