package services

import com.google.inject.Singleton
import connectors.LibraryConnector
import models.{DataModel, GoogleBook}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LibraryService @Inject()(connector: LibraryConnector) {
  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): Future[Option[GoogleBook]] = {


    val returnedBook: Future[DataModel] = connector.get[DataModel](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term"))
    returnedBook.map {
      case dataModel: DataModel => Some(GoogleBook(
        _id = dataModel._id,
        name = dataModel.name,
        description = dataModel.description,
        pageCount = dataModel.pageCount
      )
      )
      case _ => None

    }

  }
}


