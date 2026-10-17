# dimore タスク管理

## フェーズ 1: プロジェクト基盤

- [x] [設計] CLAUDE.md 作成: バージョニング v{n}.0.0 形式、バージョンファイルの整数を更新毎に +1 する強制ルール、開発規約
- [x] [設計] Android プロジェクトスキャフォールド: Kotlin, Jetpack Compose, targetSdk=Android 16, minSdk=36 (Android 16 のみ)
- [x] [型] バージョンファイル作成: version.txt に整数 1 を記録
- [x] [ロジック] ビルド設定: build.gradle.kts, バージョンファイル読み込み, APK 出力設定 (ストア配布なし、APK 直接インストール前提)

## フェーズ 2: コア機能 (オーバーレイ)

依存: フェーズ 1 完了

- [x] [設計] オーバーレイ設計: docs/design-overlay.md に記載。WindowManager + SYSTEM_ALERT_WINDOW、alpha 制御 (0.0〜0.9)、フォアグラウンドサービス
- [x] [ロジック] 権限要求フロー: SYSTEM_ALERT_WINDOW 権限の宣言・確認・設定画面遷移
- [x] [ロジック] オーバーレイ描画: WindowManager で半透明黒 View を被せる (OverlayService)
- [x] [ロジック] 明度制御: オーバーレイの alpha 値で明度を調整 (0.0〜0.9)
- [x] [ロジック] フォアグラウンドサービス: OverlayService (specialUse foregroundServiceType)

## フェーズ 3: 通知

依存: フェーズ 2 完了

- [x] [設計] 通知設計: docs/design-notification.md に記載。通知アクションで OFF/暗く/明るく、タップでアプリ内スライダー
- [x] [ロジック] 通知チャンネル作成 (既存、変更なし)
- [x] [ロジック] pinned 通知 (オーバーレイ有効時): OFF/暗く/明るくアクション、タップでアプリ起動、明度%表示
- [x] [ロジック] 通知からの明度調整: BRIGHTNESS_UP/DOWN アクションで 10% 刻み調整
- [x] [ロジック] オフ時通知制御: SharedPreferences "off_notification_mode" で always/dismiss_0s/dismiss_10s を切り替え。ONアクション付き停止中通知

## フェーズ 4: 設定 UI

依存: フェーズ 2, 3 の設計完了

- [x] [設計] 設定 UI 設計: docs/design-settings.md に記載。SharedPreferences、SettingsActivity 別画面
- [x] [ロジック] 設定永続化: SharedPreferences で auto_enable_on_launch (Boolean), off_notification_mode (String)
- [x] [UI] 設定画面: SettingsActivity。起動時自動有効化 Switch、オフ時通知ラジオグループ (常に表示/即時非表示/10秒後非表示)
- [x] [UI] メイン画面: 設定ボタン追加。オン/オフトグル・明度スライダーは既存

## フェーズ 5: 統合・仕上げ

依存: フェーズ 1〜4 完了

- [x] [ロジック] 起動時自動有効化: onCreate で SharedPreferences を読み、権限ありならオーバーレイ自動起動
- [ ] [QA] 全機能の動作確認: オーバーレイ、通知、設定の連携
- [x] [メタ] README 作成: ビルド・インストール手順、使い方、通知操作、設定項目

## 判断が必要な事項

- ~~minSdk をどこに設定するか~~ → API 36 のみ (Android 16 専用) に決定
- ~~通知からの明度調整~~ → RemoteViews スライダーは不安定。通知アクションボタン (+/-) で 10% 刻み + タップでアプリ内スライダーに決定
