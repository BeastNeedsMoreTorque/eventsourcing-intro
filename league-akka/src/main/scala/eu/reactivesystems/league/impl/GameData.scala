package eu.reactivesystems.league.impl

import eu.reactivesystems.league.api.Game
import spray.json.{JsValue, _}

case class GameData(home: ClubData, away: ClubData)(val round: Int,
                                                    val homeGoals: Int,
                                                    val awayGoals: Int)

object GameData {
  def apply(game: Game) =
    new GameData(ClubData(game.home), ClubData(game.away))(game.round,
                                                           game.homeGoals,
                                                           game.awayGoals)

  implicit object GameDataFormat extends RootJsonFormat[GameData] {
    def write(g: GameData) = JsObject(
      "home" -> g.home.toJson,
      "away" -> g.away.toJson,
      "round" -> JsNumber(g.round),
      "homeGoals" -> JsNumber(g.homeGoals),
      "awayGoals" -> JsNumber(g.awayGoals)
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields("home",
                                 "away",
                                 "round",
                                 "homeGoals",
                                 "awayGoals") match {
        case Seq(home @ JsObject(_),
                 away @ JsObject(_),
                 JsNumber(round),
                 JsNumber(homeGoals),
                 JsNumber(awayGoals)) =>
          new GameData(home.convertTo[ClubData], away.convertTo[ClubData])(
            round.toInt,
            homeGoals.toInt,
            awayGoals.toInt)
        case _ => throw new DeserializationException("Color expected")
      }
    }
  }
}
