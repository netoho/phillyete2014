package org.eigengo.philyete.api

import akka.actor.{ActorRefFactory, Props, ActorSystem}
import akka.io.IO
import spray.can.Http
import spray.routing._
import spray.http.{HttpResponse, StatusCodes}
import spray.http.HttpHeaders.RawHeader

class MainService(route: Route) extends HttpServiceActor {
  def receive: Receive = runRoute(route)
}

object MainService extends DemoRoute with UriMatchingRoute with HeadersMatchingRoute with CookiesMatchingRoute with TweetAnalysisRoute {

  def route(arf: ActorRefFactory) = uriMatchingRoute ~ headersMatchingRoute ~ cookiesMatchingRoute ~ tweetAnalysisRoute(arf)
}

object Main extends App {
  val system = ActorSystem()
  val service = system.actorOf(Props(new MainService(MainService.route(system))))

  IO(Http)(system) ! Http.Bind(service, "0.0.0.0", port = 8080)
  Console.readLine()

  system.shutdown()
}
