package controllers

import config.AppContext
import models.{TokenRequest, _}
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

import scala.concurrent.Future
import scala.concurrent.Future.{failed, successful}
import scala.concurrent.duration._

class AuthorityControllerSpec extends UnitSpec with MockitoSugar {

  val token = Token(DateTime.now().plusHours(4), Set("scope"), "accessToken", "refreshToken")
  val delegatedAuthority = DelegatedAuthority("clientId", "userId", Environment.PRODUCTION, token,
    DateTime.now(), DateTime.now().plusHours(4))

  val tokenRequest = TokenRequest("clientId", "userId", Set("scope"), Environment.PRODUCTION)
  val tokenResponse = TokenResponse(delegatedAuthority.token, 14400)

  val refreshTokenRequest = RefreshTokenRequest("clientId", "refreshToken")
  val refreshedToken = token.copy(accessToken = "newAccessToken", refreshToken = "newRefreshToken")
  val refreshedDelegatedAuthority = delegatedAuthority.copy(token = refreshedToken)
  val refreshedTokenResponse = TokenResponse(refreshedToken, 14400)

  trait Setup {
    val mockTokenService: AuthorityService = mock[AuthorityService]
    val mockAppContext = mock[AppContext]
    val underTest = new AuthorityController(Helpers.stubControllerComponents(), mockTokenService, mockAppContext)

    val request = FakeRequest()

    given(mockAppContext.tokenExpiry).willReturn(4 hours)
    given(mockTokenService.createAuthority(tokenRequest)).willReturn(successful(delegatedAuthority))
    given(mockTokenService.refreshAuthority(refreshTokenRequest)).willReturn(successful(refreshedDelegatedAuthority))
  }

  "createToken" should {

    "succeed with a 200 with the token when payload is valid and service responds successfully" in new Setup {

      val result: Result = await(underTest.createToken()(request.withBody(Json.toJson(tokenRequest))))

      status(result) shouldBe Status.OK
      jsonBodyOf(result).as[TokenResponse] shouldBe tokenResponse
    }

    "fail with a 400 (Bad Request) when the json payload is invalid for the request" in new Setup {

      val body = """{ "invalid": "json" }"""

      val result: Result = await(underTest.createToken()(request.withBody(Json.parse(body))))

      status(result) shouldBe Status.BAD_REQUEST
      verifyZeroInteractions(mockTokenService)
    }
  }

  "refreshToken" should {

    "succeed with a 200 with the refreshed token when payload is valid and service responds successfully" in new Setup {

      val result: Result = await(underTest.refreshToken()(request.withBody(Json.toJson(refreshTokenRequest))))

      status(result) shouldBe Status.OK
      jsonBodyOf(result).as[TokenResponse] shouldBe refreshedTokenResponse
    }

    "fail with a 400 (Bad Request) and code = INVALID_REFRESH_TOKEN when refreshing the token fails with DelegatedAuthorityNotFoundException" in new Setup {

      given(mockTokenService.refreshAuthority(refreshTokenRequest)).willReturn(failed(new DelegatedAuthorityNotFoundException()))

      val result: Result = await(underTest.refreshToken()(request.withBody(Json.toJson(refreshTokenRequest))))

      status(result) shouldBe Status.BAD_REQUEST
      jsonBodyOf(result) shouldBe Json.obj("code" -> "INVALID_REFRESH_TOKEN", "message" -> "Invalid refresh token")
    }

    "fail with a 400 (Bad Request) when the json payload is invalid for the request" in new Setup {

      val body = """{ "invalid": "json" }"""

      val result: Result = await(underTest.refreshToken()(request.withBody(Json.parse(body))))

      status(result) shouldBe Status.BAD_REQUEST
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
