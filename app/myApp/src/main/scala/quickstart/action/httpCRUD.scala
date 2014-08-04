package quickstart.action

import xitrum.{Action, SkipCsrfCheck}
import xitrum.annotation.{GET, POST, PUT, DELETE, PATCH}

trait HttpCRUD extends Action {
  def respondClassNameAsText(){
    respondText(getClass)
  }
}

@GET("/httpcrud")
class GetIndex extends HttpCRUD {
  def execute() {
    log.debug("getIndex")
    respondClassNameAsText()
  }
}

@POST("/httpcrud")
class PostIndex extends HttpCRUD with SkipCsrfCheck {
  def execute() {
    log.debug("postIndex")
    respondClassNameAsText()
  }
}

@PUT("/httpcrud")
class PutIndex extends HttpCRUD with SkipCsrfCheck {
  def execute() {
    log.debug("putIndex")
    respondClassNameAsText()
  }
}

@DELETE("/httpcrud")
class DeleteIndex extends HttpCRUD with SkipCsrfCheck {
  def execute() {
    log.debug("deleteIndex")
    respondClassNameAsText()
  }
}

@PATCH("/httpcrud")
class PatchIndex extends HttpCRUD with SkipCsrfCheck {
  def execute() {
    log.debug("patchIndex")
    respondClassNameAsText()
  }
}
