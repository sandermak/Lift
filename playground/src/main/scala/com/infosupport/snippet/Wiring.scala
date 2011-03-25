package com.infosupport.snippet

import net.liftweb.http._
import net.liftweb.util._
import js.jquery.JqWiringSupport
import js.JsCmds
import JsCmds._
import Helpers._
import net.liftweb.common.ParseDouble

class Wiring {

  val amount1 = ValueCell(2)
  val fxRate  = ValueCell(0.5)
  val amount2 =
    FuncCell(amount1, fxRate) { (amt, fx) => amt * fx }

  def render =
    "#amount1" #> SHtml.ajaxText(amount1.get.toString,
      amount => { amount1.set(Helpers.toInt(amount)); Noop }) &
    "#fxRate"  #> SHtml.ajaxText(fxRate.get.toString,
      rate   => { fxRate.set(ParseDouble(rate)); Noop })      &
    "#amount2" #> WiringUI.asText(amount2, JqWiringSupport.fade)

}

class RecipeWiring {

  object Recipes {
    case class RecipeStep(descr: String, grams: Int)

    val steps      = ValueCell[List[RecipeStep]](List(RecipeStep("Sugar", 100)))
    val totalSteps = steps.lift(_.size)
    val totalGrams = steps.lift(_.foldLeft(0)((sum,step) => sum + step.grams))
    val largest    = steps.lift(_.max(Ordering.by((r:RecipeStep) => r.grams)))

    def randomStep = RecipeStep(randomString(10), randomInt(1000))
    def addStep(step: RecipeStep) = steps.set(step :: steps)
  }

  import Recipes._
  def render = handleInputs &
               "#randomLine [onclick]" #> SHtml.ajaxInvoke(() => { addStep(randomStep); Noop }) &
               "#totalGrams" #> WiringUI.asText(totalGrams) &
               "#totalSteps" #> WiringUI.asText(totalSteps) &
               "#largest"    #> WiringUI.asText(largest)

  def handleInputs = {
    var descr = ""
    var grams = 0
    "#descrInput" #> SHtml.onSubmit(s =>   descr = s) &
    "#gramsInput" #> SHtml.onSubmit(s => { grams = Helpers.toInt(s)
                                           addStep(RecipeStep(descr, grams))
                                           SetValById("descrInput", "") &
                                           SetValById("gramsInput", "")
                                         })
  }

  def renderSteps = WiringUI.toNode(steps) { (steps, in) =>
    (".step" #> steps.map(step => ".descr *" #> step.descr &
                                  ".grams *" #> step.grams)).apply(in)
  }

}