package eu.reactivesystems.league.impl

import akka.Done
import akka.actor.Props
import akka.persistence.{PersistentActor, SnapshotOffer}

import scala.collection.immutable.Seq

/**
  *
  */
class LeagueEntity extends PersistentActor {
  import LeagueEntity._

  val snapShotInterval = 100
  var state = LeagueState(Set.empty[ClubData], Set.empty[GameData])

  override def persistenceId: String = "LeagueEntity-" + self.path.name

  override def receiveCommand: Receive = {
    case AddClub(club: ClubData) =>
      validateAddClub(club)
        .fold(
          message => sender() ! InvalidCommand(message),
          event => {
            persist(event)(e => {
              updateStateFromEvent(e)
              saveSnapshotIfNecessary()
            })
            sender() ! Done
          }
        )
    case AddGame(game: GameData) =>
      validateAddGame(game)
        .fold(
          message => sender() ! InvalidCommand(message),
          events => {
            persistAll(events)(e => {
              updateStateFromEvent(e)
              saveSnapshotIfNecessary()
            })
            sender() ! Done
          }
        )
    case ChangeGame(game: GameData) =>
      validateChangeGame(game)
        .fold(
          message => sender() ! InvalidCommand(message),
          events => {
            persistAll(events)(e => {
              updateStateFromEvent(e)
              saveSnapshotIfNecessary()
            })
            sender() ! Done
          }
        )
  }

  override def receiveRecover: Receive =
    updateStateFromSnapshot orElse updateStateFromEvent

  private def updateStateFromSnapshot: Receive = {
    case SnapshotOffer(_, snapshot: LeagueState) => state = snapshot
  }

  private def updateStateFromEvent: Receive = {
    case ClubRegistered(club) =>
      state = state.copy(clubs = state.clubs + club)
    case GamePlayed(game) => state = state.copy(games = state.games + game)
    case ResultRevoked(game) =>
      state = state.copy(games = state.games - game)
  }

  private def validateAddClub(club: ClubData): Either[String, LeagueEvent] =
    if (state.clubs(club)) Left(s"Duplicate club $club")
    else if (state.clubs.size == LEAGUE_MAX)
      Left(s"Max league size $LEAGUE_MAX exceeded")
    else Right(ClubRegistered(club))

  private def validateAddGame(
      game: GameData): Either[String, Seq[LeagueEvent]] =
    if (state.games(game)) Left(s"Duplicate game $game")
    else {
      val newClubs = Set(game.home, game.away) diff state.clubs
      if ((state.clubs.size + newClubs.size) > LEAGUE_MAX)
        Left(s"Max league size $LEAGUE_MAX exceeded")
      else {
        Right(newClubs.map(ClubRegistered(_)).toVector :+ GamePlayed(game))
      }
    }
  private def validateChangeGame(
      game: GameData): Either[String, Seq[LeagueEvent]] =
    state.games
      .find(_ == game)
      .fold[Either[String, Seq[LeagueEvent]]](Left(s"Game $game not found."))(
        oldGame => Right(Seq(ResultRevoked(oldGame), GamePlayed(game))))

  private def saveSnapshotIfNecessary() =
    if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0)
      saveSnapshot(state)

}

object LeagueEntity {
  import spray.json.DefaultJsonProtocol._
  val LEAGUE_MAX = 18

  def props: Props = Props[LeagueEntity]

  // Commands
  sealed trait LeagueCommand
  case class AddClub(club: ClubData) extends LeagueCommand
  object AddClub {
    implicit val format = jsonFormat1(AddClub.apply)

  }
  case class AddGame(game: GameData) extends LeagueCommand
  object AddGame {
    implicit val format = jsonFormat1(AddGame.apply)

  }
  case class ChangeGame(game: GameData) extends LeagueCommand
  object ChangeGame {
    implicit val format = jsonFormat1(ChangeGame.apply)
  }

  // Response can either be "Done" or an error, wrapped in this message
  case class InvalidCommand(message: String)
  object InvalidCommand {
    implicit val format = jsonFormat1(InvalidCommand.apply)
  }

  /**
    * The current state held by the persistent entity.
    */
  case class LeagueState(clubs: Set[ClubData], games: Set[GameData])
  object LeagueState {
    implicit val format = jsonFormat2(LeagueState.apply)
  }
}
