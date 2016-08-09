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
    //Generated using https://mkjwk.org/
    Json.parse("""{"keys":[{"alg": "RS256","e": "AQAB","n": "rvnioIqZaydxDwgSzHojZAf5uMAWtDvI15Azy8yxwAvkpYDe1wKAifOhKVxSsFa9pc88aJFFVMe9rkDumVS_DNrT0LmlBqQAV2sklYTd7jq5yJh3HuI83VXqTgQ1ITqaACdo_nwZ7NP__LhSHYtxGHoM4qac56z4GrTvph67jw9NdSKHwDtQFoQid6f9kXXzcmC8T7t957eZbVyJ1eexm1eGmxpq2ira5-02YF-fuqzyAZN8idcyXYq4nnXfbCmoM8JEBtzcZLw3uYaL3cGEd1n0VcbkqiBBGRDLCVlqe_PhOkVtQfuNDIuA-ikhmd4o_OzjCSGPrrnR6y0F6dpLYw","kty": "RSA","use": "sig","kid": "Demo Keys"}]}""")
  }

  def key() = Action.async {
    implicit request =>
      Future.successful(Ok(publicKey()))
  }
}

object DiscoveryController extends DiscoveryController
