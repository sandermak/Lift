package com.infosupport
package snippet

import scala.xml.{NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import js.JsCmds
import JsCmds._
import java.util.Date
import com.infosupport.lib._
import Helpers._

class WelcomeSnippet {
  // As a DI example, inject the Date. Possibility 1, use injection based on type:
  lazy val date1: Box[Date] = DependencyFactory.inject[Date]

  // Possibility 2: use the explicit time factory we defined (advantage: no Box around value,
  // since we know that the dependency can be created for sure):
  lazy val date2: Date = DependencyFactory.time.vend
  // Explanation of Lift's DI: http://www.assembla.com/wiki/show/liftweb/Dependency_Injection


  def date(in: NodeSeq): NodeSeq =
    Helpers.bind("b", in, "time" -> date2.toString)


  def tableContents(in: NodeSeq): NodeSeq = {
    val persons = List(("voornaam 1", "achternaam 1"), ("voornaam2", "achternaam2"))

    // The following shows how we can repeatedly call bind to create new nodes.
    // Here, flatMap maps the function and concatenates the results into a single NodeSeq
    persons.flatMap(naam =>
      bind("t", in, "naam" -> naam._1, "achternaam" -> naam._2));
  }

}


// A StatefulSnippet is retained until unregister..() is called
// Therefore, we can use the name variable to keep the state
class NameSnippet extends StatefulSnippet {
  // Manual dispatching for stateful snippets:
  val dispatch: DispatchIt = {case "sayHi" => sayHi}

  var name = ""

  def sayHi(xhtml: NodeSeq): NodeSeq = {
    // Function called when Click is clicked
    def processForm() {
      if (name.length > 10) {
        S.error("Get a real nickname, this is too long!")
      } else {S.notice("Nickname is " + name); unregisterThisSnippet()}
    }

    SHtml.ajaxForm(
      bind("t", xhtml,
        "message" -> Text("Hi, choose a nickname:"),
        "name"    -> SHtml.text(name, name = _),
        "button"  -> SHtml.ajaxSubmit("Send", () => {processForm; Noop})))
  }

}



