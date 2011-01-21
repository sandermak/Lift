package nl.mak.service

import java.util.Date
import java.text.SimpleDateFormat

object StatementParser {

  def parseStatements(input: String): Seq[Statement] = {
    for (line <- input.lines.filter(!_.isEmpty).toList;
         stat <- toStatement(line.split("\t")))
    yield stat
  }

  def toStatement(tokens: Array[String]) = {
    if ("null" == tokens(5))
      None
    else {
      val date = new SimpleDateFormat("yyyyMMDD").parse(tokens(2))
      val amount = BigDecimal(tokens(6).replace(',', '.'))
      Some(Statement(tokens(0), tokens(1), date, amount, Origin(tokens(7)), tokens(7)))
    }
  }

  object Origin extends Enumeration {
    type Origin = Value

    val GEA = Value(1, "Geldautomaat")
    val BEA = Value(2, "Betaalautomaat")
    val GIRO = Value(3, "Girobetaling")
    val OTHER = Value(4, "Onbekend")

    def apply(input: String) = input.substring(0, 4) match {
      case "GEA" => GEA
      case "BEA" => BEA
      case "GIRO" => GIRO
      case _ => OTHER
    }
  }

  import Origin._

  case class Statement(account: String, currency: String, date: Date, amount: BigDecimal, origin: Origin, descr: String)

}

