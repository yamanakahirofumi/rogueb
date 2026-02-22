# rogueb (Rogue Backend Services)

`rogueb` は、マルチモジュール構成で構築されたローグライクゲームのバックエンドサービス群です。
Spring Boot 3.5.5, Java 21, Spring WebFlux を使用しており、リアクティブな設計となっています。

## プロジェクト構成

主要なモジュールとその役割は以下の通りです。

- **PlayerOperations**: BFF (Backend For Frontend) として機能し、プレイヤーの操作を統合管理します。
- **Dungeon**: ダンジョンの構造、フロア生成、座標管理を担当します。
- **Objects**: アイテム、オブジェクト、その履歴管理を担当します。
- **BookOfAdventure**: プレイヤーの状態、セーブデータの永続化を担当します。
- **World**: サービスレジストリおよびワールドの初期情報提供を担当します。

## 詳細ドキュメント

システムの詳細な仕様については、`docs/` フォルダ配下のドキュメントを参照してください。

- [コンポーネント概要とAPI仕様](docs/components.md)
- [ドメインモデルの詳細](docs/domain_models/index.md)

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
