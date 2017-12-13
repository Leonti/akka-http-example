package com.leonti.akka.http.example.routes

import akka.actor.{ActorSystem, Scheduler}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import akka.typed.scaladsl.adapter._
import akka.util.Timeout

import scala.concurrent.duration._
import com.leonti.akka.http.example.JsonSupport
import com.leonti.akka.http.example.models.User
import com.leonti.akka.http.example.registry.{UserOperations, UserRegistry}

class UserRoutesSpec extends WordSpec with Matchers with ScalatestRouteTest with JsonSupport {
  implicit lazy val scheduler: Scheduler = system.scheduler
  implicit lazy val timeout: Timeout     = Timeout(5.seconds)

  val createRoutes: Seq[User] => Route = users => {
    val userOperations: UserOperations =
      new UserOperations(ActorSystem("testSystem").spawn(UserRegistry.behavior(users), "userRegistry"))
    UserRoutes.routes(userOperations)
  }

  "/GET" should {
    "return no users if no present" in {
      Get("/users") ~> createRoutes(Seq()) ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"users":[]}""")
      }
    }

    "return list of users" in {
      val users = List(User(id = 1), User(id = 2))

      Get("/users") ~> createRoutes(users) ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"users":[{"_id":1},{"_id":2}]}""")
      }
    }

    "filter users" in {
      val users = List(User(id = 1), User(id = 2))

      Get("/users?_id=1") ~> createRoutes(users) ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"users":[{"_id":1}]}""")
      }
    }
  }

  "/POST" should {
    "be able to add users" in {
      Post("/users", User(id = 1)) ~> createRoutes(Seq()) ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should include("created")
      }
    }
  }

  "/DELETE" should {
    "be able to remove users" in {
      Delete("/users/1") ~> createRoutes(Seq()) ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should include("deleted")
      }
    }
  }
}
