package eu.reactivesystems.league.impl

import eu.reactivesystems.league.api.Club

case class ClubData(name: String)

object ClubData {

  def apply(club: Club) = new ClubData(club.name)

}
