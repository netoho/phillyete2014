package org.eigengo.philyete.api

import spray.routing.{RequestContext, Route, Directives}
import akka.actor.{Props, Actor, ActorRefFactory, ActorRef}
import spray.http._
import spray.http.HttpResponse
import spray.routing.RequestContext
import spray.can.Http
import akka.actor.Actor.Receive
import org.eigengo.philyete.core.{OAuthTwitterAuthorization, TweetReaderActor}

trait TweetAnalysisRoute extends Directives {

  def tweetAnalysisRoute(implicit actorRefFactory: ActorRefFactory): Route =
    post {
      path("tweets")
        parameter('q.as[String])(sendTweetAnalysis)
      }

  def sendTweetAnalysis(query: String)(ctx: RequestContext)(implicit actorRefFactory: ActorRefFactory): Unit = {
    actorRefFactory.actorOf(Props(new TweetAnalysisStreamingActor(query, ctx.responder)))
  }

  class TweetAnalysisStreamingActor(query: String, responder: ActorRef) extends Actor {
    import ContentTypes._
    val reader = context.actorOf(Props(new TweetReaderActor(TweetReaderActor.twitterUri, self) with OAuthTwitterAuthorization))
    val responseStart = HttpResponse(entity = HttpEntity(`application/json`, "{}"))
    responder ! ChunkedResponseStart(responseStart).withAck('start)

    def receive: Receive = {
      case 'start =>
        reader ! query
      case _: Http.ConnectionClosed =>
        context.stop(reader)
        context.stop(self)
      case analysed: Map[String, Map[String, Int]] =>
        println("Got " + analysed)
        responder ! MessageChunk(s"""{ "x":"$analysed" } """)
    }
  }

}
