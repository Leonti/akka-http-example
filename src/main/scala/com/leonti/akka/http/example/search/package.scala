package com.leonti.akka.http.example

import spray.json._

package object search {

  case class Predicate(field: String, searchTerm: String)

  type SearchQuery = Seq[Predicate]

  object SearchQuery {
    def apply(map: Map[String, String]): SearchQuery = {
      map.toSeq.map { case (k, v) => Predicate(k, v) }
    }

    def apply(predicate: (String, String)): SearchQuery = apply(Map(predicate))

    def apply(predicates: (String, String)*): SearchQuery =
      predicates.map { case (k, v) => Predicate(k, v) }

    def unapply(searchQuery: SearchQuery): Option[(String, String)] = searchQuery.headOption.map(q => q.field -> q.searchTerm)
  }

  def matches(jsObject: JsObject, predicates: SearchQuery): Boolean = predicates.forall(matchesSingle(jsObject, _))

  private def matchesSingle: (JsObject, Predicate) => Boolean =
    (jsObject, predicate) =>
      jsObject.fields.exists { field =>
        field._1 == predicate.field && fieldMatches(field._2, predicate.searchTerm)
    }

  private def fieldMatches: (JsValue, String) => Boolean =
    (jsValue, searchTerm) =>
      jsValue match {
        case simpleField @ (JsString(_) | JsNumber(_) | JsBoolean(_)) => simpleFieldMatches(simpleField, searchTerm)
        case JsArray(values)                                          => arrayMatches(values, searchTerm)
        case _                                                        => false
    }

  private def simpleFieldMatches: (JsValue, String) => Boolean =
    (jsValue, searchTerm) =>
      jsValue match {
        case JsString(string)   => string.toLowerCase.contains(searchTerm.toLowerCase)
        case JsNumber(number)   => number.toString == searchTerm
        case JsBoolean(boolean) => boolean.toString == searchTerm
        case _                  => false
    }

  private def arrayMatches: (Vector[JsValue], String) => Boolean =
    (jsValues, searchTerm) => jsValues.exists(value => simpleFieldMatches(value, searchTerm))

}
