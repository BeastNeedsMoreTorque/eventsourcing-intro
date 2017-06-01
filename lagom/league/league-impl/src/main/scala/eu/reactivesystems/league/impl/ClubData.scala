package eu.reactivesystems.league.impl

import eu.reactivesystems.league.api.Club
import play.api.libs.json.{Format, Json}

case class ClubData(name: String)

object ClubData {
  def apply(club: Club) = new ClubData(club.name)

  implicit val format: Format[ClubData] = Json.format[ClubData]
}

