# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 7. リクエストとスコープ:

今回からはリクエストおよびスコープについて勉強します。
クライアントからのリクエストがどのように処理されるか、リクエストパラメーターの扱い方などを取り上げたいと思います。

公式ドキュメントは以下のページが参考になります。
 * [スコープ](http://xitrum-framework.github.io/guide/3.18/ja/scopes.html)
 * [Nettyハンドラ](http://xitrum-framework.github.io/guide/3.18/ja/handler.html)


### 7-1. チャネルパイプラインとActionのライフサイクル

アプリケーションのロジックでリクエストを処理するうえで、Xitrumの処理の流れの概要を掴んでおきたいと思います。
Xitrumアプリケーションを起動時に
```scala
xitrum.Server.start()
```
と呼び出す必要があります。[xitrum.Server](https://github.com/xitrum-framework/xitrum/blob/master/src/main/scala/xitrum/Server.scala)は、
Nettyの[ChannelPipeline](http://netty.io/4.0/api/io/netty/channel/ChannelPipeline.html)を構築し、Nettyサーバを起動しています。

Xitrumが[デフォルトで構築するチャネルパイプライン](https://github.com/xitrum-framework/xitrum/blob/master/src/main/scala/xitrum/handler/ChannelInitializer.scala#L99-L141)は、
次のようなイメージです

```
                                        +~~~~~~~~~~~~~~~~~~~~~~+
                                ------->|         Action       |
                                |       +~~~~~~~~~~~~~~~~~~~~~~+
                                |                   |
+-------------------------------|-------------------+---------------+
| ChannelPipeline               |                   |               |
|                               |                   |               |
|    +---------------------+    |                   |               |
|    |  BadClientSilencer  |    |                   |               |
|    +----------+----------+    |                   |               |
|              /|\              |                   |               |
|               |          _____|                  \|/     OutBound |
|    +--------------------/+            +-----------+----------+    |
|    |      Dispatcher     |            |     XSendResource    |    |
|    +----------+----------+            +-----------+----------+    |
|              /|\                                  |               |
|               |                                  \|/              |
|    +---------------------+            +-----------+----------+    |
|    |   MethodOverrider   |            |       XSendFile      |    |
|    +----------+----------+            +-----------+----------+    |
|              /|\                                  |               |
|               |                                  \|/              |
|    +----------+----------+            +-----------+----------+    |
|    |      UriParser      |            |   FixiOS6SafariPOST  |    |
|    +----------+----------+            +-----------+----------+    |
|              /|\                                  .               |
|               |                                  \|/              |
|    +----------+----------+            +-----------+----------+    |
|    |    WebJarsServer    |            |    OPTIONSResponse   |    |
|    +----------+----------+            +-----------+----------+    |
|              /|\                                  |               |
|               |                                  \|/              |
|    +----------+----------+            +-----------+----------+    |
|    |   PublicFileServer  |            |        SetCORS       |    |
|    +----------+----------+            +-----------+----------+    |
|              /|\                                  |               |
|               |                                  \|/              |
|    +----------+----------+            +-----------+----------+    |
|    |    BaseUrlRemover   |            |     Env2Response     |    |
|    +----------+----------+            +-----------+----------+    |
|              /|\                                  |               |
|               |                                  \|/              |
|    +----------+----------+            +-----------+----------+    |
|    |     Request2Env     |            | ChunkedWriteHandler  |    |
|    +----------+----------+            +-----------+----------+    |
|              /|\                                  |               |
|               |                                  \|/              |
|    +----------+----------+            +-----------+----------+    |
|    | HttpRequestDecoder  |            | HttpResponseEncoder  |    |
|    +----------+----------+            +-----------+----------+    |
| InBound      /|\                                  |               |
+---------------+-----------------------------------+---------------+
                |                                  \|/
+---------------+-----------------------------------+---------------+
|               |                                   |               |
|       [ Socket.read() ]                    [ Socket.write() ]     |
|                                                                   |
|  Netty Internal I/O Threads (Transport Implementation)            |
+-------------------------------------------------------------------+
```

クライアントからのリクエストに対して、NettyのI／Oスレッド上でそれぞれパイプラインが生成・実行されます。
リクエストはInboundハンドラーを経て、DispacherにてルーティングにマッチしたActionへディスパッチされます。
このパイプラインを経ることで、Actionが実行される段階では生のリクエストはアプリケーション開発者にとって利用しやすい形になっています。
Actionは``respondView``などを実行した段階で処理を終え、処理の流れはOutBoundへと進みます。

クライアントからのリクエスト毎に割り当てられるこのスレッド上でActionクラスのインスタンスは生成されます。
一般にアプリケーション開発者が意識すべきActionのライフサイクルは
Actionの``execute``メソッド内に限られており、
アノテーションで指定されたルーティングにマッチするリクエストを受け付けた時に、
Actionインスタンスは生成され、``execute``メソッドが実行されます。
``execute``メソッドの最後に``respondXXX``を実行することoutBoundハンドラーへの電文を作成しActionは役目を終えます。

WebSocketおよびSockJSクライアントのように、接続を持続するリクエストに対しては、
[Action以降の不要なハンドラーが削除されます](https://github.com/xitrum-framework/xitrum/blob/38c74257054b680d85787318e83d498288b8930f/src/main/scala/xitrum/handler/ChannelInitializer.scala#L55-L86)

なお、XitrumにはActionの実行をFutureやAkkaのスレッドで実行する仕組みも用意されています。
その話はまた今度。

クライアントからのリクエストがActionに届く流れがは大体こんな感じなので、
次回は実際にHTTPリクエストをAction内で扱うための便利機能について使ってみたいと思います。
