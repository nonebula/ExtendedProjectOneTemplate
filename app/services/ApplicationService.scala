package services

import com.google.inject.Singleton
import connectors.LibraryConnector
import models.DataModel

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LibraryService @Inject()(connector: LibraryConnector) {
  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): Future[DataModel] =
    connector.get[DataModel](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term"))


  //  We have also provided an example of a response body Book, go ahead and create a case class that can accept information from the Google APIs. Simply enter the search url with a term and search parameter into your browser, and it will show you the Json response body. You shouldn't include all the fields provided by Google APIs. Instead, choose the ones that match the fields of the DataModel you have already created.

  //case class GoogleInformation()

}
