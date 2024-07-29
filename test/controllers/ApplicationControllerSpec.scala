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

    val result = TestApplicationController.index()(FakeRequest())

    "return TODO" in {
      status(result) shouldBe Status.OK
    }
  }

  "ApplicationController .create()" should {

  }

  "ApplicationController .read()" should {

  }

  "ApplicationController .update()" should {

  }

  "ApplicationController .delete()" should {

  }
}
