package com.leonti.akka.http.example.routes

import akka.actor.{ActorSystem, Scheduler}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.typed.scaladsl.adapter._
import akka.util.Timeout
import com.leonti.akka.http.example.JsonSupport
import com.leonti.akka.http.example.models.Ticket
import com.leonti.akka.http.example.registry._
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class TicketRoutesSpec extends WordSpec with Matchers with ScalatestRouteTest with JsonSupport {
  implicit lazy val scheduler: Scheduler = system.scheduler
  implicit lazy val timeout: Timeout     = Timeout(5.seconds)

  val createRoutes: Seq[Ticket] => Route = tickets => {
    val ticketOperations: TicketOperations =
      new TicketOperations(ActorSystem("testSystem").spawn(TicketRegistry.behavior(tickets), "ticketRegistry"))
    TicketRoutes.routes(ticketOperations)
  }

  "/GET" should {
    "return no tickets if no present" in {
      Get("/tickets") ~> createRoutes(Seq()) ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"tickets":[]}""")
      }
    }

    "return list of tickets" in {
      val tickets = List(Ticket(id = "1"), Ticket(id = "2"))

      Get("/tickets") ~> createRoutes(tickets) ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"tickets":[{"_id":"1"},{"_id":"2"}]}""")
      }
    }

    "filter tickets" in {
      val tickets = List(Ticket(id = "1"), Ticket(id = "2"))

      Get("/tickets?_id=1") ~> createRoutes(tickets) ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"tickets":[{"_id":"1"}]}""")
      }
    }
  }

  "/POST" should {
    "be able to add tickets" in {
      Post("/tickets", Ticket(id = "1")) ~> createRoutes(Seq()) ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should include("created")
      }

    }
  }

  "/DELETE" should {
    "be able to remove tickets" in {
      Delete("/tickets/1") ~> createRoutes(Seq()) ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should include("deleted")
      }
    }
  }
}
