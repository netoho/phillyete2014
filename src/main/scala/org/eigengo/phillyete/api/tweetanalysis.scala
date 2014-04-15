package org.eigengo.phillyete.api

import spray.routing.{RequestContext, Route, Directives}
import akka.actor.{ActorRefFactory, ActorRef}
import spray.http._
import spray.http.HttpResponse
import spray.routing.RequestContext
import spray.can.Http

trait TweetAnalysisRoute extends Directives {
  import akka.actor.ActorDSL._

  def tweetAnalysisRoute(tweetActor: ActorRef)(implicit actorRefFactory: ActorRefFactory): Route =
    post {
      path("tweets")
        parameter('q.as[String])(sendTweetAnalysis(tweetActor))
      }

  def sendTweetAnalysis(tweetActor: ActorRef)(query: String)(ctx: RequestContext): Unit = {
    import ContentTypes._
    import akka.pattern.ask

    val responseStart = HttpResponse(entity = HttpEntity(`application/json`, "{}"))
    ctx.responder ! ChunkedResponseStart(responseStart).withAck('continue)

  }

}
