package com.leonti.akka.http.example.registry

import com.leonti.akka.http.example.registry.EntityOperations.SearchResult
import com.leonti.akka.http.example.search.SearchQuery

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object AllEntitiesQuery {

  def combined(
      getUsers: SearchQuery => Future[SearchResult],
      getOrganizations: SearchQuery => Future[SearchResult],
      getTickets: SearchQuery => Future[SearchResult]
  ): SearchQuery => Future[SearchResult] =
    (searchQuery: SearchQuery) =>
      for {
        users         <- getUsers(searchQuery)
        organizations <- getOrganizations(searchQuery)
        tickets       <- getTickets(searchQuery)
      } yield
        SearchResult(
          users = users.users,
          organizations = organizations.organizations,
          tickets = tickets.tickets
      )

}
