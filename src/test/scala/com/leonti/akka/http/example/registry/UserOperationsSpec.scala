package com.leonti.akka.http.example.registry

import akka.actor.{ActorSystem, Scheduler}
import akka.testkit.TestKit
import akka.util.Timeout
import com.leonti.akka.http.example.models.User
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._
import akka.typed.scaladsl.adapter._
import com.leonti.akka.http.example.search.SearchQuery
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.ExecutionContextExecutor

class UserOperationsSpec
    extends TestKit(ActorSystem("UserOperationsSpec"))
    with WordSpecLike
    with Matchers
    with ScalaFutures
    with BeforeAndAfterAll {
  implicit lazy val scheduler: Scheduler                       = system.scheduler
  implicit lazy val timeout: Timeout                           = Timeout(5.seconds)
  implicit lazy val executionContext: ExecutionContextExecutor = system.dispatcher

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "userOperations" should {
    "not insert duplicates" in {
      val userOperations: UserOperations =
        new UserOperations(system.spawn(UserRegistry.behavior(List(User(id = 1))), "userRegistry"))

      val resultFuture = for {
        _   <- userOperations.add(User(id = 1))
        all <- userOperations.all(SearchQuery())
      } yield all

      whenReady(resultFuture) { result =>
        result.users should ===(Some(Seq(User(id = 1))))
      }
    }
  }

}
