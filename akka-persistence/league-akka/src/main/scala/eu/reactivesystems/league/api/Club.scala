package eu.reactivesystems.league.api

import spray.json.DefaultJsonProtocol._


case class Club(name: String)

object Club {
  implicit val format = jsonFormat1(Club.apply)
}

