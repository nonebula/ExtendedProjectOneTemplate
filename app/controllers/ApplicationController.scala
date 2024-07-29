package controllers

import play.api.mvc.{BaseController, ControllerComponents, Results}
import repositories.DataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext


@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val dataRepository: DataRepository)(implicit val ec: ExecutionContext) extends BaseController {

  def index() = Action {
    Results.Ok("This is a placeholder response for index - 200 OK")
  }

  def create() = TODO

  def read(id: String) = TODO

  def update(id: String) = TODO

  def delete(id: String) = TODO
}
