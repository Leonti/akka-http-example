package com.leonti.akka.http.example.registry

import akka.actor.Scheduler
import akka.typed.ActorRef
import akka.typed.scaladsl.AskPattern._
import akka.util.Timeout
import com.leonti.akka.http.example.models.User
import com.leonti.akka.http.example.registry.EntityOperations.{ActionPerformed, SearchResult}
import com.leonti.akka.http.example.registry.UserRegistry._
import com.leonti.akka.http.example.search.SearchQuery

import scala.concurrent.{ExecutionContext, Future}

class UserOperations(userRegistry: ActorRef[UserRegistry.Command])(implicit scheduler: Scheduler, timeout: Timeout, ec: ExecutionContext)
    extends EntityOperations[User, Int] {
  override val add: User => Future[ActionPerformed]   = user => userRegistry ? (CreateUser(user, _))
  override val findById: Int => Future[Option[User]]  = id => userRegistry ? (GetUser(id, _))
  override val delete: Int => Future[ActionPerformed] = id => userRegistry ? (DeleteUser(id, _))
  override val all: SearchQuery => Future[SearchResult] = searchQuery => {
    val users: Future[Seq[User]] = userRegistry ? (GetUsers(searchQuery, _))
    users.map(users => SearchResult(users = Some(users)))
  }
}
