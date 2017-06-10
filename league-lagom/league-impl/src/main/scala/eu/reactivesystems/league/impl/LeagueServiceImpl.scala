package eu.reactivesystems.league.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import eu.reactivesystems.league.api.{Club, Game, LeagueService}

/**
  * Implementation of the LeagueService.
  */
class LeagueServiceImpl(leagueRegistry: PersistentEntityRegistry)
    extends LeagueService {

  override def addClub(leagueID: String): ServiceCall[Club, Done] =
    ServiceCall { club: Club =>
      val ref = leagueRegistry.refFor[LeagueEntity](leagueID)
      ref.ask(AddClub(ClubData(club)))
    }

  override def addGame(leagueID: String): ServiceCall[Game, Done] =
    ServiceCall { game: Game =>
      val ref = leagueRegistry.refFor[LeagueEntity](leagueID)
      ref.ask(AddGame(GameData(game)))
    }

  override def changeGame(leagueID: String): ServiceCall[Game, Done] =
    ServiceCall { game: Game =>
      val ref = leagueRegistry.refFor[LeagueEntity](leagueID)
      ref.ask(ChangeGame(GameData(game)))
    }

}
