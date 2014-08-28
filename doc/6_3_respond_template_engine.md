# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 6. レスポンスとビュー:

今回はテンプレートエンジンについてです。

公式ドキュメントは以下のページが参考になります。

 * [Action と view](http://xitrum-framework.github.io/guide/3.18/ja/action_view.html)
 * [テンプレートエンジン](http://xitrum-framework.github.io/guide/3.18/ja/template_engines.html)

### 6-3. テンプレートエンジンとXitrum-Scalate

Xitrumのテンプレートエンジンのインターフェイスは[こちら](http://xitrum-framework.github.io/api/3.18/#xitrum.view.TemplateEngine)にあり、
デフォルトテンプレートエンジンは[xitrum-scalate](https://github.com/xitrum-framework/xitrum-scalate)です。
xitrum-scalateは内部で[Scalate](http://scalate.fusesource.org/)を使用しています。

#### Xitrum-scalateの設定

テンプレートエンジンの指定は`xitrum.conf`で行います。
xitrum-scalateを使用するには、`xitrum.view.TemplateEngine`の実装である`xitrum.view.Scalate`を指定します。

##### xitrum.conf

    # Comment out if you don't use template engine
    template {
      "xitrum.view.Scalate" {
        defaultType = jade  # jade, mustache, scaml, or ssp
      }
    }

テンプレートのsyntaxについては`jade`の他`mustache`、`scaml`、`ssp`が使用可能です。
configでは指定したシンタックス以外を使用する場合は、以下のように指定します。

    val options = Map("type" ->"mustache")
    respondView(options)


また、sbtの設定を以下のように行います。

##### project/plugins.sbt

    // For precompiling Scalate templates in the compile phase of SBT
    addSbtPlugin("com.mojolly.scalate" % "xsbt-scalate-generator" % "0.5.0")

##### build.sbt

    // Scalate template engine config for Xitrum -----------------------------------

    libraryDependencies += "tv.cntt" %% "xitrum-scalate" % "2.2"

    // Precompile Scalate templates
    seq(scalateSettings:_*)

    ScalateKeys.scalateTemplateConfig in Compile := Seq(TemplateConfig(
      file("src") / "main" / "scalate",
      Seq(),
      Seq(Binding("helper", "xitrum.Action", true))
    ))

なお、Viewを必要としないアプリケーションの場合これらのテンプレートエンジンに関する設定は全て削除することもできます。

#### Xitrum-scalateの機能

xitrum-scalateはAction名からテンプレートファイルを選択しViewを生成する他にいくつかの機能を持っています。

１つ目はActionの各メソッドを`helper`としてバインドします。
そのため、テンプレートファイルの中では、Scalateのメソッドの他に、
`publicUrl`などの[xitrum.ActionのAPI](http://xitrum-framework.github.io/api/3.18/#xitrum.Action)を使用することができます。

2つ目は、現在のActionのインスタンスを`currentAction`という変数から取得することができます。

    - val myAction = currentAction.asInstanceOf[MyAction];

次回はこれらを使用したサンプルを作成します。

#### Scalateについての補足

タグ名に続き`.`で文字列を追加すればCSSクラス名に、`#`で文字列を追加すればDOMのIDに変換されます。
また、()で引数を渡すことでDOMエレメントの属性に変換されます。
Scalateについて詳しくは[公式ドキュメント](http://scalate.fusesource.org/documentation/index.html)が参考になります。
個人的な感覚ですがXitrumアプリケーションの開発において一番エラーが発生するのはテンプレートファイルの
シンタックスエラーのように感じます。最初は難しいですが慣れると便利です。
例えば以下はログインフォームを作成した例となります。

    - import mypackage.LoginAction
    div.col-md-4.col-md-offset-4
      form.form-signin(method="post" action={url[LoginAction]})
        h4.form-signin-heading = t("Please sign in")
        != antiCsrfInput
        div.control-group
          div.controls
            label(for="userName") =t("User Name")
            input.form-control#userName(placeholder={t("Type your name")} type="text" name="name" minlength=5 maxlenght=10 required=true)
        div.control-group
          div.controls
            label(for="password") =t("Password")
            input.form-control#password(placeholder={t("Type password")} type="password" name="password" minlength=8 required=true)
        button.btn.btn-large.btn-primary#loginSubmit(type="submit") = t("login")


出力されるレスポンス

    <div class="col-md-4 col-md-offset-4">
      <form class="form-signin" method="post" action="/login">
        <h4 class="form-signin-heading">Please sign in</h4>
        <input type="hidden" name="csrf-token" value="3e8f6edf-ea44-4de8-8ab1-962609b821ce">
        <div class="control-group">
          <div class="controls">
            <label for="userName">User Name</label>
            <input id="userName" class="form-control" placeholder="Type your name" type="text" name="name" minlength="5" maxlenght="10" required="required">
          </div>
        </div>
        <div class="control-group">
          <div class="controls">
            <label for="password">Password</label>
            <input id="password" class="form-control" placeholder="Type password" type="password" name="password" minlength="8" required="required">
          </div>
        </div>
        <button id="loginSubmit" class="btn btn-large btn-primary" type="submit">login</button>
      </form>
    </div>


※上記の例で使用している`t`ファンクションについてはi18nに向けた機能なのでまた後程。
