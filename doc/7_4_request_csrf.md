# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 7. リクエストとスコープ:

今回はリクエストに直接アクセスする方法を取り上げたいと思います。

公式ドキュメントは以下のページが参考になります。
 * [スコープ](http://xitrum-framework.github.io/guide/3.18/ja/scopes.html)
 * [CSRF対策](http://xitrum-framework.github.io/guide/3.18/ja/restful.html#csrf)

### 7-4. CSRF対策

#### 7-3-1. XitrumのCSRF対策の仕組み


`[Xitrum.Action](https://github.com/xitrum-framework/xitrum/blob/561d214d2af847c7ad4ab7bf1b3b1b9835f0f9a0/src/main/scala/xitrum/Action.scala#L85-L90)`は、
リクエストメソッドが、`POST`、`PUT`、`PATCH`、`DELETE`の場合、デフォルトでCSRF対策トークンチェックを行います。

```
if ((request.getMethod == HttpMethod.POST ||
     request.getMethod == HttpMethod.PUT ||
     request.getMethod == HttpMethod.PATCH ||
     request.getMethod == HttpMethod.DELETE) &&
    !isInstanceOf[SkipCsrfCheck] &&
    !Csrf.isValidToken(this)) throw new InvalidAntiCsrfToken
```

では、このCSRFトークンチェックの内容はというと、
`[Csrf.isValidToken](https://github.com/xitrum-framework/xitrum/blob/46f330ac6c360688417406dcc1539ebb8704b721/src/main/scala/xitrum/scope/session/Csrf.scala#L18-L29)`の処理は次の用に、
リクエストヘッダーあるいはリクエストボディに指定されたキーでセットされたトークンと、セッション内のトークンが一致しているかを判定しています。
トークン自体はランダムな文字列ですが、シリアライズされた形式でセッションに保存されています。

```scala
def isValidToken(action: Action): Boolean = {
  // The token must be in the request body for more security
  val bodyTextParams = action.handlerEnv.bodyTextParams
  val headers        = action.handlerEnv.request.headers
  val tokenInRequest = Option(headers.get(X_CSRF_HEADER)).getOrElse(action.param(TOKEN, bodyTextParams))

  // Cleaner for application developers when seeing access log
  bodyTextParams.remove(TOKEN)

  val tokenInSession = action.antiCsrfToken
  tokenInRequest == tokenInSession
}
```

#### 7-3-2. AntiCSRFトークンのセット

クライアントはリクエストヘッダーまたはリクエストボディにセッション内のトークンと一致する値を含める必要があります。

#### HTMLメタタグとXitrum.jsを用いてリクエストヘッダーにトークンを含める方法

レイアウト内で、{antiCsrfMeta}を使用することでメタタグが生成されます。
Scaffoldプロジェクトにあるように通常デフォルトレイアウトで用います。

```jade
!!! 5
html
  head
    != antiCsrfMeta
    != xitrumCss
    meta(content="text/html; charset=utf-8" http-equiv="content-type")
```

こう書くことで、以下のようにHTMLに展開されます。

```html
<!DOCTYPE html>
<html>
  <head>
    <meta name="csrf-token" content="9f16c39b-3456-4020-9695-484d101908ca"/>
    <link href="/webjars/xitrum/3.18/xitrum.css?mhIAFrxv3tBMQXtHcoYT7w" type="text/css" rel="stylesheet" media="all"/>
    <meta content="text/html; charset=utf-8" http-equiv="content-type"/>
```

Xitrum.jsを使用した場合AJAXリクエスト送信時に、このメタタグの値をリクエストヘッダーの値に自動的に含めてくれます。
Xitrum.jsのインポートの仕方は以下のとおりです。

```jade
!= jsDefaults

# または、

script(type="text/javascript" src={url[xitrum.js]})
```


#### リクエストボディにトークンを含める方法

```jade
form(method="post" action={url[SiteIndex]})
  != antiCsrfInput

#または

form(method="post" action={url[SiteIndex]})
  input(type="hidden" name="csrf-token" value={antiCsrfToken})
```

いずれも以下のように展開されます。

```html
<form method="post" action="/">
  <input type="hidden" name="csrf-token" value="9f16c39b-3456-4020-9695-484d101908ca"/>
</form>
```

#### 7-3-3. CSRF対策をスキップする

debug時やcurlクライントなどCSRF対策を省略したい場合があります。
[ActionとHTTPメソッドアノテーションの回](http://george-osd-blog.heroku.com/46)で確認したように、
``SkipCsrfCheck``を継承したActionを使用することで前述の``!isInstanceOf[SkipCsrfCheck]``という条件にあてはまらなくなるなるので、
CSRF対策チェックは実行されません。

---
次回は、CSRFトークンも保存されているセッションおよびクッキーについて掘り下げたいと思います。



