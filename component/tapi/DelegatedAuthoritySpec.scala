package tapi

import models.{Environment, AuthorityRequest, DelegatedAuthority}
import play.api.libs.json.Json
import play.mvc.Http.HeaderNames.CONTENT_TYPE

import scalaj.http.Http
import models.JsonFormatters._
import play.mvc.Http.Status.OK

class DelegatedAuthoritySpec extends BaseFeatureSpec {

  val authorityRequest = AuthorityRequest("clientId", "userId", Set("scope1"), Environment.PRODUCTION)

  feature("create and fetch delegated authority") {

    scenario("happy path") {

      When("An authority create request is received")
      val createdResponse = Http(s"$serviceUrl/authority")
        .headers(Seq(CONTENT_TYPE -> "application/json"))
        .postData(Json.toJson(authorityRequest).toString()).asString

      Then("I receive a 200 (ok) with the token")
      createdResponse.code shouldBe OK
      val createdAuthority = Json.parse(createdResponse.body).as[DelegatedAuthority]

      And("The authority can be retrieved")
      val fetchResponse = Http(s"$serviceUrl/authority?accessToken=${createdAuthority.token.accessToken}").asString
      fetchResponse.code shouldBe OK
      Json.parse(fetchResponse.body) shouldBe Json.toJson(createdAuthority)
    }
  }
}
