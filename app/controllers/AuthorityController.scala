package controllers

import javax.inject.{Inject, Singleton}

import models.{AuthorityNotFound, AuthorityRequest}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.AuthorityService

import scala.concurrent.ExecutionContext.Implicits.global
import models.JsonFormatters._

@Singleton
class AuthorityController  @Inject()(cc: ControllerComponents, tokenService: AuthorityService) extends AbstractController(cc) with CommonControllers {

  def createAuthority() = Action.async(parse.json) { implicit request =>
    withJsonBody[AuthorityRequest] { authorityRequest: AuthorityRequest =>
      tokenService.createToken(authorityRequest) map { delegatedAuthority => Ok(Json.toJson(delegatedAuthority))}
    }
  }

  def fetchByAccessToken(accessToken: String) = Action.async { implicit request =>
    tokenService.fetchByAccessToken(accessToken) map {
      case Some(delegatedAuthority) => Ok(Json.toJson(delegatedAuthority))
      case _ => AuthorityNotFound.toHttpResponse
    }
  }
}
