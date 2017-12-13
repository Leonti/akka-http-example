package com.leonti.akka.http.example.routes

import akka.actor.{ActorSystem, Scheduler}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.typed.scaladsl.adapter._
import akka.util.Timeout
import com.leonti.akka.http.example.JsonSupport
import com.leonti.akka.http.example.models.Organization
import com.leonti.akka.http.example.registry.{OrganizationOperations, OrganizationRegistry}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class OrganizationRoutesSpec extends WordSpec with Matchers with ScalatestRouteTest with JsonSupport {
  implicit lazy val scheduler: Scheduler = system.scheduler
  implicit lazy val timeout: Timeout     = Timeout(5.seconds)

  val createRoutes: Seq[Organization] => Route = organizations => {
    val organizationOperations: OrganizationOperations =
      new OrganizationOperations(ActorSystem("testSystem").spawn(OrganizationRegistry.behavior(organizations), "organizationRegistry"))
    OrganizationRoutes.routes(organizationOperations)
  }

  "/GET" should {
    "return no organizations if no present" in {
      Get("/organizations") ~> createRoutes(Seq()) ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"organizations":[]}""")
      }
    }

    "return list of organizations" in {
      val organizations = List(Organization(id = 1), Organization(id = 2))

      Get("/organizations") ~> createRoutes(organizations) ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"organizations":[{"_id":1},{"_id":2}]}""")
      }
    }

    "filter organizations" in {
      val organizations = List(Organization(id = 1), Organization(id = 2))

      Get("/organizations?_id=1") ~> createRoutes(organizations) ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"organizations":[{"_id":1}]}""")
      }
    }
  }

  "/POST" should {
    "be able to add organizations" in {
      Post("/organizations", Organization(id = 1)) ~> createRoutes(Seq()) ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should include("created")
      }

    }
  }

  "/DELETE" should {
    "be able to remove organizations" in {
      Delete("/organizations/1") ~> createRoutes(Seq()) ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should include("deleted")
      }
    }
  }
}
