# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 6. レスポンスとビュー:

Viewに関連する項目として今回は、webJarsを利用したフロントエンドのライブラリ活用についてやります。

Scala(Java)プロジェクトにおけるフロントエンドライブラリ管理の手法として[WebJar](http://www.webjars.org/)という仕組みがあります。
[Xitrumもこの仕組をサポート](http://www.webjars.org/documentation#xitrum)しています。

また、Xitrumのコア自体もwebJarを利用していくつかのフロントエンドライブラリを使用しています。

#### build.sbt

    libraryDependencies += "org.webjars" % "jquery" % "2.1.1"

    libraryDependencies += "org.webjars" % "jquery-validation" % "1.13.0"

    libraryDependencies += "org.webjars" % "sockjs-client" % "0.3.4"

    libraryDependencies += "org.webjars" % "swagger-ui" % "2.0.22"

    libraryDependencies += "org.webjars" % "d3js" % "3.4.11"


webJarを利用することで、
bowerやyoeman、npmなどScala以外のツールの導入なしでプロジェクトのリソース管理を全て
sbtにまとめることができます。

もちろんbowerなどのツールを利用することも、CDNなどを利用することもできますが、
今回はこのwebJarsを利用してみようと思います。

公式ドキュメントは以下のページが参考になります。

 * [WebJarによるクラスパス上のリソースファイルの配信](http://xitrum-framework.github.io/guide/3.18/ja/static.html#webjar)

### 6-5. JavaScriptとCSS

webJarを利用するには、使用したいライブラリが公開されている必要があります。
http://www.webjars.org/ から該当のライブラリを探します。

JavaScriptライブラリとして[Underscore.js](http://underscorejs.org/)を導入してみます。
またxitrum-newをscaffoldとして使用した場合、CSSライブラリとしてbootstrapが既に導入されています。

プロジェクトのbuild.sbtに該当のライブラリをlibraryDependenciesとして追加します。

#### build.sbt

    libraryDependencies += "org.webjars" % "bootstrap" % "3.2.0"

    libraryDependencies += "org.webjars" % "underscorejs" % "1.6.0-3"

build.sbtを更新したら、sbtを使用してライブラリをダウンロードします。

``sbt/sbt update``

以下のように

    [info] Resolving org.webjars#underscorejs;1.6.0-3 ...
    [info] downloading http://repo1.maven.org/maven2/org/webjars/underscorejs/1.6.0-3/underscorejs-1.6.0-3.jar ...
    [info] 	[SUCCESSFUL ] org.webjars#underscorejs;1.6.0-3!underscorejs.jar (751ms)
    [info] Done updating.

該当のライブラリがダウンロードされます。

これらはjar形式でプロジェクト起動時のクラスパスに含まれる事になります。

アプリケーションのViewから該当のリソースを取得するには、``Action``の``webJarsUrl``を使用します。
テンプレートエンジンにxitrum-scalateを使用している場合、``webJarsUrl``はテンプレートファイル内でそのまま使えます。

#### DefaultLayout.jade

    link(type="text/css" rel="stylesheet" media="all" href={webJarsUrl("bootstrap/3.2.0/css", "bootstrap.css", "bootstrap.min.css")})

    script(src={webJarsUrl("underscorejs/1.6.0", "underscore.js", "underscore-min.js")})


`webJarsUrl`には、

 * 第１引数として該当のjarファイルのパス(META-INF/resources/webjars以下)を指定します。
   jar内のファイル群がどういう構成かは、http://www.webjars.org/ のFilesボタンを押せば分かります。

 * 第2引数には該当のファイルを指定します。

 * 第3引数にはプロダクション環境用のファイルを指定します。通常は圧縮版のファイルを指定します。該当のファイルの圧縮版が存在しない場合は第2引数と同じものを指定します。


ビルドしてDefaultLayoutを使用しているSiteIndexにアクセスしてみます。

http://localhost:8000/

webJarsUrlは以下のリンクに展開されています。

```
<link type="text/css" rel="stylesheet" media="all" href="/webjars/bootstrap/3.2.0/css/bootstrap.css?4pWKTr6RZtuqbFkxGygQIQ">

<script src="/webjars/underscorejs/1.6.0/underscore.js?3ZZjvppx81cLw18O26KHEg"></script>
```

ブラウザはこれらのタグを解析して、GETリクエストとしてXitrumサーバに送信する事になります。
Xitrumサーバは受け付けたリクエストをどのようにルーティングしているのでしょうか。

少し内部の難しい話になりますが、
Xitrumの[ChannelPipeline](http://xitrum-framework.github.io/guide/3.18/ja/handler.html)にはinboundハンドラーとして
[WebJarsServer.scala](https://github.com/xitrum-framework/xitrum/blob/46f330ac6c360688417406dcc1539ebb8704b721/src/main/scala/xitrum/handler/inbound/WebJarsServer.scala)があります。
このハンドラーがwebjarasで始まるURLの場合に、該当のファイルをjarファイルの中から見つけてレスポンスしてくれるという事になります。
該当のリソースが見つからない場合404エラーフラグをセットして次のハンドラーへ処理が移ります。

一方でユーザーが書くアプリケーションのActionへのルーティングは、inboundハンドラーの最後にあります。
そのため、該当のリソースがwebJarsに見つからなければユーザーが書いたルーティングで処理されうるということです。

実験してみましょう。

#### WebJarsNoRoute.scala

    @GET("/webjars/underscorejs/1.6.0/underscore.js", "/webjars/underscorejs/1.7.0/underscore.js")
    class WebJarsNoRoute extends Action {
      def execute() {
        respondText("underscorejs-1.7.0 is not found")
      }
    }


`curl http://localhost:8000/webjars/underscorejs/1.6.0/underscore.js`


`curl http://localhost:8000/webjars/underscorejs/1.7.0/underscore.js`


1つめのリクエストは正しいwebJarsリソースが存在するので、underscore.jsがレスポンスされて、
2つめのリクエストはWebJarsNoRouteが実行されます。

---

今回はこれまで。
次回は、レスポンスおよびルーティングに関わるトピックの最後として
リダイレクトとフォーワード、ポストバックをやってみます。
