package controllers

import models.DataModel
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Results}
import repositories.DataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val dataRepository: DataRepository)(implicit val ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map {
      case Right(item: Seq[DataModel]) => Ok {
        Json.toJson(item)
      }
      case Left(error) => Status(error)(Json.toJson("Unable to find any books"))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map {
          case true => Created(Json.toJson(dataModel))
          case false => InternalServerError(Json.toJson("Failed to create item"))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.toJson("Invalid data format")))
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.read(id).map {
      case Some(dataModel) => Ok(Json.toJson(dataModel))
      case None => NotFound(Json.toJson("Item not found"))
    }.recover {
      case ex: Exception => InternalServerError(Json.toJson("Error retrieving item"))
    }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.update(id, dataModel).map {
          case true =>
            Accepted(Json.toJson(dataModel))
          case false =>
            NotFound(Json.toJson("Item not found"))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.toJson("Invalid data format")))
    }
  }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.delete(id).map { result =>
      if (result.getDeletedCount > 0) {
        Accepted
      } else {
        Accepted
      }
    }.recover {
      case ex: Exception => InternalServerError(Json.toJson("Error deleting item"))
    }
  }
}



