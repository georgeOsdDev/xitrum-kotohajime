# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](http://george-osd-blog.heroku.com/40)

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。

## 6. レスポンスとビュー:

今回はレイアウトファイルを使用してViewを表示するところを試します。

公式ドキュメントは以下のページが参考になります。

 * [Action と view](http://xitrum-framework.github.io/guide/3.17/ja/action_view.html)

### 6-2. Templateを使用してViewをレスポンスする

テンプレートエンジンはデフォルトのまま、[xitrum-scalate](https://github.com/xitrum-framework/xitrum-scalate)を使用します。
カスタムテンプレートエンジンの使用については今後やってみたいと思います。

[Scaffoldのコードリーディング](http://george-osd-blog.heroku.com/44)で確認しましたが、[respondView](http://xitrum-framework.github.io/api/index.html#xitrum.Action@respondView)を使用することで、
テンプレートエンジンが生成したViewを（[xitrum-scalate](https://github.com/xitrum-framework/xitrum-scalate)の場合、Actionのクラス名に対応するScalateファイルがテンプレートになる）
が生成し、クライアントにレスポンスすることができます。

#### 現在のActionに対応するViewをレスポンスする

##### RespondViewExample.scala


    @GET("respond/view1")
    class RespondViewExample1 extends Action {
      def execute() {
        respondView()
      }
    }

##### RespondViewExample1.jade

    p This is a "RespondViewExample1" template

これによって、RespondViewExample1.jadeを元に生成されたViewがレスポンスされます。
[http://localhost:8000/respond/view1](http://localhost:8000/respond/view1)

#### 現在のActionとは異なるViewをレスポンスする

Actionの型を指定することで、指定したActionに対応するテンプレートを利用することができます。

##### RespondViewExample.scala

    @GET("respond/view2")
    class RespondViewExample2 extends Action {
      def execute() {
        respondView[RespondViewExample1]()
      }
    }


これも同じく、RespondViewExample1.jadeを元に生成されたViewがレスポンスされます。
[http://localhost:8000/respond/view2](http://localhost:8000/respond/view2)

#### レイアウトを使用する
レイアウトとなるViewを指定し、その中でrenderViewを呼び出すことでレイアウトを使用したレスポンスを返すことができます。
`html`タグや`header`タグなどの共通項目を`trait`として再利用することができます。

##### RespondViewExample.scala

    trait CustomLayout extends Action {
      override def layout = renderViewNoLayout[CustomLayout]()
    }

    @GET("respond/view3")
    class RespondViewExample3 extends CustomLayout {
      def execute() {
        respondView()
      }
    }

##### CustomeLayout.jade

    p
      This is a "CustomLayout" template

    div
      != renderedView

この場合、CustomeLayout.jadeにRespondViewExample3.jadeがネストしてレスポンスされます。
[http://localhost:8000/respond/view3](http://localhost:8000/respond/view3)

#### フラグメントを使用する

複数のActionでViewを共有するには、上記のようにレイアウトを使用する他に、
`renderFragment`を使用することでも実現できます。
fragmenはscalateフォルダ内に、使用するアクションのパッケージディレクトリに"_"prefixとして保存します。

##### RespondViewExample.scala

    @GET("respond/fragment1")
    class RespondFragmentExample1 extends CustomLayout {
      def execute() {
        respondView()
      }
    }

##### RespondFragmentExample1.jade

    p This is a "RespondFragmentExample1" template
    div
      != renderFragment("myfragment")

##### _myfragment.jade

    p This is a "myfragment" fragment

この場合、ResondFragmentExampleX.jade内でrenderFragment("myfragment")とすることで
_myfragment.jadeがRespondViewFragment1/RespondViewFragment2の両方で使用できます。
[http://localhost:8000/respond/fragment1](http://localhost:8000/respond/fragment1)
[http://localhost:8000/respond/fragment2](http://localhost:8000/respond/fragment2)

fragmentの他に、[Component](http://xitrum-framework.github.io/guide/3.18/ja/action_view.html#component)を使用するやり方もあります。
fragmentはViewテンプレートを複数のActionで共有する仕組みですが、Componentはさらに高度な仕組みとして使うことができます。
Componentについては応用編で詳しく掘り下げます。

---

ActionとViewファイルの関係は以上です。
MVCフレームワークに当てはめると

* M: Actionクラスがexecuteメソッド内で呼び出す処理が該当
* V: Actionクラス名に対応したscalateファイル(Actionクラスからはexecuteメソッドの最後にrespondViewで呼び出す)が該当
* C: Actionクラスが該当

といった感じでしょうか。

Viewの返し型が分かったので、次回以降はViewに関するヘルパーや、Xitrum-Scalateについて掘り下げます。
