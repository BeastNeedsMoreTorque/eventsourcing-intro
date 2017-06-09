package eu.reactivesystems.league.api

import play.api.libs.json.{Format, Json}

case class Game(home: Club, away: Club, round: Int, homeGoals: Int, awayGoals: Int)

object Game {
  implicit val format: Format[Game] = Json.format[Game]
}
