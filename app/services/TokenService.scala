package services

import javax.inject.{Inject, Singleton}

import config.AppContext
import models.{DelegatedAuthority, AuthorityRequest}
import repository.DelegatedAuthorityRepository

import scala.concurrent.Future

@Singleton
class TokenService @Inject()(delegatedAuthorityRepository: DelegatedAuthorityRepository, appContext: AppContext) {

  def createToken(authorityRequest: AuthorityRequest): Future[DelegatedAuthority] = {
    delegatedAuthorityRepository.save(DelegatedAuthority(authorityRequest, appContext.tokenExpiry, appContext.authorityExpiry))
  }
}
