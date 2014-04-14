package org.eigengo.phillyete.api

import spray.routing.{Route, Directives, HttpServiceActor}

trait DemoRoute extends Directives {

  lazy val demoRoute =
    get {
      complete {
        "Hello, world"
      }
    }

}

class MainService(route: Route) extends HttpServiceActor {
  def receive: Receive = runRoute(route)
}

object MainService extends DemoRoute {
  lazy val route = demoRoute
}