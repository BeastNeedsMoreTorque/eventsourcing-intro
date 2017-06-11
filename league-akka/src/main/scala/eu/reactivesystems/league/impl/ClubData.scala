package eu.reactivesystems.league.impl

import eu.reactivesystems.league.api.Club
import spray.json.{
  DeserializationException,
  JsObject,
  JsString,
  JsValue,
  RootJsonFormat
}

case class ClubData(name: String)

object ClubData {

  def apply(club: Club) = new ClubData(club.name)

  implicit object ClubDataFormat extends RootJsonFormat[ClubData] {
    def write(c: ClubData) = JsObject(
      "name" -> JsString(c.name)
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields("name") match {
        case Seq(JsString(name)) =>
          new ClubData(name)
        case _ => throw new DeserializationException("ClubData expected")
      }
    }
  }
}
