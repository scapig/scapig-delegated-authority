package connectors

import javax.inject.Inject

import config.AppContext
import play.api.libs.ws.WSClient

class ApplicationConnector @Inject()(appContext: AppContext, wsClient: WSClient) {

  val serviceUrl = appContext.serviceUrl("application")

  def fetchApplication(applicationId: String): Unit = {

  }
}
