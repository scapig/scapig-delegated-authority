package models

import java.util.UUID

import org.joda.time.DateTime

case class DelegatedAuthority(clientId: String,
                              userId: String,
                              authType: AuthType.AuthType,
                              token: Token,
                              createdAt: DateTime,
                              expiresAt: DateTime,
                              id: UUID = UUID.randomUUID())

case class Token(accessToken: String,
                 refreshToken: String,
                 expiresAt: DateTime,
                 scopes: Set[String])
