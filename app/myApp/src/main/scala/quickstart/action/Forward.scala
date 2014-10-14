package quickstart.action

import xitrum.Action
import xitrum.annotation.GET

@GET("/forward")
class ForwardIndex extends Action {
  def execute() {
    log.debug("ForwardIndex")
    log.debug(textParams.toString)
    forwardTo[ForwardedPage]()
  }
}

@GET("/forwarded")
class ForwardedPage extends Action {
  def execute() {
    log.debug("ForwardedPage")
    log.debug(textParams.toString)
    respondText(getClass)
  }
}
