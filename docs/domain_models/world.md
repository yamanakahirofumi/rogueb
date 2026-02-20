# Worldモジュール ドメインモデル

このドキュメントは、**World**モジュールのコアとなるドメインモデルについて説明します。

## コアコンセプト

Worldモジュールは、ゲームワールドのためのサービスディスカバリまたは情報サービスとして機能します。その主な責務は、システムを構成する各サービスの場所（エンドポイント）を管理し、プレイヤーが入ることができる利用可能なダンジョンに関する情報を提供することです。

他のモジュールがMongoDBを使用しているのに対し、WorldモジュールはRDB（H2/MySQL等）を使用し、MyBatisによってデータアクセスを行います。

---

## 主要なデータオブジェクト

このモジュールには、ビジネスロジックを持つ複雑なドメインエンティティは含まれていません。代わりに、情報を伝達するために単純なデータ転送オブジェクト（DTO）を使用します。

### `DungeonInfo` (レコード)
- **ファイル:** `World/src/main/java/net/hero/rogueb/world/o/DungeonInfo.java`
- **説明:** 単一のダンジョンに関する基本情報を転送するために使用される単純なレコードです。これは、WorldモジュールのAPIによって公開される主要なデータ構造です。
- **フィールド:**
    - `id`: ダンジョンの一意な識別子。
    - `name`: ダンジョンの表示名。

### `ServiceInfo`
- **ファイル:** `World/src/main/java/net/hero/rogueb/world/o/ServiceInfo.java`
- **説明:** サービス情報に関連する機能のためのクラスです。

---

## データベース構造 (RDB)

Worldモジュールは以下のテーブルを使用してサービス情報とワールド情報を管理します。

### `World` テーブル
- サービスの論理的なグループ（ワールド）を管理します。
- カラム: `id`, `name`, `namespace`, `type`, `endpoint`

### `Service` テーブル
- 各モジュールの実体（エンドポイント）を管理します。
- カラム: `id`, `service_type`, `world_id`, `endpoint`

### `World_Type`, `Service_Type` テーブル
- それぞれの種類を定義するマスターテーブルです。

#### `World_Type` 定義
| ID | 名称 | 説明 |
|---|---|---|
| 1 | Self | 自サービス |
| 2 | From | 呼び出し元 |
| 20 | To | 呼び出し先 |
| 22 | Mutual | 相互参照 |
| 30 | Known | 既知のサービス |
| 99 | Unknown | 不明 |

#### `Service_Type` 定義
| ID | 名称 | モジュール名 |
|---|---|---|
| 100 | World | World |
| 200 | BookOfAdventure | BookOfAdventure |
| 300 | Dungeon | Dungeon |
| 400 | Objects | Objects |
| 500 | PlayerOperations | PlayerOperations |
| 99999 | Others | その他 |

## 実装の詳細

### `ServiceMapper` (MyBatis)
- **ファイル:** `World/src/main/java/net/hero/rogueb/world/mapper/ServiceMapper.java`
- **説明:** `Service`テーブルへのアクセスを担うMapperインターフェース。`findByType`メソッドにより、サービス種別に応じたエンドポイントURLを取得します。
