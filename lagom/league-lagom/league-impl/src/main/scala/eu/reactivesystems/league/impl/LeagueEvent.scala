package eu.reactivesystems.league.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import play.api.libs.json.{Format, Json}

/**
  * This interface defines all the events that the LeagueEntity supports.
  */
sealed trait LeagueEvent extends AggregateEvent[LeagueEvent]{
  override val aggregateTag = LeagueEvent.Tag
}

object LeagueEvent {
  val Tag: AggregateEventTag[LeagueEvent] = AggregateEventTag[LeagueEvent]
}

case class ClubAdded(club: ClubData) extends LeagueEvent

object ClubAdded {
  implicit val format: Format[ClubAdded] = Json.format
}

case class GameAdded(game: GameData) extends LeagueEvent

object GameAdded {
  implicit val format: Format[GameAdded] = Json.format
}

case class ResultRevoked(game: GameData) extends LeagueEvent

object ResultRevoked {
  implicit val format: Format[ResultRevoked] = Json.format
}
