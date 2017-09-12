package controllers

import models._
import org.joda.time.DateTime
import org.mockito.BDDMockito.given
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, verifyZeroInteractions}
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{FakeRequest, Helpers}
import services.TokenService
import utils.UnitSpec
import models.JsonFormatters._

import scala.concurrent.Future.successful

class TokenControllerSpec extends UnitSpec with MockitoSugar {

  val token = Token(DateTime.now().plusHours(4), Set("scope"), "accessToken", "refreshToken")
  val delegatedAuthority = DelegatedAuthority("clientId", "userId", AuthType.PRODUCTION, token,
    DateTime.now(), DateTime.now().plusHours(4))

  val tokenRequest = TokenRequest("clientId", "userId", Set("scope"), AuthType.PRODUCTION)

  trait Setup {
    val mockTokenService: TokenService = mock[TokenService]
    val underTest = new TokenController(Helpers.stubControllerComponents(), mockTokenService)

    val request = FakeRequest()

    given(mockTokenService.createToken(any())).willReturn(successful(delegatedAuthority))
  }

  "token" should {

    "succeed with a 200 with the delegated authority when payload is valid and service responds successfully" in new Setup {

      val result: Result = await(underTest.token()(request.withBody(Json.toJson(tokenRequest))))

      status(result) shouldBe Status.OK
      jsonBodyOf(result).as[DelegatedAuthority] shouldBe delegatedAuthority
    }

    "fail with a 400 (Bad Request) when the json payload is invalid for the request" in new Setup {

      val body = """{ "invalid": "json" }"""

      val result: Result = await(underTest.token()(request.withBody(Json.parse(body))))

      status(result) shouldBe Status.BAD_REQUEST
      jsonBodyOf(result) shouldBe Json.parse("""{"code":"INVALID_REQUEST","message":"scopes is required"}""")
      verifyZeroInteractions(mockTokenService)
    }
  }
}
