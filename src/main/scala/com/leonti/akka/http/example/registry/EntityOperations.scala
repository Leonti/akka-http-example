package com.leonti.akka.http.example.registry

import com.leonti.akka.http.example.models._
import com.leonti.akka.http.example.search.SearchQuery

import scala.concurrent.Future

object EntityOperations {

  final case class ActionPerformed(description: String)
  final case class SearchResult(
      users: Option[Seq[User]] = None,
      organizations: Option[Seq[Organization]] = None,
      tickets: Option[Seq[Ticket]] = None
  )
}

trait EntityOperations[E, ID] {
  import EntityOperations._

  val add: E => Future[ActionPerformed]
  val findById: ID => Future[Option[E]]
  val delete: ID => Future[ActionPerformed]
  val all: SearchQuery => Future[SearchResult]

}
