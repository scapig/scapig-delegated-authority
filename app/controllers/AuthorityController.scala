package controllers

import javax.inject.{Inject, Singleton}

import config.AppContext
import models._
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

  def refreshToken() = Action.async(parse.json) { implicit request =>
    withJsonBody[RefreshTokenRequest] { refreshTokenRequest: RefreshTokenRequest =>
      authorityService.refreshAuthority(refreshTokenRequest) map { delegatedAuthority =>
        Ok(Json.toJson(TokenResponse(delegatedAuthority.token, appContext.tokenExpiry.toSeconds.toInt)))}
    } recover {
      case _: DelegatedAuthorityNotFoundException => BadRequest(Json.obj("code" -> "INVALID_REFRESH_TOKEN", "message" -> "Invalid refresh token"))
    }
  }

  def fetchByAccessToken(accessToken: String) = Action.async { implicit request =>
    authorityService.fetchByAccessToken(accessToken) map {
      case Some(delegatedAuthority) => Ok(Json.toJson(delegatedAuthority))
      case _ => AuthorityNotFound.toHttpResponse
    }
  }
}
