package models

import java.util.UUID

import models.Generator.random
import org.joda.time.{DateTime, ReadableDuration}

import scala.concurrent.duration.FiniteDuration

case class DelegatedAuthority(clientId: String,
                              userId: String,
                              authType: AuthType.AuthType,
                              token: Token,
                              expiresAt: DateTime,
                              createdAt: DateTime = DateTime.now(),
                              id: UUID = UUID.randomUUID())

object DelegatedAuthority {
  def apply(authorityRequest: AuthorityRequest, tokenExpiry: FiniteDuration, authorityExpiry: FiniteDuration): DelegatedAuthority =
    DelegatedAuthority(authorityRequest.clientId, authorityRequest.userId, authorityRequest.authType,
      Token(DateTime.now().plus(tokenExpiry.toMillis), authorityRequest.scopes), DateTime.now().plus(authorityExpiry.toMillis))
}

case class Token(expiresAt: DateTime,
                 scopes: Set[String],
                 accessToken: String = random(),
                 refreshToken: String = random())

object Generator {
  def random(): String = UUID.randomUUID().toString.replaceAll("-", "")
}