package eu.reactivesystems.league.impl

import akka.actor.{Actor, ActorLogging, Props, Status}
import akka.pattern.pipe
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.{EventEnvelope2, PersistenceQuery}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcSession

class LeagueProjection(jdbcSession: JdbcSession) extends Actor with ActorLogging {

  import DBOperations._

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
    ()
  }

  private def projectEvent(event: EventEnvelope2) =
    event.event match {
      case ClubAdded(club) => addClub(jdbcSession, event.offset, club)
      case GameAdded(game) => addGame(jdbcSession, event.offset, game)
    }
}


object LeagueProjection {
  val readSideId = "leagueProjection"

  def props(jdbcSession: JdbcSession) = Props(new LeagueProjection(jdbcSession))
}


