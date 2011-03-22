package com.infosupport.snippet

import net.liftweb.wizard.Wizard
import net.liftweb.http.{S, StringField, IntField}

/**
 * A wizard is composed of multiple screens. Try to create your own simple wizard
 * following the example code given in this class.
 */
object ExampleWizard extends Wizard {

  // A wizard scoped variable
  object completeInfo extends WizardVar(false)

  // First screen with custom nextScreen logic
  val you = new Screen {
    val yourName = field("First Name", "",
       valMinLen(2, "Name Too Short"), valMaxLen(40, "Name Too Long"))

    val yourAge = field("Age", 1,
      minVal(5, "Too young"), maxVal(125, "You should be dead"))

    override def nextScreen = if (yourAge.is < 18) parents else pets
  }

  // Second screen (might be skipped)
  val parents =  new Screen {
    val parentName = field("Parent or Guardian's name", "",
      valMinLen(2, "Name Too Short"), valMaxLen(40, "Name Too Long"))
  }

  // Last screen
  val pets = new Screen { val pet = field("Pet's name", "",
    valMinLen(2, "Name Too Short"), valMaxLen(40, "Name Too Long"))
  }

  // Logic after last step
  def finish(){
    S.notice("Thank you for registering your pet " + pets.pet.is)
    completeInfo.set(true)
  }

}