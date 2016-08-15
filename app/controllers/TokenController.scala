package controllers

import models.{TokenErrorResponse, TokenSuccessResponse}
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.{Action, AnyContent, Request}
import utils.OpenIDConnectUtil

trait TokenEndpointController {

  def process() = Action {
    implicit request =>
      val grant_type = extractParam("grant_type")
      val code = extractParam("code")
      val redirect_uri = extractParam("redirect_uri")

      //validate redirect_uri is the same as the original request
      val resp = grant_type match {
        case "authorization_code" => createResponse(code, redirect_uri)
        case _ => BadRequest(Json.toJson(TokenErrorResponse("invalid_tag")))
      }
      resp.withHeaders("Cache-Control" -> "no-store","Pragma" -> "no-cache")
  }

  def extractParam(param : String)(implicit request: Request[AnyContent]) = {
    request.body.asFormUrlEncoded.flatMap(_.get(param)).flatMap(_.headOption).getOrElse(throw new SecurityException(s"Mandatory parameter, $param, missing from request"))
  }

  private def createResponse(authCode : String, redirect_uri : String)(implicit request: Request[AnyContent]) = {
    val option = OpenIDConnectUtil.users.remove(authCode)
    option match {
      case None => BadRequest(Json.toJson(TokenErrorResponse("invalid_request")))
      case Some(found) => val token = TokenSuccessResponse(id_token = OpenIDConnectUtil.createIDToken(found))
        OpenIDConnectUtil.users.put(token.access_token,found)
        Ok(Json.toJson(token))
    }
  }
}

object TokenEndpointController extends TokenEndpointController