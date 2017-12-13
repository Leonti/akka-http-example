package com.leonti.akka.http.example

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.leonti.akka.http.example.registry.EntityOperations.{ActionPerformed, SearchResult}
import com.leonti.akka.http.example.models._

trait JsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val searchResultFormat        = jsonFormat3(SearchResult)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
