package models

import play.api.libs.json.{Json, OFormat}

case class GoogleBook(_id: String, name: String, description: String, pageCount: Int)

object GoogleBook {
  implicit val formats: OFormat[GoogleBook] = Json.format[GoogleBook]
}