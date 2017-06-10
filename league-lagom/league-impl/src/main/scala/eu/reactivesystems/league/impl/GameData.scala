package eu.reactivesystems.league.impl

import eu.reactivesystems.league.api.Game
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class GameData(home: ClubData, away: ClubData)(val round: Int,
                                                    val homeGoals: Int,
                                                    val awayGoals: Int)

object GameData {
  def apply(game: Game) =
    new GameData(ClubData(game.home), ClubData(game.away))(game.round,
                                                           game.homeGoals,
                                                           game.awayGoals)

  implicit val gameDataWrites = new Writes[GameData] {
    def writes(gameData: GameData) = Json.obj(
      "home" -> gameData.home,
      "away" -> gameData.away,
      "round" -> gameData.round,
      "homeGoals" -> gameData.homeGoals,
      "awayGoals" -> gameData.awayGoals
    )
  }

  implicit val gameDataReads: Reads[GameData] =
    ((JsPath \ "home").read[ClubData] and
      (JsPath \ "away").read[ClubData] and
      (JsPath \ "round").read[Int] and
      (JsPath \ "homeGoals").read[Int] and
      (JsPath \ "awayGoals").read[Int])(
      (home: ClubData,
       away: ClubData,
       round: Int,
       homeGoals: Int,
       awayGoals: Int) =>
        new GameData(home, away)(round, homeGoals, awayGoals))
}
