package com.leonti.akka.http.example.search

import org.scalatest.{Matchers, WordSpec}
import spray.json.{JsArray, JsBoolean, JsNumber, JsObject, JsString}

class SearchSpec extends WordSpec with Matchers {

  "string field" should {
    "find a partial match" in {
      val map: Map[String, String] = Map("key" -> "value")
      val result                   = matches(JsObject("testField" -> JsString("Some text value")), SearchQuery("testField" -> "Some"))
      result should ===(true)
    }

    "find exact match" in {
      val result = matches(JsObject("testField" -> JsString("some")), SearchQuery("testField" -> "some"))
      result should ===(true)
    }

    "ignore case" in {
      val result = matches(JsObject("testField" -> JsString("SOME")), SearchQuery("testField" -> "some"))
      result should ===(true)
    }

    "not match if a needle is not in haystack" in {
      val result = matches(JsObject("testField" -> JsString("different")), SearchQuery("testField" -> "some"))
      result should ===(false)
    }

    "match a number in a string" in {
      val result = matches(JsObject("testField" -> JsString("The answer is 42")), SearchQuery("testField" -> "42"))
      result should ===(true)
    }

    "match string for empty predicate value" in {
      val result = matches(JsObject("testField" -> JsString("The answer is 42")), SearchQuery("testField" -> ""))
      result should ===(true)
    }
  }

  "number field" should {
    "find exact match for a number" in {
      val result = matches(JsObject("testField" -> JsNumber(42)), SearchQuery("testField" -> "42"))
      result should ===(true)
    }

    "not find number by partial match" in {
      val result = matches(JsObject("testField" -> JsNumber(142)), SearchQuery("testField" -> "42"))
      result should ===(false)
    }
  }

  "boolean field" should {
    "match a boolean field" in {
      val result = matches(JsObject("testField" -> JsBoolean(false)), SearchQuery("testField" -> "false"))
      result should ===(true)
    }
  }

  "list field" should {
    "find match for a string value in list field" in {
      val result =
        matches(JsObject("testField" -> JsArray(JsString("one"), JsString("two"), JsString("three"))), SearchQuery("testField" -> "two"))
      result should ===(true)
    }

    "find match for a number value in list field" in {
      val result = matches(JsObject("testField" -> JsArray(JsNumber(1), JsNumber(42))), SearchQuery("testField" -> "42"))
      result should ===(true)
    }

    "not find match in nested list" in {
      val result = matches(JsObject("testField" -> JsArray(JsArray(JsNumber(1), JsNumber(42)))), SearchQuery("testField" -> "42"))
      result should ===(false)
    }

    "not find a match in empty list" in {
      val result = matches(JsObject("testField" -> JsArray()), SearchQuery("testField" -> "42"))
      result should ===(false)
    }

  }

  "invalid predicate" should {
    "not match if field doesn't exist" in {
      val result = matches(JsObject("testField" -> JsNumber(42)), SearchQuery("invalid" -> "42"))
      result should ===(false)
    }
  }

  "empty search query" should {
    "match everything" in {
      val result = matches(JsObject("testField" -> JsNumber(42)), SearchQuery())
      result should ===(true)
    }
  }

  "multiple predicates" should {
    "match only if all predicates match" in {
      val jsObject = JsObject(
        "field1" -> JsString("string value"),
        "field2" -> JsNumber(42)
      )

      val result = matches(jsObject, SearchQuery(Map("field1" -> "value", "field2" -> "42")))
      result should ===(true)
    }

    "not match if one of the predicates doesn't match" in {
      val jsObject = JsObject(
        "field1" -> JsString("string value"),
        "field2" -> JsNumber(43)
      )

      val result = matches(jsObject, SearchQuery(Map("field1" -> "value", "field2" -> "42")))
      result should ===(false)
    }

    "match list field for multiple predicates for the same field" in {
      val result = matches(JsObject("testField" -> JsArray(JsNumber(1), JsNumber(42), JsNumber(43))),
                           SearchQuery(Map("testField" -> "42", "testField" -> "1")))
      result should ===(true)
    }

  }

}
