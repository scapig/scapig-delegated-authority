package models

case class AuthorityRequest(clientId: String,
                            userId: String,
                            scopes: Set[String],
                            authType: AuthType.AuthType) {

  require(!clientId.trim.isEmpty, "clientId cannot be empty")
  require(!userId.trim.isEmpty, "userId cannot be empty")
  require(scopes.nonEmpty, "scopes cannot be empty")
  scopes.foreach { scope =>
    require(!scope.trim.isEmpty, "scope cannot be empty")
  }
}

object AuthType extends Enumeration {
  type AuthType = Value
  val PRODUCTION, SANDBOX = Value
}
