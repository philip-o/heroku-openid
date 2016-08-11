package controllers

import java.util.UUID

import models.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, AnyContent, Controller, Request}
import utils.OpenIDConnectUtil

trait AuthorisationController extends Controller {

  val discoveryController : DiscoveryController

  def loginForm = Form(mapping ("Username" -> nonEmptyText, "Password" -> nonEmptyText,"State" -> text,"Consumer" -> text,
  "ClientId" -> text)(User.apply)(User.unapply))

  def processAuthorisePostRequest() = Action {
    implicit request =>
      val response_type = extractParam("response_type")
      val scope = extractParam("scope")
      val clientId = extractParam("client_id")
      val state = request.body.asFormUrlEncoded.flatMap(_.get("state")).flatMap(_.headOption).getOrElse("")
      val redirectURL = extractParam("redirect_uri")

      processRequest(response_type, scope, clientId, state, redirectURL)
  }

  def processAuthoriseGetRequest = Action {
    implicit request =>
      val response_type = extractFromRequest("response_type")
      val scope = extractFromRequest("scope")
      val clientId = extractFromRequest("client_id")
      val state = request.session.get("state").getOrElse("")
      val redirectURL = extractFromRequest("redirect_uri")

      processRequest(response_type, scope, clientId, state, redirectURL)
  }

  private def extractFromRequest(property : String)(implicit request : Request[AnyContent]) = {
    request.session.get(property).getOrElse(throw new SecurityException(s"Mandatory parameter, $property, missing from request"))
  }

  private def extractParam(param : String)(implicit request: Request[AnyContent]) = {
    request.body.asFormUrlEncoded.flatMap(_.get(param)).flatMap(_.headOption)
      .getOrElse(throw new SecurityException(s"Mandatory parameter, $param, missing from request"))
  }

  private def processRequest(responseType : String, scope : String, clientId : String, state : String, returnAddress : String)
                            (implicit request: Request[AnyContent]) = {
    var params : Map[String,Seq[String]] = Map()
    scope.contains("openid") match {
      case true  =>
        val result = verifyClientMatchesConfig(responseType, scope, clientId, returnAddress)
        result match {
          case "valid" =>
            Ok(views.html.login(loginForm,Some(state),Some(returnAddress),Some(clientId)))
          case _ =>
            params = params + ("error" -> Seq("invalid_request")) + ("error_desription" -> Seq(result))
            Redirect(returnAddress, params)
        }
      case false => params = params + ("error" -> Seq("invalid_request")) + ("error_desription" -> Seq("invalid_response_type"))
        Redirect(returnAddress, params)
    }
  }

  def processLogin = Action(parse.form(loginForm)) {
    implicit request =>
      loginForm.bindFromRequest().fold(
        errors => BadRequest(views.html.login(errors)),
        success => { val id = UUID.randomUUID.toString
          OpenIDConnectUtil.users.put(id, success)
          val params = Map("code" -> Seq(id), "state" -> Seq(success.state))
          Redirect(success.consumer, params)
        }
      )
  }

  private def verifyClientMatchesConfig(responseType : String, scope : String, clientId : String, returnAddress : String) = {
    OpenIDConnectUtil.clients.get(clientId) match {
      case None => "notfound"
      case Some(client) => client.redirect_uris.contains(returnAddress) match {
        case false => "invalid_return_address"
        case true => val config = discoveryController.config("morning-chamber-29407.herokuapp.com")
          config.response_types_supported.contains(responseType) match {
            case false => "invalid_response_type"
            case true => scope.replaceFirst("openid","").split(" ").filterNot(line => line.equalsIgnoreCase(""))
              .filterNot(claim => config.claims_supported.contains(claim)).length match {
              case 0 => "valid"
              case _ => "invalid_claims_requested"
            }
          }
      }
    }
  }
}

object AuthorisationController extends AuthorisationController {
  override val discoveryController = DiscoveryController
}
