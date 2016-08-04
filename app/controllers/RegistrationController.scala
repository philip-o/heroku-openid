package controllers

import java.util.UUID

import models.ClientRegistration
import play.api.Logger
import play.api.mvc.BodyParsers.parse
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Results._
import utils.OpenIDConnectUtil

import scala.concurrent.Future

trait RegistrationController extends JsonController {

  val discoveryController : DiscoveryController

  def register() = Action.async(parse.json) {
    implicit request =>
      withJsonBody[ClientRegistration] {
        clientRequest =>
          val clientRegistration = new ClientRegistration(clientRequest.redirect_uris, clientRequest.client_name,
            clientRequest.jwks_uri, clientRequest.id_token_signed_response_alg, clientRequest.id_token_encrypted_response_alg,
            clientRequest.id_token_encrypted_response_enc,clientRequest.userinfo_signed_response_alg,clientRequest.userinfo_encrypted_response_alg,
            clientRequest.userinfo_encrypted_response_enc)
          validateClientRegistrationRequest(clientRegistration) match {
              case "valid" =>
                val persist = clientRegistration.copy(client_id = UUID.randomUUID.toString)
                OpenIDConnectUtil.clients.put(persist.client_id , persist)
                Logger.info(s"Client request persisted $persist")
                Future.successful(Created(Json.toJson(persist)))
              case other => Future.successful(BadRequest(Json.parse(s"""{"error":"invalid_client_metadata","error_description":"${other}"}""")))
            }

      }
  }

  private def validateClientRegistrationRequest(clientRegistration: ClientRegistration) = {
    if(clientRegistration.client_id.equals("")) {
      val res = for ((id, client) <- OpenIDConnectUtil.clients if (client.client_name.equals(clientRegistration.client_name))) yield client
      res.size match {
        case 0 => val config = discoveryController.config("morning-chamber-29407.herokuapp.com")
          OpenIDConnectUtil.checkEncoding(config,clientRegistration)
        case _ => "client_already_exists"
      }
    }
    else "client_already_exists"
  }
}

object RegistrationController extends RegistrationController{
  override val discoveryController = DiscoveryController
}