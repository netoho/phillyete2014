package org.eigengo.phillyete.api

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import spray.can.Http

object Main {
  val system = ActorSystem()
  val service = system.actorOf(Props(new MainService(MainService.route)))

  IO(Http)(system) ! Http.Bind(service, "0.0.0.0", port = 8080)
}
