package quickstart.action

import xitrum.Action
import xitrum.annotation.GET

@GET("/redirect")
class RedirectIndex extends Action {
  def execute() {
    log.debug("RedirectIndex")
    log.debug(textParams.toString)
    redirectTo[RedirectedPage]()
  }
}

@GET("/redirected")
class RedirectedPage extends Action {
  def execute() {
    log.debug("RedirectedPage")
    log.debug(textParams.toString)
    respondText(getClass)
  }
}
