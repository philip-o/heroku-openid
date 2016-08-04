package controllers

import models.Configuration
import play.api.Play
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Request}
import play.api.mvc.Results._
import utils.OpenIDConnectUtil

import scala.concurrent.Future

trait DiscoveryController {

  def configuration() = Action.async {
    implicit request =>
      request.secure match {
        case true => Future.successful(Ok(Json.toJson(config("morning-chamber-29407.herokuapp.com"))))
        case false => Future.successful(BadRequest(Json.parse("""{"message":"your request was not secure"}""")))
      }
  }

  private[controllers] def config(host : String = "") = {
    val issuer = s"https://${host}"
    Configuration(issuer, s"$issuer/authorise", s"$issuer/token", s"$issuer/userinfo", s"$issuer/jwks.json",
      s"$issuer/register", OpenIDConnectUtil.loadConfig("oidc.scopes-supported"),
      OpenIDConnectUtil.loadConfig("oidc.response-types-supported"),
      OpenIDConnectUtil.loadConfig("oidc.subject-types-supported"),
      OpenIDConnectUtil.loadConfig("oidc.signing-algorithms-supported"),
      OpenIDConnectUtil.loadConfig("oidc.encryption-encodings-supported"),
      OpenIDConnectUtil.loadConfig("oidc.encryption-algorithms-supported"),
      OpenIDConnectUtil.loadConfig("oidc.signing-algorithms-supported"),
      OpenIDConnectUtil.loadConfig("oidc.encryption-encodings-supported"),
      OpenIDConnectUtil.loadConfig("oidc.encryption-algorithms-supported"), OpenIDConnectUtil.loadConfig("oidc.claims-supported"))
  }
}

object DiscoveryController extends DiscoveryController
