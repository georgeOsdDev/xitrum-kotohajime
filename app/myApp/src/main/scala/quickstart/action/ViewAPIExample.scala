package quickstart.action

import xitrum.Action
import xitrum.annotation.GET

case class Person(name:String,age:Int)

@GET("/viewapi/at")
class AtExample extends Action {
  def execute() {

    at("key1") = "value"

    at("taro") = new Person("Taro", 10)

    at("jiro") = new Person("Jiro", 20)

    at("serializable") = Map("key" -> "val")

    respondView()
  }
}

@GET("/viewapi/js")
class JsExample extends DefaultLayout {
  def execute() {

    flash("Hello World")

    jsAddToView("""console.log("Hello js Add To View")""")
    jsAddToView("""console.log("Hello js Add To View Again!")""")
    //jsAddToView("""xitrum.flash = function(msg){alert("This is custome flash:" + msg);}""")
    respondView()
  }
}
