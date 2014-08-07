# Xitrumことはじめ　(応用編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 1. クラスタリング:

応用編の第1弾はクラスタリングについて勉強します。
Xitrumアプリケーションにおけるクラスタリングの方法は、何をクラスタリングするかによってやり方はいくつかあります。
今回は[Glokka](https://github.com/xitrum-framework/glokka)を使用したActorのクラスタリングをやってみます。
作成したサンプルは[glokka-demo](https://github.com/georgeOsdDev/glokka-demo)にコミットしてあります。

### 1-1. Akka/Glokka

>    Glokka = Global + Akka

Akka自体に[クラスタリングを実現する方法](http://doc.akka.io/docs/akka/2.3.4/common/cluster.html)がありますが、
Glokkaはその[cluster-singleton](http://doc.akka.io/docs/akka/2.3.4/contrib/cluster-singleton.html#cluster-singleton)を、Erlangの[globalモジュール](http://erlang.org/doc/man/global.html)のように使いやすくしてくれるライブラリです。

Xitrum自体も、SockJS機能やMetrics機能を提供するために内部的にGlokkaを使用しています。

Xitrumアプリケーション起動時のログに

    [INFO] [14-08-06 18:17:16] g.Registry$: Glokka actor registry "xitrum.sockjs.SockJsAction$" starts in cluster mode
    [INFO] [14-08-06 18:17:16] g.Registry$: Glokka actor registry "metrics" starts in cluster mode

というログが出力されるのはこのためです。

#### 1−1−1. Glokkaの機能

Glokkaが提供する機能はざっくりいうと、

 * クラスタ間で共有されたRegistryという領域を作成する

        val registry  = Registry.start(system, proxyName)

 * Registryに名前付きでActorを登録できる

        registry ! Registry.Register(actorName, props)

 * Registryから名前を指定してActorを取得できる

        registry ! Lookup(actorName)

という3点になります。

#### 1−1−2. サンプルアプリケーションの構成

       +--------------------+             +--------------------+
       |        Xitrum      |             |        Xitrum      |
       | +~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+ |
       | | Glokka                                            | |
       | |             _ _ _ [ Hub Actor ] _ _ _             | |
       | |           /                           \           | |
       | +~~~~~~~~~~/~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\~~~~~~~~~~+ |
       |           /        |             |        \           |
       |  [HubClientActor]  |             |  [HubClientActor]  |
       |         |          |             |          |         |
       +---------|----------+             +----------|---------+
                 |                                   |
    +------------|------------+         +------------|------------+
    |  Browser                |         |  Browser                |
    |                         |         |                         |
    | var sock = SockJS       |         | var sock = SockJS       |
    | (http://localhost:8000) |         | (http://localhost:8001) |
    |                         |         |                         |
    +-------------------------+         +-------------------------+

今回は、このregistryにHUBとなるActorを登録して、
各ノードのActorがHUBを介してメッセージをやりとりする構成にします。
HUBに接続するActorは実際のクライアントと直接やりとりを行うものである必要はありませんが、
サンプルとして分かりやすいことや、利用用途としてこの仕組にマッチするため、
今回は各ノードのActorは実際のクライアントからのリクエストをSockJS(WebSocket)で処理する、
SockJsActionとして、以下の処理の流れがシームレスに行われるアプリケーションを作成します。

    [ブラウザ①] <-> [SockJsAction①] <-> [HUB] <-> [SockJsAction②] <-> [ブラウザ②]


Glokkaの機能によって、各XitrumノードにおいてHUBとなるActorにメッセージを送ることができるようになります。
HUB自体はただのActorなのでどのようなメッセージを送受信し、どう振る舞うかはアプリケーションが実装する必要があります。
今回のアプリケーションでは以下のようなメッセージ設計としました。

* `Subscribe(option:Map[String, Any])`

  HUBに接続するためのメッセージ
  HUBはこのメッセージを受け取ったら、`sender`をクライアントとして保持する
  `sender`には`Done`を返却する。

       [HubClientActor] - Subscribe -> [Hub Actor]
                                           |
                         <--- Done --------」


* `Unsubscribe(option:Map[String, Any])`

  HUBから離脱するためのメッセージ
  HUBはこのメッセージを受け取ったら、`sender`を保持しているクライアントから削除する。
  `sender`には`Done`を返却する。

      [HubClientActor] - Unubscribe -> [Hub Actor]
                                          |
                        <--- Done --------」

* `Push(option:Map[String, Any])`

  HUBに何かを送りつけるためのメッセージ
  HUBはこのメッセージを受け取ったら、何かしら処理を行った後に保持しているクライアントにたいして`Publish(option:Map[String, Any])`を送信する。
  `sender`には`Done`を返却する。

      [HubClientActor] - Push -> [Hub Actor] ---- Publish ---> [(another) HubClient Actor]
                                    |          \
                                    |           ---- Publish ---> [(another) HubClient Actor]
                       <--- Done ---」           \
                                                  --- Publish ---> [(another) HubClient Actor]

* `Pull(option:Map[String, Any])`

  HUBにから情報を引き出すためのメッセージ
  HUBはこのメッセージを受け取ったら、何かしら処理を行った後に`sender`に対して結果を`Done(option:Map[String, Any])`として送信する。
  `sender`には`Done`を返却する。

      [HubClientActor] - Pull -> [Hub Actor]
                                     |
                       <--- Done ----」

では早速アプリケーションを作成します。
アプリの雛形には[Xitrum-new]((https://github.com/xitrum-framework/xitrum-new)を使用します。

#### 1−1−3. Registryの作成

##### HUB.scala

    object Hub {
      val KEY_PROXY = "HUB_PROXY"
      // Glokka registry
      val actorRegistry = Registry.start(Config.actorSystem, KEY_PROXY)

      // To force start registry at process start up,
      // Call this method at `main` before start `xitrum.Server`
      def start(){}
    }

Registryの作成にはactorSystemとプロキシ名を指定します。
actorSystemはXitrumが内部で使用している(`Config.actorSystem`)ものをそのまま流用できます。
アプリケーション開始時にレジストリーを確実にスタートさせるために、
`xitrum.Server.start()`の前に`Hub.start()`を呼び出します。

##### Boot.scala

    object Boot {
      def main(args: Array[String]) {
        Hub.start()
        Server.start()
      }
    }


#### 1-1-4. RegistryへのHUBの登録と取得

作成したRegistryへHubとなるActorを登録、または取得するための処理は以下のようにしました。
複数の用途に使いまわせるように、traitとしています。

##### HUB.scala

    trait HubClient extends Actor {
      protected lazy val node = self.toString
      def lookUpHub(key: String, hubProps: Props, option: Any = None) {
        xitrum.Log.debug(s"[HubClient][${node}] Searching HUB node...")
        Hub.actorRegistry ! Registry.Register(key, hubProps)
        context.become {
          case result: Registry.FoundOrCreated => doWithHub(result.ref, option)
          case ignore =>
            xitrum.Log.warn(s"[HubClient][${node}] Unexpected message: $ignore")
        }
      }

      // Implement these method as you like
      def doWithHub(publisher: ActorRef, option: Any)
    }

Registryに対して`Registry.Register`というメッセージで名前と`Props`を指定すると、
GlokkaはRegistry内に指定した名前のActorが存在しなければ、`Props`を元に新しく登録したものを、
既に指定の名前で登録されたActorが存在する場合、それを返却してくれます。
いずれの場合` Registry.FoundOrCreated `というメッセージとなります。


#### 1-1-5. Hubの実装

Registryに登録するHubとなるActorは以下のようにしました。
`Subscribe（またはUnsubscribe)`メッセージに応じて、`sender`を`watch(またはunwatch)`し、`clients`に保持(または削除)します。
`Push`や`Pull`メッセージを受け取った際は何かしらの処理`handlePush`、`handlePull`を行い、
他のクライアントに`Publish`メッセージを送信したり、`sender`に`Done`を返却します。
こちらについてもtraitとして、メッセージの型だけで判定しています。
具体的な処理は`option[Map[String,Any]]`を元にアプリケーションの各ロジックで実装します。

`watch`しているActor(この場合はclientsの１つ)が死んだ場合、`Terminated`というメッセージを受け取るため、
その場合も該当のclientを削除します。

##### Hub.scala

    trait Hub extends Actor {
      protected var clients = Seq[ActorRef]()
      private lazy val node = self.toString

      def receive = {
        case Push(option) =>
          xitrum.Log.debug(s"[Hub][${node}] Received Push request")
          val result = handlePush(option)
          clients.foreach { client =>
            if (client != sender) client ! Publish(result)
          }
          sender ! Done(result)

        case Pull(option) =>
          xitrum.Log.debug(s"[Hub][${node}] Received Pull request")
          sender ! Done(handlePull(option))

        case Subscribe(option) =>
          xitrum.Log.debug(s"[Hub][${node}] Received Subscribe request")
          clients = clients.filterNot(_ == sender) :+ sender
          context.watch(sender)
          sender ! Done(option)

        case UnSubscribe(option) =>
          xitrum.Log.debug(s"[Hub][${node}] Received UnSubscribe request")
          clients =  clients.filterNot(_ == sender)
          context.unwatch(sender)
          sender ! Done(option)

        case Terminated(client) =>
          xitrum.Log.debug(s"[Hub][${node}] Received Terminated event"+client.toString)
          clients = clients.filterNot(_ == client)

        case ignore =>
          xitrum.Log.warn(s"[Hub][${node}] Unexpected message: $ignore")
      }

      // Implement these method as you like
      def handlePush(msg: Map[String, Any]): Map[String, Any]
      def handlePull(option: Map[String, Any]): Map[String, Any]
    }

Hubのベースとなる振る舞いは以上のとおりで、
実際にHubに処理してもらう内容を以下のようにしました。(ちょっと保存したファイルは微妙ですが...)
`Push`メッセージを受け取った際には、`option`内の`cmd`に応じて処理を返します。
今回は"text"というコマンドの場合、その内容を転送します。
それ以外の場合はエラーとして転送します。HUBは`Push`の処理結果を全てにclientに
`Publish`で転送するので、受け取った`client`が無視できるように(後述)`targets`というフィールドに空文字を指定しておきます。

##### HubClientActor.scala

    class HubImpl extends Hub {
      override def handlePush(msg: Map[String, Any]):  Map[String, Any] = {
        msg.getOrElse("cmd", "invalid") match {
          case "text" =>
            Map(
              "error"   -> SUCCESS,
              "seq"       -> msg.getOrElse("seq", -1),
              "targets"   -> msg.getOrElse("targets", "*"),
              "tag"       -> "text",
              "body"      -> msg.getOrElse("body",""),
              "senderName"-> msg.getOrElse("senderName","Anonymous"),
              "senderId"  -> msg.getOrElse("senderId","")
            )

          case unknown =>
            Map(
              "tag"     -> "system",
              "error"   -> INVALID_CMD,
              "seq"     -> msg.getOrElse("seq", -1),
              "targets" -> msg.getOrElse("uuid", "")
            )
        }
      }

      override def handlePull(msg: Map[String, Any]):  Map[String, Any] = {
        msg.getOrElse("cmd", "invalid") match {
          case "count" =>
            Map(
              "tag"     -> "system",
              "error"   -> SUCCESS,
              "seq"     -> msg.getOrElse("seq", -1),
              "count"   -> clients.size
            )
          case unknown =>
            Map(
              "tag"     -> "system",
              "error"   -> INVALID_CMD,
              "seq"     -> msg.getOrElse("seq", -1)
            )
        }
      }
    }


#### 1-1-6. HubClientの実装

Hubに対して接続するHubClientの実際の処理の流れは以下のようになります。

  * クライアント(ブラウザ)からのリクエスト(socket.open)を受け付ける
  * Xitrum内でActionが生成される
  * `execute`メソッドが呼び出される
  * 認証処理(`checkAPIKey`)を行う(これはHUBの機能とは直接関係ありません)
  * 認証OKの場合、HUBを探す(`lookUpHub(hubKey, hubProps, parsed)`)
    このActionで使用するHubは以下のようになります。
        private val hubKey    = "glokkaExampleHub"
        private val hubProps  = Props[HubImpl]
  * HUBが見つかったあと(`override def doWithHub`)は、
    * クライアントからのメッセージは、`tag`を解析してHUBへ送る
    * HUBからのメッセージは`targets`を解析してクライアントへ送る

SockJS(WebSocket)クライアントとのやりとりは全てJSON形式で行うようにしています。
クライアントとのやりとりには、`tag`、`cmd`、`seq`などのキーをAPIとして
アプリに応じてAPIとして定義すると良いと思います。


    @SOCKJS("connect")
    class HubClientActor extends SockJsAction with HubClient {
      private val hubKey    = "glokkaExampleHub"
      private val hubProps  = Props[HubImpl]

      def execute() {
        log.debug(s"[HubClient][$node] is assigned to client")
        checkAPIKey()
      }

      private def checkAPIKey() {
        context.become {
          case SockJsText(msg) =>
            log.debug(s"[HubClient][$node] Received first frame from client")
            parse2MapWithTag(msg) match {
              case ("login", parsed) =>
                if (Utils.auth(parsed.getOrElse("apikey", ""))) {
                  lookUpHub(hubKey, hubProps, parsed)
                } else {
                  respondSockJsTextWithLog(
                    parse2JSON(
                      Map(
                        "error"   -> INVALID_APIKEY,
                        "tag"     -> "system",
                        "seq"     -> parsed.getOrElse("seq", -1),
                        "message" -> "Invalid api key"
                      )
                    )
                  )
                  log.debug(s"Auth error: ${parsed.toString}")
                  respondSockJsClose()
                }
              case (_, parsed) =>
                respondSockJsTextWithLog(
                  parse2JSON(
                    Map(
                      "error"   -> NOT_CONNECTED,
                      "tag"     -> "system",
                      "seq"     -> parsed.getOrElse("seq", -1),
                      "message" -> "First frame must be `login` request"
                    )
                  )
                )
                log.debug(s"Unexpected first frame: ${parsed.toString}")
                respondSockJsClose()
            }

          case ignore =>
            log.warn(s"Unexpected message: ${ignore}")
        }
      }

      override def doWithHub(hub: ActorRef, option: Any) {
        val loginRequest = option.asInstanceOf[Map[String, Any]]
        val name         = loginRequest.getOrElse("name", "Anonymous").toString
        val uuid         = node //Utils.md5(node)

        log.debug(s"[HubClient][${uuid}] HUB found: " + hub.toString)

        // Start subscribing
        log.debug(s"[HubClient][${uuid}] Start Subscribing HUB")
        hub ! Subscribe()
        context.watch(hub)

        respondSockJsText(
          parse2JSON(
            Map(
              "error"   -> SUCCESS,
              "seq"     -> loginRequest.getOrElse("seq", -1),
              "tag"     -> "system",
              "node"    -> node,
              "hub"     -> hub.toString,
              "uuid"    -> uuid,
              "message" -> s"Welcome ${name}!. Your uuid is ${uuid}"
            )
          )
        )

        context.become {

          // (AnotherNode -> ) Hub -> LocalNode
          case Publish(msg) =>
            if (!msg.isEmpty) {
              log.debug(s"[HubClient][${uuid}] Received Publish message from HUB")
              msg.getOrElse("targets", "*") match {
                case list:Array[String] if (list.contains(uuid)) =>
                  // LocalNode -> client
                  respondSockJsTextWithLog(parse2JSON(msg - ("error", "seq", "targets")))
                case targetId:String if (targetId == uuid) =>
                  // LocalNode -> client
                  respondSockJsTextWithLog(parse2JSON(msg - ("error", "seq", "targets")))
                case "*" =>
                  // LocalNode -> client
                  respondSockJsTextWithLog(parse2JSON(msg - ("error", "seq", "targets")))
                case ignore =>
              }
            }

          // (LocalNode ->) Hub -> LocalNode
          case Done(result) =>
            log.debug(s"[HubClient][${uuid}] Received Done message from HUB")
            if (!result.isEmpty) respondSockJsTextWithLog(parse2JSON(result + ("tag" -> "system")))

            // Client -> LocalNode
          case SockJsText(msg) =>
            log.debug(s"[HubClient][${uuid}] Received message from client: $msg")
            parse2MapWithTag(msg) match {
              case ("subscribe", parsed) =>
                // LocalNode -> Hub (-> LocalNode)
                log.debug(s"[HubClient][${uuid}] Send Subscribe request to HUB")
                hub ! Subscribe(Map(
                                    "error"   -> SUCCESS,
                                    "tag"     -> "system",
                                    "seq"     -> parsed.getOrElse("seq", -1)
                                  ))

              case ("unsubscribe", parsed) =>
                // LocalNode -> Hub (-> LocalNode)
                log.debug(s"[HubClient][${uuid}] Send UnSubscribe request to HUB")
                hub ! UnSubscribe(Map(
                                    "error"   -> SUCCESS,
                                    "tag"     -> "system",
                                    "seq"     -> parsed.getOrElse("seq", -1)
                                  ))

              case ("pull", parsed) =>
                // LocalNode -> Hub (-> LocalNode)
                log.debug(s"[${uuid}] Send Pull request to HUB")
                hub ! Pull(parsed + ("uuid" -> uuid))

              case ("push", parsed) =>
                // LocalNode -> Hub (-> AnotherNode)
                log.debug(s"[HubClient][${uuid}] Send Push request to HUB")
                hub ! Push(parsed + ("senderName" -> name, "senderId" -> uuid))

              case (invalid, parsed) =>
                // LocalNode -> client
                respondSockJsTextWithLog(
                  parse2JSON(
                    Map(
                      "error"   -> INVALID_TAG,
                      "tag"     -> "system",
                      "seq"     -> parsed.getOrElse("seq", -1),
                      "message" -> s"Invalid tag:${invalid}. Tag must be `subscribe` or `unsubscribe` or `pull` or `push`."
                    )
                  )
                )
            }

          case Terminated(hub) =>
            log.warn("Hub is terminatad")
            // Retry to lookup hub
            Thread.sleep(100L * (scala.util.Random.nextInt(3) + 1))
            lookUpHub(hubKey, hubProps, option)

          case ignore =>
            log.warn(s"Unexpected message: $ignore")
        }
      }

      private def parse2MapWithTag(jsonStr: String): (String, Map[String, String]) = {
        SeriDeseri.fromJson[Map[String, String]](jsonStr) match {
          case Some(json) =>
              (json.getOrElse("tag", "invalidTag"), json)
          case None =>
            log.warn(s"Failed to parse request: $jsonStr")
            ("invalid", Map.empty)
        }
      }

      private def parse2JSON(ref: AnyRef) = SeriDeseri.toJson(ref)

      private def respondSockJsTextWithLog(text: String):Unit = {
        log.debug(s"[HubClient][${node}] send message to client")
        respondSockJsText(text)
      }
    }

#### 1-1-7. JavaScriptクライアントの実装

サーバ側の実装が終わったので、
視覚的にわかるようにJavaScriptクライアントを書きます。
今回はGlokkaによるクラスタリングの理解が目的のため、JavaScriptはとりあえずの実装です。

JavaScript側からはSockJSで

 * `HubClientActor`(/connect)に接続する
 * 接続したら認証用のリクエストを送る
 * 認証完了後、ボタンに応じて各種コマンドを送る
 * サーバ側からのメッセージ受信時は"tag"や"seq"に応じてコールバックを行う

という流れです。
クライアント側でクラスタリングを意識することはありません。
自分が見ているhtmlと同一のホストに対して接続しているだけで、CORS等も必要としません。
テンプレートファイルに`jsAddToView`を使用して直接JavaScriptを書いてあります。

##### SiteIndex.jade

    "var url = '" + sockJsUrl[HubClientActor] + "';" +
    """
    var socket;
    var counter = -1;
    var callbacks = {};

    $("#btn_connect").on("click",function(e){
      e.preventDefault();
      var loginRequest = {
        tag:"login",
        apikey:$("#api").val(),
        name:$("#name").val(),
        seq:counter
      }
      callbacks[counter] = function(obj){
        var text;
        if(obj.error === 0){
          text = '<b>[Success: Connect with HUB]</b><br />';
          text = text+"hub-node: " + obj.hub +'<br />';
          text = text+"your-node: " + obj.node +'<br />';
          $("#controller").show();
          $("login").hide();
        } else {
          text = '<b style="color:red">[Fail: Connect with HUB]</b>'+obj.message+'<br />';
        }
        xitrum.appendAndScroll('#output', text);
      }
      socket.send(JSON.stringify(loginRequest));
      counter++;
    });

    $("#btn_send").on("click",function(e){...

    //省略

    var initSocket = function() {
      socket = new SockJS(url);
      socket.counter = 0;

      socket.onopen = function(event) {
        var text = '<b>[Socket is open]</b><br />';
        xitrum.appendAndScroll('#output', text);
      };

      socket.onclose = function(event) {
        var text = '<b>[Socket is closed]</b><br />';
        xitrum.appendAndScroll('#output', text);
        $('#controller').hide();
      };

      socket.onmessage = function(event) {
        var obj = JSON.parse(event.data);
        var text;
        if (obj.tag === "system") {
          text = '<b>[SYSTEM MESSAGE from HUB]</b><br />';
        } else {
          text = '<b>['+obj.tag.toUpperCase()+' from '+ obj.senderId+' via HUB]</b><br />';
        }
        xitrum.appendAndScroll('#output', text);
        text = '- ' + xitrum.escapeHtml(event.data) + '<br />';
        xitrum.appendAndScroll('#output', text);
        if (typeof callbacks[obj.seq] === "function") callbacks[obj.seq](obj);
      };
    };
    initSocket();


#### 1-1-8. クラスタリング設定

今回は2つのインスタンスを使用するので使用するportがかぶらないように、
akka_forN.confとxitrum_forN.confをそれぞれ作成し、
アプリ起動時にapplication.confを修正して対象の設定ファイルを読み込むようにします。

Akkaのクラスタリングを有効にする設定はakka_forN.confに記載します。
`remote`の項には自身が使用するポートを、
`cluster`の`seed-nodes`には自身とクラスタリングする別のインスタンスを指定します。
Xitrumアプリケーションの場合actorSystemは、`Config.actorSystem`が`xitrum`という名前のため
"akka.tcp://ClusterSystem@host:port"の`ClusterSystem`は"xitrum"となります。

##### akka_for1.conf

    # Config Akka cluster if you want distributed SockJS
    akka {
      loggers = ["akka.event.slf4j.Slf4jLogger"]
      logger-startup-timeout = 30s

       actor {
         provider = "akka.cluster.ClusterActorRefProvider"
       }

       # This node
       remote {
         log-remote-lifecycle-events = off
         netty.tcp {
           hostname = "127.0.0.1"
           port = 2551  # 0 means random port
         }
       }

       cluster {
         seed-nodes = [
           "akka.tcp://xitrum@127.0.0.1:2551",
           "akka.tcp://xitrum@127.0.0.1:2552"]

         auto-down-unreachable-after = 10s
       }
    }


HTTPサーバも起動するポートがかぶらないようにそれぞれ設定します。

##### xitrum_forN.conf

    # Comment out the one you don't want to start
    port {
      http              = 8000
      https             = 4430
      #flashSocketPolicy = 8430  # flash_socket_policy.xml will be returned
    }


#### 1-1-9. アプリケーションの実行

`sbt/sbt xitrum-package`でパッケージ化したら、2箇所にコピーします。

    sbt/sbt xitrum-package
  	cd /path/to/temp
  	cp -r /path/to/glokka-demo/target/xitrum node1
  	cp -r /path/to/glokka-demo/target/xitrum node2


node2の方はakka_for2.confとxitrum_for2.confを読み込むように`config/application.conf`を修正します。
準備ができたらそれぞれのアプリを起動します。

    script/runner glokka.demo.Boot


実際に操作してnode1とnode2のクライアントがメッセージをやりとりできれば完成です。


![glokka_cluster](http://i.gyazo.com/fff4396836702347325c8e85a94c182b.gif)
