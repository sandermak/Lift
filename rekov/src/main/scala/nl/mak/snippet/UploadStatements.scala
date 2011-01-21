package nl.mak
package snippet

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import Helpers._
import service.StatementParser._
import net.liftweb.http._

class UploadStatements {

  private object theUpload extends RequestVar[Box[FileParamHolder]](Empty)

  private object uploadedStatements extends SessionVar[Box[Seq[Statement]]](Empty)

  def upload = "#upload_button" #> SHtml.fileUpload(file => {
    theUpload(Full(file)); processUpload
  })

  def uploadResult = ".statement *" #> uploadedStatements.map( stat =>
     ".account *" #> stat.account & ".amount *" #> stat.amount.toString & ".date *" #> stat.date.toString
  )

  def processUpload = {
     uploadedStatements(theUpload.is.map(file => parseStatements(new String(file.file))))
     S.redirectTo("uploadResult.html")
  }

}
