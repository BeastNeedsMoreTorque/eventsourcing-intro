package eu.reactivesystems.league.infra

import java.nio.charset.StandardCharsets

import akka.serialization.Serializer
import eu.reactivesystems.league.impl.LeagueEntity._
import eu.reactivesystems.league.impl.{
  ClubRegistered,
  GamePlayed,
  ResultRevoked
}
import spray.json._

/**
  * league-akka
  */
class JsonSerializer extends Serializer {
  override def identifier: Int = JsonSerializer.identifier
  private val charset = StandardCharsets.UTF_8

  /*
  TODO do this differently...
   */
  override def toBinary(o: AnyRef): Array[Byte] =
    o match {
      case msg @ ClubRegistered(_) => msg.toJson.compactPrint.getBytes(charset)
      case msg @ GamePlayed(_) => msg.toJson.compactPrint.getBytes(charset)
      case msg @ ResultRevoked(_) => msg.toJson.compactPrint.getBytes(charset)
      case msg @ AddClub(_) => msg.toJson.compactPrint.getBytes(charset)
      case msg @ AddGame(_) => msg.toJson.compactPrint.getBytes(charset)
      case msg @ ChangeGame(_) => msg.toJson.compactPrint.getBytes(charset)
      case msg @ InvalidCommand(_) => msg.toJson.compactPrint.getBytes(charset)
      case msg @ LeagueState(_, _) => msg.toJson.compactPrint.getBytes(charset)
      case x =>
        throw new RuntimeException(
          s"Missing json serializer for ${x.getClass.getName}")
    }

  override def includeManifest: Boolean = false

  override def fromBinary(bytes: Array[Byte],
                          manifest: Option[Class[_]]): AnyRef = ???
}

object JsonSerializer {
  val identifier = 101;
}
