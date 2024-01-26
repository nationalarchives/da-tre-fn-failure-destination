import Dependencies._

ThisBuild / scalaVersion := "2.13.12"
ThisBuild / version := "0.1.0"

val awsVersion = "2.23.11"

lazy val root = (project in file("."))
  .settings(
    name := "da-tre-fn-failure-destination",
    libraryDependencies ++= Seq(
      lambdaRuntimeInterfaceClient
    ),
    assembly / assemblyOutputPath := file("target/function.jar")
  )

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case _                        => MergeStrategy.first
}

libraryDependencies ++= Seq(
  "io.cucumber" %% "cucumber-scala" % "8.20.0" % Test,
  "io.cucumber" % "cucumber-junit" % "7.15.0" % Test,
  "io.cucumber" % "cucumber-core" % "7.11.2" % Test,
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
  "org.scalatest" %% "scalatest" % "3.2.17" % Test,
  "org.scalatestplus" %% "mockito-4-11" % "3.2.17.0" % Test,
  "uk.gov.nationalarchives" % "da-transform-schemas" % "2.5",
  "com.amazonaws" % "aws-lambda-java-events" % "3.11.4",
  "com.typesafe.play" %% "play-json" % "2.10.4",
  "software.amazon.awssdk" % "sns" % awsVersion,
  "software.amazon.awssdk" % "sso" % awsVersion,
  "software.amazon.awssdk" % "ssooidc" % awsVersion,
  "io.circe" %% "circe-generic-extras" % "0.14.3"
)


val circeVersion = "0.14.6"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
