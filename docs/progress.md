# dimore 進捗

## 現在のステータス

- フェーズ: フェーズ 3 完了 → フェーズ 4 (設定 UI) へ
- 次のタスク: 設定 UI 設計
- 備考: ビルド確認未実施

## 完了済み

- docs/spec.md 作成
- docs/tasks.md 作成
- CLAUDE.md 作成
- Android プロジェクトスキャフォールド (Kotlin + Jetpack Compose, API 36)
- version.txt 作成 (初期値: 1)
- ビルド設定 (build.gradle.kts, version.txt 読み込み, APK 出力)
- オーバーレイ設計 (docs/design-overlay.md)
- 権限要求フロー (SYSTEM_ALERT_WINDOW)
- OverlayService: オーバーレイ描画 + 明度制御 + フォアグラウンドサービス
- MainActivity: オン/オフトグル + 明度スライダー
- 通知設計 (docs/design-notification.md)
- pinned 通知: OFF/暗く/明るくアクション + タップでアプリ + 明度%表示
- オフ時通知制御: always/dismiss_0s/dismiss_10s 切り替え + ON アクション付き停止中通知
