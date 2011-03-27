package nl.mak.service

import java.util.Date
import java.text.SimpleDateFormat
import nl.mak.model.Origin._
import nl.mak.model.Origin

object StatementParser {

  def parseStatement(input: String): Seq[ParsedTransaction] = {
    for (line <- input.lines.filter(!_.isEmpty).toList;
         stat <- toParsedTransaction(line.split("\t")))
    yield stat
  }

  def toParsedTransaction(tokens: Array[String]) = {
    if ("null" == tokens(5))
      None
    else {
      val date = new SimpleDateFormat("yyyyMMDD").parse(tokens(2))
      val amount = BigDecimal(tokens(6).replace(',', '.'))
      Some(ParsedTransaction(tokens(0), tokens(1), date, amount, Origin(tokens(7)), tokens(7)))
    }
  }

  case class ParsedTransaction(account: String, currency: String, date: Date, amount: BigDecimal, origin: Origin, descr: String)

}

