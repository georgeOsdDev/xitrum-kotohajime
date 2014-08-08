package quickstart.action

import xitrum.Action
import xitrum.annotation.GET


@GET("static")
class PublicRootAction1 extends ClassNameResponder {
  def execute() {
    log.debug("PublicRootAction1")
    respondClassNameAsText()
  }
}

@GET("static/index")
class PublicRootAction2 extends ClassNameResponder {
  def execute() {
    log.debug("PublicRootAction2")
    respondClassNameAsText()
  }
}

@GET("static/index.html")
class PublicRootAction3 extends ClassNameResponder {
  def execute() {
    log.debug("PublicRootAction3")
    respondClassNameAsText()
  }
}

@GET("static/image")
class PublicRootAction4 extends ClassNameResponder {
  def execute() {
    log.debug("PublicRootAction4")
    respondClassNameAsText()
  }
}

@GET("static/image.png")
class PublicRootAction5 extends ClassNameResponder {
  def execute() {
    log.debug("PublicRootAction5")
    respondClassNameAsText()
  }
}
