# dimore

画面を暗くするAndroidアプリ。ソフトウェアオーバーレイで画面上に半透明の黒いレイヤーを被せる。

Android 16 (API 36) 専用。APK直接インストール。

## ビルド

```
ANDROID_HOME=/path/to/android-sdk ./gradlew assembleDebug
```

APKは `app/build/outputs/apk/debug/app-debug.apk` に出力される。

## インストール

```
adb install app/build/outputs/apk/debug/app-debug.apk
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
