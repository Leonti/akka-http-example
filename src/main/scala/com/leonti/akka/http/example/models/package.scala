package com.leonti.akka.http.example

package object models {
  import spray.json.DefaultJsonProtocol._

  final case class User(
      id: Int,
      url: Option[String] = None,
      externalId: Option[String] = None,
      name: Option[String] = None,
      alias: Option[String] = None,
      createdAt: Option[String] = None,
      active: Option[Boolean] = None,
      verified: Option[Boolean] = None,
      shared: Option[Boolean] = None,
      locale: Option[String] = None,
      timezone: Option[String] = None,
      lastLoginAt: Option[String] = None,
      email: Option[String] = None,
      phone: Option[String] = None,
      signature: Option[String] = None,
      organizationId: Option[Int] = None,
      tags: Option[Seq[String]] = None,
      suspended: Option[Boolean] = None,
      role: Option[String] = None
  )

  implicit val userJsonFormat = jsonFormat(
    User,
    "_id",
    "url",
    "external_id",
    "name",
    "alias",
    "created_at",
    "active",
    "verified",
    "shared",
    "locale",
    "timezone",
    "last_login_at",
    "email",
    "phone",
    "signature",
    "organization_id",
    "tags",
    "suspended",
    "role"
  )

  final case class Ticket(
      id: String,
      url: Option[String] = None,
      externalId: Option[String] = None,
      createdAt: Option[String] = None,
      `type`: Option[String] = None,
      subject: Option[String] = None,
      description: Option[String] = None,
      priority: Option[String] = None,
      status: Option[String] = None,
      submitterId: Option[Int] = None,
      asigneeId: Option[Int] = None,
      organizationId: Option[Int] = None,
      tags: Option[Seq[String]] = None,
      hasIncidents: Option[Boolean] = None,
      dueAt: Option[String] = None,
      via: Option[String] = None
  )

  implicit val ticketJsonFormat = jsonFormat(
    Ticket,
    "_id",
    "url",
    "external_id",
    "created_at",
    "type",
    "subject",
    "description",
    "priority",
    "status",
    "submitter_id",
    "assignee_id",
    "organization_id",
    "tags",
    "has_incidents",
    "due_at",
    "via"
  )

  final case class Organization(
      id: Int,
      url: Option[String] = None,
      externalId: Option[String] = None,
      name: Option[String] = None,
      domainNames: Option[Seq[String]] = None,
      createdAt: Option[String] = None,
      details: Option[String] = None,
      sharedTickets: Option[Boolean] = None,
      tags: Option[Seq[String]] = None
  )

  implicit val organizationJsonFormat =
    jsonFormat(Organization, "_id", "url", "external_id", "name", "domain_names", "created_at", "details", "shared_tickets", "tags")
}
