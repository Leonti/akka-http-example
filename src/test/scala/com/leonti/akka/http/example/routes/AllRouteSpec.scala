package com.leonti.akka.http.example.routes

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.leonti.akka.http.example.JsonSupport
import com.leonti.akka.http.example.models._
import com.leonti.akka.http.example.registry.EntityOperations.SearchResult
import com.leonti.akka.http.example.search.SearchQuery
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

class AllRouteSpec extends WordSpec with Matchers with ScalatestRouteTest with JsonSupport {

  "/GET" should {

    val allResult = SearchResult(
      users = Some(List(User(id = 1))),
      organizations = Some(List(Organization(id = 1))),
      tickets = Some(List(Ticket(id = "ticket_id")))
    )
    val usersOnlyResult = SearchResult(
      users = Some(List(User(id = 1)))
    )

    val getAll: SearchQuery => Future[SearchResult] = {
      case SearchQuery(("tags", "all"))        => Future.successful(allResult)
      case SearchQuery(("tags", "users-only")) => Future.successful(usersOnlyResult)
      case _                                   => Future.successful(SearchResult())
    }

    "return all entities" in {
      Get("/all?tags=all") ~> AllRoute.route(getAll) ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"users":[{"_id":1}],"organizations":[{"_id":1}],"tickets":[{"_id":"ticket_id"}]}""")
      }
    }

    "return users only" in {
      Get("/all?tags=users-only") ~> AllRoute.route(getAll) ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"users":[{"_id":1}]}""")
      }
    }

  }
}
