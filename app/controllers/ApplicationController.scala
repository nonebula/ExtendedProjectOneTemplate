package controllers

import play.api.mvc.{BaseController, ControllerComponents, Results}

import javax.inject.{Inject, Singleton}


@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index() = Action {
    Results.Ok("This is a placeholder response for index - 200 OK")
  }

  def create() = TODO

  def read(id: String) = TODO

  def update(id: String) = TODO

  def delete(id: String) = TODO
}
