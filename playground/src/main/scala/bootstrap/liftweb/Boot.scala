package bootstrap.liftweb

import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.http.provider._
import net.liftweb.sitemap._
import net.liftweb.widgets.autocomplete.AutoComplete


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    // where to search for snippets
    LiftRules.addToPackages("com.infosupport")

    // Build SiteMap. More info on sitemap: http://www.assembla.com/wiki/show/liftweb/SiteMap
    def sitemap() = SiteMap(
      Menu("Home") / "index",
      Menu("Autocomplete") / "autocomplete",
      Menu("Screen") / "screen",
      Menu("Chat") / "chat")

    LiftRules.setSiteMapFunc(sitemap)

    // Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
            Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
            Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

    // Initialize a widget we want to use in our app.
    AutoComplete.init
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}
