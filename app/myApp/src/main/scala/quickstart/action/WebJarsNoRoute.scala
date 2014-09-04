package quickstart.action

import xitrum.Action
import xitrum.annotation.GET

@GET("/webjars/underscorejs/1.6.0/underscore.js", "/webjars/underscorejs/1.7.0/underscore.js")
class WebJarsNoRoute extends Action {
  def execute() {
    respondText("underscorejs-1.7.0 is not found")
  }
}
