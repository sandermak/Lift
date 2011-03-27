package nl.mak.service

import nl.mak.service.StatementParser.ParsedTransaction
import nl.mak.model.Transaction

object TxService {

  def minMaxAmount(txs : Seq[Transaction]) = {
    val amounts = txs.map(_.amount.is)
    (amounts.min, amounts.max)
  }

  def inOutTotals(txs: Seq[Transaction]) = {
    val amounts = txs.map(_.amount.is)
    val parts = amounts.partition(_ > 0)
    (parts._1.sum, parts._2.sum)
  }

}