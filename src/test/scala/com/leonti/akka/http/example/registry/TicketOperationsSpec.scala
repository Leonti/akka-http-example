package com.leonti.akka.http.example.registry

import akka.actor.{ActorSystem, Scheduler}
import akka.testkit.TestKit
import akka.util.Timeout
import com.leonti.akka.http.example.models.Ticket
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._
import akka.typed.scaladsl.adapter._
import com.leonti.akka.http.example.search.SearchQuery
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.ExecutionContextExecutor

class TicketOperationsSpec
    extends TestKit(ActorSystem("TicketOperationSpec"))
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

  "ticketOperations" should {
    "not insert duplicates" in {
      val ticketOperations: TicketOperations =
        new TicketOperations(system.spawn(TicketRegistry.behavior(List(Ticket(id = "1"))), "ticketRegistry"))

      val resultFuture = for {
        _   <- ticketOperations.add(Ticket(id = "1"))
        all <- ticketOperations.all(SearchQuery())
      } yield all

      whenReady(resultFuture) { result =>
        result.tickets should ===(Some(Seq(Ticket(id = "1"))))
      }
    }
  }

}
