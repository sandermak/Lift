package com.infosupport.snippet

import net.liftweb.http.S
import net.liftweb.widgets.autocomplete.AutoComplete
import xml.NodeSeq

/**
 * Creating an autocomplete field using the AutoComplete widget
 */
object AutoCompleteSnippet {
  val data = List("Green", "Red", "Yellow", "Blue", "Brown", "Pink", "Purple")

  def completeData(current: String, limit: Int) =
    data.filter(_.toLowerCase.startsWith(current.toLowerCase))

  def input(in: NodeSeq) =
    AutoComplete("", completeData, v => S.notice("Submitted " + v))

}