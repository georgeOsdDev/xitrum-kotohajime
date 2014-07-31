# Xitrumことはじめ　(基本編)

Xitrumことはじめシリーズでは、Xitrumを使ったWebアプリケーション開発を勉強します。

目次は[こちら](https://)

## 2. Xitrumアプリケーション開発環境の構築

Xitrumを使ったWebアプリケーション開発を行うための環境構築をおこないます。

2014/07/25現在の

 * Xitrumの安定バージョンは [3.16(π)](http://search.maven.org/#artifactdetails%7Ctv.cntt%7Cxitrum_2.11%7C3.16%7Cjar)
 * Scalaの安定バージョンは [2.11.2](http://downloads.typesafe.com/scala/2.11.2/scala-2.11.2.tgz?_ga=1.179477284.109116044.1401169577)

を使用していきます。
また、筆者はMacOSXを使用しています。WindowsやLinux環境で実施する場合は、コマンドやパスなど適宜変更してください。

### 2-1. 前提条件

本シリーズを進める上でGitやJDK、Scalaのインストール手順については細かく触れません。
以下の参考資料をもとに適宜インストールしてください。

* [Gitのインストール](http://git-scm.com/book/ja/%E4%BD%BF%E3%81%84%E5%A7%8B%E3%82%81%E3%82%8B-Git%E3%81%AE%E3%82%A4%E3%83%B3%E3%82%B9%E3%83%88%E3%83%BC%E3%83%AB)
* [JDK 7のインストール](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
* [Scalaのインストール](http://www.scala-lang.org/download/)

Javaについては今回はJava7を使用します。ただし、Java8でも動くことでしょう。
また、OracleJDKを使用します。OpenJDKでも動作はしますが、
[FileMonitorに関わる機能](https://github.com/xitrum-framework/xitrum/blob/ded7bbbd81688f036d48c1792a8460a2d45e1a16/src/main/scala/xitrum/util/FileMonitor.scala#L41)や、[Scalive](https://github.com/xitrum-framework/scalive)などOracleJDKでしか使用できない機能があるため、OracleJDKを使用することを推奨します。


### 2-2. Scala IDEのインストール

今回はEclipseベースの[Scala IDE](http://scala-ide.org/index.html)を使用します。
[ダウンロードページ](http://scala-ide.org/download/sdk.html)に各マシン環境向けのzipファイルがあるため、For Scala 2.11.2というところから
ダウンロードします。
インストールは簡単です。ダウンロードしたzipファイルを展開して、eclipseというフォルダ内のEclipse.appを起動するだけです。

	wget http://downloads.typesafe.com/scalaide-pack/3.0.4.vfinal-211-20140723/scala-SDK-3.0.4-2.11-2.11-macosx.cocoa.x86_64.zip
	unzip scala-SDK-3.0.4-2.11-2.11-macosx.cocoa.x86_64.zip
	rm scala-SDK-3.0.4-2.11-2.11-macosx.cocoa.x86_64.zip
	mv -R eclipse Applications/ScalaIDE

2014/07/26現在の最新版は3.0.4で以下の内容が含まれます。

>Content
 * Eclipse 4.3.1 (Kepler)
 * Scala IDE 3.0.3/3.0.4
 * Sbt 0.13.2
 * Scala Worksheet 0.2.3
 * Play Framework support 0.4.3 (Scala 2.10 only)
 * m2eclipse-scala Maven connector 0.4.3
 * access to the full Scala IDE ecosystem

Scala IDEの他にもScalaの開発環境は[Eclipse](https://www.eclipse.org/)、 [Intelligent IDE](http://www.jetbrains.com/idea/features/scala.html)、[Activator](https://typesafe.com/activator)などがあり、どれを使用しても開発を行うことは可能です。
ちなみに、Xitrum作者のNgocが主に使用しているのはEclipseです。

さあこれでScala開発をおこなう準備が完了しました。
基本的にはXitrumアプリケーションの開発にはこれ以外に特別な準備は必要ありません。
以降必要な外部ライブラリなどはsbtでダウンロードを行うことになります。


### 2-3. 実行環境の準備

Xitrumアプリケーションは基本的にはJVM(Java7+)がある環境であればどこでも動かすことができます。
開発時はローカルマシン上での動作確認を行うことができます。
その他のミドルウェアの準備は必要ありません。（サンプルアプリケーション作成時にMongoDBが必要になります。）
もちろんNginxやHAProxyをリバースプロキシとしてXitrumと併用することも可能です。

ちなみに[VXiM](https://github.com/georgeOsdDev/vxim)はXitrum（とMongoDB)を簡単に動かすVagrantBOXとなりますので、
興味があれば使ってみてください。

サーバーへデプロイする場合も基本的にはJVM以外は必要ありませんし、OSの制約もありません
Xitrumをプロダクション環境へで使用しているサービスには、
オンプレミスで運用しているケース、[Amazon EC2](http://aws.amazon.com/jp/ec2/)の他、[Google Compute Engine](https://cloud.google.com/products/compute-engine/?hl=ja)、[Heroku](https://www.heroku.com/)、[CloudN](http://www.ntt.com/cloudn/)などのクラウドサービス上で動いているものがあります。
