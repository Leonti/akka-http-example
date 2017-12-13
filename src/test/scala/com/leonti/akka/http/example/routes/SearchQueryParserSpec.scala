package com.leonti.akka.http.example.routes

import com.leonti.akka.http.example.search.SearchQuery
import org.scalatest.{Matchers, WordSpec}

class SearchQueryParserSpec extends WordSpec with Matchers {

  "query parser" should {

    "parse single field" in {
      val searchQuery = SearchQueryParser.parseSearchQuery(Map("tags" -> "funny"))

      searchQuery should ===(SearchQuery("tags" -> "funny"))
    }

    "parse multiple fields" in {
      val searchQuery = SearchQueryParser.parseSearchQuery(
        Map(
          "tags"            -> "funny",
          "organization_id" -> "3"
        ))

      searchQuery should ===(
        SearchQuery(
          Map(
            "tags"            -> "funny",
            "organization_id" -> "3"
          )))
    }

    "parse comma separated value into multiple predicates" in {
      val searchQuery = SearchQueryParser.parseSearchQuery(
        Map(
          "tags" -> "funny,sad"
        ))

      searchQuery should ===(SearchQuery("tags" -> "funny", "tags" -> "sad"))
    }

    "trim spaces from search queries" in {
      val searchQuery = SearchQueryParser.parseSearchQuery(
        Map(
          "tags" -> " funny , sad "
        ))

      searchQuery should ===(SearchQuery("tags" -> "funny", "tags" -> "sad"))
    }

  }

}
