package eu.reactivesystems.league.api

import spray.json.DefaultJsonProtocol._

case class Game(home: Club, away: Club, round: Int, homeGoals: Int, awayGoals: Int)

object Game {
  implicit val format = jsonFormat5(Game.apply)
}
