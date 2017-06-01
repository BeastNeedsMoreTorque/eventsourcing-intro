package eu.reactivesystems.league.impl

import java.sql.Connection
import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props, Status}
import akka.pattern.pipe
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.{EventEnvelope2, Offset, PersistenceQuery, TimeBasedUUID}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcSession
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcSession.tryWith

import scala.concurrent.Future

class LeagueProjection(jdbcSession: JdbcSession) extends Actor with ActorLogging {

  import LeagueProjection._

  override def receive: Receive = {
    case Status.Failure(ex) =>
      log.error(ex, "read side generation terminated")
      context.stop(self)
  }


  override def preStart(): Unit = {
    val system = context.system
    val materializer = ActorMaterializer.create(system)
    val readJournal = PersistenceQuery.get(system).readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)
    implicit val ec = context.dispatcher

    val result = getOffset(jdbcSession)
      .flatMap(offset =>
        readJournal
          .eventsByTag(LeagueEvent.Tag.tag, offset)
          .mapAsync(1)(e => projectEvent(e))
          .runWith(Sink.ignore)(materializer))

    result pipeTo self
  }

  private def projectEvent(event: EventEnvelope2) =
    event.event match {
      case ClubAdded(club) => addClub(jdbcSession, event.offset, club)
      case GameAdded(game) => addGame(jdbcSession, event.offset, game)
    }

  private def points(goalsA: Int, goalsB: Int): Int = {
    goalsA - goalsB match {
      case x if x == 0 => 1
      case x if x < 0 => 0
      case x if x > 0 => 3
    }
  }

  /*
  Everything below here is just helpers to deal with the plain JDBC connection,
  not essential to the CQRS part.
  It's just cruft, really, please ignore.
   */
  private def getOffset(jdbcSession: JdbcSession): Future[Offset] =
    jdbcSession.withConnection(
      connection =>
        tryWith(connection.prepareStatement("SELECT time_uuid_offset FROM read_side_offsets WHERE read_side_id = ? AND tag = ?")) {
          statement => {
            statement.setString(1, readSideId)
            statement.setString(2, LeagueEvent.Tag.tag)
            val rs = statement.executeQuery()
            if (rs.first())
              Option(rs.getString(1)) match {
                case Some(uuid) => TimeBasedUUID(UUID.fromString(uuid))
                case None => Offset.noOffset
              }
            else Offset.noOffset
          }
        }
    )

  private def addClub(jdbcSession: JdbcSession, offset: Offset, club: ClubData) =
    jdbcSession.withTransaction(
      connection => {
        tryWith(connection.prepareStatement(
          "INSERT INTO league (team, gamesPlayed, points) VALUES (?, ?, ?)")) { statement =>
          statement.setString(1, club.name)
          statement.setInt(2, 0)
          statement.setInt(3, 0)
          statement.execute()
        }
        updateOffset(connection, offset)
      }
    )

  private def updateOffset(connection: Connection, offset: Offset): Unit = {
    tryWith(connection.prepareStatement(
      "UPDATE read_side_offsets SET time_uuid_offset = ? WHERE read_side_id = ? AND tag = ?")) { statement =>
      statement.setString(1, offset.asInstanceOf[TimeBasedUUID].value.toString)
      statement.setString(2, readSideId)
      statement.setString(3, LeagueEvent.Tag.tag)
      statement.execute()
    }
  }

  private def addGame(jdbcSession: JdbcSession, offset: Offset, game: GameData) =
    jdbcSession.withTransaction(
      connection => {
        tryWith(connection.prepareStatement(
          "UPDATE league set gamesPlayed = gamesPlayed + 1, points = points + ? WHERE team = ?")) { statement =>
          statement.setInt(1, points(game.homeGoals, game.awayGoals))
          statement.setString(2, game.home.name)
          statement.execute()
        }
        tryWith(connection.prepareStatement(
          "UPDATE league set gamesPlayed = gamesPlayed + 1, points = points + ? WHERE team = ?")) { statement =>
          statement.setInt(1, points(game.awayGoals, game.homeGoals))
          statement.setString(2, game.away.name)
          statement.execute()
        }
        updateOffset(connection, offset)
      })

}


object LeagueProjection {
  val readSideId = "leagueProjection"

  def props(jdbcSession: JdbcSession) = Props(new LeagueProjection(jdbcSession))
}


