# Worldモジュール ドメインモデル

このドキュメントは、**World**モジュールのコアとなるドメインモデルについて説明します。

## 1. コアコンセプト

Worldモジュールは、ゲームワールドのためのサービスディスカバリまたは情報サービスとして機能します。その主な責務は、システムを構成する各サービスの場所（エンドポイント）を管理し、プレイヤーが入ることができる利用可能なダンジョンに関する情報を提供することです。

他のモジュールがMongoDBを使用しているのに対し、WorldモジュールはRDB（H2/MySQL等）を使用し、MyBatisによってデータアクセスを行います。

---

## 2. 主要なデータオブジェクト

このモジュールには、ビジネスロジックを持つ複雑なドメインエンティティは含まれていません。代わりに、情報を伝達するために単純なデータ転送オブジェクト（DTO）を使用します。

### `DungeonInfo` (レコード)
- **ファイル:** `World/src/main/java/net/hero/rogueb/world/o/DungeonInfo.java`
- **説明:** 単一のダンジョンに関する基本情報を転送するために使用される単純なレコードです。これは、WorldモジュールのAPIによって公開される主要なデータ構造です。
- **フィールド:**
    - `id`: ダンジョンの一意な識別子。
    - `name`: ダンジョンの表示名。
    - `entryFee`: ダンジョンの入場料。

### `ServiceInfo`
- **ファイル:** `World/src/main/java/net/hero/rogueb/world/o/ServiceInfo.java`
- **説明:** サービス情報に関連する機能のためのクラスです。

---

## 3. データベース構造 (RDB)

Worldモジュールは以下のテーブルを使用してサービス情報とワールド情報を管理します。

### 3.1 テーブル定義

#### `World` テーブル
- サービスの論理的なグループ（ワールド）を管理します。
- カラム: `id`, `name`, `namespace`, `type`, `endpoint`, `public_key`, `item_carry_policy`, `level_sync_policy`

#### `Service` テーブル
- 各モジュールの実体（エンドポイント）を管理します。
- カラム: `id`, `service_type`, `world_id`, `endpoint`

#### `World_Type`, `Service_Type` テーブル
- それぞれの種類を定義するマスターテーブルです。

##### `World_Type` 定義
| ID | 名称 | 説明 |
|---|---|---|
| 1 | Self | 自サービス |
| 2 | From | 呼び出し元 |
| 20 | To | 呼び出し先 |
| 22 | Mutual | 相互参照 |
| 30 | Known | 既知のサービス |
| 99 | Unknown | 不明 |

##### `Service_Type` 定義
| ID | 名称 | モジュール名 |
|---|---|---|
| 100 | World | World |
| 200 | BookOfAdventure | BookOfAdventure |
| 300 | Dungeon | Dungeon |
| 400 | Objects | Objects |
| 500 | PlayerOperations | PlayerOperations |
| 600 | EconomicSystem | EconomicSystem |
| 99999 | Others | その他 |

### 3.2 トラストポリシー (Trust Policies)
信頼関係のあるワールド間でのデータ移動に関するポリシーを定義します。

#### `ItemCarryPolicy` (アイテム持ち込み設定)
| 名称 | 説明 |
| :--- | :--- |
| **双方向可能** | 互いのサーバー間で自由にアイテムを持ち込み・持ち出しができる。 |
| **片方向のみ** | 一方のサーバーからのみ持ち込みを許可し、逆方向は制限する。 |
| **移動不可** | キャラクターの移動は許可するが、アイテムの持ち込みは一切禁止する。 |

#### `LevelSyncPolicy` (レベル共有設定)
| 名称 | 説明 |
| :--- | :--- |
| **レベル共有** | 信頼するサーバー間ですべての経験値・レベルを完全に同期する。 |
| **レベル引き継ぐ** | 移動時点のレベルをコピーして開始する。移動後の成長は各サーバーで独立する。 |
| **新たに1から始める** | キャラクターの外見や名前などは引き継ぐが、レベルは 1 にリセットされた状態で開始する。 |

## 4. 実装の詳細

### `ServiceMapper` (MyBatis)
- **ファイル:** `World/src/main/java/net/hero/rogueb/world/mapper/ServiceMapper.java`
- **説明:** `Service`テーブルへのアクセスを担うMapperインターフェース。`findByType`メソッドにより、サービス種別に応じたエンドポイントURLを取得します。

