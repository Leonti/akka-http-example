package com.leonti.akka.http.example.registry

import akka.typed.{ActorRef, Behavior}
import akka.typed.scaladsl.Actor
import com.leonti.akka.http.example.models.Organization
import com.leonti.akka.http.example.registry.EntityOperations.ActionPerformed
import com.leonti.akka.http.example.search.{EntityMatchers, SearchQuery}

object OrganizationRegistry {

  sealed trait Command
  final case class GetOrganizations(searchQuery: SearchQuery, replyTo: ActorRef[Seq[Organization]])   extends Command
  final case class CreateOrganization(organization: Organization, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetOrganization(id: Int, replyTo: ActorRef[Option[Organization]])                  extends Command
  final case class DeleteOrganization(id: Int, replyTo: ActorRef[ActionPerformed])                    extends Command

  val behavior: Seq[Organization] => Behavior[Command] = organizations => {
    Actor.immutable[Command] { (_, msg) =>
      msg match {
        case GetOrganizations(searchQuery, replyTo) =>
          replyTo ! organizations.filter(EntityMatchers.organizationMatcher(searchQuery))
          Actor.same
        case CreateOrganization(organization, replyTo) =>
          replyTo ! ActionPerformed(s"Organization ${organization.id} created.")

          val organizationMap = organizations.map(o => o.id -> o).toMap
          behavior((organizationMap + (organization.id -> organization)).values.toSeq)
        case GetOrganization(id, replyTo) =>
          replyTo ! organizations.find(_.id == id)
          Actor.same
        case DeleteOrganization(id, replyTo) =>
          replyTo ! ActionPerformed(s"Organization $id deleted.")
          behavior(organizations.filterNot(_.id == id))
      }
    }
  }

}
