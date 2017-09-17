package services

import javax.inject.{Inject, Singleton}

import config.AppContext
import models.{TokenRequest, DelegatedAuthority}
import repository.DelegatedAuthorityRepository

import scala.concurrent.Future

@Singleton
class AuthorityService @Inject()(delegatedAuthorityRepository: DelegatedAuthorityRepository, appContext: AppContext) {

  def createAuthority(tokenRequest: TokenRequest): Future[DelegatedAuthority] = {
    delegatedAuthorityRepository.save(DelegatedAuthority(tokenRequest, appContext.tokenExpiry, appContext.authorityExpiry))
  }

  def fetchByAccessToken(accessToken: String) = delegatedAuthorityRepository.fetchByAccessToken(accessToken)
}
