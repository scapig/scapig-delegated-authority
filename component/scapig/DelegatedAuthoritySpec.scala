package scapig

import models._
import play.api.libs.json.Json
import play.mvc.Http.HeaderNames.CONTENT_TYPE

import scalaj.http.Http
import models.JsonFormatters._
import play.api.http.Status
import play.mvc.Http.Status.OK

class DelegatedAuthoritySpec extends BaseFeatureSpec {

  val tokenRequest = TokenRequest("clientId", "userId", Set("scope1"), Environment.PRODUCTION)

  feature("create and fetch delegated authority") {

    scenario("happy path") {

      When("An token create request is received")
      val createdResponse = Http(s"$serviceUrl/token")
        .headers(Seq(CONTENT_TYPE -> "application/json"))
        .postData(Json.toJson(tokenRequest).toString()).asString

      Then("I receive a 200 (ok) with the token")
      createdResponse.code shouldBe OK
      val createdToken = Json.parse(createdResponse.body).as[TokenResponse]

      And("The authority can be retrieved")
      val fetchResponse = Http(s"$serviceUrl/authority?accessToken=${createdToken.access_token}").asString
      fetchResponse.code shouldBe OK
    }
  }

  feature("refresh token") {

    scenario("happy path") {

      Given("a delegated authority")
      val createTokenResponse = Http(s"$serviceUrl/token")
        .headers(Seq(CONTENT_TYPE -> "application/json"))
        .postData(Json.toJson(tokenRequest).toString()).asString
      val createdToken = Json.parse(createTokenResponse.body).as[TokenResponse]

      When("I refresh the token")
      val refreshResponse = Http(s"$serviceUrl/token/refresh")
        .headers(Seq(CONTENT_TYPE -> "application/json"))
        .postData(Json.toJson(RefreshTokenRequest("clientId", createdToken.refresh_token)).toString()).asString

      Then("I receive a 200 (ok) with the refreshed token")
      refreshResponse.code shouldBe OK
      val refreshedToken = Json.parse(refreshResponse.body).as[TokenResponse]
      refreshedToken should not be createdToken

      And("The refreshed token is active")
      val fetchRefreshedToken = Http(s"$serviceUrl/authority?accessToken=${refreshedToken.access_token}").asString
      fetchRefreshedToken.code shouldBe OK

      And("The original token is now inactive")
      val fetchOriginalToken = Http(s"$serviceUrl/authority?accessToken=${createdToken.access_token}").asString
      fetchOriginalToken.code shouldBe Status.NOT_FOUND
    }
  }
}