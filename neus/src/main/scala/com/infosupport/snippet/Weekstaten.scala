package com.infosupport.snippet

import xml.NodeSeq
import com.infosupport.model.Weekstaat
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import Helpers._
import net.liftweb.mapper.By

/**
 * Created by IntelliJ IDEA.
 * User: sbmak
 * Date: Oct 28, 2010
 * Time: 10:20:52 PM
 * To change this template use File | Settings | File Templates.
 */

class Weekstaten {

  val weeknr = S.param("weeknummer")

  def overzicht = {

    val weekstaat =
      for(weeknr    <- weeknr;
          weekstaat <- Weekstaat.find(By(Weekstaat.weekNr, weeknr.toInt)))
        yield weekstaat
      


    ".weekentry *" #>
      weekstaat.open_!.regels.map {
        regel => { ".code   "      #> regel.code      &
                   ".maandag"      #> regel.maandag   &
                   ".dinsdag"      #> regel.dinsdag   &
                   ".woensdag"     #> regel.woensdag  &
                   ".donderdag"    #> regel.donderdag &
                   ".vrijdag"      #> regel.vrijdag
                 }
      }
  }

  def week = "#weeknr" #> weeknr
}