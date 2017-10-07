package services

import config.AppContext
import models._
import org.joda.time.{DateTime, DateTimeUtils}
import org.mockito.BDDMockito.given
import org.mockito.{BDDMockito, Matchers, Mockito}
import org.mockito.Mockito.{verify, when}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.mockito.MockitoSugar
import repository.DelegatedAuthorityRepository
import utils.UnitSpec

import scala.concurrent.Future
import scala.concurrent.Future.{failed, successful}
import scala.concurrent.duration.DurationLong

class AuthorityServiceSpec extends UnitSpec with MockitoSugar with BeforeAndAfterAll {

  val delegatedAuthority = DelegatedAuthority("clientId", "userId", Environment.PRODUCTION, Token(DateTime.now(), Set("scope1")), DateTime.now())

  trait Setup {
    val delegatedAuthorityRepository = mock[DelegatedAuthorityRepository]
    val appContext = mock[AppContext]

    val underTest = new AuthorityService(delegatedAuthorityRepository, appContext)

    when(appContext.authorityExpiry).thenReturn(365.days)
    when(appContext.tokenExpiry).thenReturn(4.hours)

    when(delegatedAuthorityRepository.save(Matchers.any())).thenAnswer(returnSame)
  }

  override def beforeAll {
    DateTimeUtils.setCurrentMillisFixed(1000)
  }

  override def afterAll() {
    DateTimeUtils.setCurrentMillisSystem()
  }

  "createToken" should {
    val authorityRequest = TokenRequest("clientId", "userId", Set("scope1"), Environment.PRODUCTION)

    "create the token and save it in the repository" in new Setup {

      val result = await(underTest.createAuthority(authorityRequest))

      result shouldBe DelegatedAuthority("clientId", "userId", Environment.PRODUCTION, result.token, DateTime.now().plusDays(365), DateTime.now(), result.id)
      result.token shouldBe Token(DateTime.now().plusHours(4), authorityRequest.scopes, result.token.accessToken, result.token.refreshToken)
    }

    "fail when the repository fails" in new Setup {
      given(delegatedAuthorityRepository.save(Matchers.any())).willReturn(failed(new RuntimeException("test error")))

      intercept[RuntimeException]{await(underTest.createAuthority(authorityRequest))}
    }
  }

  "refreshAuthority" should {
    val refreshTokenRequest = RefreshTokenRequest("clientId", "refreshToken")

    "refresh the token and save it in the repository" in new Setup {
      given(delegatedAuthorityRepository.fetchByRefreshToken(refreshTokenRequest.refreshToken)).willReturn(successful(delegatedAuthority))

      val result = await(underTest.refreshAuthority(refreshTokenRequest))

      result shouldBe delegatedAuthority.copy(token = result.token)
      result.token.accessToken should not be delegatedAuthority.token.accessToken
      result.token.refreshToken should not be delegatedAuthority.token.refreshToken

      verify(delegatedAuthorityRepository).save(result)
    }

    "fail when the repository fails" in new Setup {
      given(delegatedAuthorityRepository.save(Matchers.any())).willReturn(failed(new RuntimeException("test error")))

      intercept[RuntimeException]{await(underTest.refreshAuthority(refreshTokenRequest))}
    }

    "fail with DelegatedAuthorityNotFoundException when there is no delegated-authority for the refreshToken" in new Setup {
      given(delegatedAuthorityRepository.fetchByRefreshToken(refreshTokenRequest.refreshToken)).willReturn(failed(DelegatedAuthorityNotFoundException()))

      intercept[DelegatedAuthorityNotFoundException]{await(underTest.refreshAuthority(refreshTokenRequest))}
    }


    "fail with DelegatedAuthorityNotFoundException when the delegated-authority clientId does not match refresh-request clientId" in new Setup {
      val refreshRequestInvalidClientId = refreshTokenRequest.copy(clientId = "invalidClientId")

      given(delegatedAuthorityRepository.fetchByRefreshToken(refreshRequestInvalidClientId.refreshToken)).willReturn(successful(delegatedAuthority))

      intercept[DelegatedAuthorityNotFoundException]{await(underTest.refreshAuthority(refreshRequestInvalidClientId))}
    }

  }

  "fetchByAccessToken" should {
    "return the token from the repository" in new Setup {
      given(delegatedAuthorityRepository.fetchByAccessToken(delegatedAuthority.token.accessToken)).willReturn(successful(Some(delegatedAuthority)))

      val result = await(underTest.fetchByAccessToken(delegatedAuthority.token.accessToken))

      result shouldBe Some(delegatedAuthority)
    }

    "propagate the exception when the repository failed" in new Setup {
      given(delegatedAuthorityRepository.fetchByAccessToken(delegatedAuthority.token.accessToken)).willReturn(failed(new RuntimeException("test error")))

      intercept[RuntimeException]{await(underTest.fetchByAccessToken(delegatedAuthority.token.accessToken))}
    }
  }
}
