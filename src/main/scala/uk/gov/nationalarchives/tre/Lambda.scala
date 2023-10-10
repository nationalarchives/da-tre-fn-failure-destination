package uk.gov.nationalarchives.tre

import com.amazonaws.services.lambda.runtime.events.LambdaDestinationEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.PublishRequest
import uk.gov.nationalarchives.common.messages.Producer.TRE
import uk.gov.nationalarchives.common.messages.Properties
import uk.gov.nationalarchives.tre.Lambda.buildTreErrorMessage
import uk.gov.nationalarchives.tre.messages.treerror.{Parameters, Status, TreError}

import java.time.Instant
import java.util.UUID

class Lambda extends RequestHandler[LambdaDestinationEvent, Unit] {

  private lazy val region = Region.EU_WEST_2
  private lazy val topicOption = sys.env.get("DA_EVENTBUS_TOPIC_ARN")

  override def handleRequest(event: LambdaDestinationEvent, context: Context): Unit = {
    context.getLogger.log(s"Building error message from destination event: $event\n")
    val errorMessage = buildTreErrorMessage(event)
    topicOption match {
      case Some(internalPublishingTopic) =>
        context.getLogger.log(s"Publishing tre error message to event bus publishing topic")
        val snsClient = SnsClient.builder().region(region).build()
        val request =
          PublishRequest.builder.message(MessageParsingUtils.toJsonString(errorMessage))
            .topicArn(internalPublishingTopic).build
        snsClient.publish(request)
      case None =>
        context.getLogger.log("No event bus publishing topic set")
    }
  }
}

object Lambda {
  def buildTreErrorMessage(event: LambdaDestinationEvent): TreError = {
    TreError(
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
          """
        )
      )
    )
  }
}
