package eu.reactivesystems.league.infra

import akka.persistence.journal.{Tagged, WriteEventAdapter}
import eu.reactivesystems.league.impl.LeagueEvent

class EventTagger extends WriteEventAdapter {

  override def manifest(event: Any): String = ""

  override def toJournal(event: Any): Any = event match {
    case e: LeagueEvent => Tagged(e, e.tags)
    case _ => event
  }
}
