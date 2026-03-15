# ドメインモデルドキュメント

このドキュメントは、`rogueb`アプリケーションにおける主要な各コンポーネント（サービス）のコアとなるドメインモデルの概要を提供します。

各ドキュメントでは、特定のモジュールに関する主要な責務、エンティティ、および値オブジェクトについて概説します。

## 1. モジュール別ドキュメント

### アプリケーションロジック

- **[Dungeonモジュール](./Dungeon.md)**
  - ダンジョン、フロア、タイルの構造、生成、および状態を管理します。

- **[Worldモジュール](./World.md)**
  - 利用可能なダンジョンを発見し、高レベルのワールド情報を提供するサービスです。

- **[Objectsモジュール](./Objects.md)**
  - 武器、指輪、金などのすべてのゲーム内アイテム（`Thing`）を定義および管理します。

- **[Monsterモジュール (実装予定)](./Monster.md)**
  - モンスターの種族、個体状態、およびダンジョン内での配置を管理します。
  - **[モンスタードロップシステム](../Monster-Drop-System.md)**: ドロップテーブルと流通制限のロジック。

- **[PlayerOperationsモジュール](./Player-Operations.md)**
  - プレイヤーの操作、およびインベントリ（`Bag`）の管理を調整します。

- **[BookOfAdventureモジュール](./Book-Of-Adventure.md)**
  - プレイヤーの状態、キャラクター情報の永続化を担当します。

- **[経済システム (実装予定)](./Economic-System.md)**
  - アイテムの流通量、動的な価格計算、およびショップ管理を担当します。

### データベース構造

- **[Dungeonモジュール (MongoDB)](./Dungeon-MongoDB.md)**
  - `dungeon`および`floor`コレクションのスキーマ情報。

- **[Objectsモジュール (MongoDB)](./Objects-MongoDB.md)**
  - `ring`および`objectHistory`コレクションのスキーマ情報。

- **[Monsterモジュール (MongoDB) (実装予定)](./Monster-MongoDB.md)**
  - `monsterDomain`および`monsterInstanceDomain`コレクションのスキーマ情報。

- **[BookOfAdventureモジュール (MongoDB)](./Book-Of-Adventure-MongoDB.md)**
  - `playerDomain`コレクションのスキーマ情報。

---

## 2. 共通の規約

### MongoDB コレクションの命名規則
本プロジェクトでは、Spring Data MongoDB を使用しており、`@Document` アノテーションで明示的にコレクション名を指定していない場合、コレクション名はドメインクラス名の最初の文字を小文字にしたもの（camelCase）になります。

- 例: `PlayerDomain` クラス → `playerDomain` コレクション
- 例: `RingDomain` クラス → `ringDomain` コレクション
