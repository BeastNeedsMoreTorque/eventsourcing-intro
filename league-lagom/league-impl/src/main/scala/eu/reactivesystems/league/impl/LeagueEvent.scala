package eu.reactivesystems.league.impl

import com.lightbend.lagom.scaladsl.persistence.{
  AggregateEvent,
  AggregateEventTag
}
import play.api.libs.json.{Format, Json}

/**
  * This interface defines all the events that the LeagueEntity supports.
  */
sealed trait LeagueEvent extends AggregateEvent[LeagueEvent] {
  override val aggregateTag = LeagueEvent.Tag
}

object LeagueEvent {
  val Tag: AggregateEventTag[LeagueEvent] = AggregateEventTag[LeagueEvent]
}

case class ClubRegistered(club: ClubData) extends LeagueEvent

object ClubRegistered {
  implicit val format: Format[ClubRegistered] = Json.format
}

case class GamePlayed(game: GameData) extends LeagueEvent

object GamePlayed {
  implicit val format: Format[GamePlayed] = Json.format
}

case class ResultRevoked(game: GameData) extends LeagueEvent

object ResultRevoked {
  implicit val format: Format[ResultRevoked] = Json.format
}
