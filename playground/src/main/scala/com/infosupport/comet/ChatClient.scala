package com.infosupport.comet

import net.liftweb.http.{ListenerManager, SHtml, CometListener, CometActor}
import SHtml._
import net.liftweb.actor.LiftActor
import net.liftweb.http.js.JsCmds
import JsCmds._
import net.liftweb.common.Full

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

case object Clear

// Comet client - one instance per client page 
class ChatClient extends CometActor with CometListener {
  private var msgs: List[String] = Nil

  override def defaultPrefix = Full("chat")

  def registerWith = ChatServer

  // Actor message handling
  override def highPriority = {
    case m: List[String] => msgs = m; reRender(false)
  }

  // Render controls that part of the screen that is
  // enclosed by the <lift:comet type="ChatClient"> tag
  def render =
    bind("messages" -> msgs.reverse.map(m => <li>{m}</li>),
         "controls" -> ajaxForm(
                            text("", s => ChatServer ! s)
                         ++ <input type="submit" value="Chat!" />
                         ++ ajaxButton("Clear", () =>
                              { ChatServer ! Clear; Noop }))
        )

}

