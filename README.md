# dimore

画面を暗くするAndroidアプリ。ソフトウェアオーバーレイで画面上に半透明の黒いレイヤーを被せる。

Android 16 (API 36) 専用。adb install でインストール。

## ビルド

```
ANDROID_HOME=/path/to/android-sdk ./gradlew assembleDebug
```

APKは `app/build/outputs/apk/debug/app-debug.apk` に出力される。

## インストール

APKファイルを端末上で直接タップしてインストールすると、Android 15+ の Enhanced Confirmation Mode により SYSTEM_ALERT_WINDOW 権限が制限され、オーバーレイ権限の設定画面で「App was denied access」と表示されて許可できない。

信頼されたインストーラー (shell uid) 経由でインストールする必要がある。

### 方法1: adb install (PC必要)

```
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 方法2: Shizuku + rish (PC不要)

[Shizuku](https://shizuku.rikka.app/) を端末にセットアップ済みなら、rish 経由でインストールできる。rish は adb と同じ shell uid で動作するため、信頼されたインストーラーとして扱われる。

```
rish -c "pm install /path/to/app-debug.apk"
```

### APKタップでインストール済みの場合

一度アンインストールしてから上記いずれかの方法で再インストールする。

```
adb uninstall com.lumakernel.dimore
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 権限だけ手動で付与する方法 (Shizuku)

APKタップでインストール済みでも、オーバーレイ権限だけ手動で許可できる。

```
rish -c "appops set com.lumakernel.dimore SYSTEM_ALERT_WINDOW allow"
```

## 使い方

1. アプリを起動
2. 「権限を許可する」からオーバーレイ権限を付与
3. 「オーバーレイ ON」で画面を暗くする
4. スライダーで明度を調整 (0%〜90%)

## 通知

オーバーレイ有効時、通知から操作できる:

- OFF: オーバーレイを停止
- 暗く / 明るく: 明度を10%刻みで調整
- タップ: アプリを開いてスライダーで細かく調整

## 設定

- 起動時自動有効化: アプリ起動時にオーバーレイを自動でオンにする
- オフ時の通知: 常に表示 / 即時非表示 / 10秒後に非表示
