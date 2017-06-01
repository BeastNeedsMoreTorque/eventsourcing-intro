package eu.reactivesystems.league.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

import scala.collection.immutable.Seq

class LeagueEntity extends PersistentEntity {

  import LeagueEntity._

  override type Command = LeagueCommand[_]
  override type Event = LeagueEvent
  override type State = LeagueState

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  override def initialState: LeagueState = LeagueState(Set.empty[ClubData], Set.empty[GameData])

  override def behavior: Behavior = {
    case LeagueState(clubs, games) =>
      Actions().onCommand[AddClub, Done] {
        case (AddClub(club), ctx, state) =>
          validateAddClub(club, state).fold(message => {
            ctx.invalidCommand(message)
            ctx.done
          },
            event =>
              ctx.thenPersist(event) { _ =>
                ctx.reply(Done)
              })
      }.onCommand[AddGame, Done] {
        case (AddGame(game), ctx, state) =>
          validateAddGame(game, state).fold(message => {
            ctx.invalidCommand(message)
            ctx.done
          },
            events =>
              ctx.thenPersistAll(events: _*) { () =>
                ctx.reply(Done)
              })
      }.onCommand[ChangeGame, Done] {
        case (ChangeGame(game), ctx, state) =>
          validateChangeGame(game, state).fold(message => {
            ctx.invalidCommand(message)
            ctx.done
          },
            events =>
              ctx.thenPersistAll(events: _*) { () =>
                ctx.reply(Done)
              })
      }.onEvent {
        case (ClubAdded(club), state) => state.copy(clubs = state.clubs + club)
        case (GameAdded(game), state) => state.copy(games = state.games + game)
        case (ResultRevoked(game), state) => state.copy(games = state.games + game)
      }
  }

  def validateAddClub(club: ClubData, state: LeagueState): Either[String, LeagueEvent] =
    if (state.clubs(club)) Left(s"Duplicate club $club")
    else if (state.clubs.size == LEAGUE_MAX) Left(s"Max league size $LEAGUE_MAX exceeded")
    else Right(ClubAdded(club))

  def validateAddGame(game: GameData, state: LeagueState): Either[String, Seq[LeagueEvent]] =
    if (state.games(game)) Left(s"Duplicate game $game")
    else {
      val newClubs = Set(game.home, game.away) &~ state.clubs
      if ((state.clubs.size + newClubs.size) > LEAGUE_MAX) Left(s"Max league size $LEAGUE_MAX exceeded")
      else {
        Right(newClubs.map(ClubAdded(_)).toVector :+ GameAdded(game))
      }
    }

  def validateChangeGame(game: GameData, state: LeagueState): Either[String, Seq[LeagueEvent]] =
    state.games.find(_ == game)
      .fold[Either[String, Seq[LeagueEvent]]](Left(s"Game $game not found.")
    )(oldGame => Right(Seq(ResultRevoked(oldGame), GameAdded(game))))
}

object LeagueEntity {
  val LEAGUE_MAX = 18
}





