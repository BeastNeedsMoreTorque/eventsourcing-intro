package eu.reactivesystems.league.infra

import eu.reactivesystems.league.impl.{
  ClubRegistered,
  GamePlayed,
  ResultRevoked
}
import eu.reactivesystems.league.impl.LeagueEntity._
import spray.json.JsonFormat

import scala.reflect.ClassTag

/**
  * TODO..
  */
object JsonRegistry {
  val mappings: Map[String, JsonFormat[AnyRef]] =
    Map(
      Entry[AddGame],
      Entry[AddClub],
      Entry[ChangeGame],
      Entry[InvalidCommand],
      Entry[LeagueState],
      Entry[ClubRegistered],
      Entry[GamePlayed],
      Entry[ResultRevoked]
    )

  object Entry {
    def apply[A: ClassTag: JsonFormat](): (String, JsonFormat[AnyRef]) =
      (implicitly[ClassTag[A]].runtimeClass
         .asInstanceOf[Class[A]]
         .getName,
       implicitly[JsonFormat[A]].asInstanceOf[JsonFormat[AnyRef]])

  }
}
