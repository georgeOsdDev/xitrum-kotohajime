package quickstart.action

import xitrum.{Action, SkipCsrfCheck}
import xitrum.annotation.{GET, POST, PUT, DELETE, PATCH}

trait HttpCRUD extends Action {
  def respondClassNameAsText(){
    respondText(getClass)
  }
}

@GET("/httpcrud")
class getIndex extends HttpCRUD {
  def execute() {
    log.debug("getIndex")
    respondClassNameAsText()
  }
}

@POST("/httpcrud")
class postIndex extends HttpCRUD with SkipCsrfCheck {
  def execute() {
    log.debug("postIndex")
    respondClassNameAsText()
  }
}

@PUT("/httpcrud")
class putIndex extends HttpCRUD with SkipCsrfCheck {
  def execute() {
    log.debug("putIndex")
    respondClassNameAsText()
  }
}

@DELETE("/httpcrud")
class deleteIndex extends HttpCRUD with SkipCsrfCheck {
  def execute() {
    log.debug("deleteIndex")
    respondClassNameAsText()
  }
}

@PATCH("/httpcrud")
class patchIndex extends HttpCRUD with SkipCsrfCheck {
  def execute() {
    log.debug("patchIndex")
    respondClassNameAsText()
  }
}
