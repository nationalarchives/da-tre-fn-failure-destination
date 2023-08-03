package uk.gov.nationalarchives.tre

import MessageParsingUtils._
import com.amazonaws.services.lambda.runtime.events.LambdaDestinationEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import io.circe.syntax.EncoderOps
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.PublishRequest
import uk.gov.nationalarchives.common.messages.Producer.TRE
import uk.gov.nationalarchives.common.messages.Properties
import uk.gov.nationalarchives.tre.messages.treerror.{Parameters, Status, TreError}
import java.time.Instant
import java.util.UUID

class Lambda extends RequestHandler[LambdaDestinationEvent, Unit] {

  private lazy val region = Region.EU_WEST_2
  private lazy val topicOption = sys.env.get("TRE_INTERNAL_TOPIC_ARN")

  override def handleRequest(event: LambdaDestinationEvent, context: Context): Unit = {
    val errorMessage = TreError(
      properties = Properties(
        messageType = "uk.gov.nationalarchives.tre.messages.treerror.TreError",
        function = "da-tre-fn-failure-destination",
        timestamp = Instant.now.toString,
        producer = TRE,
        executionId = UUID.randomUUID().toString,
        parentExecutionId = None
      ),
      parameters = Parameters(
        status = Status.TRE_ERROR,
        originator = None,
        reference = "",
        errors = Some(
          s"""
            |Arrived at failure destination with error details:
            |
            |${event.getResponsePayload}
            |
            |Upstream request payload:
            |
            |${event.getRequestPayload}
            |""".stripMargin
        )
      )
    )
    val snsClient = SnsClient.builder().region(region).build()
    val topic = topicOption.get
    val request = PublishRequest.builder.message(errorMessage.asJson.toString).topicArn(topic).build
    snsClient.publish(request)
  }
}
