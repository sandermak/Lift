package com.infosupport.snippet

import net.liftweb.http._
import js.jquery.JqWiringSupport
import net.liftweb.util._
import js.JsCmds
import JsCmds._
import Helpers._
import net.liftweb.common.ParseDouble
import xml.NodeSeq

class Wiring {

  val amount1 = ValueCell(2)
  val fxRate  = ValueCell(0.5)
  val amount2 = FuncCell(amount1, fxRate) { (amt, fx) => amt * fx }

  def render =
    "#amount1" #> SHtml.ajaxText(amount1.get.toString,
      (amount: String) => { amount1.set(Helpers.toInt(amount)); Noop }) &
    "#fxRate"  #> SHtml.ajaxText(fxRate.get.toString,
      (rate: String) => { fxRate.set(ParseDouble(rate)); Noop }) &
    "#amount2" #> WiringUI.asText(amount2, JqWiringSupport.fade)


}

class RecipeWiring {

  object Recipes {
    case class RecipeStep(descr: String, grams: Int)
    val steps = ValueCell[List[RecipeStep]](List(RecipeStep("a", 1)))
    val totalSteps = steps.lift(_.size)
    val totalGrams = steps.lift(_.foldLeft(0)((sum,step) => sum + step.grams))
  }

  import Recipes._
  def render = "#addLine [onclick]" #> SHtml.ajaxInvoke(() => { steps.set(RecipeStep("empty", 2) +: steps); Noop }) &
               "#totalGrams" #> WiringUI.asText(totalGrams) &
               "#totalSteps" #> WiringUI.asText(totalSteps)

  def renderSteps = WiringUI.toNode(steps) { (steps, in) =>
    (".step *" #> steps.map(step => ".descr *" #> step.descr &
                                    ".grams *" #> step.grams)).apply(in)
  }

}