package repository

import javax.inject.{Inject, Singleton}

import models.JsonFormatters._
import models.{DelegatedAuthority, DelegatedAuthorityNotFoundException}
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.UpdateWriteResult
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import reactivemongo.play.json._

@Singleton
class DelegatedAuthorityRepository @Inject()(val reactiveMongoApi: ReactiveMongoApi)  {

  def repository: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection[JSONCollection]("scapig-delegated-authority"))

  def save(delegatedAuthority: DelegatedAuthority): Future[DelegatedAuthority] = {
    repository.flatMap(collection =>
      collection.update(
        Json.obj("id"-> delegatedAuthority.id.toString()), delegatedAuthority, upsert = true) map {
        case result: UpdateWriteResult if result.ok => delegatedAuthority
        case error => throw new RuntimeException(s"Failed to save delegated-authority ${error.errmsg}")
      }
    )
  }

  def fetch(id: String): Future[Option[DelegatedAuthority]] = {
    repository.flatMap(collection =>
      collection.find(Json.obj("id"-> id)).one[DelegatedAuthority]
    )
  }

  def fetchByAccessToken(accessToken: String): Future[Option[DelegatedAuthority]] = {
    repository.flatMap(collection =>
      collection.find(Json.obj("token.accessToken"-> accessToken)).one[DelegatedAuthority]
    )
  }

  def fetchByRefreshToken(refreshToken: String): Future[DelegatedAuthority] = {
    repository.flatMap(collection =>
      collection.find(Json.obj("token.refreshToken"-> refreshToken)).one[DelegatedAuthority] map {
        case Some(delegatedAuthority) => delegatedAuthority
        case _ => throw DelegatedAuthorityNotFoundException()
      }
    )
  }

}
