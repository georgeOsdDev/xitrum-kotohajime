# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 7. リクエストとスコープ:

今回はリクエストに直接アクセスする方法を取り上げたいと思います。

公式ドキュメントは以下のページが参考になります。
 * [スコープ](http://xitrum-framework.github.io/guide/3.18/ja/scopes.html)
 * [リクエストコンテンツの取得](http://xitrum-framework.github.io/guide/3.18/ja/restful.html#id6)

### 7-3. FullHttpRequest

#### 7-3-1. リクエストに直接アクセスする

作成したActionクラスの``execute``メソッドが呼ばれた時、生のリクエスト[FullHttpRequest](http://netty.io/4.0/api/io/netty/handler/codec/http/FullHttpRequest.html)は
``request``という変数に、リクエストボディは``requestContentString``という変数で文字列として取得することができます。

##### RequestRawExample.scala

```scala
@GET("/requestraw")
class RequestRawIndex extends Action {
  def execute() {
    val whaleRequest = request
    log.debug("Request:" + whaleRequest.toString)
    respondText(
s"""
Request:${whaleRequest.toString}
"""
    )
  }
}

@GET("/requestbody")
class RequestBodyIndex extends Action {
  def execute() {
    val body = requestContentString
    log.debug("body:" + requestContentString)
    respondText(
s"""
body:${requestContentString}
"""
    )
  }
}
```

これらのURLにアクセスすると以下のような結果となります。

```
curl -X GET http://localhost:8000/requestraw\?query\=Hello -H "X-MyHeader:World" -d "message=xxx"

Request:DefaultFullHttpRequest(decodeResult: success)
GET /requestraw?query=Hello HTTP/1.1
User-Agent: curl/7.32.0
Host: localhost:8000
Accept: */*
X-MyHeader: World
Content-Length: 11
Content-Type: application/x-www-form-urlencoded


curl -X GET http://localhost:8000/requestbody\?query\=Hello -H "X-MyHeader:World" -d "message=xxx"

body:message=xxx
```

#### 7-3-2. リクエストヘッダーにアクセスする

HTTP Headerにアクセスするには``param``、``paramo``は使用することができません。
直接リクエストから取得する必要があります。

##### RequestHeaderExample.scala

```scala
@GET("/requestheader")
class RequestHeaderIndex extends Action {
  def execute() {
    val headers = request.headers
    log.debug("Header:" + headers.toString)

    val entries = headers.entries
    log.debug("Entries:" + entries.toString)

    val myHeader = headers.get("X-MyHeader")
    log.debug("X-MyHeader:" + myHeader.toString)


    respondText(
s"""
Header:${headers.toString}
Entries:${entries.toString}
X-MyHeader:${myHeader.toString}
"""
    )
  }
}
```

結果は以下のようになります。

```
curl -X GET http://localhost:8000/requestheader\?query\=Hello -H "X-MyHeader:World" -d "message=xxx"

Header:io.netty.handler.codec.http.DefaultHttpHeaders@2f1bc1e1
Entries:[User-Agent=curl/7.32.0, Host=localhost:8000, Accept=*/*, X-MyHeader=World, Content-Length=11, Content-Type=application/x-www-form-urlencoded]
myHeader:World
```

ヘッダーパラメーターが``param``、``paramo``の対象外である理由としては、
通常のActionからはヘッダーパラメータにアクセスする機会が少ないからであるといえます。
ヘッダーパラメータをアプリケーションが利用するユースケースとして、認証処理などが考えられます。
たとえば、```X-APP_TOKEN```などといった形式でアプリケーションのトークンを全てのリクエストのヘッダーに含めて認証を行うアプリの場合、
認証処理は全てのActionに共通であるため、通常は[フィルター](http://xitrum-framework.github.io/guide/3.18/ja/filter.html)が使用されます。
リクエストへの直接のアクセスはフィルターに限定して、通常のActionはそのActionのためのリクエスト（クエリー、パス、ボディ）のみを参照する設計がよいと考えられます。
フィルターを使ったサンプルはまた次の機会にやってみます。
