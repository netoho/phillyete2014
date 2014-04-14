package org.eigengo.phillyete

import akka.actor.Actor
import spray.http.{HttpEntity, HttpResponse, HttpRequest}

object HelloWorld extends App {


}

class HelloWorldService extends Actor {
  override def receive: Receive = {
    case r: HttpRequest =>
      sender ! HttpResponse(entity = HttpEntity("Hello, world"))
  }
}