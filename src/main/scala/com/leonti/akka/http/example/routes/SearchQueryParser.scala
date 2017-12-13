package com.leonti.akka.http.example.routes

import com.leonti.akka.http.example.search.{Predicate, SearchQuery}

object SearchQueryParser {

  val parseSearchQuery: Map[String, String] => SearchQuery = parameters =>
    parameters.toSeq.flatMap { case (k, v) => v.split(",").map(v1 => Predicate(k, v1.trim)) }

}
