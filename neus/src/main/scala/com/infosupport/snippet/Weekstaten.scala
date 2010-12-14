package com.infosupport
package snippet

import com.infosupport.model.Weekstaat
import net.liftweb.util._
import net.liftweb.http._
import Helpers._
import net.liftweb.mapper.By
import model.User

class Weekstaten {

  val weeknr = S.param("weeknummer").map(_.toInt) openOr latestWeek

  def overzicht = {

    val weekstaatBox =
      for(user      <- User.currentUser;
          weekstaat <- Weekstaat.find(By(Weekstaat.weekNr, weeknr), By(Weekstaat.user, user)))
        yield weekstaat

    val weekstaat = weekstaatBox openOr Weekstaat.create.weekNr(weeknr)


    ".weekentry *" #>
      weekstaat.regels.map {
        regel => { ".code   "      #> regel.code      &
                   ".maandag"      #> regel.maandag   &
                   ".dinsdag"      #> regel.dinsdag   &
                   ".woensdag"     #> regel.woensdag  &
                   ".donderdag"    #> regel.donderdag &
                   ".vrijdag"      #> regel.vrijdag   &
                   ".regelTotaal"  #> regel.totaal.toString
                 }
      } & ".maandagTot"   #> weekstaat.totaalMaandag   &
          ".dinsdagTot"   #> weekstaat.totaalDinsdag   &
          ".woensdagTot"  #> weekstaat.totaalWoensdag  &
          ".donderdagTot" #> weekstaat.totaalDonderdag &
          ".vrijdagTot"   #> weekstaat.totaalVrijdag

  }

  def week = "#weeknr" #> weeknr

  private def latestWeek = {
    val weekstaten = User.currentUser.open_!.weekstaten
    val weeknummers = weekstaten.map(_.weekNr.is)
    weeknummers.max
  }

}