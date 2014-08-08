# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 5. ルーティングを追加する:

今回から実際にソースコードを作成して、Xitrumアプリケーション開発を行います。
まずは、Webアプリケーションとして画面表示やAPIのエンドポイントとなるルーティングを追加していきます。

公式ドキュメントは以下のページが参考になります。
 * [Action と view](http://xitrum-framework.github.io/guide/3.17/ja/action_view.html)
 * [RESTful APIs](http://xitrum-framework.github.io/guide/3.17/ja/restful.html)


### 5-1. ActionとHTTPメソッドアノテーション

Xitrumの特徴の1つに[ルートの自動収集](http://xitrum-framework.github.io/guide/3.17/ja/intro.html)があります。

> JAX-RSとRailsエンジンの思想に基づく自動ルートコレクション。全てのルートを１箇所に宣言する必要はありません。 この機能は分散ルーティングと捉えることができます。この機能のおかげでアプリケーションを他のアプリケーションに取り込むことが可能になります。 もしあなたがブログエンジンを作ったならそれをJARにして別のアプリケーションに取り込むだけですぐにブログ機能が使えるようになるでしょう。 ルーティングには更に2つの特徴があります。 ルートの作成（リバースルーティング）は型安全に実施され、 Swagger Doc を使用したルーティングに関するドキュメント作成も可能となります。

アプリケーションがルートを追加する場合、RoRの`config/routes.rb`のような設定ファイルは特に必要ありません。
`xitrum.Action`を継承して、アノテーションを宣言することでそれがWebアプリケーションのルートの一つに成ります。

HTTPメソッドに対応したクラスを作成します。

#### quickstart.action.HttpCRUD.scala

    package quickstart.action

    import xitrum.Action
    import xitrum.annotation.{GET, POST, PUT, DELETE, PATCH}

    trait HttpCRUD extends Action {
      // この処理は現在のクラス名をtextをレスポンスとして返す
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
    class PostIndex extends HttpCRUD {
      def execute() {
        log.debug("postIndex")
        respondClassNameAsText()
      }
    }

    @PUT("/httpcrud")
    class PutIndex extends HttpCRUD {
      def execute() {
        log.debug("putIndex")
        respondClassNameAsText()
      }
    }

    @DELETE("/httpcrud")
    class DeleteIndex extends HttpCRUD {
      def execute() {
        log.debug("deleteIndex")
        respondClassNameAsText()
      }
    }

    @PATCH("/httpcrud")
    class PatchIndex extends HttpCRUD {
      def execute() {
        log.debug("patchIndex")
        respondClassNameAsText()
      }
    }


各HTTPメソッドに対応したActionを作成しました。
サーバ側のログと、クライアントへのレスポンスに現在実行されているAction名を出力する簡単な例です。

Xitrumを起動します。

`sbt/sbt run`

Xitrumを起動すると、以下の様なログが表示されます。

    [INFO] Normal routes:
    GET     /          quickstart.action.SiteIndex
    GET     /httpcrud  quickstart.action.GetIndex
    POST    /httpcrud  quickstart.action.PostIndex
    PUT     /httpcrud  quickstart.action.PutIndex
    PATCH   /httpcrud  quickstart.action.PatchIndex
    DELETE  /httpcrud  quickstart.action.DeleteIndex

各HTTPメソッドに対応したActionがルーティングテーブルに追加されました。

リクエストを投げてみます。

    > curl http://localhost:8000/httpcrud                                                                                                                               10:32:42
    class quickstart.action.GetIndex%

getIndexが実行されました。

サーバ側のログ(sbt/sbt run コンソール)には

    [DEBUG] GetIndex
    [INFO] 0:0:0:0:0:0:0:1 GET /httpcrud -> quickstart.action.GetIndex -> 200, 2 [ms]

と表示されました。
1行目はプログラム中で記載した`log.debug`による出力。
2行目はXitrumデフォルトのアクセスログとなります。


次に別のHTTPメソッドを投げてみます。

    > curl -X POST http://localhost:8000/httpcrud                                                                                                                               10:32:42
    Missing param: csrf-token%

csrf-tokenが無いという文字列が帰ってきました。

サーバ側のログ(sbt/sbt run コンソール)には

    [INFO] 0:0:0:0:0:0:0:1 POST /httpcrud -> quickstart.action.PostIndex -> 400, 34 [ms]

とあります。HTTPステータスコードは400となっています。
これはXitrumがデフォルトでCSRF対策を行っているため、curlで実施したリクエストにトークンが含まれないことに起因します。

https://github.com/xitrum-framework/xitrum/blob/b360234713562409e5a3d00ebdd0d9deb8664953/src/main/scala/xitrum/Action.scala#L85-L90

    if ((request.getMethod == HttpMethod.POST ||
         request.getMethod == HttpMethod.PUT ||
         request.getMethod == HttpMethod.PATCH ||
         request.getMethod == HttpMethod.DELETE) &&
        !isInstanceOf[SkipCsrfCheck] &&
        !Csrf.isValidToken(this)) throw new InvalidAntiCsrfToken

Xitrumが発行するCSRFトークンについては、フォーム画面作成時に詳しく見ていきますので、
ここではこれを一旦無効にします。

    // import xitrum.Action
    import xitrum.{Action, SkipCsrfCheck}

    //class PostIndex extends HttpCRUD {
    class PostIndex extends HttpCRUD with SkipCsrfCheck {

`SkipCsrfCheck`というtraitをimportして、`with` 句でそれを継承します。
XitrumによるCSRFチェックが有効になるHTTPメソッドは、`POST`,`PUT`,`PATCH`,`DELETE`であるため、
GET以外のリクエストを扱うActionにそれぞれ追記します。

修正を反映するにはXitrumを再起動します。
`Ctrl +c`で プロセスを停止し、再度`sbt/sbt run`とします。
ただし、ちょっとしたソース修正の度に毎回再起動を行うと時間がとてもかかってしまいますので、
次からはソース修正時にXitrumの再起動をいちいち行わなくて済むようにします。
DCEVMをalternativeインストール使用している場合は、`sbt/sbt`ファイルに`-XXaltjvm=dcevm`オプションを追記します。

ターミナルウィンドウを2つ用意して
1つ目は

`sbt/sbt run`

もう一方は

`sbt/sbt ~compile` (zshを使用している場合は `sbt/sbt "~compile"`)

とします。~をつけてコンパイルを実行した場合、sbtがファイルを監視してコンパイルを自動で実行してくれます。
コンパイルされたclassは[Agent7](https://github.com/xitrum-framework/agent7)によって稼働中のアプリケーションにロードされます。

では再びリクエストを行ってみます。

    curl -X POST http://localhost:8000/httpcrud                                                                                                                       11:21:15
    class quickstart.action.PostIndex%

POSTリクエストには`postIndex`が動作していることが確認できました。

#### HEADリクエストとOPTIONSリクエストについて

ここまでCRUD操作を行うHTTPメソッドに対するアノテーションとルーティングを見てきましたが、
`HEAD`メソッドと`OPTION`メソッドについてはどうでしょうか。

まず、`HEAD`メソッドについては、Xitrumではbodyの無い`GET`メソッドとして扱われます。

    curl -X HEAD http://localhost:8000/httpcrud -v                                                                                                                    11:24:17
    * Adding handle: conn: 0x7ff049003a00
    * Adding handle: send: 0
    * Adding handle: recv: 0
    * Curl_addHandleToPipeline: length: 1
    * - Conn 0 (0x7ff049003a00) send_pipe: 1, recv_pipe: 0
    * About to connect() to localhost port 8000 (#0)
    *   Trying ::1...
    * Connected to localhost (::1) port 8000 (#0)
    > HEAD /httpcrud HTTP/1.1
    > User-Agent: curl/7.30.0
    > Host: localhost:8000
    > Accept: */*
    >
    < HTTP/1.1 200 OK
    < Connection: keep-alive
    < Content-Type: text/plain; charset=UTF-8
    < Content-Length: 32
    <

サーバ側のログでは`getIndex`が動作していることがわかります。

    [INFO] 0:0:0:0:0:0:0:1 HEAD /httpcrud -> quickstart.action.GetIndex -> 200, 3 [ms]

`OPTIONS`メソッドについては、主にSOCKJSの[CORS](https://developer.mozilla.org/ja/docs/HTTP_access_control)対応目的でXitrumには実装されています。
デフォルトではCORSは無効となっています。

curl -X OPTIONS http://localhost:8000/httpcrud -v                                                                                                                 11:32:11
* Adding handle: conn: 0x7fc0f1803c00
* Adding handle: send: 0
* Adding handle: recv: 0
* Curl_addHandleToPipeline: length: 1
* - Conn 0 (0x7fc0f1803c00) send_pipe: 1, recv_pipe: 0
* About to connect() to localhost port 8000 (#0)
*   Trying ::1...
* Connected to localhost (::1) port 8000 (#0)
> OPTIONS /httpcrud HTTP/1.1
> User-Agent: curl/7.30.0
> Host: localhost:8000
> Accept: */*
>
< HTTP/1.1 204 No Content
< Connection: keep-alive
< Cache-Control: public, max-age=31536000
< Access-Control-Max-Age: 31536000
< Expires: Mon, 03 Aug 2015 02:32:15 GMT
< Content-Length: 0
<
* Connection #0 to host localhost left intact

サーバ側のログ

    [INFO] OPTIONS /httpcrud

CORS対応を有効にするには、
`xitrum.conf`内の、`corsAllowOrigins`に配列形式で許可するoriginを記載します。全てのサイトを許可する場合`*`を指定します。
デフォルトではコメントアウトされているので有効にします。

    corsAllowOrigins = ["*"]

configファイルを修正した場合はXitrumの再起動が必要となるため、
`sbt/sbt run`を実行しているターミナルから再起動します。

OPTIONSリクエストをもう一度投げてみます。

    curl -X OPTIONS http://localhost:8000/httpcrud -v                                                                                                                 11:32:15
    * Adding handle: conn: 0x7fd32180e600
    * Adding handle: send: 0
    * Adding handle: recv: 0
    * Curl_addHandleToPipeline: length: 1
    * - Conn 0 (0x7fd32180e600) send_pipe: 1, recv_pipe: 0
    * About to connect() to localhost port 8000 (#0)
    *   Trying ::1...
    * Connected to localhost (::1) port 8000 (#0)
    > OPTIONS /httpcrud HTTP/1.1
    > User-Agent: curl/7.30.0
    > Host: localhost:8000
    > Accept: */*
    >
    < HTTP/1.1 204 No Content
    < Connection: keep-alive
    < Cache-Control: public, max-age=31536000
    < Access-Control-Max-Age: 31536000
    < Expires: Mon, 03 Aug 2015 02:37:08 GMT
    < Access-Control-Allow-Origin: *
    < Access-Control-Allow-Credentials: true
    < Access-Control-Allow-Methods: OPTIONS, GET, HEAD, POST, PUT, PATCH, DELETE
    < Content-Length: 0
    <
    * Connection #0 to host localhost left intact

CORS対応したレスポンスヘッダーが帰ってくることが確認できました。

---

ここまでHTTPメソッドとActionのルーティングについて見てきました。
次回は、アノテーション内に指定するパスとActionのルーティングについて勉強します。
