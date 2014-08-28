package quickstart.action

import xitrum.Action
import xitrum.annotation.GET


@GET("respond/view1")
class RespondViewExample1 extends Action {
  def execute() {
    respondView()
  }
}

@GET("respond/view2")
class RespondViewExample2 extends Action {
  def execute() {
    respondView[RespondViewExample1]()
  }
}


trait CustomLayout extends Action {
  override def layout = renderViewNoLayout[CustomLayout]()
}

@GET("respond/view3")
class RespondViewExample3 extends CustomLayout {
  def execute() {
    respondView()
  }
}

@GET("respond/fragment1")
class RespondFragmentExample1 extends CustomLayout {
  def execute() {
    respondView()
  }
}

@GET("respond/fragment2")
class RespondFragmentExample2 extends CustomLayout {
  def execute() {
    respondView()
  }
}
