package models

case class TokenRequest(clientId: String,
                        userId: String,
                        scopes: Set[String],
                        environment: Environment.Environment) {

  require(!clientId.trim.isEmpty, "clientId cannot be empty")
  require(!userId.trim.isEmpty, "userId cannot be empty")
  require(scopes.nonEmpty, "scopes cannot be empty")
  scopes.foreach { scope =>
    require(!scope.trim.isEmpty, "scope cannot be empty")
  }
}

case class RefreshTokenRequest(clientId: String,
                               refreshToken: String)

object Environment extends Enumeration {
  type Environment = Value
  val PRODUCTION, SANDBOX = Value
}

case class TokenResponse(access_token: String,
                         refresh_token: String,
                         expires_in: Int,
                         scope: String,
                         token_type: String = "bearer")

object TokenResponse {
  def apply(token: Token, expiresIn: Int): TokenResponse = TokenResponse(token.accessToken, token.refreshToken, expiresIn, token.scopes.mkString(" "))
}