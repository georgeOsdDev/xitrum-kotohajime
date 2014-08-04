# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 5. ルーティングを追加する:

[前回](http://george-osd-blog.heroku.com/46)はHTTPメソッドとActionの関連付けを確認したので、
今回はURLとActionの関連について見ていきます。

公式ドキュメントは以下のページが参考になります。
 * [Action と view](http://xitrum-framework.github.io/guide/3.17/ja/action_view.html)
 * [RESTful APIs](http://xitrum-framework.github.io/guide/3.17/ja/restful.html)


### 5-2. ActionとURL

Xitrumのソースを見るとAnnotationの実態はcase classでpathというフィールドを設定できることがわかります。

https://github.com/xitrum-framework/xitrum/blob/master/src/main/scala/xitrum/annotation/Routes.scala#L9-L13

    package xitrum.annotation

    import scala.annotation.StaticAnnotation

    sealed trait Route        extends StaticAnnotation
    sealed trait Error        extends StaticAnnotation
    sealed trait RouteOrder   extends StaticAnnotation

    case class GET   (paths: String*) extends Route
    case class POST  (paths: String*) extends Route
    case class PUT   (paths: String*) extends Route
    case class PATCH (paths: String*) extends Route
    case class DELETE(paths: String*) extends Route


[前回](http://george-osd-blog.heroku.com/46) httpの各メソッドに対応するActionは全て`POST("httpcrud")`など、
`httpcrud`という引数のアノテーションを指定しました。

その結果それぞれのルートは、

    [INFO] Normal routes:
    GET     /          quickstart.action.SiteIndex
    GET     /httpcrud  quickstart.action.getIndex
    POST    /httpcrud  quickstart.action.postIndex
    PUT     /httpcrud  quickstart.action.putIndex
    PATCH   /httpcrud  quickstart.action.patchIndex
    DELETE  /httpcrud  quickstart.action.deleteIndex

という風に、/httpcrudというURLに対応してルーティングテーブルに登録されました。
ではこの`paths: String*`という引数にはどのようなものが指定できるのかを[Xitrumガイド](http://xitrum-framework.github.io/guide/3.17/ja/action_view.html)を中心に見ていきます。

#### rootExample.scala

##### 通常パターン

まず通常のパターンは、`SiteIndex`や、`httpcrud`の各Actionのように単一の文字列を指定するパターンとなります。
この場合、`/<指定された文字列>` というURLがActionに対応するルートとなります。

文字列中に`/`を含むことでURLに階層を持たせる事もできます。

    @GET("/path/to/myaction")
    class MyAction extends ClassNameResponder {
      def execute() {
        log.debug("MyAction")
        respondClassNameAsText()
      }
    }

##### 複数パスの関連付け

続いて、複数の文字列をアノテーションで指定するパターンでは、それぞれのURLを同じActionが対応するというルートになります。

    @GET("one" ,"two")
    class MultiPathAction extends ClassNameResponder {
      def execute() {
        log.debug("MultiPathAction")
        respondClassNameAsText()
      }
    }

##### .(ドット)を含むルート

パスに`.`(ドット)を含むことも可能です。

    @GET("/dot.html")
    class DotInPathAction extends ClassNameResponder {
      def execute() {
        log.debug("DotInPathAction")
        respondClassNameAsText()
      }
    }


##### pathParam

Xitrumにはリクエストスコープの1つに[pathParams](http://xitrum-framework.github.io/guide/3.17/ja/scopes.html#id2)というものがあります。
リクエストスコープやパラメータ取得について詳しくは今後掘り下げますが、ルーティングという視点から、pathParamをどう定義することができるかを確認します。

pathParamを定義するには`:`(コロン)で区切ります。
以下の例の場合、`/item/lsit/1` や `/item/list/mobile`というURLが`ItemListAction`にルーティングされることになります。

    @GET("/item/list/:categoryId")
    class ItemListAction extends ClassNameResponder {
      def execute() {
        log.debug("ItemListAction")

        // pathParamの取得
        val categoryId = param("categoryId")
        log.debug(categoryId)

        respondClassNameAsText()
      }
    }

##### ルートの優先順位

同じHTTPメソッドに対するルーティングが競合した場合に優先順位を指定することができます。

例えば以下の例では、`/item/{カテゴリーID}/{アイテムID}`というルーティングを期待しています。

    @GET("/item/:categoryId/:itemId")
    class ItemDetailAction extends ClassNameResponder {
      def execute() {
        log.debug("ItemDetailAction")

        // pathParamの取得
        val categoryId = param("categoryId")
        log.debug(categoryId)

        val itemId = param("itemId")
        log.debug(itemId)

        respondClassNameAsText()
      }
    }


しかし、一つ前のサンプルの`ItemListAction`のルーティングである、`/item/list/mobile`というパスにアクセスした場合、
`list`という文字列がカテゴリーIDとして認識される可能性があります。

ためしに、`/item/list/10`に何度かリクエストを投げてみますと、以下のように同じURLに対してその時によって異なるActionが実行されていることがわかります。

    [DEBUG] ItemDetailAction
    [INFO] 0:0:0:0:0:0:0:1 GET /item/list/10 -> quickstart.action.ItemDetailAction, pathParams: {categoryId: list, itemId: 10} -> 304, 1 [ms]
    [DEBUG] ItemListAction
    [INFO] 0:0:0:0:0:0:0:1 GET /item/list/10 -> quickstart.action.ItemListAction, pathParams: {categoryId: 10} -> 200, 1 [ms]

このような事故を避けるために、Xitrumには[`@First`、`@Last`](http://xitrum-framework.github.io/guide/3.17/ja/restful.html#firstlast)アノテーションが用意されています。
今回の場合、/item/list/で始まる場合は、`ItemListAction`を優先したいため、`ItemListAction`に`@First`を指定します。（または、`ItemDetailAction`に`@Last`を指定）。

これによって、/item/listで始まるURLは`ItemListAction`が優先的に使用される事になります。
同じURLで3つのActionに対応させる必要がある場合は、`@First`を最も優先すべきActionに、`@Last`を最も優先度が低いActionに、残った1つのActionには優先度は指定しないことで実現できます。
先ほどのように優先度を指定しなかった場合の処理はどのActionが使用されるかは神のみぞ知るという状態です。
すなわち、4つ以上のActionを同じURLで処理するには、Xitrumでは優先度の判定手段がありません。その場合URL設計を見なおした方が良いということですね。

##### 正規表現によるpathParamの指定

`pathParam`に正規表現でフィルターを追加することも可能です。
正規表現のフィルターを使用するには`<>`をpathParamに続けて指定します。
以下の例では、前述の`ItemDetailAction`と`ItemListAction`の関係と全く同じ`AnimalDetailAction`と`AnimalListAction`があります。
ただし、`AnimalDetailAction`のcategoryIdは数字であることを制限する正規表現が追加されています。

    @GET("animal/:categoryId<[0-9]+>/:animalId")
    class AnimalDetailAction extends ClassNameResponder {
      def execute() {
        log.debug("AnimalDetailAction")

        // pathParamの取得
        val categoryId = param("categoryId")
        log.debug(categoryId)

        val animalId = param("animalId")
        log.debug(animalId)

        respondClassNameAsText()
      }
    }

    @GET("/animal/list/:categoryId")
    class AnimalListAction extends ClassNameResponder {
      def execute() {
        log.debug("AnimalListAction")

        // pathParamの取得
        val categoryId = param("categoryId")
        log.debug(categoryId)

        respondClassNameAsText()
      }
    }

上記の場合ルーティングは以下のように行われます。


url                                      | Action
-----------------------------------------|--------------------
http://localhost:8000/animal/10/1001     | AnimalDetailAction
http://localhost:8000/animal/10/monkey   | AnimalDetailAction
http://localhost:8000/animal/list/1001   | AnimalListAction
http://localhost:8000/animal/list/monkey | AnimalListAction
http://localhost:8000/animal/fish/tuna   | ErrorAction(404エラー)

`fish`のようにcategoryIdがの正規表現にマッチしないリクエストの場合、該当のルートが見つからないためXitrumは404を返します。


---

以上ActionとURLについてでした。
次回はAction以外のリソースについてのルーティングとURLについて見て行きたいと思います。
