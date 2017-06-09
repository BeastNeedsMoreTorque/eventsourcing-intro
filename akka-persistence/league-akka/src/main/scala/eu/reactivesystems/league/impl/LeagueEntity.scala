package eu.reactivesystems.league.impl

import akka.persistence.PersistentActor

/**
  * 
  */
class LeagueEntity extends PersistentActor {

  override val persistenceId = s"${context.parent.path.name}-${self.path.name}"

  override def receiveCommand: Receive = ???

  override def receiveRecover: Receive = ???

}
