package services

import config.AppContext
import models.{AuthType, DelegatedAuthority, Token, TokenRequest}
import org.joda.time.{DateTime, DateTimeUtils}
import org.mockito.BDDMockito.given
import org.mockito.{BDDMockito, Matchers}
import org.mockito.Mockito.when
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.mockito.MockitoSugar
import repository.DelegatedAuthorityRepository
import utils.UnitSpec

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong

class TokenServiceSpec extends UnitSpec with MockitoSugar with BeforeAndAfterAll {

  trait Setup {
    val delegatedAuthorityRepository = mock[DelegatedAuthorityRepository]
    val appContext = mock[AppContext]

    val underTest = new TokenService(delegatedAuthorityRepository, appContext)

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

  val tokenRequest = TokenRequest("clientId", "userId", Set("scope1"), AuthType.PRODUCTION)

  "createToken" should {
    "create the token and save it in the repository" in new Setup {

      val result = await(underTest.createToken(tokenRequest))

      result shouldBe DelegatedAuthority("clientId", "userId", AuthType.PRODUCTION, result.token, DateTime.now().plusDays(365), DateTime.now(), result.id)
      result.token shouldBe Token(DateTime.now().plusHours(4), tokenRequest.scopes, result.token.accessToken, result.token.refreshToken)
    }

    "fail when the repository fails" in new Setup {
      given(delegatedAuthorityRepository.save(Matchers.any())).willReturn(Future.failed(new RuntimeException("test error")))

      intercept[RuntimeException]{await(underTest.createToken(tokenRequest))}
    }
  }
}
