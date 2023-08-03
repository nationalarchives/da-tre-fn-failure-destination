package uk.gov.nationalarchives.tre

import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}
import uk.gov.nationalarchives.common.messages.{Producer, Properties}
import uk.gov.nationalarchives.tre.messages.treerror.{Status, TreError}

object MessageParsingUtils {
  implicit val propertiesEncoder: Encoder[Properties] = deriveEncoder[Properties]
  implicit val producerEncoder: Encoder[Producer.Value] = Encoder.encodeEnumeration(Producer)
  implicit val producerDecoder: Decoder[Producer.Value] = Decoder.decodeEnumeration(Producer)
  implicit val statusEncoder: Encoder[Status.Value] = Encoder.encodeEnumeration(Status)
  implicit val statusDecoder: Decoder[Status.Value] = Decoder.decodeEnumeration(Status)
  implicit val treErrorEncoder: Encoder[TreError] = deriveEncoder[TreError]
}
