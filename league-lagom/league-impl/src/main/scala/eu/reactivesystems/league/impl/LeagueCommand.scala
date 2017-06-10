package eu.reactivesystems.league.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}

sealed trait LeagueCommand[R] extends ReplyType[R]

case class AddClub(club: ClubData) extends LeagueCommand[Done]

object AddClub {
  implicit val format: Format[AddClub] = Json.format
}

case class AddGame(game: GameData) extends LeagueCommand[Done]

object AddGame {
  implicit val format: Format[AddGame] = Json.format
}

case class ChangeGame(game: GameData) extends LeagueCommand[Done]

object ChangeGame {
  implicit val format: Format[ChangeGame] = Json.format
}
