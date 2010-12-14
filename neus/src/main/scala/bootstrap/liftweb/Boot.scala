package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, StandardDBVendor}
import _root_.java.sql.{Connection, DriverManager}
import _root_.com.infosupport.model._
import net.liftweb.widgets.autocomplete.AutoComplete


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor =
      new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
        Props.get("db.url") openOr
                "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
        Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // where to search for snippets
    LiftRules.addToPackages("com.infosupport")
    Schemifier.schemify(true, Schemifier.infoF _, User, Weekstaat, WeekstaatRegel)

    // rewrite the weeknumber from a pretty url like /weekstaat/42 to a plain
    // invocation of the /weekstaat view with 42 as weeknummer parameter (see the Map)
    LiftRules.statelessRewrite.append {
      case RewriteRequest(ParsePath(List("weekstaat",weeknummer),_,_,_),_,_) =>
              RewriteResponse("weekstaat" :: Nil, Map("weeknummer" -> weeknummer))  }


    val LoggedIn = If(
          () => User.loggedIn_?,
          () => RedirectWithState(User.loginPageURL, RedirectState(() => S.error("You must be logged in"))))

    // Build the sitemap
    def sitemap() = SiteMap(List(
      Menu("Home") / "index", // Simple menu form
      (Menu("Weekstaat") / "weekstaat" >> LoggedIn)) ++
      // Menu entries for the User management stuff
      User.sitemap: _*)  

    LiftRules.setSiteMapFunc(sitemap)



    // Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
            Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
            Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    S.addAround(DB.buildLoanWrapper)
    clearDb()
    fillDb()
  }

  private def clearDb() {
    DB.runUpdate("DELETE FROM WEEKSTAAT_REGEL", Nil)
    DB.runUpdate("DELETE FROM WEEKSTAAT", Nil)
    DB.runUpdate("DELETE FROM USERS", Nil)
  }

  private def fillDb() {
    for(i <- 1 to 3) {
      val user = User.create.email("lift" + i + "@lift.com").firstName("Test" + i).lastName("User").password("password")
      val regel1 = WeekstaatRegel.create.code("Test" + i).maandag(8).dinsdag(9)
      val regel2 = WeekstaatRegel.create.code("ND").woensdag(8).donderdag(7).vrijdag(8)
      val weekstaat = Weekstaat.create.weekNr(20 + i)
      weekstaat.regels += regel1
      weekstaat.regels += regel2
      user.weekstaten += weekstaat
      user.validated(true).save
    }
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}
