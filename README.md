<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="128" alt="dimore icon" />
</p>

<h1 align="center">dimore</h1>

<p align="center">
  画面をもっと暗く。ソフトウェアオーバーレイで限界を超えた暗さを実現する Android アプリ。
</p>

<p align="center">
  Android 16 (API 36) 専用
</p>

---

## ダウンロード

[GitHub Releases](../../releases/latest) から最新の APK をダウンロードできます。

> [!IMPORTANT]
> APK を端末上で直接タップしてインストールすると、Android 15+ の Enhanced Confirmation Mode によりオーバーレイ権限が制限されます。下記「インストール」の手順に従ってください。

## インストール

Enhanced Confirmation Mode を回避するため、信頼されたインストーラー (shell uid) 経由でインストールする必要があります。

### 方法 1: adb install (PC 必要)

```sh
adb install dimore.apk
```

### 方法 2: Shizuku + rish (PC 不要)

[Shizuku](https://shizuku.rikka.app/) をセットアップ済みなら rish 経由でインストールできます。

```sh
rish -c "pm install /path/to/dimore.apk"
```

### APK タップでインストール済みの場合

一度アンインストールしてから上記いずれかの方法で再インストールしてください。

```sh
adb uninstall com.lumakernel.dimore
adb install dimore.apk
```

### 権限だけ手動で付与する (Shizuku)

APK タップでインストール済みでも、権限だけ後から付与できます。

```sh
rish -c "appops set com.lumakernel.dimore SYSTEM_ALERT_WINDOW allow"
```

## 使い方

1. アプリを起動
2. 「権限を許可する」からオーバーレイ権限を付与
3. 「オーバーレイ ON」で画面を暗くする
4. スライダーで明度を調整 (0% - 90%)

> [!TIP]
> Shizuku がインストールされていれば、アプリ内から直接オーバーレイ権限を付与できます。

## 通知操作

オーバーレイ有効時、通知から素早く操作できます。

| アクション | 動作 |
|---|---|
| OFF | オーバーレイを停止 |
| 暗く / 明るく | 明度を 10% 刻みで調整 |
| タップ | アプリを開いてスライダーで細かく調整 |

## 設定

| 項目 | 説明 |
|---|---|
| 起動時自動有効化 | アプリ起動時にオーバーレイを自動で ON にする |
| オフ時の通知 | 常に表示 / 即時非表示 / 10 秒後に非表示 |

## ビルド

```sh
ANDROID_HOME=/path/to/android-sdk ./gradlew assembleDebug
```

APK は `app/build/outputs/apk/debug/app-debug.apk` に出力されます。

## ライセンス

MIT
