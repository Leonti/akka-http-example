package com.leonti.akka.http.example.registry

import akka.actor.Scheduler
import akka.typed.ActorRef
import akka.typed.scaladsl.AskPattern._
import akka.util.Timeout
import com.leonti.akka.http.example.models.Ticket
import com.leonti.akka.http.example.registry.EntityOperations.{ActionPerformed, SearchResult}
import com.leonti.akka.http.example.registry.TicketRegistry.{CreateTicket, DeleteTicket, GetTicket, GetTickets}
import com.leonti.akka.http.example.search.SearchQuery

import scala.concurrent.{ExecutionContext, Future}

class TicketOperations(registry: ActorRef[TicketRegistry.Command])(implicit scheduler: Scheduler, timeout: Timeout, ec: ExecutionContext)
    extends EntityOperations[Ticket, String] {
  override val add: Ticket => Future[ActionPerformed]     = user => registry ? (CreateTicket(user, _))
  override val findById: String => Future[Option[Ticket]] = id => registry ? (GetTicket(id, _))
  override val delete: String => Future[ActionPerformed]  = id => registry ? (DeleteTicket(id, _))
  override val all: SearchQuery => Future[SearchResult] = searchQuery => {
    val users: Future[Seq[Ticket]] = registry ? (GetTickets(searchQuery, _))
    users.map(tickets => SearchResult(tickets = Some(tickets)))
  }
}
