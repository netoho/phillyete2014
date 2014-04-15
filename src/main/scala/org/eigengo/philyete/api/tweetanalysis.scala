package org.eigengo.philyete.api

import spray.routing.{RequestContext, Route, Directives}
import akka.actor.{Props, Actor, ActorRefFactory, ActorRef}
import spray.http._
import spray.http.HttpResponse
import spray.routing.RequestContext
import spray.can.Http
import akka.actor.Actor.Receive
import org.eigengo.philyete.core.{OAuthTwitterAuthorization, TweetReaderActor}
import spray.json._
import spray.http.HttpResponse
import spray.routing.RequestContext
import spray.http.ChunkedResponseStart
import spray.http.HttpHeaders.RawHeader
import spray.http.HttpResponse
import spray.routing.RequestContext
import spray.http.HttpHeaders.RawHeader
import spray.http.ChunkedResponseStart

trait TweetAnalysisRoute extends Directives {

  def tweetAnalysisRoute(implicit actorRefFactory: ActorRefFactory): Route =
    post {
      path("tweets")
        parameter('q)(sendTweetAnalysis)
      }

  def sendTweetAnalysis(query: String)(ctx: RequestContext)(implicit actorRefFactory: ActorRefFactory): Unit = {
    actorRefFactory.actorOf(Props(new TweetAnalysisStreamingActor(query, ctx.responder)))
  }

  class TweetAnalysisStreamingActor(query: String, responder: ActorRef) extends Actor {
    val allCrossOrigins =
      RawHeader("Access-Control-Allow-Origin", "*") ::
      RawHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE") :: Nil

    import ContentTypes._
    val reader = context.actorOf(Props(new TweetReaderActor(TweetReaderActor.twitterUri, self) with OAuthTwitterAuthorization))
    val responseStart = HttpResponse(entity = HttpEntity(`application/json`, "{}"), headers = allCrossOrigins)
    responder ! ChunkedResponseStart(responseStart).withAck('start)

    def receive: Receive = {
      case 'start =>
        reader ! query
      case _: Http.ConnectionClosed =>
        context.stop(reader)
        context.stop(self)
      case analysed: Map[String, Map[String, Int]] =>
        // { "counts":{"positive.gurus":100, "negative.gurus": 200},
        //   "languages":{"ar": 1, "en": 3}
        // }

        val items = analysed.map { case (category, elements) => category -> JsObject(elements.mapValues(JsNumber.apply)) }
        val body = CompactPrinter(JsObject(items))
        responder ! MessageChunk(body)
    }
  }

}
