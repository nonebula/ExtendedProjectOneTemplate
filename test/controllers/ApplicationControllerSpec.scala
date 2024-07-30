package controllers

import baseSpec.BaseSpecWithApplication
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.test.Helpers._
import org.mockito.Mockito._
import repositories.DataRepository

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val mockDataRepository: DataRepository = mock[DataRepository]

  val TestApplicationController = new ApplicationController(
    component, mockDataRepository
  )(executionContext)

  "ApplicationController .index" should {
    "return OK and the list of items" in {
      val mockDataModels = Seq(DataModel("1", "Book1", "Description1", 100))
      when(mockDataRepository.index()).thenReturn(Future.successful(Right(mockDataModels)))
      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe Status.OK
      contentAsJson(result) shouldBe Json.toJson(mockDataModels)
    }
  }

  "ApplicationController .create" should {
    "create a new item and return Created" in {
      val dataModel = DataModel("1", "Book", "A book", 100)
      when(mockDataRepository.create(any[DataModel])).thenReturn(Future.successful(dataModel))
      val json = Json.parse("""{ "_id": "1", "name": "Book", "description": "A book", "pageCount": 100 }""")
      val result = TestApplicationController.create()(FakeRequest().withBody(json))
      status(result) shouldBe Status.CREATED
    }
  }

  "ApplicationController .read" should {
    "return OK and the item when found" in {
      val dataModel = DataModel("1", "Book", "A book", 100)
      when(mockDataRepository.read("1")).thenReturn(Future.successful(Some(dataModel)))
      val result = TestApplicationController.read("1")(FakeRequest())
      status(result) shouldBe Status.OK
      contentAsJson(result) shouldBe Json.toJson(dataModel)
    }

    "return NotFound when the item is not found" in {
      when(mockDataRepository.read("1")).thenReturn(Future.successful(None))
      val result = TestApplicationController.read("1")(FakeRequest())
      status(result) shouldBe Status.NOT_FOUND
    }
  }

  "ApplicationController .update" should {
    "update an existing item and return Accepted" in {
      val updatedDataModel = DataModel("1", "Updated Book", "Updated description", 200)
      when(mockDataRepository.update(any[String], any[DataModel])).thenReturn(Future.successful(UpdateResult.acknowledged(1L, 1L, null)))
      val json = Json.parse("""{ "_id": "1", "name": "Updated Book", "description": "Updated description", "pageCount": 200 }""")
      val result = TestApplicationController.update("1")(FakeRequest().withBody(json))
      status(result) shouldBe Status.ACCEPTED
    }

    "return BadRequest when the data is invalid" in {
      val json = Json.parse("""{ "name": "Updated Book", "description": "Updated description", "pageCount": 200 }""") // Missing _id
      val result = TestApplicationController.update("1")(FakeRequest().withBody(json))
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "ApplicationController .delete" should {
    "delete an item and return NoContent" in {
      when(mockDataRepository.delete("1")).thenReturn(Future.successful(DeleteResult.acknowledged(1L)))
      val result = TestApplicationController.delete("1")(FakeRequest())
      status(result) shouldBe Status.NO_CONTENT
    }

    "return NotFound when the item is not found" in {
      when(mockDataRepository.delete("1")).thenReturn(Future.successful(DeleteResult.acknowledged(0L)))
      val result = TestApplicationController.delete("1")(FakeRequest())
      status(result) shouldBe Status.NOT_FOUND
    }
  }
}
