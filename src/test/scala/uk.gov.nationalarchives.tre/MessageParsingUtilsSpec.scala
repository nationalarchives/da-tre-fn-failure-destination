package uk.gov.nationalarchives.tre

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import uk.gov.nationalarchives.common.messages.Producer.TRE
import uk.gov.nationalarchives.common.messages.Properties
import uk.gov.nationalarchives.tre.messages.treerror.{Parameters, Status, TreError}

import java.time.Instant
import java.util.UUID

class MessageParsingUtilsSpec extends AnyFlatSpec {
  "toJsonString" should "convert a TreError to the expected json string" in {
    val testInstant = Instant.now.toString
    val testUUID = UUID.randomUUID().toString

    val testError = TreError(
      properties = Properties(
        messageType = "uk.gov.nationalarchives.tre.messages.treerror.TreError",
        function = "da-tre-fn-failure-destination",
        timestamp = testInstant,
        producer = TRE,
        executionId = testUUID,
        parentExecutionId = None
      ),
      parameters = Parameters(
        status = Status.TRE_ERROR,
        originator = None,
        reference = "",
        errors = Some("Error message")
      )
    )

    MessageParsingUtils.toJsonString(testError) shouldBe s"""{
     |  "properties" : {
     |    "messageType" : "uk.gov.nationalarchives.tre.messages.treerror.TreError",
     |    "timestamp" : "$testInstant",
     |    "function" : "da-tre-fn-failure-destination",
     |    "producer" : "TRE",
     |    "executionId" : "$testUUID",
     |    "parentExecutionId" : null
     |  },
     |  "parameters" : {
     |    "status" : "TRE_ERROR",
     |    "originator" : null,
     |    "reference" : "",
     |    "errors" : "Error message"
     |  }
     |}""".stripMargin
  }
}
