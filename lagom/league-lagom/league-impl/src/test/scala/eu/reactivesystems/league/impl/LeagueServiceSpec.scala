package eu.reactivesystems.league.impl

import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import eu.reactivesystems.league.api._
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

class LeagueServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra(true)
  ) { ctx =>
    new LeagueApplication(ctx) with LocalServiceLocator
  }

  val client = server.serviceClient.implement[LeagueService]

  override protected def afterAll() = server.stop()

  "league service" should {

//    "say hello" in {
//      client.hello("Alice").invoke().map { answer =>
//        answer should ===("Hello, Alice!")
//      }
//    }
//
//    "allow responding with a custom message" in {
//      for {
//        _ <- client.useGreeting("Bob").invoke(GreetingMessage("Hi"))
//        answer <- client.hello("Bob").invoke()
//      } yield {
//        answer should ===("Hi, Bob!")
//      }
//    }
  }
}
