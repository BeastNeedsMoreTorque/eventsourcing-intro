package eu.reactivesystems.league.api

import akka.Done
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives

import scala.concurrent.Future


trait LeagueService extends Directives with SprayJsonSupport {


    val routes = pathPrefix("league") {
      path(Segment / "club") { leagueId =>
        post {
          entity(as[Club]) { club =>
            complete(addClub(leagueId, club))
          }
        }
      } ~
        path(Segment / "game") { leagueId =>
          post {
            entity(as[Game]) { game =>
              complete(addGame(leagueId, game))
            }
          } ~
            put {
              entity(as[Game]) { game =>
                complete(changeGame(leagueId, game))
              }
            }
        }
    }


    def addClub(leagueId: String, club: Club): Future[Done]
    def addGame(leagueId: String, game: Game): Future[Done]
    def changeGame(leagueId: String, game: Game): Future[Done]

}
