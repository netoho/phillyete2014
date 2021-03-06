package org.eigengo.phillyete.api

import spray.routing.Directives
import spray.http.HttpCookie

trait DemoRoute extends Directives {

  lazy val demoRoute =
    get {
      complete {
        "Hello, world"
      }
    }

}

trait UriMatchingRoute extends Directives {

  case class Colour(r: Int, g: Int, b: Int) {
    require(r >= 0 && r <= 255)
    require(g >= 0 && g <= 255)
    require(b >= 0 && b <= 255)
  }

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
      path("colour") {
        parameters(('r.as[Int], 'g.as[Int], 'b.as[Int])).as(Colour) { colour: Colour =>
          import colour._
          complete {
            <html>
              <body>
                <p>{r}</p>
                <p>{g}</p>
                <p>{b}</p>
              </body>
            </html>
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

trait CookiesMatchingRoute extends Directives {

  lazy val cookiesMatchingRoute =
    get {
      path("cookie") {
        cookie("spray") { spray =>
          complete {
            s"The value is $spray"
          }
        }
      }
    } ~
    post {
      path("cookie") {
        setCookie(HttpCookie("spray", "SGVsbG8sIHdvcmxkCg==", httpOnly = true)) {
          complete {
            "Cookie created"
          }
        }
      }
    }
}

