package models

case class TokenRequest(clientId: String,
                        clientSecret: String,
                        userId: String,
                        code: String,
                        scopes: Set[String],
                        redirectUri: String,
                        authType: AuthType.AuthType) {

  require(!clientId.trim.isEmpty, "clientId cannot be empty")
  require(!clientSecret.trim.isEmpty, "clientSecret cannot be empty")
  require(!userId.trim.isEmpty, "userId cannot be empty")
  require(scopes.nonEmpty, "scopes cannot be empty")
  require(!code.trim.isEmpty, "code cannot be empty")
  require(!redirectUri.trim.isEmpty, "redirectUri cannot be empty")
  scopes.foreach { scope =>
    require(!scope.trim.isEmpty, "scope cannot be empty")
  }
}

object AuthType extends Enumeration {
  type AuthType = Value
  val PRODUCTION, SANDBOX = Value
}
