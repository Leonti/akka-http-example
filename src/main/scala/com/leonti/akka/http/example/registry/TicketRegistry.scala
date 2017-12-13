package com.leonti.akka.http.example.registry

import akka.typed.{ActorRef, Behavior}
import akka.typed.scaladsl.Actor
import com.leonti.akka.http.example.models.Ticket
import com.leonti.akka.http.example.registry.EntityOperations.ActionPerformed
import com.leonti.akka.http.example.search.{EntityMatchers, SearchQuery}

object TicketRegistry {

  sealed trait Command
  final case class GetTickets(searchQuery: SearchQuery, replyTo: ActorRef[Seq[Ticket]])   extends Command
  final case class CreateTicket(organization: Ticket, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetTicket(id: String, replyTo: ActorRef[Option[Ticket]])               extends Command
  final case class DeleteTicket(id: String, replyTo: ActorRef[ActionPerformed])           extends Command

  val behavior: Seq[Ticket] => Behavior[Command] = tickets => {
    Actor.immutable[Command] { (_, msg) =>
      msg match {
        case GetTickets(searchQuery, replyTo) =>
          replyTo ! tickets.filter(EntityMatchers.ticketMatcher(searchQuery))
          Actor.same
        case CreateTicket(ticket, replyTo) =>
          replyTo ! ActionPerformed(s"Ticket ${ticket.id} created.")

          val ticketMap = tickets.map(t => t.id -> t).toMap
          behavior((ticketMap + (ticket.id -> ticket)).values.toSeq)
        case GetTicket(id, replyTo) =>
          replyTo ! tickets.find(_.id == id)
          Actor.same
        case DeleteTicket(id, replyTo) =>
          replyTo ! ActionPerformed(s"Ticket $id deleted.")
          behavior(tickets.filterNot(_.id == id))
      }
    }
  }

}
