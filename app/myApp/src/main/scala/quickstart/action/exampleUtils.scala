package quickstart.action

import xitrum.{Action}

trait ClassNameResponder extends Action {
  def respondClassNameAsText(){
    respondText(getClass)
  }
}
