package models

import java.util.UUID

import models.Generator.random
import org.joda.time.{DateTime, ReadableDuration}

import scala.concurrent.duration.FiniteDuration

case class DelegatedAuthority(clientId: String,
                              userId: String,
                              environment: Environment.Environment,
                              token: Token,
                              expiresAt: DateTime,
                              createdAt: DateTime = DateTime.now(),
                              id: UUID = UUID.randomUUID())

object DelegatedAuthority {
  def apply(authorityRequest: TokenRequest, tokenExpiry: FiniteDuration, authorityExpiry: FiniteDuration): DelegatedAuthority =
    DelegatedAuthority(authorityRequest.clientId, authorityRequest.userId, authorityRequest.environment,
      Token(DateTime.now().plus(tokenExpiry.toMillis), authorityRequest.scopes), DateTime.now().plus(authorityExpiry.toMillis))
}

case class Token(expiresAt: DateTime,
                 scopes: Set[String],
                 accessToken: String = random(),
                 refreshToken: String = random())

object Generator {
  def random(): String = UUID.randomUUID().toString.replaceAll("-", "")
}