package com.leonti.akka.http.example.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{delete, get, post}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.leonti.akka.http.example.JsonSupport
import com.leonti.akka.http.example.models.Ticket
import com.leonti.akka.http.example.registry.EntityOperations

object TicketRoutes extends JsonSupport {

  val routes: EntityOperations[Ticket, String] => Route = entityOperations =>
    pathPrefix("tickets") {
      concat(
        pathEnd {
          concat(
            get {
              parameterMap { params =>
                complete(entityOperations.all(SearchQueryParser.parseSearchQuery(params)))
              }
            },
            post {
              entity(as[Ticket]) { ticket =>
                onSuccess(entityOperations.add(ticket)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        },
        path(Segment) { id =>
          concat(
            get {
              rejectEmptyResponse {
                complete(entityOperations.findById(id))
              }
            },
            delete {
              onSuccess(entityOperations.delete(id)) { performed =>
                complete((StatusCodes.OK, performed))
              }
            }
          )
        }
      )
  }

}