---

## 5. API仕様 (API Specifications)

Worldモジュールが提供するREST APIのエンドポイント、リクエスト・レスポンスのデータ構造です。

### 5.1 初期ダンジョン照会 (`GET /api/v1/world/dungeon/init`)
プレイヤーが冒険を開始するための初期ダンジョン情報（ダンジョンID、表示名、入場料）を取得します。

#### レスポンス JSON スキーマ (200 OK)
```json
{
  "id": "dungeon_cave_01",
  "name": "dungeon",
  "entryFee": 0
}
```

---

### 5.2 サービス情報登録 (`POST /api/v1/world/service`)
システム内の各マイクロサービス（Dungeon, Objects, BookOfAdventure等）が自サービスのエンドポイント情報をWorldモジュールに登録します。

#### リクエスト JSON スキーマ
```json
{
  "serviceType": 300,
  "worldId": "world_alpha_01",
  "endpoint": "http://dungeon-service.rogueb.internal:8080"
}
```

#### レスポンス JSON スキーマ (200 OK)
```json
{
  "status": "SUCCESS",
  "serviceType": 300,
  "registeredEndpoint": "http://dungeon-service.rogueb.internal:8080",
  "updatedAt": "2026-03-31T12:00:00Z"
}
```

---

### 5.3 サービスエンドポイント照会 (`GET /api/v1/world/service/{serviceType}`)
指定されたサービス種別（`serviceType`）に対応する稼働中のエンドポイントURLを取得します。

#### レスポンス JSON スキーマ (200 OK)
```json
{
  "serviceType": 300,
  "worldId": "world_alpha_01",
  "endpoint": "http://dungeon-service.rogueb.internal:8080"
}
```

---

### 5.4 ワールド情報・トラストポリシー照会 (`GET /api/v1/world/info/{worldId}`)
指定したワールドの基本情報および連携トラストポリシー（アイテム持ち込み設定、レベル共有設定）を取得します。

#### レスポンス JSON スキーマ (200 OK)
```json
{
  "worldId": "world_alpha_01",
  "name": "Alpha World",
  "namespace": "rogueb_alpha",
  "type": 1,
  "endpoint": "http://world-service.rogueb.internal:8080",
  "itemCarryPolicy": "MUTUAL_ALLOWED",
  "levelSyncPolicy": "LEVEL_SYNCED"
}
```

---

## 6. エラーハンドリング仕様 (Error Handling Specification)

WorldモジュールのAPI実行時にエラーや不正リクエストが発生した場合、以下の統一エラーフォーマットに従ってエラーレスポンスを返却します。

### 6.1 エラーレスポンス共通 JSON スキーマ
```json
{
  "errorCode": "SERVICE_NOT_FOUND",
  "message": "指定されたサービス種別のエンドポイントが見つかりません。",
  "timestamp": "2026-03-31T12:00:00Z"
}
```

### 6.2 エラーコード一覧およびマッピング

| エラーコード | HTTPステータス | 発生条件・説明 |
| :--- | :---: | :--- |
| `WORLD_NOT_FOUND` | `404 Not Found` | 指定された `worldId` に該当するワールド情報が存在しない場合。 |
| `SERVICE_NOT_FOUND` | `404 Not Found` | 指定された `serviceType` に該当するエンドポイントがルーティングテーブルに未登録の場合。 |
| `INVALID_SERVICE_TYPE` | `400 Bad Request` | 定義されていない無効なサービス種別 ID（例: 未定義の数値）が指定された場合。 |
| `DUPLICATE_SERVICE_REGISTRATION` | `409 Conflict` | サービス登録時、既存のエンドポイントと競合し上書き権限がない場合。 |
| `START_DUNGEON_NOT_CONFIGURED` | `500 Internal Server Error` | 初期ダンジョン情報取得時、Dungeonモジュールからのデータ取得・保存に失敗した場合。 |
| `UNAUTHORIZED_SERVICE_REGISTRATION` | `403 Forbidden` | 権限キーまたは認証トークンなしでサービス登録APIが呼び出された場合。 |
