package com.infosupport
package snippet

import scala.xml.NodeSeq
import net.liftweb.util._
import net.liftweb.http._
import java.util.Date
import Helpers._

class IndexSnippet {

  val currentDate = new Date().toString

  def date(in: NodeSeq): NodeSeq =
    Helpers.bind("b", in, "date" -> currentDate)

  def dateCSS: NodeSeq => NodeSeq = "#date" #> currentDate

  // Some arbitrary data to fill our table
  val persons = List(("Martin", "Odersky"), ("David", "Pollak"))

  // Filling a dynamic table using bind() calls
  def tableContents(in: NodeSeq): NodeSeq = {
    persons.flatMap(naam =>
      bind("t", in, "firstName" -> naam._1,
                    "lastName" -> naam._2));
  }

  // Filling a dynamic table using the new CSS binding facilities. No more
  // Lift tags necessary in the view!
  def tableContentsCSS() =
    ".nameLine *" #> persons.map(name => ".firstName" #> name._1 &
                                         ".lastName"  #> name._2)

}


class NameSnippet {

  def sayHi = {
    var name = ""
    def processForm() {
      if (name.length > 6)
        S.error("Get a real nickname, this is too long!")
      else
        S.notice("Nickname is " + name)
    }

    ".message"   #> "Choose a nickname:"       &
    "#nameInput" #> SHtml.text(name, name = _) &
    "#button"    #> SHtml.onSubmitUnit(processForm)
  }

}



