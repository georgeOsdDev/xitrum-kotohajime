# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 5. ルーティングを追加する:

[前回](http://george-osd-blog.heroku.com/47)はActionとURLの関連付けを確認したので、
今回はAction以外のリソースのURLについて見ていきます。

公式ドキュメントは以下のページが参考になります。
 * [静的ファイル](http://xitrum-framework.github.io/guide/3.17/ja/static.html)

### 5-3. 静的リソースとindex.html

XitrumのScaffoldプロジェクトに[publicというディレクトリが含まれていることを以前確認しました](http://george-osd-blog.heroku.com/44#4-6-public-)。
Xitrumは`public`ディレクトリに含まれる静的ファイルを自動で配信します。
`public`内に配置されたファイルは

    http(s)://<host>:<port>/<ファイルパス>

のURLでアクセスすることができます。

プログラムから上記のURLを取得するためのヘルパーとして、Actionには`publicUrl`というメソッドが用意されています。
`publicUrl`メソッドは、主にScalateテンプレート内で使用されることを想定しており、
開発環境とプロダクション環境における圧縮ファイルと非圧縮ファイルの出し分けや、Etagに応じたクエリストリングの付加を自動で行ってくれます。

Scaffoldでは以下の用に使用されています。

    // DefaultLayout.jade内
    link(rel="shortcut icon" href={publicUrl("favicon.ico")})
    link(type="text/css" rel="stylesheet" media="all" href={publicUrl("app.css")})

    // SiteIndex.jade内
    img(src={publicUrl("whale.png")})

`publicUrl`の使い方の詳細については`View`の章で詳しく見たいと思います。

また、JavaScriptライブラリやCSSライブラリなどのフロントエンドリソースの配信については、
[WebJars](http://www.webjars.org/)を使用することができます。WebJarsについては今後改めて掘り下げたいと思いますので、
今回は触れません。

#### パスの優先順位とindex.htmlフォールバック

`public`内にあるファイルと、Actionに定義したルーティングの優先順位について見てみます。
以下のような実験用ファイルを作成します。

##### PublicRoot.scala

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

また`public`ディレクトリ内には以下のファイルを用意します。

    .
    └── static
        ├── file.xls
        ├── foo
        │   └── index.html
        ├── image.png
        └── index.html


アプリを起動すると以下のルーティングテーブルが出力されます

    GET     /static                                quickstart.action.PublicRootAction1
    GET     /static/image                          quickstart.action.PublicRootAction4
    GET     /static/image.png                      quickstart.action.PublicRootAction5
    GET     /static/index                          quickstart.action.PublicRootAction2
    GET     /static/index.html                     quickstart.action.PublicRootAction3

この状態で各URLにアクセスすると以下のようになりました。

URL                                         | Reponse（対応したサーバリソース）
--------------------------------------------|------------------------------
http://localhost:8000/static                | PublicRootAction1
http://localhost:8000/static/index          | PublicRootAction2
http://localhost:8000/static/index.html     | static/index.html
http://localhost:8000/static/index.htm      | NotFoundError
http://localhost:8000/static/image          | PublicRootAction4
http://localhost:8000/static/image.png      | static/image.png
http://localhost:8000/static/file.xls       | NotFoundError
http://localhost:8000/static/foo            | static/foo/index.html
http://localhost:8000/static/foo/index      | NotFoundError
http://localhost:8000/static/foo/index.html | static/foo/index.html


注目すべきは`http://localhost:8000/static/foo`にアクセスした場合です。
xitrumは対応するpublicリソースおよび、Actionが存在しない場合該当のパスに対応する`public`ディレクトリ内の`index.html`を探してフォールバックします。

すなわち優先順位は `静的ファイル > Action > index.htmlフォールバック > 404`ということになります。

#### ファイル拡張子とContent-Type

上記の実験で`http://localhost:8000/static/file.xls`にアクセスした場合404となっています。
`file.xls`というファイルは、`public`ディレクトリ内に存在しますが何故でしょうか。

Xitrumには不要なファイル存在チェックを避けるための機能があります。
`.xls`という拡張子はWEBサイトにおいて使用される拡張子として一般的ではないため、
Xitrumはファイルの存在チェックを行いません。
もし、`.xls`形式のファイルを配信したい場合、`xitrum.conf`の`staticFile/pathRegex`の項に追加する必要があります。
デフォルトでは一般的にWEBサイトで使用される拡張子が正規表現で指定されています。

##### xitrum.conf

    staticFile {
      # This regex is to optimize static file serving speed by avoiding unnecessary
      # file existance check. Ex:
      # - "\\.(ico|txt)$": files should end with .txt or .ico extension
      # - ".*": file existance will be checked for all requests (not recommended)
      pathRegex = "\\.(ico|jpg|jpeg|gif|png|html|htm|txt|css|js|map)$"


`xitrum.conf`を以下のように修正してみます。

    pathRegex = "\\.(xls|ico|jpg|jpeg|gif|png|html|htm|txt|css|js|map)$"

再起動後に`http://localhost:8000/static/file.xls`にアクセスしてみます。

    > curl http://localhost:8000/static/file.xls -v                                                                                                                                               11:15:45
    * About to connect() to localhost port 8000 (#0)
    *   Trying ::1...
    * Adding handle: conn: 0x7fd7dc003000
    * Adding handle: send: 0
    * Adding handle: recv: 0
    * Curl_addHandleToPipeline: length: 1
    * - Conn 0 (0x7fd7dc003000) send_pipe: 1, recv_pipe: 0
    * Connected to localhost (::1) port 8000 (#0)
    > GET /static/file.xls HTTP/1.1
    > User-Agent: curl/7.32.0
    > Host: localhost:8000
    > Accept: */*
    >
    < HTTP/1.1 200 OK
    < Connection: keep-alive
    < Cache-Control: public, max-age=31536000
    < Access-Control-Max-Age: 31536000
    < Expires: Sat, 08 Aug 2015 02:17:31 GMT
    < Content-Type: application/vnd.ms-excel
    < Content-Length: 17
    < Access-Control-Allow-Origin: *
    < Access-Control-Allow-Credentials: true
    < Access-Control-Allow-Methods: OPTIONS, GET, HEAD
    < ETag: ""IBAo8Vbj6iq2b0Njv5A1Ew""
    <
    This is file.xls

無事`file.xls`がレスポンスされました。
この時、`Content-Type`には`application/vnd.ms-excel`というものが設定されています。`file.xls`の拡張子から自動でXitrumが判定されました。
このContent-Typeの仕組みは
[xitrum.util.Mine](https://github.com/xitrum-framework/xitrum/blob/ded7bbbd81688f036d48c1792a8460a2d45e1a16/src/main/scala/xitrum/util/Mime.scala)が、[mime.types](https://github.com/xitrum-framework/xitrum/blob/ded7bbbd81688f036d48c1792a8460a2d45e1a16/src/main/resources/META-INF/mime.types)を元に自動で設定してくれます。

#### 404.htmlと500.html

`@Error400`と`@Error500`のエラーアノテーションがプロジェクトで使用されていない場合、
エラー発生時にXitrumがpublicディレクトリ内の`404.html`または`500.html`がを自動的にレスポンスします。

エラーアノテーションも、エラーhtmlもプロジェクトで使用されていない場合、
エラー発生時には
HTTPステータスコードに`404`や`500`が設定され、
レスポンスヘッダには`Content-Length:0`、レスポンスボディは空というレスポンスが返却される事になります。

一時的に`@404Error`をコメントアウト、`404.html`をリネームして実験してみると分かります。

#### 最適化のためのキャッシュ、ETag、GZIPと設定ファイル

`xitrum.conf`には静的ファイル配信の最適化のためのいくつかの設定があります。

Xitrumは静的リソースのファイルのディスクからの読み込み負荷を避けるため、
サイズに応じてファイルをメモリ上にキャッシュします。
キャッシュするサイズのしきい値と個数は、`staticFile`の`maxSizeInKBOfCachedFiles`と`maxNumberOfCachedFiles`で
設定することができます。

    maxSizeInKBOfCachedFiles = 512
    maxNumberOfCachedFiles   = 1024

また、クライアントサイドへファイルをキャッシュさせるためのレスポンスヘッダには、
`Etag`ヘッダはファイルの更新日時等に応じてXitrumが自動で設定してくれます。
`Etag`に応じた`304 Not Modified`レスポンスも上記でキャッシュした内容に応じてXitrumが自動で判定してくれます。
また、`Cache-Control:max-age`、`Expire`もXitrumが自動で1年の期間を設定してくれます。
クライアントにEtag問い合わせ矯正したい場合、`staticFile`の`revalidate`を`true`に設定します。

    # true:  ETag response header is set for  static files.
    #        Before reusing the files, clients must send requests to server
    #        to revalidate if the files have been changed. Use this when you
    #        create HTML directly with static files.
    # false: Response headers are set so that clients will cache static files
    #        for one year. Use this when you create HTML from templates and use
    #        publicUrl("path/to/static/file") in templates.
    revalidate = false

`response.autoGzip`を`true`にセットすると、
`Content-Type`がテキストベースの場合Xitrumは自動的にGZIP圧縮したレスポンスを返してくれます。
なお、この設定は、Actionがレスポンスした内容など静的ファイルに以外にも適用されます。

    response {
      # Set to true to tell Xitrum to gzip big textual response when
      # request header Accept-Encoding contains "gzip"
      # http://en.wikipedia.org/wiki/HTTP_compression
      autoGzip = true

---
以上で、ルーティングおよびActionについての章は完了です。
次回からはViewの書き方について勉強します。
