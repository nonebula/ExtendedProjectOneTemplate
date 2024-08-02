package controllers

import org.mongodb.scala.result.{UpdateResult, DeleteResult}
import models.{APIError, DataModel, GoogleBook}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Result}
import repositories.DataRepository
import services.{LibraryService, RepositoryService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

//class ApplicationController @Inject()(val dataRepository: DataRepository, val controllerComponents: ControllerComponents, val libraryService: LibraryService, val repositoryService: RepositoryService)(implicit val ec: ExecutionContext) extends BaseController {


@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val libraryService: LibraryService, val repositoryService: RepositoryService)(implicit val ec: ExecutionContext) extends BaseController {

  //  Your ApplicationController methods should call those in the service layer

  //  def index(): Action[AnyContent] = Action.async { implicit request =>
  //    repositoryService.readAll().map {
  //      case Right(items) => Ok(Json.toJson(items))
  //      case Left(error) => Status(error.httpResponseStatus)(Json.toJson(error.reason))
  //    }
  //  }

  def index(): Action[AnyContent] = Action.async { implicit request =>
    repositoryService.readAll().map {
      case Right(item: Seq[DataModel]) => Ok(Json.toJson(item))
      case Left(error: APIError.BadAPIResponse) =>
        Status(error.httpResponseStatus)(Json.toJson(error.reason))
    }.recover {
      case ex: Exception => InternalServerError(Json.toJson("An unexpected error occurred"))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        repositoryService.create(dataModel).map(_ => Created).recover {
          case ex: Exception => InternalServerError(Json.toJson("Invalid JSON"))
        }
      case JsError(_) => Future.successful(BadRequest(Json.toJson("Invalid JSON")))
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request =>
    repositoryService.read(id).map {
      case data => Ok(Json.toJson(data))
      case _ => NotFound(Json.toJson("Item not found"))
    }.recover {
      case ex: Exception => InternalServerError(Json.toJson("An unexpected error occurred"))
    }
  }

  def readName(name: String): Action[AnyContent] = Action.async { implicit request =>
    repositoryService.readName(name).map {
      case data => Ok(Json.toJson(data))
      case _ => NotFound(Json.toJson("Item not found"))
    }.recover {
      case ex: Exception => InternalServerError(Json.toJson("An unexpected error occurred"))
    }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        repositoryService.update(id, dataModel).map {
          case updateResult if updateResult.getModifiedCount > 0 => Accepted
          case _ => NotFound(Json.toJson("Item not found or no changes made"))
        }.recover {
          case ex: Exception => InternalServerError(Json.toJson("An error occurred while updating."))
        }
      case JsError(_) => Future.successful(BadRequest(Json.toJson("Invalid JSON")))
    }
  }

  //return to and complete
  //  def updateField(id: String, fieldName: String, newValue: JsValue): Action[AnyContent] = Action.async { implicit request =>
  //    repositoryService.updateField(id, fieldName, newValue).map { result =>
  //      if (result.getModifiedCount > 0) {
  //        Ok(Json.toJson("Update successful"))
  //      } else {
  //        NotFound(Json.toJson("Document not found or no update occurred"))
  //      }
  //    }.recover {
  //      case ex: Exception => InternalServerError(Json.toJson(s"Error updating document: ${ex.getMessage}"))
  //    }
  //  }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    repositoryService.delete(id).map { result =>
      if (result.getDeletedCount > 0) {
        Accepted
      } else {
        NotFound(Json.toJson("Item not found"))
      }
    }.recover {
      case ex: Exception => InternalServerError(Json.toJson("An error occurred while deleting the item"))
    }
  }

  // Fill in the missing implementation to return a Ok response, along with a Json body containing the book we found.
  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    libraryService.getGoogleBook(search = search, term = term).value.map {
      case Right(book) => Ok(Json.toJson(book))
      case Left(APIError.BadAPIResponse(_, _)) => NotFound(Json.toJson("Book not found"))
    }.recover {
      case ex: Exception => InternalServerError(Json.toJson("An unexpected error occurred"))
    }
  }
}

//  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
//    libraryService.getGoogleBook(search = search, term = term).map {
//      case Some(book) => Ok(Json.toJson(book))
//      case None => NotFound(Json.toJson("Book not found"))
//    }
//  }





