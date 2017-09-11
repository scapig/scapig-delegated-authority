package connectors

import play.api.inject.guice.{GuiceApplicationBuilder, GuiceApplicationLoader}
import play.api.libs.ws.WSClient
import utils.UnitSpec

class ApplicationConnectorSpec extends UnitSpec {

  val application = new GuiceApplicationBuilder().build()

  trait Setup {
    val applicationConnector = application.injector.instanceOf[ApplicationConnector]
  }

  "fetchApplication" should {
    "return the application" in new Setup {
    }
  }
}
