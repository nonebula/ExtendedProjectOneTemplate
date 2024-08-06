package services

import cats.data.EitherT
import com.google.inject.Singleton
import connectors.LibraryConnector
import models.{APIError, DataModel, GoogleBook}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LibraryService @Inject()(connector: LibraryConnector)(implicit ec: ExecutionContext) {
  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String): EitherT[Future, APIError, GoogleBook] = {
    val url = urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term")
    val returnedBook: EitherT[Future, APIError, DataModel] = connector.get[DataModel](url)

    returnedBook.map { dataModel =>
      GoogleBook(
        _id = dataModel._id,
        name = dataModel.name,
        description = dataModel.description,
        pageCount = dataModel.pageCount
      )
    }
  }
}
