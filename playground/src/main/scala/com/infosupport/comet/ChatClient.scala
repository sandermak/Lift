package com.infosupport.comet

import net.liftweb.http.{SHtml, CometListener, CometActor}
import SHtml._
import net.liftweb.http.js.JsCmds
import JsCmds._
import com.infosupport.lib.ChatServer
import com.infosupport.lib.ChatServer._
import net.liftweb.util.ClearClearable

// Comet client - one instance per client page 
class ChatClient extends CometActor with CometListener {
  private var msgs: List[String] = Nil

  def registerWith = ChatServer

  // Actor message handling
  override def highPriority = {
    case ChatMessages(chatMsgs) => msgs = chatMsgs;
                                   reRender()
  }

  def render =
    "li *"             #> msgs.reverse                  &
    "#message"         #> onSubmit(s => ChatServer ! s) &
    "@Clear [onclick]" #>
      ajaxInvoke(() => { ChatServer ! Clear; Noop })    &
    ClearClearable

}

