package quickstart.action

import xitrum.Action
import xitrum.annotation.GET


@GET("respond/html")
class RespondExample1 extends ClassNameResponder {
  def execute() {
    respondHtml("""
<html>
    <head>
      <script src="//code.jquery.com/jquery-2.1.1.min.js"></script>
      <script src="/respond/js"></script>
      <script src="/respond/jsonp"></script>
      <script src="/respond/jsonptext"></script>
    </head>
    <body>
      <p>This is respondHtml</p>
      <script>
        $.ajax("/respond/json")
        .done(function(d){
          console.log("Response from respondJson")
          console.log(d);
        });
        $.ajax("/respond/jsontext")
        .done(function(d){
          console.log("Response from respondJsonText")
          console.log(d);
        });
        </script>
    </body>
</html>
    """)
  }
}

@GET("respond/js")
class RespondExample2 extends ClassNameResponder {
  def execute() {
    val jsText = "function myCallback(x){console.log("This is Callback for jsonP"); console.log(x);}"
    respondJs(jsText)
  }
}

@GET("respond/json")
class RespondExample3 extends ClassNameResponder {
  def execute() {
    val jsonObj = Map[String, Any](
                    "key1" -> "this is json",
                    "key2" -> List("x","y",true),
                    "key3" -> Map("nest" -> "foo")
                  )
    respondJson(jsonObj)
  }
}

@GET("respond/jsontext")
class RespondExample4 extends ClassNameResponder {
  def execute() {
    val jsonText = """
                   {"key1":"this is jsonText","key2":[1,2,3,true]}
                   """
    respondJsonText(jsonText)
  }
}

@GET("respond/jsonp")
class RespondExample5 extends ClassNameResponder {
  def execute() {
    val jsonpObj = Map("key" -> "this is jsonP","key2":[1,2,3,true])
    respondJsonP(jsonpObj, "myCallback")
  }
}

@GET("respond/jsonptext")
class RespondExample6 extends ClassNameResponder {
  def execute() {
    val jsonptext = """
                    {"key1":"this is jsonPText","key2":[1,2,3,true]}
                    """
    respondJsonPText(jsonptext,"myCallback")
  }
}
