package com.infosupport.comet

import net.liftweb.http.{SHtml, CometListener, CometActor}
import SHtml._
import net.liftweb.http.js.JsCmds
import JsCmds._
import net.liftweb.common.Full
import com.infosupport.lib.ChatServer
import com.infosupport.lib.ChatServer._
import net.liftweb.util.ClearClearable


// Comet client - one instance per client page 
class ChatClient extends CometActor with CometListener {
  private var msgs: List[String] = Nil

  override def defaultPrefix = Full("chat")

  def registerWith = ChatServer

  // Actor message handling
  override def highPriority = {
    case ChatMessages(chatMsgs) => msgs = chatMsgs; reRender(false)
  }

  def render =
    "li"               #> msgs.reverse.map(m => "li *" #> m)             &
    "#msg"             #> onSubmit(s => ChatServer ! s)                  &
    "@Clear [onclick]" #> ajaxInvoke(() => { ChatServer ! Clear; Noop }) &
    ClearClearable

}

