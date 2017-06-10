package eu.reactivesystems.league.impl

import eu.reactivesystems.league.api.Game

case class GameData(home: ClubData, away: ClubData)(val round: Int,
                                                    val homeGoals: Int,
                                                    val awayGoals: Int)

object GameData {
  def apply(game: Game) =
    new GameData(ClubData(game.home), ClubData(game.away))(game.round,
                                                           game.homeGoals,
                                                           game.awayGoals)
}
