package controllers

import models.Configuration
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.OpenIDConnectUtil

import scala.concurrent.Future

trait DiscoveryController extends Controller {

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
      s"$issuer/register", OpenIDConnectUtil.loadConfigSeq("oidc.scopes-supported"),
      OpenIDConnectUtil.loadConfigSeq("oidc.response-types-supported"),
      OpenIDConnectUtil.loadConfigSeq("oidc.subject-types-supported"),
      OpenIDConnectUtil.loadConfigSeq("oidc.signing-algorithms-supported"),
      OpenIDConnectUtil.loadConfigSeq("oidc.encryption-encodings-supported"),
      OpenIDConnectUtil.loadConfigSeq("oidc.encryption-algorithms-supported"),
      OpenIDConnectUtil.loadConfigSeq("oidc.signing-algorithms-supported"),
      OpenIDConnectUtil.loadConfigSeq("oidc.encryption-encodings-supported"),
      OpenIDConnectUtil.loadConfigSeq("oidc.encryption-algorithms-supported"), OpenIDConnectUtil.loadConfigSeq("oidc.claims-supported"))
  }
}

object DiscoveryController extends DiscoveryController

trait PublicKeyController extends Controller {

  def showKey = Action.async {
    implicit request =>
      Future.successful(Ok(Json.toJson(OpenIDConnectUtil.constructKey())))
  }

}

object PublicKeyController extends PublicKeyController
