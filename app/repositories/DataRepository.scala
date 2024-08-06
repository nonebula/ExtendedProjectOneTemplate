package repositories

import com.google.inject.ImplementedBy
import com.mongodb.client.result.{DeleteResult, UpdateResult}
import models.{APIError, DataModel}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.empty
import org.mongodb.scala.model._
import org.mongodb.scala.{MongoCollection, result}
import play.api.libs.json.JsValue
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[DataRepository])
trait MockDataRepository {
  def index(): Future[Either[APIError, Seq[DataModel]]]

  def create(dataModel: DataModel): Future[Either[APIError, DataModel]]

  def read(id: String): Future[Either[APIError, Option[DataModel]]]

  def readName(name: String): Future[Either[APIError, Option[DataModel]]]

  def update(id: String, book: DataModel): Future[Either[APIError, UpdateResult]]

  def delete(id: String): Future[Either[APIError, DeleteResult]]
}

@Singleton
class DataRepository @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends PlayMongoRepository(
  collectionName = "dataModels",
  mongoComponent = mongoComponent,
  domainFormat = DataModel.formats,
  indexes = Seq(IndexModel(
    Indexes.ascending("_id")
  )),
  replaceIndexes = false
) with MockDataRepository {

  def index(): Future[Either[APIError, Seq[DataModel]]] = {
    collection.find().toFuture().map {
      books => Right(books)
    }.recover {
      case ex: Exception =>
        Left(APIError.DatabaseError("Failed to fetch books", Some(ex)))
    }
  }

  def create(book: DataModel): Future[Either[APIError, DataModel]] = {
    collection
      .insertOne(book)
      .toFuture()
      .map(_ => Right(book))
      .recover {
        case ex: Exception =>
          Left(APIError.DatabaseError("Failed to create book", Some(ex)))
      }
  }

  private def byID(id: String): Bson =
    Filters.and(
      Filters.equal("_id", id)
    )

  private def byName(name: String): Bson =
    Filters.and(
      Filters.equal("name", name)
    )

  def read(id: String): Future[Either[APIError, Option[DataModel]]] = {
    collection.find(byID(id)).headOption.map { book =>
      Right(book)
    }.recover {
      case ex: Exception =>
        Left(APIError.DatabaseError(s"Failed to read book with id $id", Some(ex)))
    }
  }

  def readName(name: String): Future[Either[APIError, Option[DataModel]]] = {
    collection.find(byName(name)).headOption.map { book =>
      Right(book)
    }.recover {
      case ex: Exception =>
        Left(APIError.DatabaseError(s"Failed to read book with name $name", Some(ex)))
    }
  }

  def update(id: String, book: DataModel): Future[Either[APIError, result.UpdateResult]] =
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(false) //What happens when we set this to false? When you set the upsert option to false, the replaceOne operation will only update an existing document and will not insert a new one if a document with the specified _id does not exist. This means that if there is no document with the given _id, the operation will fail and no document will be created.
    ).toFuture().map { result =>
      Right(result)
    }.recover {
      case ex: Exception =>
        Left(APIError.DatabaseError(s"Failed to update book with id $id", Some(ex)))
    }

  def delete(id: String): Future[Either[APIError, result.DeleteResult]] = {
    collection.deleteOne(byID(id)).toFuture().map(Right(_)).recover {
      case ex: Exception => Left(APIError.DatabaseError(s"Failed to delete book with id $id", Some(ex)))
    }
  }

  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) //Hint: needed for tests

}





//Return to and complete
//  def updateField(id: String, fieldName: String, newValue: JsValue): Future[result.UpdateResult] = {
//    collection.updateOne(
//      filter = byID(id),
//      update = Updates.set(fieldName, newValue),
//      options = new UpdateOptions().upsert(false) // Ensures no document is created if the _id does not exist
//    ).toFuture()
//  }