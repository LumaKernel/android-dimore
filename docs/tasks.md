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
- [x] [バグ修正] インストール方法をadb installに統一: Android 15+ Enhanced Confirmation Mode 対応。README, spec, design-overlay, CLAUDE.md を更新
- [x] [QA] 全機能の動作確認: オーバーレイ、通知、設定の連携
- [x] [バグ修正] オーバーレイ alpha の FLAG_NOT_TOUCHABLE 制限回避: View.alpha → Color.argb による背景色 alpha に変更
- [x] [バグ修正] オフ時通知 dismiss_10s が動作しない: stopSelf() のタイミング修正
- [x] [メタ] README 作成: ビルド・インストール手順、使い方、通知操作、設定項目

## フェーズ 6: アイコン設定

依存: なし

- [x] [ロジック] ランチャーアイコン設定: download.ignore/dimore-icon-09-bulb から各解像度 mipmap 生成、Adaptive Icon 対応、AndroidManifest.xml に android:icon/roundIcon 追加
- [x] [ロジック] 通知アイコン変更: android.R.drawable.ic_menu_view → R.drawable.ic_notification (SVGから変換したVector Drawable)
- [x] [QA] アイコン表示確認: ランチャー (Adaptive Icon 丸型マスク) ・通知 (電球シルエット) ともに正常表示確認済み

## フェーズ 7: ドキュメント改善

- [x] [メタ] README インストール手順拡充: Enhanced Confirmation Mode の解説、adb install / Shizuku rish / appops set の3パターンの手順を追記

## フェーズ 8: 権限管理

依存: なし

- [x] [設計] spec.md 更新: 権限セクション追加 (SYSTEM_ALERT_WINDOW, POST_NOTIFICATIONS, Enhanced Confirmation Mode, Shizuku 連携)
- [x] [ロジック] POST_NOTIFICATIONS ランタイム権限要求: MainActivity の onCreate でランタイム権限要求、権限状態に応じたUI表示
- [x] [ロジック] Shizuku 連携 (オプショナル): Shizuku API 依存追加、Shizuku 利用可能時にオーバーレイ権限自動付与を提案するUI
- [ ] [QA] 権限管理の動作確認: 通知権限要求ダイアログ表示、Shizuku 連携 (実機確認待ち。コードレビュー済み: ANR修正 grantOverlayPermissionViaShizuku をバックグラウンドスレッド化)

## フェーズ 9: CI/CD

依存: なし

- [x] [設計] CI/CD 設計: docs/design-ci-cd.md に記載。GitHub Actions、ktlint、署名、自動リリース
- [x] [ロジック] CI workflow: .github/workflows/ci.yml — push/PR で lint (ktlint), compile check (assembleDebug)
- [x] [ロジック] ktlint 導入: build.gradle.kts に ktlint プラグイン追加、.editorconfig で Compose 関数名許可、既存コードフォーマット修正
- [x] [ロジック] リリース署名設定: app/build.gradle.kts に signingConfigs 追加 (環境変数から keystore 読み込み、未設定時は debug fallback)
- [x] [ロジック] CD workflow: .github/workflows/release.yml — tag push (v*) で assembleRelease + GitHub Release に署名済み APK アップロード
- [x] [メタ] CLAUDE.md 更新: CI/CD 関連の規約 (ktlint フォーマット必須、tag push でリリース)
- [ ] [QA] CI/CD 動作確認: CI が PR で動作、tag push でリリース APK が生成されること

## フェーズ 10: README 改善

- [x] [メタ] README リデザイン: アイコン画像、GitHub Releases 導線、テーブル形式、GitHub Alerts 活用

## フェーズ 11: 権限 UI 改善

依存: なし

- [x] [設計] spec.md にメイン画面 UI セクション追加: 権限ごとの状態分岐・表示内容・アクションを定義
- [x] [ロジック] 通知権限 UI: 未許可時に警告テキスト + 「通知を許可する」ボタン表示
- [x] [ロジック] Shizuku 権限要求 UI: Shizuku 起動済み・権限未付与時に「Shizuku 権限を許可する」ボタン表示
- [ ] [QA] 権限 UI 動作確認: 各権限状態での UI 表示が spec 通りか確認

## フェーズ 12: インストール手順の整理

