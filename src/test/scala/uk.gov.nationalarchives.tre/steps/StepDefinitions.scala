package uk.gov.nationalarchives.tre.steps

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.api.client.logging.LambdaContextLogger
import com.amazonaws.services.lambda.runtime.events.{LambdaDestinationEvent, SNSEvent, SQSEvent}
import io.cucumber.datatable.DataTable
import io.cucumber.scala.{EN, ScalaDsl}
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsString, JsValue, Json}
import uk.gov.nationalarchives.tre.Lambda
import uk.gov.nationalarchives.tre.TestHelpers._

class StepDefinitions extends ScalaDsl with EN
    with MockitoSugar {

    val mockContext: Context = mock[Context]
    val logHolder = new LogHolder

    when(mockContext.getLogger).thenReturn(new LambdaContextLogger(new TestLogSink(logHolder)))

    When("a lambda destination event is received with payload") { (data: String) =>
        val lambdaHandler = new Lambda
        lambdaHandler.handleRequest(lambdaDestinationEvent(Json.parse(data).as[Map[String, JsValue]]), mockContext)
    }

    Then("a lambda destination event is logged with payload") { (data: String) =>
        logHolder.getLatestLog shouldBe s"Destination event received with payload: ${Json.parse(data).as[Map[String, JsValue]]}"
    }
}
