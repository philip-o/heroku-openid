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
        case true => Future.successful(Ok(Json.toJson(config(OpenIDConnectUtil.loadConfig("oidc.host").headOption.head))))
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

  private[controllers] def publicKey() = {
    Json.parse("""{"keys":[{"alg": "RS256","e": "AQAB","n": "iwylWnEtNvWQTnnU4-u51h_J0EqDfgia73Ddk5z3V9bX7ToqmXwFuvbDkjcUR97_illEfhiO_BS0-JIA9vX9IO4jRu9P9EQLvHh3ddmkDgV_VF_STWTBdvz1NQ6TPcYJwD3PQQkV70Sw5D86y-lokBkioO2tE_DKgf5OHg8GJdYM1TPSAjvXZhpg7qYDMpqfC3s0k3ztW-urWFvJx2uwjSIY3X3HEhpSk45a8aESYHnGVH9FkMB4ZsdGlvFTXsh-A1UkVXWZAd2j4uOsoB5moAwnmfIjHWTfEHjvBzIhdlXVXELn7k8pVdY4SzCDAVtWeqblsS75MfZgELWJPJKYkQ","kty": "RSA","use": "sig","kid": "Demo Keys"}]}""")
  }

  def key() = Action.async {
    implicit request =>
      Future.successful(Ok(publicKey()))
  }
}

object DiscoveryController extends DiscoveryController
