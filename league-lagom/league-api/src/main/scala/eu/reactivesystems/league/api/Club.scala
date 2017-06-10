package eu.reactivesystems.league.api

import play.api.libs.json.{Format, Json}

case class Club(name: String)

object Club {
  implicit val format: Format[Club] = Json.format[Club]
}
