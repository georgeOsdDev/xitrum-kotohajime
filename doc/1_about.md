# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](https://)

## 1. Xitrumと関連プロジェクトについて

今回はXitrumと関連プロジェクトについて簡単に紹介します。

### 1-1. Xitrumについて

> Xitrumは [Netty](http://netty.io) と [Akka](http://akka.io) をベースに構築された非同期でスケーラブルなHTTP(S) WEBフレームワークです。

NettyやAkkaといった低レイヤの強力なライブラリをWebアプリケーション開発者が使用しやすいカタチで提供してくれ、
Webアプリケーション構築に必要な機能（例えばルーティング)をとても簡単なAPIで実現することができます。
非同期処理やルーティングなど、難しいことはXitrumに任せてアプリケーション開発者は、
アプリケーションロジックにのみ集中して取り組むことができます。

このシリーズでもXitrumが内部で何をやっているかにはあまり触れず、Xitrumが提供するAPIの使い方を中心に紹介したいと思います。

また、XitrumはいわゆるMVCフレームワークではありません。そのため、Ruby on Railsのような規約はほとんどありません。
クラスはXitrumの提供するクラスを継承してさえいれば、自由につくることができます。
Actionの中にhtmlを書いてしまうこともできますし、テンプレートファイル内にロジックを含めることもできます。
また、XitrumにはHibernateやActiveRecordのようなORマッパー機能は含まれません。
あくまでもXitrumはルーティングを元にリクエスト・レスポンスを処理するサーバー機能にフォーカスされており、
アプリケーションロジックやプログラミングのスタイルについては開発者に委ねられています。

そのほかのXitrumの特徴は[公式ページ](http://xitrum-framework.github.io/index_ja.html)にまとめられています。

### 1-2. 関連プロジェクト

[Project リポジトリ](https://github.com/xitrum-framework/)にはいくつかの関連プロジェクトとして、
デモアプリケーションプロジェクトの他、Xitrumが内部で使用しているライブラリ、テンプレートライブラリ、プラグイン、デバッグツールなどとなどがあります。

開発者がXitrumアプリケーションを作成し始める時に必要となるプロジェクトは、
Project スケルトンである[Xitrum-new](https://github.com/xitrum-framework/xitrum-new)となります。

Xitrum-newプロジェクトのbuild.sbtにはアプリケーションが直接依存するライブラリが記載されています。
このなかにXitrumが含まれています。
Xitrumプロジェクト本体のbuild.sbtにはXitrumが依存するライブラリが記載されています。

### 1-3. リンク

[Project リポジトリ](https://github.com/xitrum-framework/)
[公式ページ](http://xitrum-framework.github.io/index_ja.html)
[公式ガイド](http://xitrum-framework.github.io/guide)
[APIドキュメント](http://xitrum-framework.github.io/api/index.html)
[デモページ](http://107.167.187.67/)
[コミュニティ](https://groups.google.com/forum/#!forum/xitrum-framework)
