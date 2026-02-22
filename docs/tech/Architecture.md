# アーキテクチャ設計

本プロジェクトは、マルチモジュール構成の Spring Boot アプリケーション群で構成されています。リアクティブプログラミング（Spring WebFlux）を採用し、スケーラビリティと非同期処理を重視した設計となっています。

## 1. モジュール構造

プロジェクトは以下の役割を持つ複数のモジュールに分割されています。

- **Service モジュール**: 独立した Spring Boot アプリケーション（Dungeon, World, Objects, BookOfAdventure）。
- **BFF (Backend For Frontend) モジュール**: プレイヤー操作を受け付け、各 Service を集約するモジュール（PlayerOperations）。
- **Client モジュール**: 他のサービスを呼び出すための Feign 的な役割を持つライブラリ（DungeonClient, WorldClient 等）。
- **Base / Commons モジュール**: 共通の型、ユーティリティ、基底クラスを保持するライブラリ（DungeonBase, Commons）。

## 2. ディレクトリ・パッケージ構造

標準的な Maven マルチモジュール構造を採用しています。

```
.
├── pom.xml                # 親 POM
├── [Module Name]          # 各モジュールディレクトリ
│   ├── pom.xml            # モジュール POM
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── net.hero.rogueb.[module]
│       │   │       ├── [Module]Application.java  # エントリーポイント
│       │   │       ├── controller/   # REST エンドポイント
│       │   │       ├── service/      # ビジネスロジック
│       │   │       ├── domain/       # ドメインモデル・エンティティ
│       │   │       └── repository/   # データアクセス層
│       │   └── resources
│       │       ├── application.properties
│       │       └── [MyBatis Mapper 等]
│       └── test           # ユニット・統合テスト
```

## 3. 主要コンポーネントの責務

### 3.1 Controller 層 (REST)
- 外部（PlayerOperations または直接）からの HTTP リクエストを処理します。
- 非同期・ノンブロッキングなレスポンス（`Mono`, `Flux`）を返却します。

### 3.2 Service 層
- ドメインロジックを実装し、複数のリポジトリや外部クライアントを調整します。

### 3.3 Domain / Repository 層
- **Domain**: ゲームの状態を保持するエンティティ。MongoDB や RDB のマッピング対象。
- **Repository**: Spring Data MongoDB や MyBatis を使用したデータ永続化。

## 4. デザイン方針
- **リアクティブ**: Spring WebFlux と Project Reactor を活用し、スレッドを効率的に利用します。
- **不変性 (Immutability)**：DungeonBase や Commons で定義されるデータ型については `record` や `final` を活用し、副作用を最小限にします。
- **疎結合**: サービス間通信は Client モジュールを介して行い、各サービスが自律的に動作するようにします。
