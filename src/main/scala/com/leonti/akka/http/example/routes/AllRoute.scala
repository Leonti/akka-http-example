package com.leonti.akka.http.example.routes

import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.leonti.akka.http.example.JsonSupport
import com.leonti.akka.http.example.registry.EntityOperations.SearchResult
import com.leonti.akka.http.example.search.SearchQuery

import scala.concurrent.Future

object AllRoute extends JsonSupport {

  val route: (SearchQuery => Future[SearchResult]) => Route = getAll => {
    pathPrefix("all") {
      pathEnd {
        get {
          parameterMap { params =>
            val all = getAll(SearchQueryParser.parseSearchQuery(params))
            complete(all)
          }
        }
      }
    }
  }

}
