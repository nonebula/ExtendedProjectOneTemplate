package services

import models.{APIError, DataModel, GoogleBook}
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import repositories.{MockDataRepository}

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

  def delete(id: String): Future[Either[APIError, DeleteResult]] = {
    dataRepository.delete(id)
  }

  //  Use the html files in the views package to display at least one book from the Google Books API. Do this by
  //  using the connector to retrieve the book by searching for the isbn in the url (you may have to change the structure of dataModels),
  //  store the book in Mongo to show you have it
  //  then display the book in your browser by returning the JSON of your book model
  //  call made at browser url → controller → service → connector → Google Books → connector → service → views → service → controller

}
