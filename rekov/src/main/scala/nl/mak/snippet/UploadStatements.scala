package nl.mak
package snippet

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import Helpers._
import service.StatementParser._
import net.liftweb.http._

class UploadStatements {

  private object theUpload extends RequestVar[Box[FileParamHolder]](Empty)

  private object uploadedStatements extends RequestVar[Seq[Statement]](Nil)

  def upload = "#upload_button" #> SHtml.fileUpload(file => {
    theUpload(Full(file));
    processUpload
  })

  def uploadResult = ".statement *" #> uploadedStatements.map(stat =>
    ".account *" #> stat.account & ".amount *" #> stat.amount.toString & ".date *" #> stat.date.toString
  )

  def processUpload = {
    val statements = theUpload.is.map(file => parseStatements(new String(file.file))).openOr(Nil)
    S.redirectTo("uploadResult.html", () => uploadedStatements(statements))
  }

}
