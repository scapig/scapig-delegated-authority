package models

case class AuthorityRequest(clientId: String,
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

object Environment extends Enumeration {
  type Environment = Value
  val PRODUCTION, SANDBOX = Value
}
