# 設定 UI 設計

## 永続化

- SharedPreferences (名前: "dimore_settings", MODE_PRIVATE)
- DataStore は不要 (設定項目が少なく、非同期アクセスの必要がない)
- OverlayService で既に同じ SharedPreferences を使用済み

## 設定項目

| キー | 型 | デフォルト | 説明 |
|---|---|---|---|
| `auto_enable_on_launch` | Boolean | false | アプリ起動時にオーバーレイを自動でオンにする |
| `off_notification_mode` | String | "always" | オフ時の通知挙動。値: "always", "dismiss_0s", "dismiss_10s" |

## 画面構成

### メイン画面 (MainActivity)

- タイトル: "dimore"
- 権限未許可時: 権限要求ボタン (既存)
- 権限許可済み時:
  - オン/オフトグルボタン (既存)
  - 明度スライダー (既存)
  - 設定画面への遷移ボタン

### 設定画面 (SettingsActivity or 同一 Activity 内の画面遷移)

- Compose Navigation は過剰。SettingsActivity を別 Activity として作成
- 設定項目:
  1. 起動時自動有効化: Switch
  2. オフ時通知: ラジオグループ
     - 常に表示
     - 即時非表示 (0秒)
     - 10秒後に非表示

## UI フロー

- メイン画面の「設定」ボタン → SettingsActivity を startActivity
- SettingsActivity で SharedPreferences を読み書き
- 戻るボタンでメイン画面に戻る
