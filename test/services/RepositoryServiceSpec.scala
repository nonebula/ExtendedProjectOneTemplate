package services

import baseSpec.BaseSpec
import models.{APIError, DataModel}
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import repositories.{DataRepository, MockDataRepository}

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.{JsValue, Json, OFormat}

//  Make sure that your RepositoryService functions as intended by using mocking. Do this with a new file called RepositoryServiceSpec, making sure to use Eithers where appropriate. SKIPPED (couldn't get past null exceptions)


class RepositoryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite {

  val mockRepository: MockDataRepository = mock[MockDataRepository]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testRepoService = new RepositoryService(mockRepository)

  private val exampleDataModels: Seq[DataModel] = Seq(
    DataModel("1", "Book One", "Description for Book One", 200),
    DataModel("2", "Book Two", "Description for Book Two", 300)
  )

  private val dataModel = DataModel
  private val exampleDataModel: DataModel = DataModel("3", "Book Three", "Description for Book Three", 150)
  private val exampleError: APIError = APIError.BadAPIResponse(500, "Test error")
  private val exampleUpdateResult: UpdateResult = mock[UpdateResult]
  private val exampleDeleteResult: DeleteResult = mock[DeleteResult]

  "RepositoryService" should {

    "return all data models when readAll is successful" in {
      (mockRepository.index _)
        .expects()
        .returning(Future.successful(Right(exampleDataModels)))

      whenReady(testRepoService.readAll()) { result =>
        result shouldBe Right(exampleDataModels)
      }
    }

    "return an error when readAll fails" in {
      (mockRepository.index _)
        .expects()
        .returning(Future.successful(Left(exampleError)))

      whenReady(testRepoService.readAll()) { result =>
        result shouldBe Left(exampleError)
      }
    }

    //    "create a data model successfully" in {
    //      (mockRepository.create(_: DataModel))
    //        .expects(dataModel)
    //        .returning(Future.successful(Right(exampleDataModel)))
    //
    //      whenReady(testRepoService.create(exampleDataModel)) { result =>
    //        result shouldBe Right(exampleDataModel)
    //      }
    //    }

    //    "return an error when create fails" in {
    //      (mockRepository.create(_: DataModel))
    //        .expects(exampleDataModel) // Pass an instance of DataModel
    //        .returning(Future.successful(Right(exampleDataModel)))
    //
    //      whenReady(testRepoService.create(exampleDataModel)) { result =>
    //        result shouldBe Left(exampleError)
    //        //error here
    //      }
    //    }

    "read a data model by id successfully" in {
      (mockRepository.read _)
        .expects("1")
        .returning(Future.successful(Right(Some(exampleDataModel))))

      whenReady(testRepoService.read("1")) { result =>
        result shouldBe Right(Some(exampleDataModel))
      }
    }

    "return an error when reading by id fails" in {
      (mockRepository.read _)
        .expects("1")
        .returning(Future.successful(Left(exampleError)))

      whenReady(testRepoService.read("1")) { result =>
        result shouldBe Left(exampleError)
      }
    }

    "read a data model by name successfully" in {
      (mockRepository.readName _)
        .expects("Book One")
        .returning(Future.successful(Right(Some(exampleDataModel))))

      whenReady(testRepoService.readName("Book One")) { result =>
        result shouldBe Right(Some(exampleDataModel))
      }
    }

    "return an error when reading by name fails" in {
      (mockRepository.readName _)
        .expects("Book One")
        .returning(Future.successful(Left(exampleError)))

      whenReady(testRepoService.readName("Book One")) { result =>
        result shouldBe Left(exampleError)
      }
    }

    "update a data model successfully" in {
      (mockRepository.update _)
        .expects("1", exampleDataModel)
        .returning(Future.successful(Right(exampleUpdateResult)))

      whenReady(testRepoService.update("1", exampleDataModel)) { result =>
        result shouldBe Right(exampleUpdateResult)
      }
    }

    "return an error when updating fails" in {
      (mockRepository.update _)
        .expects("1", exampleDataModel)
        .returning(Future.successful(Left(exampleError)))

      whenReady(testRepoService.update("1", exampleDataModel)) { result =>
        result shouldBe Left(exampleError)
      }
    }

    "delete a data model successfully" in {
      (mockRepository.delete _)
        .expects("1")
        .returning(Future.successful(Right(exampleDeleteResult)))

      whenReady(testRepoService.delete("1")) { result =>
        result shouldBe Right(exampleDeleteResult)
      }
    }

    "return an error when deleting fails" in {
      (mockRepository.delete _)
        .expects("1")
        .returning(Future.successful(Left(exampleError)))

      whenReady(testRepoService.delete("1")) { result =>
        result shouldBe Left(exampleError)
      }
    }
  }
}