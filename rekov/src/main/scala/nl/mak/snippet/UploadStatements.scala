package nl.mak
package snippet

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import Helpers._
import service.StatementParser._
import net.liftweb.http._
import nl.mak.model.Transaction._
import java.sql.SQLException

class UploadStatements {

  private object theUpload extends RequestVar[Box[FileParamHolder]](Empty)

  private object uploadedStatements extends RequestVar[Seq[ParsedTransaction]](Nil)

  def upload = "#upload_button" #> SHtml.fileUpload(file => {
    theUpload(Full(file));
    processUpload
  })

  def uploadResult = ".statement *" #> uploadedStatements.map(stat =>
    ".account *" #> stat.account & ".amount *" #> stat.amount.toString & ".date *" #> stat.date.toString
  )

  def processUpload = {
    val transactions = theUpload.is.map(file => parseStatement(new String(file.file))).openOr(Nil)
    var skipped = 0
    var saved = 0

    transactions.map { tx =>
      tryo(List(classOf[SQLException]), Full((_:Throwable) => skipped += 1))  {
        tx.save
        saved += 1
      }
    }
    S.redirectTo("uploadResult.html", () => S.notice("Saved " + saved + " transactions, skipped " + skipped + " duplicates"))
  }

}
