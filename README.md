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
  2. 　レスポンスとビュー:
    3. [textをレスポンスする](http://george-osd-blog.heroku.com/50)
    3. Templateを使用してViewをレスポンスする](http://)
    3. Xitrum-ScalateとViewに関するヘルパー](http://)
    3. JavaScriptとCSS](http://)
    3. webJARによるフロントエンドライブラリの活用](http://)
  2. リクエストパラメーター:
    3. リクエストパラメーター
    3. CSRFトークン
    3. セッションとクッキー
    3. コンポーネント間でのパラメータの共有
    3. フォーム
    3. Swaggerによるドキュメンテーションとスタブクライアントの作成
  2. ブログアプリケーションの作成
    3. ログイン機能を実装する](http://)

    3. ブログアプリケーションの作成:外部ライブラリを使用してバックエンド機能を実装する
      3. DBアクセス](http://)
      3. APIリクエスト](http://)
      3. OAuth](http://)
  2. パフォーマンス最適化を行う: キャッシュ]
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
  2. WebSocket/SockJS
  2. ChannelPipeline
- [3] Tips編
  2. セキュリティ
  2. 国際化対応
  2. ドキュメンテーション
  2. Swagger codegen
  2. デバッグ
  2. テスト
  2. モニタリング
  2. Fluentd連携
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
