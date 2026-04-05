# ドメインモデルドキュメント

このドキュメントは、`rogueb`アプリケーションにおける主要な各コンポーネント（サービス）のコアとなるドメインモデルの概要を提供します。

各ドキュメントでは、特定のモジュールに関する主要な責務、エンティティ、および値オブジェクトについて概説します。

## 1. モジュール別ドキュメント

### アプリケーションロジック

- **[Dungeonモジュール](./Dungeon.md)**
  - ダンジョン、フロア、タイルの構造、生成、および状態を管理します。
  - **[ダンジョン構築・運営システム](../Dungeon-Construction-System.md)**: 建築資材、建築モード、および管理者介入の仕組み。

- **[Worldモジュール](./World.md)**
  - 利用可能なダンジョンを発見し、高レベルのワールド情報を提供するサービスです。

- **[Objectsモジュール](./Objects.md)**
  - 武器、指輪、金などのすべてのゲーム内アイテム（`Thing`）を定義および管理します。

- **[Monsterモジュール](./Monster.md)**
  - モンスターの種族、個体状態、およびダンジョン内での配置を管理します。
  - **[モンスタードロップシステム](../Monster-Drop-System.md)**: ドロップテーブルと流通制限のロジック。
  - **[モンスター繁殖システム](../Monster-Breeding-System.md)**: モンスターの繁殖、継承、および孵化の仕組み。
  - **[モンスター捕獲システム](../Monster-Capture-System.md)**: モンスターの捕獲、アイテム、および成功率に関する仕様。
  - **[モンスター進化システム](../Monster-Evolution-System.md)**: モンスターの進化条件、プロセス、および継承の仕組み。
  - **[モンスター化・PKシステム](../Monster-PK-System.md)**: プレイヤーによるモンスター化変身と乱入・PKの仕組み。

- **[PlayerOperationsモジュール](./Player-Operations.md)**
  - プレイヤーの操作、およびインベントリ（`Bag`）の管理を調整します。
  - **[戦闘システム](../Combat-System.md)**: ダメージ計算、状態異常、戦闘アクションの詳細仕様。
  - **[スキル・魔法システム](../Skill-And-Magic-System.md)**: スキル、魔法のカテゴリ、コスト、および効果に関する仕様。

- **[BookOfAdventureモジュール](./Book-Of-Adventure.md)**
  - プレイヤーの状態、キャラクター情報の永続化を担当します。

- **[経済システム](./Economic-System.md)**
  - アイテムの流通量、動的な価格計算、およびショップ管理を担当します。

### データベース構造

- **[Dungeonモジュール (MongoDB)](./Dungeon-MongoDB.md)**
  - `dungeon`および`floor`コレクションのスキーマ情報。

- **[Objectsモジュール (MongoDB)](./Objects-MongoDB.md)**
  - `ring`および`objectHistory`コレクションのスキーマ情報。

- **[Monsterモジュール (MongoDB)](./Monster-MongoDB.md)**
  - `monsterDomain`および`monsterInstanceDomain`コレクションのスキーマ情報。

- **[BookOfAdventureモジュール (MongoDB)](./Book-Of-Adventure-MongoDB.md)**
  - `playerDomain`コレクションのスキーマ情報。

- **[経済システム (MongoDB)](./Economic-System-MongoDB.md)**
  - `itemCirculationDomain`, `shopDomain`, `transactionHistoryDomain` コレクションのスキーマ情報。

---

## 2. 共通の規約

### MongoDB コレクションの命名規則
本プロジェクトでは、Spring Data MongoDB を使用しており、`@Document` アノテーションで明示的にコレクション名を指定していない場合、コレクション名はドメインクラス名の最初の文字を小文字にしたもの（camelCase）になります。

- 例: `PlayerDomain` クラス → `playerDomain` コレクション
- 例: `RingDomain` クラス → `ringDomain` コレクション
