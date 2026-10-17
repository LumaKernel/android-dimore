# dimore 開発規約

## バージョニング

- バージョン形式: v{n}.0.0
- バージョン番号は `version.txt` に整数で記録する
- 機能追加・変更・修正を含むコミットでは、必ず `version.txt` の整数を +1 する
- ドキュメントのみの変更ではバージョンを上げない

## 技術スタック

- 言語: Kotlin
- UI: Jetpack Compose
- ターゲット: Android 16 (API 36)
- 配布: APK 直接インストール (Google Play 非使用)

## プロジェクト構造

- `docs/spec.md` — 仕様
- `docs/tasks.md` — タスク管理
- `docs/progress.md` — 進捗管理

## 開発ルール

- タスクの進行は docs/tasks.md のステータスを更新しながら進める
- 1 コミット = 1 タスク単位を目安にする
