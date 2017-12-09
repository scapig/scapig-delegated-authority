package repository

import models.{DelegatedAuthority, DelegatedAuthorityNotFoundException, Environment, Token}
import org.joda.time.DateTime
import org.scalatest.BeforeAndAfterEach
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import utils.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global

class DelegatedAuthorityRepositorySpec extends UnitSpec with BeforeAndAfterEach {

  val token = Token(DateTime.now().plusHours(4), Set("scope"), "accessToken", "refreshToken")
  val delegatedAuthority = DelegatedAuthority("clientId", "userId", Environment.PRODUCTION, token,
    DateTime.now(), DateTime.now().plusHours(4))

  lazy val fakeApplication: Application = new GuiceApplicationBuilder()
    .configure("mongodb.uri" -> "mongodb://localhost:27017/scapig-delegated-authority-test")
    .build()

  lazy val underTest = fakeApplication.injector.instanceOf[DelegatedAuthorityRepository]

  override def afterEach {
    await(underTest.repository).drop(failIfNotFound = false)
  }

  "createOrUpdate" should {
    "create a new delegated authority" in {
      await(underTest.save(delegatedAuthority))

      await(underTest.fetch(delegatedAuthority.id.toString())) shouldBe Some(delegatedAuthority)
    }

    "update an existing delegated authority" in {
      val updatedDelegatedAuthority = delegatedAuthority.copy(token = token.copy(accessToken = "newaccessToken"))
      await(underTest.save(delegatedAuthority))

      await(underTest.save(updatedDelegatedAuthority))

      await(underTest.fetch(delegatedAuthority.id.toString())) shouldBe Some(updatedDelegatedAuthority)
    }

  }

  "fetchByAccessToken" should {
    "return the delegated authority when it exists" in {
      await(underTest.save(delegatedAuthority))

      await(underTest.fetchByAccessToken(delegatedAuthority.token.accessToken)) shouldBe Some(delegatedAuthority)
    }

    "return None when the delegated authority does not exist" in {
      await(underTest.fetchByAccessToken("invalid")) shouldBe None
    }

  }

  "fetchByRefreshToken" should {
    "return the delegated authority when it exists" in {
      await(underTest.save(delegatedAuthority))

      await(underTest.fetchByRefreshToken(delegatedAuthority.token.refreshToken)) shouldBe delegatedAuthority
    }

    "fail with DelegatedAuthorityNotFoundException when the delegated authority does not exist" in {
      intercept[DelegatedAuthorityNotFoundException]{await(underTest.fetchByRefreshToken("invalid"))}
    }

  }

}
