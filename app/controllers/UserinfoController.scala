package controllers

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.OpenIDConnectUtil

trait UserinfoController extends Controller {

  def userinfo = Action {
    implicit request =>

      var params : Map[String,Seq[String]] = Map("error" -> Seq("invalid_token"))
      request.headers.headers.find(obj => obj._1.equalsIgnoreCase("Authorization")) match {
        case Some(header) =>
          println(s"Authorization header value: ${header._2}")
          OpenIDConnectUtil.users.get(header._2.replace("Bearer ","")) match {
          case None => params += ("error_description" -> Seq("Invalid token"))
            Logger.info(s"Invalid token")
            BadRequest(params)
          case Some(user) => Ok(Json.parse(s"""{"user":"${user.username}","email":"${user.username}@test.com","sub":"${user.username}"}"""))
        }
        case None =>
          params += ("error_description" -> Seq("No authorisation header provided in request"))
          Logger.info(s"No authorisation header provided")
          BadRequest(params)
      }
  }
}

object UserinfoController extends UserinfoController
