package com.leonti.akka.http.example.search

import com.leonti.akka.http.example.models.User
import org.scalatest.{Matchers, WordSpec}

class EntityMatchersSpec extends WordSpec with Matchers {

  "user matcher" should {
    "match by single tag" in {
      val matcher = EntityMatchers.userMatcher(SearchQuery("tags" -> "funny"))
      val user    = User(id = 1, tags = Some(List("funny", "sad")))

      matcher(user) should ===(true)
    }

    "match multiple tags" in {
      val matcher = EntityMatchers.userMatcher(SearchQuery("tags" -> "funny", "tags" -> "sad"))
      val user    = User(id = 1, tags = Some(List("funny", "sad", "mad")))

      matcher(user) should ===(true)
    }

    "match by a single field" in {
      val matcher = EntityMatchers.userMatcher(SearchQuery("organization_id" -> "42"))
      val user    = User(id = 1, organizationId = Some(42))

      matcher(user) should ===(true)
    }
  }

}