- [x] [メタ] README インストールセクション修正: 「方法 1: adb install」と「方法 2: 通常の APK インストール + あとで権限調整 (adb/Shizuku)」の2択に。「方法 2: Shizuku + rish」「APK タップでインストール済みの場合」セクションを削除。「権限だけ手動で付与する」セクションにアプリ内からも Shizuku で付与できる旨を追記
- [x] [メタ] spec.md 配布形態修正: 「Shizuku (rish) によるAPKインストール」の記述を削除、権限の有効化手段として整理 (既に反映済み)

## フェーズ 13: ライセンス

- [x] [メタ] UNLICENSE ファイル作成: UNLICENSE のみ (パブリックドメイン)
- [x] [メタ] README ライセンスセクション更新: UNLICENSE に修正

## フェーズ 14: Shizuku UI 常時表示

依存: なし

- [x] [設計] spec.md 更新: Shizuku 未インストール/未起動時も UI を表示する方針に変更
- [x] [ロジック] Shizuku 未インストール/未起動時の UI: Shizuku の説明と導入案内を表示 (現状は非表示)
- [ ] [QA] Shizuku UI 表示確認: 未インストール/未起動/起動済み各状態での表示

## フェーズ 15: アプリ情報 UI

依存: なし

- [x] [設計] spec.md 更新: メイン画面にアプリ名+バージョン表示、アプリ情報 (About) セクション追加
- [x] [ロジック] メイン画面にバージョン表示: BuildConfig.VERSION_NAME を使ってアプリ名の下にバージョンを表示
- [x] [ロジック] BuildConfig にリポジトリ URL 埋め込み: build.gradle.kts で GITHUB_REPOSITORY 環境変数から buildConfigField 追加、デフォルト値あり
- [x] [ロジック] CI/CD workflow 更新: GITHUB_REPOSITORY は GitHub Actions 組み込み変数のため変更不要
- [x] [UI] About 画面 / セクション: ビルド情報、ライセンス、リポジトリ URL を設定画面下部に表示 (利用ライブラリライセンス一覧は別途)
- [x] [ロジック] 利用ライブラリライセンス一覧: AboutLibraries プラグインで依存ライブラリのライセンス情報を収集し、設定画面の About セクションから表示
- [ ] [QA] アプリ情報表示確認

## フェーズ 16: UI トップにアイコン表示

依存: なし

- [x] [設計] spec.md 更新: メイン画面上部にアプリアイコンを追加
- [x] [UI] メイン画面にアプリアイコン表示: mipmap のランチャーアイコンを Image で表示

## フェーズ 17: ON/OFF ラベル改善

依存: なし

- [x] [設計] spec.md 更新: トグルボタンのラベルをアクション明示型に変更
- [x] [UI] トグルボタンラベル変更: 「オーバーレイ ON」「オーバーレイ OFF」→「暗くする」/「停止する」に変更
- [ ] [QA] ラベル表示確認

## フェーズ 18: Quick Settings タイル

依存: なし

- [x] [設計] spec.md 更新: Quick Settings タイルセクション追加 (6 タイル定義、状態共有方針)
- [x] [ロジック] オーバーレイ状態の SharedPreferences 書き出し: OverlayService が有効/無効状態と現在明度を SharedPreferences に書き出す
- [x] [ロジック] TileService 実装: 6 タイル (OpenAppTile, OverlayOnTile, OverlayOffTile, OverlayToggleTile, BrightnessUpTile, BrightnessDownTile)。トグルタイルは STATE_ACTIVE/STATE_INACTIVE で状態反映
- [x] [ロジック] AndroidManifest.xml: 6 つの TileService の <service> 宣言 + ACTION_QS_TILE intent-filter + アイコン・ラベル
- [ ] [QA] Quick Settings タイル動作確認: 各タイルの追加・操作・状態反映

## バグ修正 (フェーズ外)

- [x] [バグ修正] CI: android-actions/setup-android の 'tools' パッケージ廃止対応。packages を platform-tools のみに変更。actions/setup-java を v5 に更新
- [x] [バグ修正] アプリ起動クラッシュ: painterResource が AdaptiveIconDrawable を処理できない。AndroidView + ImageView に変更

## 判断が必要な事項

- ~~minSdk をどこに設定するか~~ → API 36 のみ (Android 16 専用) に決定
- ~~通知からの明度調整~~ → RemoteViews スライダーは不安定。通知アクションボタン (+/-) で 10% 刻み + タップでアプリ内スライダーに決定
