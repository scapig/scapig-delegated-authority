package services

import javax.inject.{Inject, Singleton}

import config.AppContext
import models.{DelegatedAuthority, DelegatedAuthorityNotFoundException, RefreshTokenRequest, TokenRequest}
import repository.DelegatedAuthorityRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuthorityService @Inject()(delegatedAuthorityRepository: DelegatedAuthorityRepository, appContext: AppContext) {

  def createAuthority(tokenRequest: TokenRequest): Future[DelegatedAuthority] = {
    delegatedAuthorityRepository.save(DelegatedAuthority(tokenRequest, appContext.tokenExpiry, appContext.authorityExpiry))
  }

  def refreshAuthority(refreshTokenRequest: RefreshTokenRequest): Future[DelegatedAuthority] = {
    for {
      delegatedAuthority <- delegatedAuthorityRepository.fetchByRefreshToken(refreshTokenRequest.refreshToken)
      _ = if(delegatedAuthority.clientId != refreshTokenRequest.clientId) throw DelegatedAuthorityNotFoundException()
      refreshedAuthority <- delegatedAuthorityRepository.save(delegatedAuthority.refresh(appContext.tokenExpiry))
    } yield refreshedAuthority
  }

  def fetchByAccessToken(accessToken: String) = delegatedAuthorityRepository.fetchByAccessToken(accessToken)
}
