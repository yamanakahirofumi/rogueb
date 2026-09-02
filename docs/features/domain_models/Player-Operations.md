# PlayerOperationsモジュール ドメインモデル

このドキュメントは、**PlayerOperations**モジュールのコアとなるドメインモデルについて説明します。

## 1. コアコンセプト

PlayerOperationsモジュールは、プレイヤーキャラクターの状態とインベントリの管理を担当します。プレイヤーが何であるか、何を持っているかを定義します。このモジュールは、プレイヤーによって開始されたアクションを調整し、Dungeon（移動用）やObjects（アイテム管理用）などの他のサービスと対話する可能性があります。

---

## 2. 主要なドメインオブジェクト

### `PlayerDto`
- **ファイル:** `BookOfAdventureClient/src/main/java/net/hero/rogueb/bookofadventureclient/o/PlayerDto.java`
- **説明:** **PlayerOperations**におけるプレイヤー状態のやり取りに使用される中心的なDTOです。プレイヤー名、経験値、所持金、および現在のステータスや位置情報を保持します。
- **詳細:** 詳細な構造については、**[BookOfAdventureモジュール ドメインモデル](./Book-Of-Adventure.md)**を参照してください。

### `Player` (インターフェース)
- **ファイル:** `PlayerOperations/src/main/java/net/hero/rogueb/character/Player.java`
- **説明:** プレイヤーキャラクターの基本的な規約を定義するインターフェースです。
- **主要なメソッド:**
    - `getName()`: プレイヤーの名前を返します。
    - `isMoved()`: プレイヤーの移動状態に関連するブール値のフラグ。

### `Human`
- **ファイル:** `PlayerOperations/src/main/java/net/hero/rogueb/character/Human.java`
- **説明:** `Player`インターフェースの具体的な実装です。
- **主要なプロパティ:**
    - `name`: キャラクターの名前。
    - `bag`: プレイヤーのインベントリを表す`Bag`クラスのインスタンス。
    - `activeParty`: パーティに編入されているモンスターのリスト。最大 3 体。

### `Bag`
- **ファイル:** `PlayerOperations/src/main/java/net/hero/rogueb/bag/Bag.java`
- **説明:** プレイヤーのインベントリ、または「バッグ」を表します。プレイヤーが運んでいるアイテムを管理します。
- **主要なプロパティとメソッド:**
    - `contents`: `ThingSimple`オブジェクトのリスト。
    - `limitSize`: バッグが保持できるアイテムの最大数（デフォルト値: 23）。
    - `addContents(ThingSimple thing)`: スペースがあればバッグにアイテムを追加します。
    - `getThingIdList()`: バッグ内のアイテムのインスタンスIDのリストを返します。

### `ThingSimple` (レコード)
- **ファイル:** `ObjectsClient/src/main/java/net/hero/rogueb/objectclient/o/ThingSimple.java`
- **説明:** `Objects`サービスから取得したアイテムの基本情報を保持するためのDTO。
- **フィールド:**
    - `instanceId`: アイテムのインスタンスID。
    - `typeId`: アイテムの種類を示すID (`Thing.getId()` に対応)。
    - `display`: マップ上での表示文字。

---

## 3. 主要なサービス

### `FieldsService`
- **ファイル:** `PlayerOperations/src/main/java/net/hero/rogueb/services/FieldsService.java`
- **説明:** プレイヤー周辺のフィールド情報をリアクティブに提供するサービス。
- **主要な機能:**
    - `getFields(userId)`: `Flux.interval(Duration.ofSeconds(20))` を用いて20秒間隔での定期更新、または即時更新 (`getFieldsNow`) を提供します。

---

## 4. 値オブジェクトと列挙型

### `MoveEnum` (列挙型)
- **ファイル:** `DungeonClient/src/main/java/net/hero/rogueb/dungeonclient/o/MoveEnum.java`
- **説明:** プレイヤーの移動方向を定義する列挙型。各値は X, Y の移動量（オフセット）を持ちます。
- **値:**
    - `Top`: (0, -1)
    - `Down`: (0, 1)
    - `Left`: (-1, 0)
    - `Right`: (1, 0)
    - `TopLeft`: (-1, -1)
    - `TopRight`: (1, -1)
    - `DownLeft`: (-1, 1)
    - `DownRight`: (1, 1)

---

## 5. API仕様

PlayerOperationsモジュールが提供する外部APIエンドポイントの仕様です。

### 5.1 プレイヤー移動 (`POST /api/v1/player/move`)

プレイヤーをダンジョン内の指定方向（8方向）へ1マス移動させます。移動に伴いスタミナが消費され、移動先タイルのイベント（トラップ発動、アイテム獲得等）が発生します。

#### リクエスト (Request)
```json
{
  "userId": "user_12345",
  "dungeonId": "dungeon_frontier_01",
  "floorNumber": 3,
  "direction": "TopRight"
}
```

