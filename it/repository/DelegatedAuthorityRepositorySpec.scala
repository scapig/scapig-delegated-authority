package repository

import models.{AuthType, DelegatedAuthority, Token}
import org.joda.time.DateTime
import org.scalatest.BeforeAndAfterEach
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import utils.UnitSpec

class DelegatedAuthorityRepositorySpec extends UnitSpec with BeforeAndAfterEach {

  val token = Token("accessToken", "refreshToken", DateTime.now().plusHours(4), Set("scope"))
  val delegatedAuthority = DelegatedAuthority("clientId", "userId", AuthType.PRODUCTION, token,
    DateTime.now(), DateTime.now().plusHours(4))

  lazy val fakeApplication: Application = new GuiceApplicationBuilder()
    .configure("mongodb.uri" -> "mongodb://localhost:27017/tapi-delegated-authority-test")
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
      val updatedDelegatedAuthority = delegatedAuthority.copy(token = token.copy("newaccessToken"))
      await(underTest.save(delegatedAuthority))

      await(underTest.save(updatedDelegatedAuthority))

      await(underTest.fetch(delegatedAuthority.id.toString())) shouldBe Some(updatedDelegatedAuthority)
    }

  }
}
