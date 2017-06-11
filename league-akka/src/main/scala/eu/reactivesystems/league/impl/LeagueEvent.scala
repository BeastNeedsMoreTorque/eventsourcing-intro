package eu.reactivesystems.league.impl

import spray.json.DefaultJsonProtocol._

// Events
sealed trait LeagueEvent {
  val tags = LeagueEvent.tags
}

object LeagueEvent {
  val tags = Set("league-event")
}
case class ClubRegistered(club: ClubData) extends LeagueEvent
object ClubRegistered extends {
  implicit val format = jsonFormat1(ClubRegistered.apply)
}
case class GamePlayed(game: GameData) extends LeagueEvent
object GamePlayed {
  implicit val format = jsonFormat1(GamePlayed.apply)
}

case class ResultRevoked(game: GameData) extends LeagueEvent
object ResultRevoked {
  implicit val format = jsonFormat1(ResultRevoked.apply)
}
