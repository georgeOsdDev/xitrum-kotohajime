# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 3. XitrumアプリケーションのScaffoldとプロジェクト構成

今回はXitrumアプリケーションのScaffoldを準備するところを紹介します。

### 3-1. Xitrum-newプロジェクト

Xitrumプロジェクトには開発者が簡単にアプリケーション開発を進めるためのScaffoldとして、
[Xitrum-newプロジェクト](https://github.com/xitrum-framework/xitrum-new)が用意されています。
では早速ダウンロードします。

    > curl -L https://github.com/xitrum-framework/xitrum-new/archive/master.zip -o /tmp/master.zip
    > unzip -d /path/to/workspace/ /tmp/master.zip

解凍したファイルは`xitrum-new-master`というディレクトリになっているので
任意のアプリケーション名に変更しておきます。

    > mv /path/to/workspace/xitrum-new-master /path/to/workspace/myApplicationName

プロジェクトはgitリポジトリで管理されているので `git clone https://github.com/xitrum-framework/xitrum-new` でも取得することができます。

### 3-2. プロジェクト構成
プロジェクトディレクトリの構成を見てみます。

    > cd myApplicationName
    > tree
    .
    ├── README.rst
    ├── build.sbt
    ├── config
    │   ├── akka.conf
    │   ├── application.conf
    │   ├── flash_socket_policy.xml
    │   ├── logback.xml
    │   ├── ssl_example.crt
    │   ├── ssl_example.key
    │   └── xitrum.conf
    ├── project
    │   ├── build.properties
    │   └── plugins.sbt
    ├── public
    │   ├── 404.html
    │   ├── 500.html
    │   ├── app.css
    │   ├── favicon.ico
    │   ├── robots.txt
    │   └── whale.png
    ├── sbt
    │   ├── agent7-1.0.jar
    │   ├── sbt
    │   ├── sbt-launch-0.13.5.jar
    │   └── sbt.bat
    ├── screenshot.png
    ├── script
    │   ├── runner
    │   ├── runner.bat
    │   ├── scalive
    │   ├── scalive-1.2.jar
    │   └── scalive.bat
    └── src
        └── main
            ├── scala
            │   └── quickstart
            │       ├── Boot.scala
            │       └── action
            │           ├── DefaultLayout.scala
            │           ├── Errors.scala
            │           └── SiteIndex.scala
            └── scalate
                └── quickstart
                    └── action
                        ├── DefaultLayout.jade
                        ├── NotFoundError.jade
                        ├── ServerError.jade
                        └── SiteIndex.jade

    13 directories, 34 files


プロジェクトルートには、
`README.rst`、`build.sbt`、`screenshot.png` があります。

#### READMEファイル

`README.rst`と`screenshot.png`はXitrum-newプロジェクト用のドキュメントなので削除します。

    > rm README.rst screenshot.png

必要に応じてこれから作成するアプリケーションのREADMEファイルを作成しましょう。
拡張子/書式はrstでもmdでも好きなもので構いません。

今回はマークダウン形式のREADMEにします。

    > echo "#My Xitrum Application" > README.md


#### build.sbt

`build.sbt` はsbtプロジェクトの設定ファイルとなります。
以下の（説明）コメントはこの資料用に記載したものとなります。

    > cat build.sbt

    // （説明）organization,name,versionはプロジェクトの情報に合わせて編集します
    organization := "jp.co.my.company"

    name         := "My-Xitrum-Application"

    version      := "1.0-SNAPSHOT"

    //（説明）scala,javaに関する設定はデフォルトのままでOK

    scalaVersion := "2.11.2"
    //scalaVersion := "2.10.4"

    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

    // Xitrum requires Java 7
    javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

    //------------------------------------------------------------------------------

    // （説明）libraryDependencies += に続く行はプロジェクトの依存ライブラリが記載されています

    // （説明）xitrum
    libraryDependencies += "tv.cntt" %% "xitrum" % "3.17"

    // （説明）log出力用にlogbackを使用します。
    // Xitrum uses SLF4J, an implementation of SLF4J is needed
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

    // （説明）logbackの設定をxmlで使用するためのライブラリ
    // For writing condition in logback.xml
    libraryDependencies += "org.codehaus.janino" % "janino" % "2.7.5"

    // （説明）デフォルトではbootstrapが内包されています
    libraryDependencies += "org.webjars" % "bootstrap" % "3.2.0"


    // （説明）デフォルトではテンプレートエンジンはxitrum-scalateを使用します
    // Scalate template engine config for Xitrum -----------------------------------

    libraryDependencies += "tv.cntt" %% "xitrum-scalate" % "2.2"

    // （説明）Xitrum-Scalateを使用するための設定が記載されています
    // Precompile Scalate templates
    seq(scalateSettings:_*)

    ScalateKeys.scalateTemplateConfig in Compile := Seq(TemplateConfig(
      file("src") / "main" / "scalate",
      Seq(),
      Seq(Binding("helper", "xitrum.Action", true))
    ))

    // （説明）i18N国際化対応を行うためのxgettextの設定が記載されています
    // xgettext i18n translation key string extractor is a compiler plugin ---------

    autoCompilerPlugins := true

    addCompilerPlugin("tv.cntt" %% "xgettext" % "1.1")

    scalacOptions += "-P:xgettext:xitrum.I18n"


    // （説明）アプリケーション起動時にconfigディレクトリをクラスパスにふくめるための設定が記載されています
    // Put config directory in classpath for easier development --------------------

    // For "sbt console"
    unmanagedClasspath in Compile <+= (baseDirectory) map { bd => Attributed.blank(bd / "config") }

    // For "sbt run"
    unmanagedClasspath in Runtime <+= (baseDirectory) map { bd => Attributed.blank(bd / "config") }

    // Copy these to target/xitrum when sbt xitrum-package is run
    XitrumPackage.copy("config", "public", "script")


build.sbt編集時の注意点として、各行は空行区切りとなっている必要があります。
また、任意のsbt処理を追加することもできます。
詳しくは[sbtドキュメント](http://www.scala-sbt.org/documentation.html)を参考にしてください。

#### configディレクトリ

configディレクトリには、アプリケーションの各種設定ファイルが含まれます。
最初はデフォルト設定のままで大丈夫です。
設定ファイルには[JSON、properties、conf形式のファイルを使用することができます](http://xitrum-framework.github.io/guide/3.17/ja/howto.html#load-config-files)。
新たに設定ファイルを作成する場合configディレクトリに保存します。

#### projectディレクトリ

sbtの設定、プラグイン設定などが含まれます。
sbtプラグインを追加した場合などに編集します。

#### publicディレクトリ

フロントエンドの静的リソースが含まれます。
静的ファイルを配信する場合はこのディレクトリに保存します。

#### sbtディレクトリ

sbtコマンドがバンドルされています。

#### scriptディレクトリ

アプリケーション起動用のスクリプトや、デバッグツールのScaliveがバンドルされています。

#### srcディレクトリ

アプリケーションのソースコードが含まれます。
デフォルトではquickstartというパッケージ名で、インデックスページとエラーページが含まれています。
アプリケーション開発時にはこのディレクトリ以下にパッケージ、クラス、テンプレートを保存します。

### 3-3. アプリケーションの起動とsbtコマンドについて

開発時のコンパイルやプロジェクトに必要なライブラリのダウンロードなどはsbtコマンドから実行できます。（IDEについては後述）
sbtの起動ファイルと本体はXitrum-newプロジェクトにバンドルされているものを使います。

sbtコンソールの起動

    > sbt/sbt
    [info] Loading project definition from /Users/oshidatakeharu/Dropbox/DEV/xitrum-tutorial/app/myApp/project
    [info] Updating {file:/Users/oshidatakeharu/Dropbox/DEV/xitrum-tutorial/app/myApp/project/}myapp-build...
    [info] Resolving org.fusesource.jansi#jansi;1.4 ...
    [info] Done updating.
    [info] Set current project to My-Xitrum-Application (in build file:/Users/oshidatakeharu/Dropbox/DEV/xitrum-tutorial/app/myApp/)
    > help

では早速動かしてみましょう。
build.sbtの内容からXitrum本体を含め、依存ライブラリのダウンロードがはじまります。
初回はこれに少し時間がかかります。

    > run
    [info] Updating {file:/Users/oshidatakeharu/Dropbox/DEV/xitrum-tutorial/app/myApp/}myapp...
    [info] Resolving org.slf4j#slf4j-api;1.6.1 ...
    [info] downloading http://repo1.maven.org/maven2/tv/cntt/xitrum_2.11/3.16/xitrum_2.11-3.16.jar ...

依存ライブラリのダウンロードが完了するとまずはテンプレートのコンパイルが行われます。

    [info] 	[SUCCESSFUL ] org.codehaus.janino#commons-compiler;2.7.4!commons-compiler.jar (722ms)
    [info] Done updating.
    [info] Compiling Templates in Template Directory: /Users/oshidatakeharu/Dropbox/DEV/xitrum-tutorial/app/myApp/src/main/scalate

次はソースコードのコンパイルが行われます。

    [info] Compiling 8 Scala sources to /Users/oshidatakeharu/Dropbox/DEV/xitrum-tutorial/app/myApp/target/scala-2.11/classes...

コンパイルが終了すると、アプリケーションのMainメソッドが実行されます。
ScaffoldのMainメソッドはquickstartというパッケージのBootというクラスに記載されています。
Xitrumは起動時にルーティングを収集し（キャッシュが存在する場合はキャッシュから）、収集されたルーティングがログ表示されます。
ScaffoldにはSiteIndexという通常のルートと(Normal routes:)、404、500のエラールート(Error routes:)、そしてXitrumが提供するルート(Xitrum routes:)が含まれています。
ルーティングの収集が終わるとサーバが起動します。
デフォルトでは 8000(http)と4430(https)ポートが使用されていますので

    [info] Running quickstart.Boot
    [INFO] Slf4jLogger started
    [INFO] Load routes.cache or recollect routes...
    [WARN] Cannot introspect on class loader: sbt.classpath.ClasspathFilter@6cbebc49 of type sbt.classpath.ClasspathFilter
    [INFO] Glokka actor registry "xitrum.sockjs.SockJsAction$" starts in local mode
    [INFO] Glokka actor registry "metrics" starts in local mode
    [INFO] Normal routes:
    GET  /  quickstart.action.SiteIndex
    [INFO] Error routes:
    404  quickstart.action.NotFoundError
    500  quickstart.action.ServerError
    [INFO] Xitrum SockJS routes:
    xitrum/metrics/channel  xitrum.metrics.XitrumMetricsChannel  websocket: true, cookie_needed: false
    [INFO] Xitrum routes:
    GET        /xitrum/xitrum.js                                           xitrum.js
    GET        /webjars/swagger-ui/2.0.17/index                            xitrum.routing.SwaggerUiVersioned
    GET        /xitrum/metrics/channel                                     xitrum.sockjs.Greeting
    GET        /xitrum/metrics/channel/:serverId/:sessionId/eventsource    xitrum.sockjs.EventSourceReceive
    GET        /xitrum/metrics/channel/:serverId/:sessionId/htmlfile       xitrum.sockjs.HtmlFileReceive
    GET        /xitrum/metrics/channel/:serverId/:sessionId/jsonp          xitrum.sockjs.JsonPPollingReceive
    POST       /xitrum/metrics/channel/:serverId/:sessionId/jsonp_send     xitrum.sockjs.JsonPPollingSend
    WEBSOCKET  /xitrum/metrics/channel/:serverId/:sessionId/websocket      xitrum.sockjs.WebSocket
    POST       /xitrum/metrics/channel/:serverId/:sessionId/xhr            xitrum.sockjs.XhrPollingReceive
    POST       /xitrum/metrics/channel/:serverId/:sessionId/xhr_send       xitrum.sockjs.XhrSend
    POST       /xitrum/metrics/channel/:serverId/:sessionId/xhr_streaming  xitrum.sockjs.XhrStreamingReceive
    GET        /xitrum/metrics/channel/info                                xitrum.sockjs.InfoGET
    WEBSOCKET  /xitrum/metrics/channel/websocket                           xitrum.sockjs.RawWebSocket
    GET        /xitrum/metrics/viewer                                      xitrum.metrics.XitrumMetricsViewer
    GET        /xitrum/metrics/channel/:serverId/:sessionId/websocket      xitrum.sockjs.WebSocketGET
    GET        /xitrum/metrics/channel/:iframe                             xitrum.sockjs.Iframe
    POST       /xitrum/metrics/channel/:serverId/:sessionId/websocket      xitrum.sockjs.WebSocketPOST
    [INFO] HTTP server started on port 8000
    [INFO] HTTPS server started on port 4430
    [INFO] Xitrum 3.16 started in development mode; routes and classes in directories List(/Users/oshidatakeharu/Dropbox/DEV/xitrum-tutorial/app/myApp/target/scala-2.11/classes, /Users/oshidatakeharu/Dropbox/DEV/xitrum-tutorial/app/myApp/config) will be reloaded
    [WARN] *** For security, change secureKey in config/xitrum.conf to your own! ***

ブラウザで http://localhost:8000/にアクセスしてみましょう。
Indexページが表示されればXitrumは正常に稼働しています。

コンソール上には以下のようなログが出力されます。
ルートへのGETリクエスト後にページ内のCSSやJavaScriptを取得するリクエストが発生していることがわかります。

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

** その他のsbtコマンドについて **

使用可能なsbtコマンドはsbtコンソールで<タブキー>を2回押すことで一覧表示可能です。

    > <tab><tab>
    > Display all 289 possibilities? (y or n) y

sbtコマンドに続けてコマンドを入力することでsbtコンソールを省略することができます。
また、;や~をオプションとして使用することもできます。

    ; <command> (; <command>)*              Runs the provided semicolon-separated commands.
    ~ <command>                             Executes the specified command whenever source files change.

Xitrumアプリケーション開発で主に使用するsbtコマンドはあまり多くないので以下のコマンドさえ覚えておけば問題ありません。

 * clean
 * compile
 * run
 * xitrum-package
 * console

### 3-4. IDE向け設定ファイルの生成

コマンドラインとテキストエディタのみでも開発を行うことは可能ですが、
このシリーズではIDEとしてScala IDE(Eclipse)を使用します。
IDEでインポートするためにメタファイルが必要となりますが、sbtで生成することができます。

Eclipse向けの`.project`ディレクトリを生成するには

    > sbt eclipse

InteliiJ iDE向けの `.idea`ディレクトリを生成するには

    > sbt gen-idea

を実行することで各IDEからインポートすることが可能となります。


### 3-5. gitリポジトリの初期化

以下のような`.gitignore`ファイルを作成します。

    .*
    log
    project/project
    project/target
    routes.cache
    target
    tmp


リポジトリを初期化します。

    > git init
    > git add -A
    > git commit -m "Happy to start Xitrum development!"

あとはgithubやbitbucketに追加するなりご自由に。
