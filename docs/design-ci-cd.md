# CI/CD 設計

## CI (GitHub Actions)

- トリガー: push to main, pull request
- ジョブ:
  - ktlint check (`./gradlew ktlintCheck`)
  - compile check (`./gradlew assembleDebug`)
- ランナー: ubuntu-latest
- JDK: 17 (temurin)
- Android SDK: compileSdk 36 (API 36 がランナーで未提供の場合は sdkmanager でインストール)

## ktlint

- Gradle プラグイン: `org.jlleitschko.ktlint-gradle` (もしくは `org.jmailen.kotlinter`)
- 既存コードは初回で `ktlintFormat` を実行してフォーマット統一
- CI では `ktlintCheck` のみ実行 (自動修正はしない)

## CD (自動リリース)

- トリガー: tag push (`v*`)
- ジョブ:
  - `./gradlew assembleRelease` (署名付き)
  - GitHub Release 作成 + APK アップロード
- tag は `v{n}.0.0` 形式 (version.txt の値と一致)

## APK 署名

- keystore は GitHub Secrets に base64 エンコードで格納
  - `KEYSTORE_BASE64`: keystore ファイルの base64
  - `KEYSTORE_PASSWORD`: keystore パスワード
  - `KEY_ALIAS`: key alias
  - `KEY_PASSWORD`: key パスワード
- CI 上で base64 デコードして一時ファイルに復元、ビルド後に削除
- build.gradle.kts の signingConfigs で環境変数から読み込み
- ローカルビルドでは debug 署名を使用 (signingConfig が未設定なら debug fallback)

## リリースフロー

1. version.txt を +1 してコミット
2. `git tag v{n}.0.0` でタグ作成
3. `git push --tags` で push
4. GitHub Actions が自動で release workflow を実行
5. GitHub Release に署名済み APK がアップロードされる
