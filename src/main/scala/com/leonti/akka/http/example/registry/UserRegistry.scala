package com.leonti.akka.http.example.registry

import akka.typed.{ActorRef, Behavior}
import akka.typed.scaladsl.Actor
import com.leonti.akka.http.example.models.User
import com.leonti.akka.http.example.registry.EntityOperations.ActionPerformed
import com.leonti.akka.http.example.search.{EntityMatchers, SearchQuery}

object UserRegistry {

  sealed trait Command
  final case class GetUsers(searchQuery: SearchQuery, replyTo: ActorRef[Seq[User]]) extends Command
  final case class CreateUser(user: User, replyTo: ActorRef[ActionPerformed])       extends Command
  final case class GetUser(id: Int, replyTo: ActorRef[Option[User]])                extends Command
  final case class DeleteUser(id: Int, replyTo: ActorRef[ActionPerformed])          extends Command

  val behavior: Seq[User] => Behavior[Command] = users => {
    Actor.immutable[Command] { (_, msg) =>
      msg match {
        case GetUsers(searchQuery, replyTo) =>
          replyTo ! users.filter(EntityMatchers.userMatcher(searchQuery))
          Actor.same
        case CreateUser(user, replyTo) =>
          replyTo ! ActionPerformed(s"User ${user.id} created.")

          val userMap = users.map(u => u.id -> u).toMap
          behavior((userMap + (user.id -> user)).values.toSeq)
        case GetUser(id, replyTo) =>
          replyTo ! users.find(_.id == id)
          Actor.same
        case DeleteUser(id, replyTo) =>
          replyTo ! ActionPerformed(s"User $id deleted.")
          behavior(users.filterNot(_.id == id))
      }
    }
  }

}
