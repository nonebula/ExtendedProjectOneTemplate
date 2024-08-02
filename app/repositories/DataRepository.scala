package repositories

import com.mongodb.client.result.UpdateResult
import models.{APIError, DataModel}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.empty
import org.mongodb.scala.model._
import org.mongodb.scala.result
import play.api.libs.json.JsValue
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRepository @Inject()(
                                mongoComponent: MongoComponent
                              )(implicit ec: ExecutionContext) extends PlayMongoRepository[DataModel](
  collectionName = "dataModels",
  mongoComponent = mongoComponent,
  domainFormat = DataModel.formats,
  indexes = Seq(IndexModel(
    Indexes.ascending("_id")
  )),
  replaceIndexes = false
) {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] =
    collection.find().toFuture().map {
      case books: Seq[DataModel] => Right(books)
      case _ => Left(APIError.BadAPIResponse(404, "Books cannot be found"))
    }

  def create(book: DataModel): Future[DataModel] =
    collection
      .insertOne(book)
      .toFuture()
      .map(_ => book)

  private def byID(id: String): Bson =
    Filters.and(
      Filters.equal("_id", id)
    )

  private def byName(name: String): Bson =
    Filters.and(
      Filters.equal("name", name)
    )

  def read(id: String): Future[Option[DataModel]] = {
    collection.find(byID(id)).headOption
  }

  def readName(name: String): Future[Option[DataModel]] = {
    collection.find(byName(name)).headOption
  }

  def update(id: String, book: DataModel): Future[result.UpdateResult] =
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(false) //What happens when we set this to false? When you set the upsert option to false, the replaceOne operation will only update an existing document and will not insert a new one if a document with the specified _id does not exist. This means that if there is no document with the given _id, the operation will fail and no document will be created.
    ).toFuture()

  //Return to and complete
  //  def updateField(id: String, fieldName: String, newValue: JsValue): Future[result.UpdateResult] = {
  //    collection.updateOne(
  //      filter = byID(id),
  //      update = Updates.set(fieldName, newValue),
  //      options = new UpdateOptions().upsert(false) // Ensures no document is created if the _id does not exist
  //    ).toFuture()
  //  }

  def delete(id: String): Future[result.DeleteResult] =
    collection.deleteOne(
      filter = byID(id)
    ).toFuture()

  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) //Hint: needed for tests

}
