# CI/CD 設定

本プロジェクトでは、GitHub Actions を使用してビルドとテストの自動化を行っています。

## 1. GitHub Actions 設定

### ワークフローの概要
GitHub へのプッシュ（Push）またはプルリクエスト（Pull Request）が作成された際に、以下のプロセスが自動的に実行されます。

1. **チェックアウト**：リポジトリのソースコードを取得します。
2. **Java のセットアップ**：プロジェクトで規定されたバージョンの JDK をセットアップします（詳細は [技術スタック](Tech-Stack.md) を参照）。
3. **Maven 依存関係のキャッシュ**：ビルド時間の短縮のため、Maven の依存関係をキャッシュします。
4. **ビルドとテスト**：`mvn verify` を実行し、プロジェクトのビルド、JUnit によるテスト、および JaCoCo によるカバレッジ測定を実施します。
5. **アーティファクトの生成（将来計画）**：カスタム JRE を含む配布用パッケージの自動生成を検討しています。

### 設定ファイルの例 (`.github/workflows/build.yml`)
以下は、Maven を使用した標準的なワークフロー構成です。

```yaml
name: Java CI with Maven

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'
        cache: maven
    - name: Build with Maven
      run: mvn -B verify
    - name: Upload Coverage
      uses: actions/upload-artifact@v4
      with:
        name: jacoco-report
        path: target/site/jacoco/
```

## 2. CI の目的
- **品質の維持**：常にビルドが通る状態を維持し、意図しない破壊的変更を早期に発見します。
- **自動テスト**：JUnit によるユニットテストを自動実行し、ロジックの正しさを検証します。
- **カバレッジの可視化**：テストの網羅率を測定し、テスト不足の箇所を特定します。
- **継続的な配布準備**：カスタム JRE 作成プロセスを自動化し、いつでも配布可能な状態を維持します。
