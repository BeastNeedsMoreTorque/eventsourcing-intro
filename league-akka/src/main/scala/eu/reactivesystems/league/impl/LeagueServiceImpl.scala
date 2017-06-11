package eu.reactivesystems.league.impl

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import eu.reactivesystems.league.api.{Club, Game, LeagueService}

import scala.concurrent.Future
import scala.io.StdIn

object LeagueServiceImpl extends LeagueService {

  implicit val system = ActorSystem("league-actorsystem")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  override def addClub(leagueId: String, club: Club): Future[Done] =
    // get sharded instance
    // send message using ask
    // process result
    Future.successful(Done)

  override def addGame(leagueId: String, game: Game): Future[Done] =
    Future.successful(Done)

  override def changeGame(leagueId: String, game: Game): Future[Done] =
    Future.successful(Done)

  def main(args: Array[String]): Unit = {
    val port =
      system.settings.config.getInt("eu.reactivesystems.league.http.port")
    val bindingFuture = Http().bindAndHandle(routes, "localhost", port)

    println(
      s"Server online at http://localhost:$port/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}
