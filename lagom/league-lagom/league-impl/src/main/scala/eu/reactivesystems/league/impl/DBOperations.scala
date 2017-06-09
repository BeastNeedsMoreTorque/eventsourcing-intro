package eu.reactivesystems.league.impl

import java.sql.Connection
import java.util.UUID

import akka.persistence.query.{Offset, TimeBasedUUID}
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcSession
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcSession.tryWith
import eu.reactivesystems.league.impl.LeagueProjection.readSideId

import scala.concurrent.Future

/**
 *  Everything in here is just helpers to deal with the plain JDBC connection,
 *  not essential to the CQRS part.
 * It's just cruft, really, please ignore.
  */
object DBOperations {


  private[impl] def getOffset(jdbcSession: JdbcSession): Future[Offset] =
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

  private[impl] def addClub(jdbcSession: JdbcSession, offset: Offset, club: ClubData): Future[Boolean] =
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

  private[impl] def updateOffset(connection: Connection, offset: Offset): Boolean = {
    tryWith(connection.prepareStatement(
      "UPDATE read_side_offsets SET time_uuid_offset = ? WHERE read_side_id = ? AND tag = ?")) { statement =>
      statement.setString(1, offset.asInstanceOf[TimeBasedUUID].value.toString)
      statement.setString(2, readSideId)
      statement.setString(3, LeagueEvent.Tag.tag)
      statement.execute()
    }
  }

  private[impl] def addGame(jdbcSession: JdbcSession, offset: Offset, game: GameData): Future[Boolean] =
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


  private def points(goalsA: Int, goalsB: Int): Int = {
    goalsA - goalsB match {
      case x if x == 0 => 1
      case x if x < 0 => 0
      case x if x > 0 => 3
    }
  }

}
