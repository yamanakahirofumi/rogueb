# rogueb (Rogue Backend Services)

`rogueb` は、マイクロサービスアーキテクチャを採用したローグライクゲームのバックエンドサービス群です。
Spring Boot 3.5.5, Java 21, Spring WebFlux を使用しており、リアクティブな設計となっています。

## 採用理由と目的
このアーキテクチャを採用する目的は以下の通りです：
- **スケールアウトの容易性**: 特定のサービス（ダンジョン生成など）のみを個別に拡張できるようにするため。
- **影響範囲の局所化とバリアント開発の促進**: サービス間の結合を疎にすることで、変更の影響範囲を小さくし、新しいバリアントや機能が開発されやすくするため。

## プロジェクト構成

主要なモジュールとその役割は以下の通りです。

- **PlayerOperations**: BFF (Backend For Frontend) として機能し、プレイヤーの操作を統合管理します。
- **Dungeon**: ダンジョンの構造、フロア生成、座標管理を担当します。
- **Objects**: アイテム、オブジェクト、その履歴管理を担当します。
- **BookOfAdventure**: プレイヤーの状態、セーブデータの永続化を担当します。
- **World**: サービスレジストリおよびワールドの初期情報提供を担当します。

## 詳細ドキュメント

システムの詳細な仕様については、`docs/` フォルダ配下のドキュメントを参照してください。

- [ドキュメント一覧](docs/README.md)
- [コンポーネント構成](docs/features/components.md)
- [ドメインモデル](docs/features/domain_models/index.md)

## 開発の始め方

### 必須環境
- Java 21
- Maven 3.x
- MongoDB (Docker Compose で起動可能)

### インフラの起動
```bash
docker-compose up -d
```

### サービスのビルド
```bash
./mvnw clean install
```

### 各サービスの起動
各モジュールディレクトリで以下を実行します（例: PlayerOperations）。
```bash
cd PlayerOperations
../mvnw spring-boot:run
```

## ライセンス
このプロジェクトは MIT ライセンスの下で公開されています。詳細は [LICENSE](LICENSE) を参照してください。
