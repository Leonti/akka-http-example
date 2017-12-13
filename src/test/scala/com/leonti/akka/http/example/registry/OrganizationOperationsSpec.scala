package com.leonti.akka.http.example.registry

import akka.actor.{ActorSystem, Scheduler}
import akka.testkit.TestKit
import akka.util.Timeout
import com.leonti.akka.http.example.models.Organization
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._
import akka.typed.scaladsl.adapter._
import com.leonti.akka.http.example.search.SearchQuery
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.ExecutionContextExecutor

class OrganizationOperationsSpec
    extends TestKit(ActorSystem("OrganizationOperationSpec"))
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

  "organizationOperations" should {
    "not insert duplicates" in {
      val organizationOperations: OrganizationOperations =
        new OrganizationOperations(system.spawn(OrganizationRegistry.behavior(List(Organization(id = 1))), "organizationRegistry"))

      val resultFuture = for {
        _   <- organizationOperations.add(Organization(id = 1))
        all <- organizationOperations.all(SearchQuery())
      } yield all

      whenReady(resultFuture) { result =>
        result.organizations should ===(Some(Seq(Organization(id = 1))))
      }
    }
  }

}
