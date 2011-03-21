package com.infosupport.lib

import com.infosupport.comet.Clear
import net.liftweb.http.ListenerManager
import net.liftweb.actor.LiftActor

// The central chat server actor (singleton)
object ChatServer extends LiftActor with ListenerManager {
  private var msgs: List[String] = Nil

  protected def createUpdate = msgs

  // Actor message handling
  override def highPriority = {
    case Clear        => msgs = Nil;
                         updateListeners()
    case s: String
      if s.length > 0 => msgs ::= s
                         updateListeners()
  }

}