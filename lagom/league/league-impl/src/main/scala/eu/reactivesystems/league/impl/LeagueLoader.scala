package eu.reactivesystems.league.impl

import akka.actor.PoisonPill
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraPersistenceComponents, WriteSideCassandraPersistenceComponents}
import com.lightbend.lagom.scaladsl.persistence.jdbc.{JdbcPersistenceComponents, ReadSideJdbcPersistenceComponents}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import com.softwaremill.macwire.akkasupport._
import eu.reactivesystems.league.api.LeagueService
import play.api.db.HikariCPComponents
import play.api.libs.ws.ahc.AhcWSComponents

import scala.collection.immutable.Seq

class LeagueLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LeagueApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LeagueApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[LeagueService]
  )
}

abstract class LeagueApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with WriteSideCassandraPersistenceComponents
    with ReadSideJdbcPersistenceComponents
    with HikariCPComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[LeagueService](wire[LeagueServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = LeagueSerializerRegistry

  // Register the league persistent entity
  persistentEntityRegistry.register(wire[LeagueEntity])

  // Register read side processor
  val leagueProjectionProps = wireProps[LeagueProjection]

  actorSystem.actorOf(
    ClusterSingletonManager.props(
      singletonProps = leagueProjectionProps,
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(actorSystem)),
    name = "leagueProjection")

}

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized. While it's
  * possible to use any serializer you want for Akka messages, out of the box
  * Lagom provides support for JSON, via this registry abstraction.
  *
  * The serializers are registered here, and then provided to Lagom in the
  * application loader.
  */
object LeagueSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[AddClub],
    JsonSerializer[AddGame],
    JsonSerializer[ChangeGame],
    JsonSerializer[ClubAdded],
    JsonSerializer[GameAdded],
    JsonSerializer[ResultRevoked],
    JsonSerializer[LeagueState]
  )
}
