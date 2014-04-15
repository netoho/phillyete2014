package org.eigengo.phillyete.api

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import spray.can.Http
import spray.routing._

class MainService(route: Route) extends HttpServiceActor {
  def receive: Receive = runRoute(route)
}

object MainService extends DemoRoute with UriMatchingRoute with HeadersMatchingRoute with CookiesMatchingRoute {
  lazy val route = uriMatchingRoute ~ headersMatchingRoute ~ cookiesMatchingRoute
}

object Main extends App {
  val system = ActorSystem()
  val service = system.actorOf(Props(new MainService(MainService.route)))

  IO(Http)(system) ! Http.Bind(service, "0.0.0.0", port = 8080)
  Console.readLine()

  system.shutdown()
}
