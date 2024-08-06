package services

import cats.data.EitherT
import models.{APIError, DataModel, GoogleBook}
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import repositories.{DataRepository, MockDataRepository}

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject

class RepositoryService @Inject()(dataRepository: MockDataRepository)(implicit ec: ExecutionContext) {

  def readAll(): Future[Either[APIError, Seq[DataModel]]] = {
    dataRepository.index().map {
      case Right(items) => Right(items)
      case Left(error) => Left(error)
    }
  }

  def create(dataModel: DataModel): Future[Either[APIError, DataModel]] = {
    dataRepository.create(dataModel)
  }

  def read(id: String): Future[Either[APIError, Option[DataModel]]] = {
    dataRepository.read(id)
  }

  def readName(name: String): Future[Either[APIError, Option[DataModel]]] = {
    dataRepository.readName(name)
  }

  def update(id: String, book: DataModel): Future[Either[APIError, UpdateResult]] =
    dataRepository.update(id, book)

  //  def updateField(id: String, fieldName: String, newValue: JsValue): Future[UpdateResult] = {
  //    dataRepository.updateField(id, fieldName, newValue)
  //  }

  def delete(id: String): Future[Either[APIError, DeleteResult]] = {
    dataRepository.delete(id)
  }
}
