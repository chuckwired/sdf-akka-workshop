package com.boldradius.sdf.akka

import java.io.{PrintWriter, File}

/**
 * Created by robertsliwinski on 22/05/15.
 */
case class ChatActor(sessionId: Long) {
  new File("chat.log" ).delete()
  val pw = new PrintWriter(new File("chat.log"))
  pw.write("Starting help chat for session nr"+sessionId)
  pw.close
}
