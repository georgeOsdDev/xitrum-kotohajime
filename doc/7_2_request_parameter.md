# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 7. リクエストとスコープ:

今回はリクエストパラメーターの扱い方を取り上げたいと思います。

公式ドキュメントは以下のページが参考になります。
 * [スコープ](http://xitrum-framework.github.io/guide/3.18/ja/scopes.html)
 * [リクエストコンテンツの取得](http://xitrum-framework.github.io/guide/3.18/ja/restful.html#id6)

### 7-2. リクエストパラメーター

#### 7-2-1. リクエストパラメータにアクセスする

XitrumのActionで扱えるリクエストパラメータは以下の通りです。

> リクエストパラメーターには2種類あります:

> テキストパラメータ
> ファイルアップロードパラメーター（バイナリー）
> テキストパラメーターは ```scala.collection.mutable.Map[String, List[String]]``` の型をとる3種類があります:

> queryParams: URL内の?以降で指定されたパラメーター 例: http://example.com/blah?x=1&y=2
> bodyTextParams: POSTリクエストのbodyで指定されたパラメーター
> pathParams: URL内に含まれるパラメーター 例: GET("articles/:id/:title")
> これらのパラメーターは上記の順番で、 textParams としてマージされます。 （後からマージされるパラメーターは上書きとなります。）

> bodyFileParams は ```scala.collection.mutable.Map[String, List[ FileUpload ]]``` の型をとります。

今回は、テキストパラメータについてのみ試します。
テキストパラメータは、``param``および、``paramo``メソッドで取得することができます。

``param``で指定したキーが存在しない場合、Xitrumが自動で``400 Bad Request``をレスポンスします。
``paramo``は指定したキーをOption型として取得し、存在しない場合はNoneとなります

また、``bodyTextParams``については、POST、PUT，PATCHメソッドの場合のみ取得されます。


##### RequestParamExample.scala

```scala
@GET("/requestparam/:path1/:path2")
@POST("/requestparam/:path1/:path2")
class RequestParamIndex extends Action with SkipCsrfCheck {
  def execute() {

    // From path param
    val path1  = param("path1")
    val path2  = param("path2")
    log.debug("path1"+path1)
    log.debug("path2"+path2)

    // From query param
    val query1  = param("query1")
    val query2  = param("query2")
    log.debug("query1"+query1)
    log.debug("query2"+query2)

    // From body param when HTTP method is POST, PUT, PATCH
    val body1   = param("body1")
    val body2   = param("body2")
    log.debug("body1"+body1)
    log.debug("body2"+body2)

    respondText(
s"""
textParams:${textParams}
queryParams:${queryParams}
bodyTextParams:${bodyTextParams}
pathParams:${pathParams}
path1:${path1}
path2:${path2}
query1:${query1}
query2:${query2}
"""
    )
  }
}

@GET("/requestparamoption/:path1/:path2")
@POST("/requestparamoption/:path1/:path2")
class RequestParamOptionIndex extends Action with SkipCsrfCheck {
  def execute() {

    // From path param
    val path1  = paramo("path1")
    val path2  = paramo("path2")
    log.debug("path1"+path1)
    log.debug("path2"+path2)

    // From query param
    val query1  = paramo("query1")
    val query2  = paramo("query2")
    log.debug("query1"+query1)
    log.debug("query2"+query2)

    // From query param
    val body1   = paramo("body1")
    val body2   = paramo("body2")
    log.debug("body1"+body1)
    log.debug("body2"+body2)

    respondText(
s"""
textParams:${textParams}
queryParams:${queryParams}
bodyTextParams:${bodyTextParams}
pathParams:${pathParams}
path1:${path1}
path2:${path2}
query1:${query1}
query2:${query2}
body1:${body1}
body2:${body2}
"""
    )
  }
}
```

それぞれのURLにGETとPOSTでアクセスすると以下の様な結果となります。

```
curl -X GET http://localhost:8000/requestparam/x/y\?query1\=q1\&query2\=q2 -H "X-MyHeader:World" -d "body1=b1" -d "body2=b2"
Missing param: body1


curl -X POST http://localhost:8000/requestparam/x/y\?query1\=q1\&query2\=q2 -H "X-MyHeader:World" -d "body1=b1" -d "body2=b2"

textParams:Map(path2 -> List(y), path1 -> List(x), body2 -> List(b2), query2 -> List(q2), body1 -> List(b1), query1 -> List(q1))
queryParams:Map(query2 -> List(q2), query1 -> List(q1))
bodyTextParams:Map(body2 -> List(b2), body1 -> List(b1))
pathParams:Map(path2 -> List(y), path1 -> List(x))
path1:x
path2:y
query1:q1
query2:q2


curl -X GET http://localhost:8000/requestparamoption/x/y\?query1\=q1\&query2\=q2 -H "X-MyHeader:World" -d "body1=b1" -d "body2=b2"

textParams:Map(path2 -> List(y), path1 -> List(x), query2 -> List(q2), query1 -> List(q1))
queryParams:Map(query2 -> List(q2), query1 -> List(q1))
bodyTextParams:Map()
pathParams:Map(path2 -> List(y), path1 -> List(x))
path1:Some(x)
path2:Some(y)
query1:Some(q1)
query2:Some(q2)
body1:None
body2:None


curl -X POST http://localhost:8000/requestparamoption/x/y\?query1\=q1\&query2\=q2 -H "X-MyHeader:World" -d "body1=b1" -d "body2=b2"

textParams:Map(path2 -> List(y), path1 -> List(x), body2 -> List(b2), query2 -> List(q2), body1 -> List(b1), query1 -> List(q1))
queryParams:Map(query2 -> List(q2), query1 -> List(q1))
bodyTextParams:Map(body2 -> List(b2), body1 -> List(b1))
pathParams:Map(path2 -> List(y), path1 -> List(x))
path1:Some(x)
path2:Some(y)
query1:Some(q1)
query2:Some(q2)
body1:Some(b1)
body2:Some(b2)
```


#### 7-2-2 型を指定してパラメータを取得する

リクエストパラメータのデフォルトはString型です。
``param``および、``paramo``に型を指定することで任意の型でパラメータを取得することができます。
デフォルトのコンバータは以下の用に定義されており、独自の型に変換する場合は、Action内で``convertTextParam``をオーバーライドします。
なお``convertTextParam``をオーバーライドする際に、Scala 2.10では[Typeの代わりにManifestを使用](https://github.com/ngocdaothanh/xitrum/issues/155)することに注意してください。

##### RequestParamConvertExample.scala

```scala
@GET("/requestparamconvert/:one")
class RequestConvertIndex extends Action {
  def execute() {

      val one_as_String   = param[String]("one")
      val one_as_Char     = param[Char]("one")
      val one_as_Byte     = param[Byte]("one")
      val one_as_Short    = param[Short]("one")
      val one_as_Int      = param[Int]("one")
      val one_as_Long     = param[Long]("one")
      val one_as_Float    = param[Float]("one")
      val one_as_Double   = param[Double]("one")
      val one_as_Implicit = param("one")


    respondText(
s"""
one_as_String =>   Class:${one_as_String.getClass.toString}, Value:${one_as_String}
one_as_Char =>     Class:${one_as_Char.getClass.toString},   Value:${one_as_Char}
one_as_Byte =>     Class:${one_as_Byte.getClass.toString},   Value:${one_as_Byte}
one_as_Short =>    Class:${one_as_Short.getClass.toString},  Value:${one_as_Short}
one_as_Int =>      Class:${one_as_Int.getClass.toString},    Value:${one_as_Int}
one_as_Long =>     Class:${one_as_Long.getClass.toString},   Value:${one_as_Long}
one_as_Float =>    Class:${one_as_Float.getClass.toString},  Value:${one_as_Float}
one_as_Double =>   Class:${one_as_Double.getClass.toString}, Value:${one_as_Double}
one_as_Implicit => Class:${one_as_Implicit.getClass.toString}, Value:${one_as_Implicit}
"""
    )
  }
}

case class MyClass(value:String)

@GET("/requestparamconvertcustome/:one")
class RequestConvertCustomeIndex extends Action {
  override  def convertTextParam[T: TypeTag](value: String): T = {
    val t = typeOf[T]
    val any: Any =
           if (t <:< typeOf[String])  value
      else if (t <:< typeOf[MyClass]) MyClass(value)
      else if (t <:< typeOf[Int])    value.toInt
      else throw new Exception("convertTextParam cannot covert " + value + " to " + t)
    any.asInstanceOf[T]
  }

  def execute() {

      val one_as_MyClass  = param[MyClass]("one")
      val one_as_String   = param[String]("one")
      val one_as_Int      = param[Int]("one")


    respondText(
s"""
one_as_String =>   Class:${one_as_String.getClass.toString},  Value:${one_as_String}
one_as_MyClass=>   Class:${one_as_MyClass.getClass.toString}, Value:${one_as_MyClass}
one_as_Int =>      Class:${one_as_Int.getClass.toString},     Value:${one_as_Int}
"""
    )
  }
}
```

実行結果は以下のようになります。

```
oshidatakeharu@oshida [~] curl -X GET http://localhost:8000/requestparamconvert/1

one_as_String =>   Class:class java.lang.String, Value:1
one_as_Char =>     Class:char,   Value:1
one_as_Byte =>     Class:byte,   Value:1
one_as_Short =>    Class:short,  Value:1
one_as_Int =>      Class:int,    Value:1
one_as_Long =>     Class:long,   Value:1
one_as_Float =>    Class:float,  Value:1.0
one_as_Double =>   Class:double, Value:1.0
one_as_Implicit => Class:class java.lang.String, Value:1


oshidatakeharu@oshida [~] curl -X GET http://localhost:8000/requestparamconvertcustome/1

one_as_String =>   Class:class java.lang.String,  Value:1
one_as_MyClass=>   Class:class quickstart.action.MyClass, Value:MyClass(1)
one_as_Int =>      Class:int,     Value:1
```

#### 7-2-3. リクエストボディをJSONとして扱う

Restful APIサーバなど、クライアントとのインターフェイスをJSONで定義したアプリケーションの場合など、
JSONをそのままMapとして扱えると便利です。Xitrumでは以下の方法で実現することができます。

##### RequestJSONExample.scala

```scala
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
```

``requestContentJson``はパースに失敗した場合は``None``を返します。

```
curl -X GET http://localhost:8000/requestbodyjson\?query\=Hello -H "X-MyHeader:World" -d "{\"message\":\"xxx\",\"code\":1,\"list\":[1,2,3],\"bool\":true}"

body as Json:Some(Map(message -> xxx, code -> 1, list -> List(1, 2, 3), bool -> true))


curl -X GET http://localhost:8000/requestbodyjson\?query\=Hello -H "X-MyHeader:World" -d "{\"json\":{\"nest\":1}}"

body as Json:Some(Map(json -> Map(nest -> 1)))


curl -X GET http://localhost:8000/requestbodyjson\?query\=Hello -H "X-MyHeader:World" -d "invalidjson"

body as Json:None
```

次回は生のリクエストにアクセスする方法をやります。
