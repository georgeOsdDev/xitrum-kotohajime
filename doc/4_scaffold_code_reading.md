# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 4. XitrumアプリケーションのScaffoldのソースコードリーディング

[前回](http://george-osd-blog.heroku.com/43)XitrumのScaffoldプロジェクトを動かすところまでやったので、
今回はScaffoldのプロジェクトがどのようなコードで動いているのかを確認していきます。

Xitrumサーバー起動時に以下のようなログが出力されました。

    [INFO] Normal routes:
    GET  /  quickstart.action.SiteIndex
    [INFO] Error routes:
    404  quickstart.action.NotFoundError
    500  quickstart.action.ServerError

また、ブラウザからサーバにアクセスした時には以下のようなログが出力されました。

    [INFO] 0:0:0:0:0:0:0:1 GET / -> quickstart.action.SiteIndex -> 200, 6 [ms]
    [INFO] 0:0:0:0:0:0:0:1 GET /app.css?V0CGnmnzXFV6l7a-UkY_7w -> 200 (static file)
    [INFO] 0:0:0:0:0:0:0:1 GET /webjars/xitrum/3.16/xitrum.css?mhIAFrxv3tBMQXtHcoYT7w -> 200 (JAR resource)
    [INFO] 0:0:0:0:0:0:0:1 GET /webjars/jquery/2.1.1/jquery.js?dAMGCVD0oTvjs9_eBJDuBQ -> 200 (JAR resource)
    [INFO] 0:0:0:0:0:0:0:1 GET /webjars/bootstrap/3.2.0/css/bootstrap.css?4pWKTr6RZtuqbFkxGygQIQ -> 200 (JAR resource)
    [INFO] 0:0:0:0:0:0:0:1 GET /whale.png?n0kYGVwRhnQKFvpqLLmf6w -> 200 (static file)
    [INFO] 0:0:0:0:0:0:0:1 GET /webjars/jquery-validation/1.12.0/additional-methods.js?VMrHLE7MT-YZGBg3T6jSGA -> 200 (JAR resource)
    [INFO] 0:0:0:0:0:0:0:1 GET /webjars/sockjs-client/0.3.4/sockjs.js?G6ezG627D2WKnJ3F55SoNQ -> 200 (JAR resource)
    [INFO] 0:0:0:0:0:0:0:1 GET /webjars/jquery-validation/1.12.0/jquery.validate.js?MoZJHtxFQR8TR6gNokHx2w -> 200 (JAR resource)
    [INFO] 0:0:0:0:0:0:0:1 GET /xitrum/xitrum.js?BMfHCVrVdosDpgtIlbqZWw -> xitrum.js, queryParams: {BMfHCVrVdosDpgtIlbqZWw: } -> 200, 1 [ms]
    [INFO] 0:0:0:0:0:0:0:1 GET /favicon.ico?BjK0shXmVIuSRS0IsYBdHA -> 200 (static file)

`quickstart.action.SiteIndex`という処理が実施されているように思われます。
試しに存在しないページにリクエストを投げてみます。

    > curl http://localhost:8000/unknown

以下のような404ページがレスポンスとして帰ってきました。

    <!DOCTYPE html>
    <html>
      <head>
        <meta name="csrf-token" content="0c628c30-967f-4c24-9e9d-234c7ed31a8f"/>
        <link href="/webjars/xitrum/3.16/xitrum.css?mhIAFrxv3tBMQXtHcoYT7w" type="text/css" rel="stylesheet" media="all"/>
        <meta content="text/html; charset=utf-8" http-equiv="content-type"/>
        <title>My new Xitrum project</title>
        <link rel="shortcut icon" href="/favicon.ico?BjK0shXmVIuSRS0IsYBdHA"/>
        <link type="text/css" rel="stylesheet" media="all" href="/webjars/bootstrap/3.2.0/css/bootstrap.css?4pWKTr6RZtuqbFkxGygQIQ"/>
        <link type="text/css" rel="stylesheet" media="all" href="/app.css?V0CGnmnzXFV6l7a-UkY_7w"/>
      </head>
      <body>
        <div class="container">
          <h1>
            <a href="/">My new Xitrum project</a>
          </h1>
          <div id="flash">

          </div>
          <p>This is custom 404 page</p>

        </div>

              <script type="text/javascript" src="/webjars/jquery/2.1.1/jquery.js?dAMGCVD0oTvjs9_eBJDuBQ"></script>
              <script type="text/javascript" src="/webjars/jquery-validation/1.12.0/jquery.validate.js?MoZJHtxFQR8TR6gNokHx2w"></script>
              <script type="text/javascript" src="/webjars/jquery-validation/1.12.0/additional-methods.js?VMrHLE7MT-YZGBg3T6jSGA"></script>

              <script type="text/javascript" src="/webjars/sockjs-client/0.3.4/sockjs.js?G6ezG627D2WKnJ3F55SoNQ"></script>
              <script type="text/javascript" src="/xitrum/xitrum.js?BMfHCVrVdosDpgtIlbqZWw"></script>


      </body>
    </html>


今度はコンソール上のログには以下のようなログが出力されました。
`quickstart.action.NotFoundError`という処理が実施されたようです。

    [INFO] 0:0:0:0:0:0:0:1 GET /unknown -> quickstart.action.NotFoundError -> 404, 156 [ms]


サーバー起動時に表示されたルーティング情報が意味するところは

 * `/` というルートは`quickstart.action.SiteIndex`という処理が対応している
 * 404エラーは、`quickstart.action.NotFoundError`という処理が対応している
 * 500エラーは、`quickstart.action.ServerError`という処理が対応している

ということになります。`quickstart.action.SiteIndex`とは何でしょうか。
ソースツリー上には`src/main/scala/quickstart/action`というディレクトリ内にSiteIndex.scalaというファイルがあります。
ということは`quickstart.action.SiteIndex`というのはパッケージ名とクラス名ですね。

### 4-1. Action

では実際にソースコードを見てみます。

#### SiteIndex.scala

まず、SiteIndex.scalaを見てみます。

    package quickstart.action

    import xitrum.annotation.GET

    @GET("")
    class SiteIndex extends DefaultLayout {
      def execute() {
        respondView()
      }
    }

パーケージ、インポート宣言の他、
`SiteIndex`というクラスが定義されています。クラス宣言の直前には`@GET("")`というアノテーションがあります。
メソッドは`execute`というメソッドが1つあり、`respondView`という処理を実行するだけです。
`SiteIndex`は`DefaultLayout`を継承しているようなので`DefaultLayout.scala`も見てみます。

    package quickstart.action

    import xitrum.Action

    trait DefaultLayout extends Action {
      override def layout = renderViewNoLayout[DefaultLayout]()
    }

`DefaultLayout`というクラスは`xitrum.Action`というAPIを継承し、`layout`メソッドを`override`で指定しています
このクラスはtraitのため、実質的な処理は行っていないようです。

では、`Xitrum.Action`とはどういったクラスなのでしょうか。
[ソース](https://github.com/xitrum-framework/xitrum/blob/master/src/main/scala/xitrum/Action.scala)および[APIドキュメント](http://xitrum-framework.github.io/api/3.17/index.html#xitrum.Action)には

  >abstract def execute(): Unit

  >Called when the HTTP request comes in. Actions have to implement this method.

とあります。
受け付けたリクエストに対する処理は`execute`メソッドの中に記載すればよいようです。

以上のことをまとめると

* `@GET("")` というアノテーションがついた`SiteIndex`というクラスがある。
* `SiteIndex`は`xitrum.Action`を継承している。
* `SiteIndex`は`execute`メソッドで`respondView`を実行する。

ということになります。

#### Error.scala

次にError.scalaを見てみます。
Error.scalaには`NotFoundError`と`ServerError`というクラスが定義されています。

    package quickstart.action

    import xitrum.annotation.{Error404, Error500}

    @Error404
    class NotFoundError extends DefaultLayout {
      def execute() {
        respondView()
      }
    }

    @Error500
    class ServerError extends DefaultLayout {
      def execute() {
        respondView()
      }
    }


`SiteIndex`と同様にアノテーションがあり、`xitrum.Action`を継承して`execute`メソッドが実装されています。
ただし、アノテーションはそれぞれ`@Error404`、`@Error500`となっています。

リクエストを処理するクラスがわかったので、では次にどうやってレスポンスを返しているのかを見てみます。
これまでに見た各Actionは`execute`メソッドの最後に`respondView`という処理を行っています。
なお、`execute`メソッド自体は:Unitなので返り値ではないようです。

`respondView`について[APIドキュメント](http://xitrum-framework.github.io/api/index.html#xitrum.Action@respondView)および、[公式ガイド](http://xitrum-framework.github.io/guide/ja/action_view.html#id1)を確認してみます。

    def respondView[T <: Action]()(implicit arg0: Manifest[T]): ChannelFuture

  > respondView: レイアウトファイルを使用または使用せずに、Viewテンプレートファイルを送信します

実際の[Xitrumのソースコード](https://github.com/xitrum-framework/xitrum/blob/master/src/main/scala/xitrum/view/Responder.scala#L224-L246)では

    /**
     * @param options specific to the configured template engine
     */
    def respondView(customLayout: () => Any, location: Class[_ <: Action], options: Map[String, Any]): ChannelFuture = {
      val string = renderView(customLayout, location, options)
      respondText(string, "text/html")
    }

    def respondView[T <: Action : Manifest](customLayout: () => Any, options: Map[String, Any]): ChannelFuture = {
      respondView(customLayout, getActionClass[T], options)
    }

    def respondView[T <: Action : Manifest](customLayout: () => Any): ChannelFuture = {
      respondView(customLayout, getActionClass[T], Map.empty)
    }

    def respondView[T <: Action : Manifest](options: Map[String, Any]): ChannelFuture = {
      respondView(layout _, getActionClass[T], options)
    }

    def respondView[T <: Action : Manifest](): ChannelFuture = {
      respondView(layout _, getActionClass[T], Map.empty)
    }


とあります。
すこし難しいですが、最終的には`respondText(string, "text/html")`にたどり着くようです。
[respondTextの処理](https://github.com/xitrum-framework/xitrum/blob/master/src/main/scala/xitrum/view/Responder.scala#L109-L162)は
レスポンスの`Content-type`を指定してボディに`string`を設定してクライアントに返していると捉えることができます。

この`string`は`renderView(customLayout, location, options)`の処理結果です。
この時`customeLayout`には`DefaultLayout.scala`でoverrideしている`renderViewNoLayout[DefaultLayout]()`が、`location`には`SiteIndex`のクラスが、`option`には`Map.empty`が渡されています。

では`renderViewNoLayout`や`renderView`は何をしているのかというと、以下の様な処理があります。（コメントの①、②などは本シリーズの説明用に追記)

[https://github.com/xitrum-framework/xitrum/blob/master/src/main/scala/xitrum/view/Renderer.scala#L44-L72](https://github.com/xitrum-framework/xitrum/blob/master/src/main/scala/xitrum/view/Renderer.scala#L44-L72)

    /**
     * Renders the template associated with an action to "renderedTemplate",
     * then calls the layout function.
     *
     * @param options specific to the configured template engine
     */
    def renderView(customLayout: () => Any, location: Class[_ <: Action], options: Map[String, Any]): String = {
      Config.xitrum.template match {
        case Some(engine) =>
          renderedView = engine.renderView(location, this, options)  // ①
          customLayout.apply().toString                              // ②

        case None =>
          log.warn("No template engine is configured")
          ""
      }
    }

[https://github.com/xitrum-framework/xitrum/blob/master/src/main/scala/xitrum/view/Renderer.scala#L76-L92](https://github.com/xitrum-framework/xitrum/blob/master/src/main/scala/xitrum/view/Renderer.scala#L76-L92)

    def renderViewNoLayout(location: Class[_ <: Action], options: Map[String, Any]): String =
      Config.xitrum.template match {
        case Some(engine) =>
          val ret = engine.renderView(location, this, options)       // ③
          renderedView = ret
          ret

        case None =>
          log.warn("No template engine is configured")
          ""
      }

いずれもテンプレートエンジンを使用して、Viewを文字列として返却しています。
デフォルトのテンプレートエンジンは[xitrum-scalate](https://github.com/xitrum-framework/xitrum-scalate)です。
実際の処理は以下の用になっています。

    /**
     * Renders the template at the location identified by the given action class:
     * {{{<scalateDir>/<class/name/of/the/location>.<templateType>}}}
     *
     * Ex:
     * When location = myapp.SiteIndex,
     * the template path will be:
     * src/main/scalate/myapp/SiteIndex.jade
     *
     * @param location      Action class used to identify the template location
     * @param currentAction Will be imported in the template as "helper"
     * @param options       "type" -> "jade"/"mustache"/"scaml"/"ssp", "date" -> DateFormat, "number" -> NumberFormat
     */
    def renderView(location: Class[_ <: Action], currentAction: Action, options: Map[String, Any]): String = {
      val tpe     = templateType(options)
      val relPath = location.getName.replace('.', File.separatorChar) + "." + tpe
      renderMaybePrecompiledFile(relPath, currentAction, options)
    }

つまり、テンプレートエンジンは`location`に指定された`Action`のクラス名から、テンプレートファイルを探しだして`renderMaybePrecompiledFile`を実行して返しています。
このActionのクラス名とテンプレートファイルのファイル名を一致させることがXitrumの数少ない規約の一つであると考えることができます。
（厳密にはXitrum本体というよりは、Xitrum-scaateの機能といったほうが正確かもしれません。)

### 4-2. View

ではテンプレートエンジンに渡すViewのテンプレートは実際にどのようなものか見てみます。

    └── src
        └── main
            └── scalate
                └── quickstart
                    └── action
                        ├── DefaultLayout.jade
                        ├── NotFoundError.jade
                        ├── ServerError.jade
                        └── SiteIndex.jade

scalateディレクトリ以下に、パッケージ名およびActionのクラス名に対応した`.jade`ファイルが有ります。

#### SiteIndex.jade

    p
      img(src={publicUrl("whale.png")})

    p
      | This is a skeleton for a new
      a(href="http://xitrum-framework.github.io/") Xitrum
      | project.

    p If you're new to Xitrum, you should visit:

    ul
      li
        a(href="http://xitrum-framework.github.io/") Xitrum Homepage
      li
        a(href="http://xitrum-framework.github.io/guide/") Xitrum Guide

    p
      | This is a skeleton project, you should modify it to suit your needs.
      | Some important parts of the skeleton:

    ul
      li
        | The program's entry point (
        code main
        | function) is in
        a(href="https://github.com/xitrum-framework/xitrum-new/tree/master/src/main/scala/quickstart/Boot.scala") src/main/scala/quickstart/Boot.scala
      li
        | Controller actions are in
        a(href="https://github.com/xitrum-framework/xitrum-new/tree/master/src/main/scala/quickstart/action") src/main/scala/quickstart/action
        | directory.
      li
        | View templates are in
        a(href="https://github.com/xitrum-framework/xitrum-new/tree/master/src/main/scalate/quickstart/action") src/main/scalate/quickstart/action
        | directory.
      li
        | Configurations are in
        a(href="https://github.com/xitrum-framework/xitrum-new/tree/master/config") config
        | directory.


`p`、`a`、`ul`、`li`など[Scalateテンプレート](http://scalate.fusesource.org/documentation/jade.html)のシンタックスでhtmlが表現されています。
2行目にある`publicUrl`というのは[xitrum.ActionのAPI](https://github.com/xitrum-framework/xitrum/blob/77186ebfee0ca83574ac0d685af5749b8ffe121e/src/main/scala/xitrum/action/Url.scala#L33-L47)です。
どうして`xitrum.Action`のAPIがテンプレート内で使えるのか、その仕組は`xitrum-scalate`テンプレートエンジンにありますが、ここでは一旦その秘密の魔法には目を瞑りましょう。

    renderedView = engine.renderView(location, this, options)  // ①

①の結果には上記のテンプレートを元に生成したhtml文字列が格納されることがわかりました。
続いて②の処理

    customLayout.apply().toString                              // ②

の実態は、`DefaultLayout`クラス内の`override def layout`なので`renderViewNoLayout`内の③

    val ret = engine.renderView(location, this, options)       // ③

になります。
ここではlocationには`DefaultLayout`が指定されているので、テンプレートファイルには`DefaultLayout.jade`が使用されます。

#### DefaultLayout.jade

    - import quickstart.action._

    !!! 5
    html
      head
        != antiCsrfMeta
        != xitrumCss

        meta(content="text/html; charset=utf-8" http-equiv="content-type")
        title My new Xitrum project

        link(rel="shortcut icon" href={publicUrl("favicon.ico")})

        link(type="text/css" rel="stylesheet" media="all" href={webJarsUrl("bootstrap/3.2.0/css", "bootstrap.css", "bootstrap.min.css")})
        link(type="text/css" rel="stylesheet" media="all" href={publicUrl("app.css")})

      body
        .container
          h1
            a(href={url[SiteIndex]}) My new Xitrum project

          #flash
            !~ jsRenderFlash()
          != renderedView        //④

        != jsDefaults
        != jsForView


こちらのテンプレートもScalateのシンタックスで記述されています。
`SiteIndex.jade`はページのコンテンツだけだったのに対し、こちらはhtmlヘッダタグ等の記述もあります。
`!=`や`!~`に続く部分や、`{}`で囲まれたコードは、Scalaのコードであるため、値やメソッドの実行結果が入ります。
先ほど①の処理で`renderView`という変数に格納された`SiteIndex.jade`の変換後の文字列を、
このテンプレート内のbodyタグの中で呼び出しています。(④)

図にしてみると以下の様な感じでしょうか。

    +-----------------+
    | Layout          |
    |                 |
    | +-------------+ |
    | | Content     | |
    | |             | |
    | +-------------+ |
    +-----------------+

`SiteIndex`の`respondView`はレイアウトとして`DefaultLayout.jade`を使用します。
`DefaultLayout.jade`によって出力される内容は、HTMLの一番外側なのでこれ以上レイアウトファイルは必要ないため、
`renderViewNoLayout`が使用されているということになります。

[デフォルトのlayoutメソッド](https://github.com/xitrum-framework/xitrum/blob/master/src/main/scala/xitrum/view/Renderer.scala#L12)は

    def layout = renderedView

なので、overrideしない場合は上記の図でいうContentのみが返却されることになります。

> respondView: レイアウトファイルを使用または使用せずに、Viewテンプレートファイルを送信します

はそういったことを意味していました。

実際に返却されるレスポンスを見てみます。

    > curl http://localhost:8000/
    <!DOCTYPE html>
    <html>
      <head>
        <meta name="csrf-token" content="7c8da946-1b53-47de-8a62-b2d361e96510"/>
        <link href="/webjars/xitrum/3.16/xitrum.css?mhIAFrxv3tBMQXtHcoYT7w" type="text/css" rel="stylesheet" media="all"/>
        <meta content="text/html; charset=utf-8" http-equiv="content-type"/>
        <title>My new Xitrum project</title>
        <link rel="shortcut icon" href="/favicon.ico?BjK0shXmVIuSRS0IsYBdHA"/>
        <link type="text/css" rel="stylesheet" media="all" href="/webjars/bootstrap/3.2.0/css/bootstrap.css?4pWKTr6RZtuqbFkxGygQIQ"/>
        <link type="text/css" rel="stylesheet" media="all" href="/app.css?V0CGnmnzXFV6l7a-UkY_7w"/>
      </head>
      <body>
        <div class="container">
          <h1>
            <a href="/">My new Xitrum project</a>
          </h1>
          <div id="flash">

          </div>
          <p>
            <img src="/whale.png?n0kYGVwRhnQKFvpqLLmf6w"/>
          </p>
          <p>
            This is a skeleton for a new
            <a href="http://xitrum-framework.github.io/">Xitrum</a>
            project.
          </p>
          <p>If you're new to Xitrum, you should visit:</p>
          <ul>
            <li>
              <a href="http://xitrum-framework.github.io/">Xitrum Homepage</a>
            </li>
            <li>
              <a href="http://xitrum-framework.github.io/guide/">Xitrum Guide</a>
            </li>
          </ul>
          <p>
            This is a skeleton project, you should modify it to suit your needs.
            Some important parts of the skeleton:
          </p>
          <ul>
            <li>
              The program's entry point (
              <code>main</code>
              function) is in
              <a href="https://github.com/xitrum-framework/xitrum-new/tree/master/src/main/scala/quickstart/Boot.scala">src/main/scala/quickstart/Boot.scala</a>
            </li>
            <li>
              Controller actions are in
              <a href="https://github.com/xitrum-framework/xitrum-new/tree/master/src/main/scala/quickstart/action">src/main/scala/quickstart/action</a>
              directory.
            </li>
            <li>
              View templates are in
              <a href="https://github.com/xitrum-framework/xitrum-new/tree/master/src/main/scalate/quickstart/action">src/main/scalate/quickstart/action</a>
              directory.
            </li>
            <li>
              Configurations are in
              <a href="https://github.com/xitrum-framework/xitrum-new/tree/master/config">config</a>
              directory.
            </li>
          </ul>

        </div>

              <script type="text/javascript" src="/webjars/jquery/2.1.1/jquery.js?dAMGCVD0oTvjs9_eBJDuBQ"></script>
              <script type="text/javascript" src="/webjars/jquery-validation/1.12.0/jquery.validate.js?MoZJHtxFQR8TR6gNokHx2w"></script>
              <script type="text/javascript" src="/webjars/jquery-validation/1.12.0/additional-methods.js?VMrHLE7MT-YZGBg3T6jSGA"></script>

              <script type="text/javascript" src="/webjars/sockjs-client/0.3.4/sockjs.js?G6ezG627D2WKnJ3F55SoNQ"></script>
              <script type="text/javascript" src="/xitrum/xitrum.js?BMfHCVrVdosDpgtIlbqZWw"></script>


      </body>
    </html>


`body`タグの中に`SiteIndex`が展開されているのがわかります。
それ以外にもScalaのコードが展開されて出力されていることがわかります。

### 4-3. Xitrum-Scalate

では、.jadeのテンプレートファイルに書かれていたScalaのコードの実態は何かを確認します。
１つめは前述した、`p`、`a`、`ul`、`li`など[Scalateテンプレート](http://scalate.fusesource.org/documentation/jade.html)の機能
こちらはhtmlタグを出力します。
そしてそれ以外の`publicUrl`や`antiCsrfMeta`などはXitrum-ScalateエンジンによってバインドされたXitrumの機能となります。

jadeファイルはhtmlとして出力されるまえにScalaのコードに変換されています。
そういえばsbtでコンパイル時に以下のログが出力されていました。

    [info] Compiling Templates in Template Directory: /Users/oshidatakeharu/Dropbox/DEV/xitrum-tutorial/app/myApp/src/main/scalate

先ほど`renderMaybePrecompiledFile`という処理が先ほど出てきましたが、このPrecompiledFileこそが、
上記のコンパイルによって生成されたScalaのコードになります。
`target/scala-2.11/src_managed/main/scalate/scalate/quickstart/action/`以下にあります。


#### DefaultLayout_jade.scala(抜粋)

    /* NOTE this file is autogenerated by Scalate : see http://scalate.fusesource.org/ */
    package scalate.quickstart.action

    object $_scalate_$DefaultLayout_jade {
      def $_scalate_$render($_scalate_$_context: _root_.org.fusesource.scalate.RenderContext): Unit = {
        import _root_.org.fusesource.scalate.support.RenderHelper.{sanitize=>$_scalate_$_sanitize, preserve=>$_scalate_$_preserve, indent=>$_scalate_$_indent, smart_sanitize=>$_scalate_$_smart_sanitize, attributes=>$_scalate_$_attributes}
        ;{
          val helper: xitrum.Action = $_scalate_$_context.attribute("helper")
          import helper._
          ;{
            val context: _root_.org.fusesource.scalate.RenderContext = $_scalate_$_context.attribute("context")
            import context._


                    import quickstart.action._

            $_scalate_$_context << ( "<!DOCTYPE html>\n<html>\n  <head>\n    " );
            $_scalate_$_context << ( $_scalate_$_indent ( "    ", $_scalate_$_context.valueUnescaped(
               antiCsrfMeta
            ) ) );

自動生成されたコードのため人間の目で読むのは困難ですが、
`helper`と呼ばれる何かがインポートされていることがわかります。
これらはXitrum-Scalateによって自動で挿入されたコードになります。
（紛らわしいですが、`import quickstart.action._`というのはDefaultLayout.jadeに直接記載された処理になります。）

この`helper`というのは、
①の処理で[engine.renderView(location, this, options)](https://github.com/xitrum-framework/xitrum-scalate/blob/20d51451e9f8f4c0502be77191fd528eb0ec4c6d/src/main/scala/xitrum/view/ScalateEngineRenderInterface.scala#L10-L27)
に渡した`this`に該当し、すなわち現在のActionがバインドされることになります。

そのためJadeファイル内では、[ActionのAPI](http://xitrum-framework.github.io/api/index.html#xitrum.Action)を使用することができるというわけです。

テンプレートエンジンについては今日のところはここまでにしておきます。
Xitrum-Scalateおよびテンプレートエンジンについてはこのシリーズの後半で詳しく掘り下げたいと思います。

### 4-4. Annotation

さて、ActionがHTMLを返すところ処理の流れがわかったので、
各Actionクラスに使用されているアノテーションについて見てみます。

Scaffoldで使用されているアノテーションは、

 * `@GET("")`
 * `@Error404`
 * `@Error500`

の3種類がありました。

これらはいずれも[xitrum.annotation.Routes.scala](https://github.com/xitrum-framework/xitrum/blob/master/src/main/scala/xitrum/annotation/Routes.scala)パッケージに含まれています。
`xitrum.annotation`パッケージには`Routes.scala`の他、`Caches.scala`、`Swagger.scala`があります。
`Routes.scala`にはルーティングに関するアノテーションとして、

* GET,POST等のHTTPメソッドに由来するアノテーションや、
* WebSocketおよび、SockJSに関連するもの、
* `First`、`Last`といった優先順位を制御するもの、
* `Error404`と`Error500`
が定義されています。

`Caches.scala`には、
`CacheActionDay`、`CachePageHour`など、[キャッシュ時間設定に関わる](http://xitrum-framework.github.io/guide/ja/cache.html#id2)アノテーションが定義されています。

`Swagger.scala`には、
[SwaggerAPIを使用したドキュメンテーション作成](http://xitrum-framework.github.io/guide/ja/restful.html?highlight=swagger#api)を行うためのアノテーションが定義されています。

Xitrumは起動時にクラスパス内のこれらのルーティングアノテーションをスキャンし、ルーティングテーブルの作成などを行います。
Annotationは1つのクラスに複数使用することもできます。
具体的な使い方は、このシリーズ内で見て行きたいと思います。

### 4-5. Bootファイル(mainクラス)

Scaffoldに含まれるソースコードはActionとView以外に`Boot.scala`というものがあります。

#### Boot.scala

    package quickstart

    import xitrum.Server

    object Boot {
      def main(args: Array[String]) {
        Server.start()
      }
    }

JVM上で動くScalaプロセス（Javaプロセス)のエントリーポイントはこの`main`クラスになります。
実行内容はシンプルで`xitrum.Server`を`start`しているだけとなります。
Xitrumの標準起動は基本はこれだけです。
また、引数としてカスタマイズした[ChannelInitializer](http://netty.io/4.0/api/io/netty/channel/ChannelInitializer.html)を渡すこともできます。

### 4-6. publicディレクトリ

さて、srcディレクトリ以下にどのようなものが含まれるかわかったので、他のディレクトリを見ていきます。
`public`ディレクトリには静的リソースが含まれています。
ブラウザから`http://localhost:8000/whale.png`にアクセスするとわかるように
`public`ディレクトリはURLのルートにマッピングされます。
ちなみにXitrumの静的ファイル配信スピードは[Nginxに匹敵](https://gist.github.com/ngocdaothanh/3293596)します。

プログラム中から静的リソースのURLを取得するには`publicUrl`というAPIが提供されています。
SiteIndex.scala内では

    img(src={publicUrl("whale.png")})

として使用されていました。

そのほか、`404.html`や`500.html`といったファイルがあります。
Scaffoldでは`@Error400`と`@Error500`アノテーションを使用してカスタムエラーページを作成しているため
これらのファイルが使用されることはありませんが、これらのエラーアノテーションがプロジェクトで使用されていない場合、
エラー発生時にXitrumがpublicディレクトリ内の`404.html`または`500.html`を自動的にレスポンスします。

試しに、`Errors.scala`から`@Error404`アノテーションをコメントアウトしてXitrumを再起動してみます。

    INFO] Normal routes:
    GET  /  quickstart.action.SiteIndex
    [INFO] Error routes:
    500  quickstart.action.ServerError
    [INFO] Xitrum SockJS routes:
    xitrum/metrics/channel  xitrum.metrics.XitrumMetricsChannel  websocket: true, cookie_needed: false

起動時のログには404ルートが含まれていません。
この状態で存在しないルートにリクエストを行うと、`404.html`がレスポンスされます。

    > curl http://localhost:8000/notfound

### 4-7. configディレクトリ

最後はconfigディレクトリを見てみます。

    ├── config
        ├── akka.conf
        ├── application.conf
        ├── flash_socket_policy.xml
        ├── logback.xml
        ├── ssl_example.crt
        ├── ssl_example.key
        └── xitrum.conf

Xitrumは起動時に`config/application.conf`というファイルをクラスパス上から探します。
configディレクトリは起動スクリプトでクラスパスに含まれるようになっています。

.conf拡張子は[TypeSafeのconfig](https://github.com/typesafehub/config)形式の設定ファイルです。
`application.conf`は`akka.conf`と`xitrum.conf`をインクルードしています。
ScaffoldプロジェクトはAkkaによるクラスタリングは行わないので、`akka.conf`はログ設定のみとなっています。
`xitrum.conf`がXitrum本体の設定となります。

例えば`port`についての設定ではhttpサーバーとhttpsがそれぞれ8000,4430を使用するように指定されています。

各設定の詳細やカスタマイズはこのシリーズの後半で詳しく見て行きたいと思います。

confファイルの他には、ログ出力設定用の`logback.xml`があります。
これはXitrum特有のものではなく一般的な設定となります。

---

非常に長くなってしまいましたが、Scaffoldのソースコードリーディングはこれで終了です。
次回からは実際にコードを書いていきたいと思います。
