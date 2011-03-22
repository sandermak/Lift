package com.infosupport.lib

import net.liftweb.http.ListenerManager
import net.liftweb.actor.LiftActor

// The central chat server actor (singleton)
object ChatServer extends LiftActor with ListenerManager {
  case class ChatMessages(msgs: List[String])
  case object Clear

  private var msgs: List[String] = Nil

  protected def createUpdate = ChatMessages(msgs)

  // Actor message handling
  override def highPriority = {
    case Clear        => msgs = Nil; println("cleared!")
                         updateListeners()
    case s: String
      if s.length > 0 => msgs ::= s
                         updateListeners()
  }

}