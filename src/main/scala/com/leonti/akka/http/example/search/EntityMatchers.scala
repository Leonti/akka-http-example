package com.leonti.akka.http.example.search

import com.leonti.akka.http.example.models._
import spray.json._

object EntityMatchers {

  val userMatcher: SearchQuery => User => Boolean = predicates => user => matches(user.toJson.asJsObject, predicates)
  val organizationMatcher: SearchQuery => Organization => Boolean = predicates =>
    organization => matches(organization.toJson.asJsObject, predicates)
  val ticketMatcher: SearchQuery => Ticket => Boolean = predicates => ticket => matches(ticket.toJson.asJsObject, predicates)
}
