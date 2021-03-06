package com.infosupport
package model

import net.liftweb.mapper._
import _root_.net.liftweb.common._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content">
			       <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, firstName, lastName, email,
  locale, timezone, password)

  // comment this line out to require email validations
  override def skipEmailValidation = true

  // redirect after login:
  override def homePage = "/weekstaat"

}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] with OneToMany[Long, User] {
  def getSingleton = User // what's the "meta" server

  // define an additional field for a personal essay
  object weekstaten extends MappedOneToMany(Weekstaat, Weekstaat.user)

}
