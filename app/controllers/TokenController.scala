package controllers

import javax.inject.{Inject, Singleton}

import models.TokenRequest
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.TokenService

@Singleton
class TokenController  @Inject()(cc: ControllerComponents, tokenService: TokenService) extends AbstractController(cc) with CommonControllers {

  def token() = Action.async(parse.json) { implicit request =>
    withJsonBody[TokenRequest] { tokenRequest: TokenRequest =>
      tokenService.createToken(tokenRequest) map { delegatedAuthority => Ok(Json.toJson(delegatedAuthority))}
    }
  }
}
