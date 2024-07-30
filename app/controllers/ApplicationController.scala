package controllers

import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Results}
import repositories.DataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext


@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val dataRepository: DataRepository)(implicit val ec: ExecutionContext) extends BaseController {

  //  def index(): Action[AnyContent] = Action {
  //    Results.Ok("This is a placeholder response for index - 200 OK")
  //  }
  //views.html.index

  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map {
      case Right(items) => Ok(Json.toJson(items))
      case Left(error) => Status(error)(Json.toJson("Unable to find any books"))
    }
  }

  def create(): Action[AnyContent] = TODO

  def read(id: String): Action[AnyContent] = TODO

  def update(id: String): Action[AnyContent] = TODO

  def delete(id: String): Action[AnyContent] = TODO
}
