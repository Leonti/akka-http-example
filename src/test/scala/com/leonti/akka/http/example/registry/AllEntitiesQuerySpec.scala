package com.leonti.akka.http.example.registry

import com.leonti.akka.http.example.models._
import com.leonti.akka.http.example.registry.EntityOperations.SearchResult
import com.leonti.akka.http.example.search.SearchQuery
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

class AllEntitiesQuerySpec extends WordSpec with Matchers with ScalaFutures {

  "all entities combined" should {

    "combine 3 results" in {
      val getCombined = AllEntitiesQuery.combined(
        _ => Future.successful(SearchResult(users = Some(List(User(id = 1))))),
        _ => Future.successful(SearchResult(organizations = Some(List(Organization(id = 1))))),
        _ => Future.successful(SearchResult(tickets = Some(List(Ticket(id = "ticket_id"))))),
      )

      whenReady(getCombined(SearchQuery())) { result =>
        result.users should ===(Some(List(User(id = 1))))
        result.organizations should ===(Some(List(Organization(id = 1))))
        result.tickets should ===(Some(List(Ticket(id = "ticket_id"))))
      }
    }
  }

}
