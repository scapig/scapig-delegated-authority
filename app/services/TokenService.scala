package services

import javax.inject.{Inject, Singleton}

import config.AppContext
import models.{DelegatedAuthority, TokenRequest}
import repository.DelegatedAuthorityRepository

import scala.concurrent.Future

@Singleton
class TokenService @Inject()(delegatedAuthorityRepository: DelegatedAuthorityRepository, appContext: AppContext) {

  def createToken(tokenRequest: TokenRequest): Future[DelegatedAuthority] = {
    delegatedAuthorityRepository.save(DelegatedAuthority(tokenRequest, appContext.tokenExpiry, appContext.authorityExpiry))
  }
}
