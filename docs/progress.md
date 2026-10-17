# dimore 進捗

## 現在のステータス

- フェーズ: 全実装完了、QA 待ち
- 残タスク (QA のみ):
  - Phase 8: 権限管理 QA (実機確認待ち)
  - Phase 9: CI/CD QA (push 後に確認)
  - Phase 11: 権限 UI QA (実機確認待ち)
  - Phase 14: Shizuku UI QA (実機確認待ち)
  - Phase 15: アプリ情報 QA (実機確認待ち)
- ブロッカー: 20コミット以上が未push。push すれば CI/CD QA を開始できる
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
- オーバーレイ alpha 修正: View.alpha → Color.argb (FLAG_NOT_TOUCHABLE の 0.8 制限回避)
- オフ時通知 dismiss_10s 修正: stopSelf() タイミングを遅延実行後に変更
- QA 完了: エミュレータで全機能動作確認
- ランチャーアイコン設定: dimore-icon-09-bulb、Adaptive Icon + 各密度 mipmap
- 通知アイコン変更: ic_notification Vector Drawable (SVGから変換した電球シルエット)
- アイコン QA 完了: ランチャー (Adaptive Icon) ・通知ともにエミュレータで確認
- README インストール手順拡充: adb install / Shizuku rish / appops set の3パターン
- spec.md 更新: 権限セクション追加 (POST_NOTIFICATIONS, Shizuku 連携)
- POST_NOTIFICATIONS ランタイム権限要求: onCreate で要求、onResume で状態更新
- Shizuku 連携: ShizukuHelper (IShizukuService.newProcess で appops set 実行)、MainActivity に「Shizuku で権限を付与」ボタン (Shizuku 利用可能時のみ表示)
- ANR修正: grantOverlayPermissionViaShizuku をバックグラウンドスレッドで実行するよう変更
- CI/CD 設計: docs/design-ci-cd.md 作成 (ktlint, GitHub Actions CI/CD, APK署名, 自動リリース)
- ktlint 導入: org.jlleitschuh.gradle.ktlint 12.1.2、.editorconfig で Compose 関数名許可、全コードフォーマット済み
- CI workflow: .github/workflows/ci.yml (ktlintCheck + assembleDebug, push/PR トリガー)
- リリース署名設定: signingConfigs で KEYSTORE_FILE/PASSWORD/KEY_ALIAS/KEY_PASSWORD 環境変数から読み込み
- CD workflow: .github/workflows/release.yml (tag push v* → assembleRelease → GitHub Release + APK)
- CLAUDE.md 更新: ktlint 必須、CI チェック、リリース手順を追記
- spec.md メイン画面 UI セクション追加: SYSTEM_ALERT_WINDOW / POST_NOTIFICATIONS / Shizuku の3権限ごとの状態分岐を定義
- spec.md 配布形態修正: Shizuku rish インストール前提を削除、権限有効化手段として整理
- 通知権限 UI: 未許可時に警告テキスト (error色) + 「通知を許可する」ボタン表示
- Shizuku 権限要求 UI: 起動済み・権限未付与時に「Shizuku 権限を許可する」ボタン、権限付与済み時のみ「Shizuku で権限を付与」ボタン表示
- README インストールセクション修正: adb install / 通常インストール+権限調整の2択に整理、Shizuku rish・APKタップ済みセクション削除
- spec.md 配布形態修正完了 (Shizuku rish インストール記述は既に削除済み)
- UNLICENSE ファイル作成 + README ライセンスセクション更新
- spec.md Shizuku UI 方針変更: 未インストール/未起動時も UI を表示する (隠さない)
- Shizuku 未インストール/未起動時: 「Shizuku を導入すると PC なしで権限を付与できます」案内テキスト表示
- spec.md アプリ情報セクション追加: メイン画面にアプリ名+バージョン、About にビルド情報/ライセンス/リポURL
- メイン画面にバージョン表示: BuildConfig 有効化、アプリ名直下に VERSION_NAME 表示
- BuildConfig に REPOSITORY_URL 埋め込み: GITHUB_REPOSITORY 環境変数 or git remote から生成 (不正ならビルドエラー)
- About セクション: 設定画面下部にバージョン、ライセンス、リポジトリ URL (クリッカブル) を表示
- 利用ライブラリライセンス一覧: AboutLibraries 導入、LicensesActivity で OSS ライセンス一覧表示、設定画面からリンク
