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

trait UriMatchingRoute extends Directives {

  lazy val uriMatchingRoute =
    get {
      path("customer" / IntNumber) { id =>
        complete {
          s"Customer with id $id"
        }
      } ~
      path("customer") {
        parameter('id.as[Int]) { id =>
          complete {
            s"Customer with id $id"
          }
        }
      }
    }

}

trait HeadersMatchingRoute extends Directives {

  lazy val headersMatchingRoute =
    get {
      path("browser") {
        headerValueByName("User-Agent") { userAgent =>
          complete {
            s"Client is $userAgent"
          }
        }
      }
    }

}

class MainService(route: Route) extends HttpServiceActor {
  def receive: Receive = runRoute(route)
}

object MainService extends DemoRoute with UriMatchingRoute with HeadersMatchingRoute {
  lazy val route = uriMatchingRoute ~ headersMatchingRoute
}