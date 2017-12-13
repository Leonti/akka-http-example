package com.leonti.akka.http.example
import com.leonti.akka.http.example.models._
import scala.io.Source
import spray.json._

object Bootstrap extends JsonSupport {
  import spray.json.DefaultJsonProtocol._

  val users: () => Seq[User] = () => Source.fromResource("users.json").mkString.parseJson.convertTo[Seq[User]]
  val organizations: () => Seq[Organization] = () =>
    Source.fromResource("organizations.json").mkString.parseJson.convertTo[Seq[Organization]]
  val tickets: () => Seq[Ticket] = () => Source.fromResource("tickets.json").mkString.parseJson.convertTo[Seq[Ticket]]

}
