package com.infosupport.snippet

import xml.NodeSeq
import net.liftweb.http.S
import net.liftweb.util._
import net.liftweb.widgets.autocomplete.AutoComplete
import Helpers._ 

/**
 * Creating an autocomplete field using the AutoComplete widget
 */
object AutoCompleteSnippet {
  val data = List("Green", "Red", "Yellow", "Blue", "Brown", "Pink", "Purple")

  def queryData(current: String, limit: Int) =
    data.filter(_.toLowerCase.startsWith(current.toLowerCase))

  def input = {
    "#autofield" #> AutoComplete("", queryData, v => S.notice("Submitted " + v))
  }

}