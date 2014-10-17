# Xitrumことはじめ

このシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

## 目次（随時更新）

- [0] はじめに
- [1] 基本編
  2. [Xitrumと関連プロジェクトについて](http://george-osd-blog.heroku.com/41)
  2. [Xitrumアプリケーション開発環境の構築](http://george-osd-blog.heroku.com/42)
  2. [XitrumアプリケーションのScaffoldとプロジェクト構成](http://george-osd-blog.heroku.com/43)
  2. [XitrumアプリケーションのScaffoldのソースコードリーディング](http://george-osd-blog.heroku.com/44)
  2. ルーティングを追加する:
    3. [ActionとHTTPメソッドアノテーション](http://george-osd-blog.heroku.com/46)
    3. [ActionとURL](http://george-osd-blog.heroku.com/47)
    3. [静的リソースとindex.html](http://george-osd-blog.heroku.com/49)
  2. レスポンスとビュー:
    3. [textをレスポンスする](http://george-osd-blog.heroku.com/50)
    3. [Templateを使用してViewをレスポンスする](http://george-osd-blog.heroku.com/52)
    3. [テンプレートエンジンとXitrum-Scalate](http://george-osd-blog.heroku.com/53)
    3. [Viewに関するAPI](http://george-osd-blog.heroku.com/54)
    3. [JavaScriptとCSS](http://george-osd-blog.heroku.com/55)
    3. [ポストバック、リダイレクトとフォーワード](http://george-osd-blog.heroku.com/57)
  2. リクエストとスコープ:
    3. [チャネルパイプライン](http://george-osd-blog.heroku.com/58)
    3. [リクエストパラメーター](http://george-osd-blog.heroku.com/59)
    3. [FullHttpRequest](http://george-osd-blog.heroku.com/60)
    3. CSRFトークン
    3. セッションとクッキー
  2. ブログアプリケーションの作成
    3. ログイン機能を実装する](http://)
    3. ブログアプリケーションの作成:外部ライブラリを使用してバックエンド機能を実装する
      3. DBアクセス](http://)
      3. APIリクエスト](http://)
      3. OAuth](http://)
  2. パフォーマンス最適化を行う: キャッシュ
  2. デプロイする](http://)
    3. 設定とパッケージング](http://)
    3. Herokuへのデプロイ](http://)

- [2] 応用編
  2. クラスタリング
    3. [Akka/Glokka](http://george-osd-blog.heroku.com/48)
    3. Hazelcast
  2. コンポーネント
  2. 非同期アクション
    3. ActorAction
    3. FutureAction
    3. Chunkレスポンス
  2. WebSocket/SockJS
- [3] Tips編
  2. フィルター
  2. Swaggerによるドキュメンテーション
  2. JSON
  2. XML
  2. ファイルアップロード
  2. ファイルレスポンス
  2. 国際化対応
  2. ログ
  2. モニタリング
  2. セキュリティ
  2. デバッグ
  2. テスト
  2. 独自テンプレートエンジン


## 0. はじめに

このシリーズはXitrumを使ったWebアプリケーション開発を通じて
Xitrumの普及、筆者自身のScala/Xitrum/Web開発スキルアップを目指します。

2014/07/25現在の

 * ~~Xitrumの安定バージョンは [3.16](http://search.maven.org/#artifactdetails%7Ctv.cntt%7Cxitrum_2.11%7C3.16%7Cjar)~~
 * ~~2014/08/02 Xitrumの安定バージョンは [3.17](http://search.maven.org/#artifactdetails%7Ctv.cntt%7Cxitrum_2.11%7C3.17%7Cjar)~~
 * 2014/08/27 Xitrumの安定バージョンは [3.18](http://search.maven.org/#artifactdetails%7Ctv.cntt%7Cxitrum_2.11%7C3.18%7Cjar)
 * Scalaの安定バージョンは [2.11.2](http://downloads.typesafe.com/scala/2.11.2/scala-2.11.2.tgz?_ga=1.179477284.109116044.1401169577)


を対象としていますが、バージョンアップがあった場合は適宜対応したいと考えています。

シリーズの構成は[Ruby on Rails チュートリアル](http://railstutorial.jp/)のようなユーザー管理ができるブログアプリケーション作成をメインに
[Xitrumガイド](http://xitrum-framework.github.io/guide/ja/index.html)のトピックや筆者自身が勉強・紹介したい項目を随時追加していきます。


## ライセンス

記事とサンプルコードは[MIT](http://opensource.org/licenses/mit-license.php)ライセンスで[github](https://github.com/georgeOsdDev/xitrum-kotohajime)で公開します。
Copyright © 2014 by [Takeharu.Oshida](http://about.me/takeharu.oshida).
