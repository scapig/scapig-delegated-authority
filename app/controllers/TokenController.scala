package controllers

import javax.inject.{Inject, Singleton}

import models.AuthorityRequest
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.TokenService
import scala.concurrent.ExecutionContext.Implicits.global
import models.JsonFormatters._

@Singleton
class TokenController  @Inject()(cc: ControllerComponents, tokenService: TokenService) extends AbstractController(cc) with CommonControllers {

  def token() = Action.async(parse.json) { implicit request =>
    withJsonBody[AuthorityRequest] { authorityRequest: AuthorityRequest =>
      tokenService.createToken(authorityRequest) map { delegatedAuthority => Ok(Json.toJson(delegatedAuthority))}
    }
  }
}
