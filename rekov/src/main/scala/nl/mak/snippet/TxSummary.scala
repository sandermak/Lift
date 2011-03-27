package nl.mak
package snippet

import _root_.net.liftweb.util._
import Helpers._
import service.TxService
import nl.mak.model.Transaction

class TxSummary {

  def render = {
    val txs = Transaction.findAll
    val (min, max) = TxService.minMaxAmount(txs)
    val (in, out) = TxService.inOutTotals(txs)

    "#minAmount *" #> min.toString & "#maxAmount *" #> max.toString & "#totalIn *" #> in.toString & "#totalOut *" #> out.toString
  }
}