#### レスポンス (Response: 200 OK)
```json
{
  "userId": "user_12345",
  "previousCoordinate": {
    "x": 10,
    "y": 15
  },
  "newCoordinate": {
    "x": 11,
    "y": 14
  },
  "staminaConsumed": 1,
  "remainingStamina": 99,
  "fieldEvent": {
    "eventType": "NONE",
    "description": "安全に移動しました。"
  }
}
```

---

### 5.2 インベントリ（バッグ）情報照会 (`GET /api/v1/player/{userId}/bag`)

指定したプレイヤーのバッグ内アイテム一覧および保持容量を取得します。

#### レスポンス (Response: 200 OK)
```json
{
  "userId": "user_12345",
  "limitSize": 23,
  "currentCount": 2,
  "contents": [
    {
      "instanceId": "item_inst_001",
      "typeId": "potion_heal_small",
      "display": "!",
      "name": "薬草",
      "category": "CONSUMABLE"
    },
    {
      "instanceId": "item_inst_002",
      "typeId": "sword_bronze",
      "display": "/",
      "name": "銅の剣",
      "category": "WEAPON"
    }
  ]
}
```

---

### 5.3 アイテム拾得 (`POST /api/v1/player/items/pick`)

プレイヤーの足元（現在座標）に存在するアイテムをバッグに拾い上げます。

#### リクエスト (Request)
```json
{
  "userId": "user_12345",
  "dungeonId": "dungeon_frontier_01",
  "floorNumber": 3,
  "coordinate": {
    "x": 11,
    "y": 14
  }
}
```

#### レスポンス (Response: 200 OK)
```json
{
  "userId": "user_12345",
  "pickedItem": {
    "instanceId": "item_inst_003",
    "typeId": "ring_strength",
    "display": "=",
    "name": "ちからの指輪"
  },
  "remainingBagSpace": 20
}
```

---

### 5.4 アイテム手放し・ドロップ (`POST /api/v1/player/items/drop`)

バッグ内の指定アイテムを足元（現在座標）の床に置きます。

#### リクエスト (Request)
```json
{
  "userId": "user_12345",
  "dungeonId": "dungeon_frontier_01",
  "floorNumber": 3,
  "instanceId": "item_inst_001"
}
```

#### レスポンス (Response: 200 OK)
```json
{
  "userId": "user_12345",
  "droppedItem": {
    "instanceId": "item_inst_001",
    "typeId": "potion_heal_small",
    "display": "!"
  },
  "droppedCoordinate": {
    "x": 11,
    "y": 14
  },
  "remainingBagSpace": 22
}
```

---

## 6. エラーハンドリング仕様

PlayerOperationsモジュールの処理実行時に発生する主要なエラーコードと対応するHTTPステータスコードです。

### 6.1 エラーコード一覧

| エラーコード | HTTPステータス | 説明 | 発生条件 |
| :--- | :--- | :--- | :--- |
| `PLAYER_NOT_FOUND` | `404 Not Found` | プレイヤー非存在 | 指定された `userId` のプレイヤーが存在しない。 |
| `INVALID_MOVE_DIRECTION` | `400 Bad Request` | 無効な移動方向 | 指定された移動方向 (`direction`) が `MoveEnum` に定義されていない。 |
| `TILE_BLOCKED` | `400 Bad Request` | 移動不可タイル | 移動先の座標が壁・水路・障害物などで進入できない。 |
| `BAG_FULL` | `400 Bad Request` | バッグ容量超過 | バッグの所持数が上限（23個）に達しており新しくアイテムを拾えない。 |
| `ITEM_NOT_FOUND` | `404 Not Found` | アイテム非存在 | 指定された `instanceId` のアイテムが存在しない。 |
| `ITEM_NOT_ON_TILE` | `400 Bad Request` | 足元アイテムなし | 拾得対象の座標に落ちているアイテムが存在しない。 |
| `ITEM_NOT_IN_BAG` | `400 Bad Request` | バッグ内アイテム非存在 | 指定された `instanceId` のアイテムがバッグ内に存在しない。 |
| `STAMINA_EXHAUSTED` | `422 Unprocessable Entity` | スタミナ枯渇 | スタミナが0で移動不能状態（HP消費移動が発生する場合を除く）。 |
| `STATUS_PREVENTS_MOVEMENT` | `422 Unprocessable Entity` | 状態異常による行動不能 | 麻痺、睡眠、影ぬい等の状態異常により移動・アイテム操作ができない。 |

### 6.2 エラーレスポンス形式例

```json
{
  "errorCode": "BAG_FULL",
  "message": "バッグが満杯のため、これ以上アイテムを持ち運べません。",
  "status": 400,
  "timestamp": "2026-03-31T12:00:00Z"
}
```
