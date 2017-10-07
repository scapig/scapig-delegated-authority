package models

import org.joda.time.DateTime
import play.api.libs.json._

object JsonFormatters {

  val datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

  implicit val formatEnvironment: Format[Environment.Value] = EnumJson.enumFormat(Environment)

  implicit val dateRead: Reads[DateTime] = JodaReads.jodaDateReads(datePattern)
  implicit val dateWrite: Writes[DateTime] = JodaWrites.jodaDateWrites(datePattern)
  implicit val dateFormat: Format[DateTime] = Format[DateTime](dateRead, dateWrite)

  implicit val formatTokenRequest = Json.format[TokenRequest]
  implicit val formatTokenResponse = Json.format[TokenResponse]

  implicit val formatRefreshTokenRequest = Json.format[RefreshTokenRequest]

  implicit val formatToken = Json.format[Token]
  implicit val formatDelegatedAuthority = Json.format[DelegatedAuthority]
  implicit val errorResponseWrites = new Writes[ErrorResponse] {
    def writes(e: ErrorResponse): JsValue = Json.obj("code" -> e.errorCode, "message" -> e.message)
  }
}
