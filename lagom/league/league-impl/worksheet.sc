import eu.reactivesystems.league.api.Club
import play.api.libs.json.{Json, __}
import eu.reactivesystems.league.impl.{ClubData, GameData}

val x = new GameData(ClubData("SVW"),ClubData("BVB"))(1,1,0)

Json.toJson(x)


