# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 6. レスポンスとビュー:

[ルーティングとActionの関連](http://george-osd-blog.heroku.com/46)が分かったので
今回からはActionがレスポンスを返す方法について確認していきます。

公式ドキュメントは以下のページが参考になります。

 * [Action と view](http://xitrum-framework.github.io/guide/3.17/ja/action_view.html)

ドキュメントにあるクライアントへのレスポンス送信パターンは以下の通りです。

> * respondView: レイアウトファイルを使用または使用せずに、Viewテンプレートファイルを送信します
* respondInlineView: レイアウトファイルを使用または使用せずに、インライン記述されたテンプレートを送信します
* respondText("hello"): レイアウトファイルを使用せずに文字列を送信します
* respondHtml("<html>...</html>"): contentTypeを”text/html”として文字列を送信します
* respondJson(List(1, 2, 3)): ScalaオブジェクトをJSONに変換し、contentTypeを”application/json”として送信します
* respondJs("myFunction([1, 2, 3])") contentTypeを”application/javascript”として文字列を送信します
* respondJsonP(List(1, 2, 3), "myFunction"): 上記2つの組み合わせをJSONPとして送信します
* respondJsonText("[1, 2, 3]"): contentTypeを”application/javascript”として文字列として送信します
* respondJsonPText("[1, 2, 3]", "myFunction"): respondJs 、 respondJsonText の2つの組み合わせをJSONPとして送信します
* respondBinary: バイト配列を送信します
* respondFile: ディスクからファイルを直接送信します。 zero-copy を使用するため非常に高速です。
* respondEventSource("data", "event"): チャンクレスポンスを送信します

Viewファイルを使用する前にの前に単純なパターンから確認していきます。

### 6-1. textをレスポンスする

Actionがレスポンスを返すときの最も単純なパターンはテキストを返すだけの処理です。

これまでのサンプルで処理を実行したActionのクラス名を出力するためにも使用しました。

    trait ClassNameResponder extends Action {
      def respondClassNameAsText(){
        respondText(getClass)
      }
    }

respondTextを行うと

    > curl http://localhost:8000/path/to/myaction -v                                                                                                                            19:01:17  ☁  master ☂ ⚡ ✭
    * About to connect() to localhost port 8000 (#0)
    *   Trying ::1...
    * Adding handle: conn: 0x7fbde8806e00
    * Adding handle: send: 0
    * Adding handle: recv: 0
    * Curl_addHandleToPipeline: length: 1
    * - Conn 0 (0x7fbde8806e00) send_pipe: 1, recv_pipe: 0
    * Connected to localhost (::1) port 8000 (#0)
    > GET /path/to/myaction HTTP/1.1
    > User-Agent: curl/7.32.0
    > Host: localhost:8000
    > Accept: */*
    >
    < HTTP/1.1 200 OK
    < Connection: keep-alive
    < Content-Type: text/plain; charset=UTF-8
    < Access-Control-Allow-Origin: *
    < Access-Control-Allow-Credentials: true
    < Access-Control-Allow-Methods: OPTIONS, GET, HEAD
    < Content-Length: 32
    < ETag: "9HVM4haHpRzVyN0-1nuYdA"
    <
    * Connection #0 to host localhost left intact
    class quickstart.action.MyAction%

Content-Typeに "text/plain"が指定されます。

[Scaladoc](http://xitrum-framework.github.io/api/3.17/#xitrum.Action)には以下のように定義してあります。
> def respondText(text: Any, fallbackContentType: String = null, convertXmlToXhtml: Boolean = true): ChannelFuture
@fallbackContentType
Only used if Content-Type header has not been set. If not given and Content-Type header is not set, it is set to "application/xml" if text param is Node or NodeSeq, otherwise it is set to "text/plain".
@convertXmlToXhtml
.toString by default returns `<br></br>` which is rendered as 2 `<br />`tags on some browsers!
Set to false if you really want XML, not XHTML. See http://www.scala-lang.org/node/492 and http://www.ne.jp/asahi/hishidama/home/tech/scala/xml.html

`text`の型は`Any`となっています。これは`String`の他に`Node`や`NodeSeq`を渡せるようにするためでしょう。
`fallbackContentType`を指定しない場合は`text/plain`もしくは`text`の型に応じて、`application/xml`が自動で設定されるようです。

`respondHtml`、`respondJson`、`respondJs`、`respondJsonP`、`respondJsonText`、`respondJsonPText`もこの応用で
指定した内容に対して、`contet-type`を決定してレスポンスを返してくれます。
`respondJson`、`respondJsonP`については、ScalaObjectを直接渡すことでXitrumがJSONに変換してくれます。

各メソッドを使用して以下のようなサンプルを作成してみました。
まず、"respond/html"に対するアクセスに対して、`RespondExample1`が`respondHtml`を使用して
インラインで記述されたhtmlを返却します。
返却されるhtml内ではscriptタグで各URLへのリクエストが行われます。

#### RexpondTextExample.scala

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

[http://localhost:8000/respond/html](http://localhost:8000/respond/html)をブラウザで表示して、
JavaScriptコンソールを見ると以下の様になりました。

    > Object {key: "jsonpObj"}
    > Object {key1: "this is jsonPtext", key2: Array[4]}
    > Response from respondJsonText
    > Object {key1: "this is jsontext", key2: Array[4]}
    > Response from respondJson
    > Object {key1: 1, key2: Array[3], key3: Object}


簡単ですね。
次回はレイアウトテンプレートを使用したViewの表示について確認します。
