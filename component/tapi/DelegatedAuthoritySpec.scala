package tapi

import models.{DelegatedAuthority, Environment, TokenRequest, TokenResponse}
import play.api.libs.json.Json
import play.mvc.Http.HeaderNames.CONTENT_TYPE

import scalaj.http.Http
import models.JsonFormatters._
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
}
