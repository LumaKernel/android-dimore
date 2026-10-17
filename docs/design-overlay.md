# オーバーレイ設計

## 権限

- `SYSTEM_ALERT_WINDOW` が必要
- AndroidManifest.xml に `<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />` を宣言
- `Settings.canDrawOverlays(context)` で権限状態を確認
- 未許可の場合、`Settings.ACTION_MANAGE_OVERLAY_PERMISSION` で設定画面へ遷移させる
- この権限はランタイムパーミッションではなく、設定画面での手動許可が必要

## オーバーレイ描画

- `WindowManager.addView()` で全画面の半透明黒 View を追加
- LayoutParams:
  - type: `WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY`
  - flags: `FLAG_NOT_FOCUSABLE or FLAG_NOT_TOUCHABLE or FLAG_LAYOUT_IN_SCREEN`
    - タッチイベントを透過させる (下のアプリを操作可能にする)
    - フォーカスを奪わない
  - format: `PixelFormat.TRANSLUCENT`
  - width/height: `MATCH_PARENT`
  - gravity: `Gravity.TOP or Gravity.START`
- View は単純な `android.view.View` で、背景色を `Color.BLACK` にし、alpha で明度を制御

## 明度制御

- View の alpha 値 (0.0f〜0.9f) で暗さを調整
  - 0.0f = 完全に透明 (暗くしない)
  - 0.9f = ほぼ真っ暗 (1.0f にすると画面が見えなくなるので上限を 0.9 にする)
- 初期値: 0.5f
- ユーザーがスライダーで調整

## フォアグラウンドサービス

- `OverlayService extends Service` として実装
- `startForeground()` で通知を表示しつつサービスを維持
- foregroundServiceType: Android 14+ では明示が必要。`specialUse` を使用
- サービスのライフサイクル:
  - `onCreate`: オーバーレイ View の生成
  - `onStartCommand`: オン/オフ・明度変更の Intent を受け取り処理
  - `onDestroy`: オーバーレイ View の除去
- `startForegroundService()` で起動、`stopSelf()` または `stopService()` で停止

## アクション定義

- `ACTION_START`: オーバーレイを開始 (View を追加)
- `ACTION_STOP`: オーバーレイを停止 (View を除去、サービス停止)
- `ACTION_SET_BRIGHTNESS`: 明度変更 (extra: `EXTRA_BRIGHTNESS` float)

## コンポーネント構成

- `OverlayService`: フォアグラウンドサービス。オーバーレイ View の管理
- `MainActivity`: 権限要求、オン/オフ UI、明度スライダー
- 権限要求は MainActivity で行い、許可後に OverlayService を起動する
