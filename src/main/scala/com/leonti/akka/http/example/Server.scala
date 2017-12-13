package com.leonti.akka.http.example

import scala.concurrent.duration._
import akka.actor.{ActorSystem, Scheduler}
import akka.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.leonti.akka.http.example.registry._
import com.leonti.akka.http.example.routes.{AllRoute, OrganizationRoutes, TicketRoutes, UserRoutes}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object Server extends App {

  implicit val system: ActorSystem                = ActorSystem("helloAkkaHttpServer")
  implicit val materializer: ActorMaterializer    = ActorMaterializer()
  implicit lazy val scheduler: Scheduler          = system.scheduler
  implicit lazy val timeout: Timeout              = Timeout(5.seconds)
  implicit val executionContext: ExecutionContext = system.dispatcher

  val userOperations = new UserOperations(system.spawn(UserRegistry.behavior(Bootstrap.users()), "userRegistry"))
  val organizationOperations = new OrganizationOperations(
    system.spawn(OrganizationRegistry.behavior(Bootstrap.organizations()), "organizationRegistry"))
  val ticketOperations = new TicketOperations(system.spawn(TicketRegistry.behavior(Bootstrap.tickets()), "ticketRegistry"))

  val routes = concat(
    AllRoute.route(AllEntitiesQuery.combined(userOperations.all, organizationOperations.all, ticketOperations.all)),
    UserRoutes.routes(userOperations),
    OrganizationRoutes.routes(organizationOperations),
    TicketRoutes.routes(ticketOperations)
  )

  val serverBindingFuture: Future[ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

  StdIn.readLine()

  serverBindingFuture
    .flatMap(_.unbind())
    .onComplete { done =>
      done.failed.map { ex =>
        println(ex, "Failed unbinding")
      }
      system.terminate()
    }
}
