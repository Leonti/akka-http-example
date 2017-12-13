package com.leonti.akka.http.example.registry

import akka.actor.Scheduler
import akka.typed.ActorRef
import akka.typed.scaladsl.AskPattern._
import akka.util.Timeout
import com.leonti.akka.http.example.models.Organization
import com.leonti.akka.http.example.registry.EntityOperations.{ActionPerformed, SearchResult}
import com.leonti.akka.http.example.registry.OrganizationRegistry._
import com.leonti.akka.http.example.search.SearchQuery

import scala.concurrent.{ExecutionContext, Future}

class OrganizationOperations(organizationRegistry: ActorRef[OrganizationRegistry.Command])(implicit scheduler: Scheduler,
                                                                                           timeout: Timeout,
                                                                                           ec: ExecutionContext)
    extends EntityOperations[Organization, Int] {
  override val add: Organization => Future[ActionPerformed]  = organization => organizationRegistry ? (CreateOrganization(organization, _))
  override val findById: Int => Future[Option[Organization]] = id => organizationRegistry ? (GetOrganization(id, _))
  override val delete: Int => Future[ActionPerformed]        = id => organizationRegistry ? (DeleteOrganization(id, _))
  override val all: SearchQuery => Future[SearchResult] = searchQuery => {
    val organizations: Future[Seq[Organization]] = organizationRegistry ? (GetOrganizations(searchQuery, _))
    organizations.map(organizations => SearchResult(organizations = Some(organizations)))
  }
}
