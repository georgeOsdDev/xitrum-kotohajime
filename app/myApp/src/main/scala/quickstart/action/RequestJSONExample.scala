package quickstart.action

import xitrum.Action
import xitrum.annotation.GET

@GET("/requestbodyjson")
class RequestBodyJsonIndex extends Action {
  def execute() {
    val bodyJson = requestContentJson[Map[String, Any]]
    log.debug("body as Json:" + bodyJson)

    bodyJson match {
      case Some(v) => log.debug("Successfully parsed")
      case None =>    log.debug("Failed to parse")
    }
    respondText(
s"""
body as Json:${bodyJson}
"""
    )
  }
}
