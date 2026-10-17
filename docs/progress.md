# dimore 進捗

## 現在のステータス

- フェーズ: フェーズ 5 (統合・仕上げ)
- 残タスク: QA (adb install で再インストール後、全機能動作確認)
- ビルド: 成功確認済み

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
- MainActivity: オン/オフトグル + 明度スライダー + 設定ボタン
- 通知設計 (docs/design-notification.md)
- pinned 通知: OFF/暗く/明るくアクション + タップでアプリ + 明度%表示
- オフ時通知制御: always/dismiss_0s/dismiss_10s 切り替え + ON アクション付き停止中通知
- 設定 UI 設計 (docs/design-settings.md)
- SettingsActivity: 起動時自動有効化 Switch + オフ時通知ラジオグループ
- 設定永続化: SharedPreferences
- 起動時自動有効化
- README 作成
- ビルド修正 (settings.gradle.kts, compileSdk 警告抑制)
- Enhanced Confirmation Mode 対応: adb install 必須をドキュメント全体に反映
