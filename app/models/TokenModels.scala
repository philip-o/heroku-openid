package models

import java.util.UUID

import org.joda.time.DateTimeUtils
import play.api.libs.json.Json

case class TokenErrorResponse(error: String)

object TokenErrorResponse {
  implicit val formats = Json.format[TokenErrorResponse]
}

case class TokenSuccessResponse(access_token : String = UUID.randomUUID().toString, token_type: String = "Bearer",
                                refresh_token : Option[String] = None,
                                expires_in : Int = 14400, id_token : String)

object TokenSuccessResponse {
  implicit val formats = Json.format[TokenSuccessResponse]
}

case class IDToken(iss : String, sub : String, aud : String,
                   exp : Long = DateTimeUtils.currentTimeMillis + 14400,
                   iat : Long = DateTimeUtils.currentTimeMillis)

object IDToken {
  implicit val formats = Json.format[IDToken]
}