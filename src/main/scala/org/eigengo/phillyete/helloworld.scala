package org.eigengo.phillyete

import akka.actor.{Props, ActorSystem, Actor}
import spray.http.{HttpEntity, HttpResponse, HttpRequest}
import spray.can.Http
import akka.io.IO

object HelloWorld extends App {
  val system = ActorSystem()
  val service = system.actorOf(Props[HelloWorldService])

  IO(Http)(system) ! Http.Bind(service, "0.0.0.0", port = 8080)
}

class HelloWorldService extends Actor {
  override def receive: Receive = {
    case _: Http.Connected =>
      println(sender)
      sender ! Http.Register(self)
    case r: HttpRequest =>
      println(r.message.entity)
      println(r.message.entity.data)
      println(r.message.entity.data.asString)
      sender ! HttpResponse(entity = HttpEntity("Hello, world"))
  }
}
