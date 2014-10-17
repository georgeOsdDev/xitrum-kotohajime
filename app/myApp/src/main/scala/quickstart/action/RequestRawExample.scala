package quickstart.action

import xitrum.Action
import xitrum.annotation.GET

@GET("/requestraw")
class RequestRawIndex extends Action {
  def execute() {
    val whaleRequest = request
    log.debug("Request:" + whaleRequest.toString)
    respondText(
s"""
Request:${whaleRequest.toString}
"""
    )
  }
}

@GET("/requestbody")
class RequestBodyIndex extends Action {
  def execute() {
    val body = requestContentString
    log.debug("body:" + requestContentString)
    respondText(
s"""
body:${requestContentString}
"""
    )
  }

}
