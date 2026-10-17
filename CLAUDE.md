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
- 配布: adb install によるAPKインストール (Google Play 非使用、APK直接タップ不可)

## プロジェクト構造

- `docs/spec.md` — 仕様
- `docs/tasks.md` — タスク管理
- `docs/progress.md` — 進捗管理

## 開発ルール

- タスクの進行は docs/tasks.md のステータスを更新しながら進める
- 1 コミット = 1 タスク単位を目安にする
- Kotlin コードは ktlint でフォーマットする (`./gradlew ktlintFormat`)
- CI で ktlintCheck と assembleDebug が通ることを確認してから push する

## リリース

- tag push (`v{n}.0.0`) で GitHub Actions が自動リリースする
- リリース手順: version.txt を +1 してコミット → `git tag v{n}.0.0` → `git push --tags`
- 署名用 keystore は GitHub Secrets に格納 (KEYSTORE_BASE64, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD)
- CD ワークフローはタグと version.txt の整合性を検証する (不一致時はビルド失敗)
- version.txt を変更したコミットを push したら、タグ push してリリースするかユーザーに確認する
