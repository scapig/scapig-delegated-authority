package config

import javax.inject.{Inject, Singleton}

import play.api.Configuration

import scala.concurrent.duration.DurationDouble

@Singleton
class AppContext @Inject()(configuration: Configuration) {

  val tokenExpiry = configuration.get[Int]("expiryTime.accessTokenInHours") hours
  val authorityExpiry = configuration.get[Int]("expiryTime.delegatedAuthorityInDays") days

  def serviceUrl(serviceName: String): String = {
    val method = configuration.getOptional[String](s"services.$serviceName.method").getOrElse("http")
    val host = configuration.get[String](s"services.$serviceName.host")
    val port = configuration.get[String](s"services.$serviceName.host")
    s"$method://$host:$port"
  }
}
