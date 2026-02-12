# ドメインモデルドキュメント

このドキュメントは、`rogueb`アプリケーションにおける主要な各コンポーonent（サービス）のコアとなるドメインモデルの概要を提供します。

各ドキュメントでは、特定のモジュールに関する主要な責務、エンティティ、および値オブジェクトについて概説します。

## モジュール別ドキュメント

### アプリケーションロジック

- **[Dungeonモジュール](./dungeon.md)**
  - ダンジョン、フロア、タイルの構造、生成、および状態を管理します。

- **[Worldモジュール](./world.md)**
  - 利用可能なダンジョンを発見し、高レベルのワールド情報を提供するサービスです。

- **[Objectsモジュール](./objects.md)**
  - 武器、指輪、金などのすべてのゲーム内アイテム（`Thing`）を定義および管理します。

- **[PlayerOperationsモジュール](./player_operations.md)**
  - プレイヤーの操作、およびインベントリ（`Bag`）の管理を調整します。

- **[BookOfAdventureモジュール](./book_of_adventure.md)**
  - プレイヤーの状態、キャラクター情報の永続化を担当します。

### データベース構造

- **[Dungeonモジュール (MongoDB)](./dungeon_mongodb.md)**
  - `dungeon`および`floor`コレクションのスキーマ情報。

- **[Objectsモジュール (MongoDB)](./objects_mongodb.md)**
  - `ring`および`objectHistory`コレクションのスキーマ情報。

- **[BookOfAdventureモジュール (MongoDB)](./book_of_adventure_mongodb.md)**
  - `playerDomain`コレクションのスキーマ情報。
