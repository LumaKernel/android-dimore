# 通知設計

## 通知チャンネル

- チャンネル ID: `dimore_overlay`
- チャンネル名: "dimore オーバーレイ"
- 重要度: `IMPORTANCE_LOW` (音なし、ステータスバーに表示)
- 既に OverlayService で作成済み。追加チャンネルは不要

## オーバーレイ有効時の通知 (pinned)

- ongoing = true で固定表示
- アクションボタン:
  - "OFF": オーバーレイを停止する (ACTION_STOP を送信)
  - "暗く": 明度を +0.1 する (ACTION_BRIGHTNESS_UP)
  - "明るく": 明度を -0.1 する (ACTION_BRIGHTNESS_DOWN)
- 通知アクションは最大3つなのでこれで枠を使い切る
- タップ時: MainActivity を開く (明度スライダーで細かい調整が可能)
- contentText に現在の明度を表示 (例: "明度: 50%")

### 明度調整の技術判断

- RemoteViews にスライダー (SeekBar) を配置する方法は不安定で非推奨
- 代わりに "+/-" アクションボタンで 10% 刻みの段階調整を採用
- 細かい調整が必要な場合は通知タップでアプリ内スライダーを使う
- この方式は spec の「技術的に困難な場合は、通知タップでアプリ内の明度調整UIに遷移する形でもよい」に合致

## オーバーレイ無効時の通知

- 設定に応じた3パターン:
  1. 常に表示: ongoing = true のまま残す。テキストを "オーバーレイ停止中" に変更。アクションは "ON" のみ
  2. 一定時間後に非表示 (10秒): stopForeground 後に通常通知を投稿し、Handler.postDelayed(10000) で cancel
  3. 一定時間後に非表示 (0秒): stopForeground(STOP_FOREGROUND_REMOVE) で即時非表示

## アクション Intent 追加

既存の ACTION_START / ACTION_STOP / ACTION_SET_BRIGHTNESS に加え:
- `ACTION_BRIGHTNESS_UP`: 現在値 +0.1 (上限 0.9)
- `ACTION_BRIGHTNESS_DOWN`: 現在値 -0.1 (下限 0.0)

これらは PendingIntent として通知アクションに設定する。

## 実装方針

- 通知の構築は OverlayService 内のメソッドで行う
- 明度変更時に通知を更新する (notificationManager.notify で上書き)
- PendingIntent は Service への Intent として構成 (PendingIntent.getService)
