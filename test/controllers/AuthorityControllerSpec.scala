package controllers

import models._
import org.joda.time.DateTime
import org.mockito.BDDMockito.given
import org.mockito.Matchers.any
import org.mockito.Mockito.verifyZeroInteractions
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{FakeRequest, Helpers}
import services.AuthorityService
import utils.UnitSpec
import models.JsonFormatters._
import play.mvc.Http.Status.OK

import scala.concurrent.Future.successful

class AuthorityControllerSpec extends UnitSpec with MockitoSugar {

  val token = Token(DateTime.now().plusHours(4), Set("scope"), "accessToken", "refreshToken")
  val delegatedAuthority = DelegatedAuthority("clientId", "userId", AuthType.PRODUCTION, token,
    DateTime.now(), DateTime.now().plusHours(4))

  val authorityRequest = AuthorityRequest("clientId", "userId", Set("scope"), AuthType.PRODUCTION)

  trait Setup {
    val mockTokenService: AuthorityService = mock[AuthorityService]
    val underTest = new AuthorityController(Helpers.stubControllerComponents(), mockTokenService)

    val request = FakeRequest()

    given(mockTokenService.createToken(any())).willReturn(successful(delegatedAuthority))
  }

  "createAuthority" should {

    "succeed with a 200 with the delegated authority when payload is valid and service responds successfully" in new Setup {

      val result: Result = await(underTest.createAuthority()(request.withBody(Json.toJson(authorityRequest))))

      status(result) shouldBe Status.OK
      jsonBodyOf(result).as[DelegatedAuthority] shouldBe delegatedAuthority
    }

    "fail with a 400 (Bad Request) when the json payload is invalid for the request" in new Setup {

      val body = """{ "invalid": "json" }"""

      val result: Result = await(underTest.createAuthority()(request.withBody(Json.parse(body))))

      status(result) shouldBe Status.BAD_REQUEST
      jsonBodyOf(result) shouldBe Json.parse("""{"code":"INVALID_REQUEST","message":"scopes is required"}""")
      verifyZeroInteractions(mockTokenService)
    }
  }

  "fetchByAccessToken" should {
    "return 200 (Ok) with the authority when it exists" in new Setup {
      given(mockTokenService.fetchByAccessToken(delegatedAuthority.token.accessToken)).willReturn(successful(Some(delegatedAuthority)))

      val result = await(underTest.fetchByAccessToken(delegatedAuthority.token.accessToken)(request))

      status(result) shouldBe OK
      Json.parse(bodyOf(result)) shouldBe Json.toJson(delegatedAuthority)
    }

    "return 404 (NotFound) when the authority does not exist" in new Setup {
      given(mockTokenService.fetchByAccessToken("invalidToken")).willReturn(successful(None))

      val result = await(underTest.fetchByAccessToken("invalidToken")(request))

      status(result) shouldBe Status.NOT_FOUND
      Json.parse(bodyOf(result)) shouldBe Json.parse("""{"code":"NOT_FOUND", "message": "Authority not found"}""")
    }

  }
}
