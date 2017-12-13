lazy val akkaHttpVersion  = "10.0.11"
lazy val akkaVersion      = "2.5.8"
lazy val scalaTestVersion = "3.0.1"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings", "-Ywarn-unused-import", "-encoding", "utf8")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "com.example",
      scalaVersion := "2.12.4"
    )),
  name := "akka-http-rest-example",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
    "com.typesafe.akka" %% "akka-typed"           % akkaVersion,
    "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-testkit"         % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion % Test,
    "org.scalatest"     %% "scalatest"            % scalaTestVersion % Test
  )
)

test in assembly := {}
mainClass in assembly := Some("Server")
