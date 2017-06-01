package eu.reactivesystems.league.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

/**
  * The league service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the LeagueService.
  */
trait LeagueService extends Service {

  override final def descriptor = {
    import Service._
    named("league").withCalls(
      restCall(Method.POST, "/league/:leagueId/club", addClub _),
      restCall(Method.POST, "/league/:leagueId/game", addGame _),
      restCall(Method.PUT, "/league/:leagueId/game", changeGame _)
    ).withAutoAcl(true)
  }

  def addClub(leagueId: String): ServiceCall[Club, Done]

  def addGame(leagueId: String): ServiceCall[Game, Done]

  def changeGame(leagueId: String): ServiceCall[Game, Done]
}
