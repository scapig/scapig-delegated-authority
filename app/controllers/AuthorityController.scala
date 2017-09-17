package controllers

import javax.inject.{Inject, Singleton}

import config.AppContext
import models.{AuthorityNotFound, TokenRequest, TokenResponse}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.AuthorityService

import scala.concurrent.ExecutionContext.Implicits.global
import models.JsonFormatters._

@Singleton
class AuthorityController  @Inject()(cc: ControllerComponents, authorityService: AuthorityService, appContext: AppContext) extends AbstractController(cc) with CommonControllers {

  def createToken() = Action.async(parse.json) { implicit request =>
    withJsonBody[TokenRequest] { tokenRequest: TokenRequest =>
      authorityService.createAuthority(tokenRequest) map { delegatedAuthority =>
        Ok(Json.toJson(TokenResponse(delegatedAuthority.token, appContext.tokenExpiry.toSeconds.toInt)))}
    }
  }

  def fetchByAccessToken(accessToken: String) = Action.async { implicit request =>
    authorityService.fetchByAccessToken(accessToken) map {
      case Some(delegatedAuthority) => Ok(Json.toJson(delegatedAuthority))
      case _ => AuthorityNotFound.toHttpResponse
    }
  }
}
