package com.infosupport.snippet

import net.liftweb.http.{S, LiftScreen}

/**
 * Form construction without writing your own snippets.
 * Also see: http://www.assembla.com/wiki/show/liftweb/Lift's_Screen
 */

object FavColorScreen extends LiftScreen {

  val color  = field("Favourite color?", "",
                      valMinLen(2,  "Too short"),
                      valMaxLen(12, "Too long!"))

  val hue    = radio("Hue", "", List("dark", "light"))

  val really = field("Really?", false)

  
  def finish() {
    // Therefore we must extract the real value using the is method
    if(really.is)
      S.notice("I like " + hue.is + " " + color.is + " too!")
    else
      S.warning("Please tell the truth next time...")

    // Notice will be displayed on home screen
    S.redirectTo("/")
  }
}