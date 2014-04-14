package org.eigengo.phillyete

import org.specs2.mutable.SpecificationLike
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import spray.http.{HttpResponse, HttpRequest}

class HelloWorldServiceSpec extends TestKit(ActorSystem()) with ImplicitSender with SpecificationLike {
  val service = TestActorRef[HelloWorldService]

  "Any request" should {
    "Reply with Hello, World" in {
      service ! HttpRequest()
      expectMsgType[HttpResponse].entity.asString mustEqual "Hello, world"
    }
  }

}
