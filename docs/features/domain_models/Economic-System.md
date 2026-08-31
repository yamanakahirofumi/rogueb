# 経済システム ドメインモデル

このドキュメントは、**Economic System**（経済システム）に関連するコアとなるドメインモデルについて説明します。本システムは、世界内のアイテム流通量の管理、基本価格の動的計算、および管理者によるショップ経営を支えます。

## 1. コアコンセプト

経済システムは、世界全体のバランスを維持するための「流通管理」と、プレイヤーおよび管理者間の相互作用を生み出す「ショップ経営」の二つの側面を持ちます。アイテムの希少性が価格に反映され、管理者が戦略的に価格を設定できる仕組みを提供します。

---

## 2. 主要なドメインオブジェクト

### `ItemCirculationDomain`
- **説明:** 世界（サーバー）全体におけるアイテムの流通量と基本価格を管理します。
- **主要なプロパティ:**
    - `id`: 一意な識別子。
    - `worldId`: 対象のワールド ID。
    - `typeId`: アイテムタイプの ID（`Thing.getId()` に対応）。
    - `currentCount`: 現在世界内に存在する総数。
    - `maxLimit`: 世界内に同時に存在できる最大数（サーキュレーション制限）。
    - `basePrice`: 流通量に基づき算出された現在の基本市場価格。

### `ShopDomain`
- **説明:** ダンジョン内に設置されたショップの状態を管理します。
- **主要なプロパティ:**
    - `id`: ショップの一意な識別子。
    - `ownerId`: ショップを経営する管理者のユーザー ID。
    - `dungeonId`: 配置されているダンジョンの ID。
    - `level`: 配置されている階層。
    - `position`: 配置座標 (`Coordinate`)。
    - `inventory`: 販売中のアイテムインスタンス ID と、管理者設定価格のマップ。
    - `isOpen`: 開店状態フラグ。

### `TransactionHistoryDomain`
- **説明:** ショップでの売買履歴を記録します。
- **主要なプロパティ:**
    - `id`: 一意な識別子。
    - `shopId`: 取引が行われたショップの ID。
    - `buyerId`: 購入者のユーザー ID。
    - `sellerId`: 販売者のユーザー ID（通常はショップオーナー）。
    - `instanceId`: 取引されたアイテムのインスタンス ID。
    - `price`: 実際の取引価格。
    - `transactionDate`: 取引日時。

---

## 3. モジュール間連携

### 3.1 Objects モジュールとの連携
- アイテムが新たに生成される際、`ItemCirculationDomain` の `currentCount` が `maxLimit` に達していないかを確認します。
- アイテムの破壊や消失時に、`currentCount` を減算します。

### 3.2 PlayerOperations モジュールとの連携
- プレイヤーがショップに立ち寄った際、`ShopDomain` の情報を取得して UI に表示します。
- 管理者が `ShopDomain` を通じて販売価格をリアルタイムに更新します。

### 3.3 BookOfAdventure モジュールとの連携
- 取引成立時、購入者の `gold` を減算し、販売者の `gold` を加算します。
- アイテムの所有権（`PlayerObjectDomain`）を更新します。

---

## 4. 価格算出アルゴリズム（概要）
基本価格 `basePrice` は、`Objects` モジュールで定義される `Thing.getStandardPrice()`（標準価格）を元に動的に計算されます。

- **算出式**: `basePrice = 標準価格 * max(0.1, 1 + (1 - (currentCount / maxLimit)))`
- **価格変動**:
    - 希少なアイテム（`currentCount` が少ない）ほど、価格は高騰し、最大で標準価格の 2 倍となります。
    - 逆に、供給過多（`currentCount` が `maxLimit` を超える）の場合、価格は下落し、最低で標準価格の 10% (0.1倍) まで低下します。
    - `currentCount` が `maxLimit` と等しい場合、価格は標準価格（1.0倍）となります。
- **管理者の役割**: 管理者はこの `basePrice` を参考に、自身のショップでの販売価格 (`ShopDomain.inventory` 内の価格) を自由に設定できます。

### 4.1 価格の再計算と反映タイミング (Recalculation Timing)
市場の基本価格 `basePrice` は、以下のタイミングで動的に再計算され、関連するシステムへ波及します。

- **取引成立時**: いずれかのショップでアイテムが売買され、流通量に実質的な変化が予測される際、または `TransactionHistoryDomain` が作成された際。
- **アイテムの生成・消失時**: ドロップによる生成や、アイテムの使用・破壊によって `ItemCirculationDomain.currentCount` が変動した際。
- **定期バッチ処理**: リアルタイムな変動に加え、1 時間に 1 回、世界全体の在庫状況を再集計し、微調整を行います。
- **反映**: 再計算された `basePrice` は即座に `ItemCirculationDomain` に保存されます。各ショップ (`ShopDomain`) は、次に在庫リストを取得・更新する際にこの最新価格を参照します。

## 5. サーキュレーション制限（存在上限）の挙動
`ItemCirculationDomain.maxLimit` によって、世界全体のアイテムバランスを維持します。

### 5.1 流通カウントの対象 (Counting Scope)
`currentCount` は、世界（サーバー）内に存在する対象アイテムの総数であり、以下のすべての状態にある個体が含まれます。

- **所持品 (Inventory)**: オンライン・オフラインを問わず、全プレイヤーが所持しているアイテム。
- **フィールド (Field)**: ダンジョン内の座標に配置されているアイテム。
- **ショップ在庫 (Shop Stock)**: 管理者が経営するショップに出品されているアイテム。
- **倉庫・預かり所 (Storage)**: プレイヤーが倉庫等に保管しているアイテム。

### 5.2 上限到達時の共通振る舞い (Global Fallback Standard)
新たなアイテムを生成（ドロップ、ショップ補充、繁殖、クリア報酬等）する際、`currentCount >= maxLimit` である場合は生成が拒否され、原則として以下の**共通代替ルール**が適用されます。

- **共通代替ルール**: 生成しようとしたアイテムの「標準価格 (`standardPrice`)」の **30%** に相当するゴールドに置換されます。
- **例外**: [モンスタードロップシステム](../Monster-Drop-System.md) のように、特定のシステムで独自の置換ロジック（下位ティアへの置換等）が定義されている場合は、そちらが優先されます。
- **手動配置の制限**: 管理者による手動配置（ショップ補充等）の際も上限チェックが行われ、上限に達している場合は配置自体がキャンセルされます。

### 5.3 生成・消失のライフサイクル
- **生成時のチェック**: アイテム生成の直前に `Objects` モジュールは `EconomicSystem` に問い合わせ、`currentCount < maxLimit` であることを確認します。承認された場合のみ生成され、`currentCount` がインクリメントされます。
- **カウントの減少**: アイテムが使用（消費）、破壊、または[世界間連携システム](../World-Interoperability-System.md)を通じて「世界の外」へ持ち出された際に、`currentCount` が減算され、再び生成が可能になります。

---

## 6. API リクエスト・レスポンス仕様

### 6.1 アイテム流通量・基本価格の照会 (`GET /api/v1/economic/circulation/{typeId}`)
指定されたアイテムタイプの現在の流通量、存在上限、および算出された基本市場価格を取得します。

- **リクエストパラメータ**:
  - `typeId` (Path): アイテムタイプのID（例: `potion_heal_01`）

- **レスポンス (200 OK)**:
```json
{
  "id": "circ_potion_heal_01",
  "worldId": "world_alpha",
  "typeId": "potion_heal_01",
  "currentCount": 420,
  "maxLimit": 1000,
  "basePrice": 158
}
```

---

### 6.2 ショップ情報の取得 (`GET /api/v1/economic/shops/{shopId}`)
指定されたショップの詳細情報、開店状態、および現在販売中のアイテムと価格一覧を取得します。

- **リクエストパラメータ**:
  - `shopId` (Path): ショップの一意な識別子

- **レスポンス (200 OK)**:
```json
{
  "shopId": "shop_dungeon_01_f1_01",
  "ownerId": "manager_user_01",
  "dungeonId": "dungeon_beginner_01",
  "level": 1,
  "position": {
    "x": 12,
    "y": 8
  },
  "isOpen": true,
  "items": [
    {
      "instanceId": "item_inst_1001",
      "typeId": "potion_heal_01",
      "name": "薬草",
      "price": 160,
      "stock": 5
    },
    {
      "instanceId": "item_inst_1002",
      "typeId": "sword_iron_01",
      "name": "鉄の剣",
      "price": 1200,
      "stock": 1
    }
  ]
}
```

---

### 6.3 ショップでのアイテム購入 (`POST /api/v1/economic/shops/{shopId}/buy`)
プレイヤーがショップで販売中のアイテムを購入します。

- **リクエストボディ**:
```json
{
  "buyerId": "player_user_99",
  "instanceId": "item_inst_1001",
  "quantity": 1
}
```

- **レスポンス (200 OK)**:
```json
{
  "transactionId": "tx_20260331_001",
  "shopId": "shop_dungeon_01_f1_01",
  "buyerId": "player_user_99",
  "sellerId": "manager_user_01",
  "instanceId": "item_inst_1001",
  "typeId": "potion_heal_01",
  "price": 160,
  "quantity": 1,
  "remainingGold": 2840,
  "transactionDate": "2026-03-31T12:00:00Z"
}
```

---

### 6.4 ショップへのアイテム売却 (`POST /api/v1/economic/shops/{shopId}/sell`)
プレイヤーが所持しているアイテムをショップに売却します。

- **リクエストボディ**:
```json
{
  "sellerId": "player_user_99",
  "instanceId": "item_inst_2005",
  "quantity": 1
}
```

- **レスポンス (200 OK)**:
```json
{
  "transactionId": "tx_20260331_002",
  "shopId": "shop_dungeon_01_f1_01",
  "buyerId": "manager_user_01",
  "sellerId": "player_user_99",
  "instanceId": "item_inst_2005",
  "typeId": "scroll_identify_01",
  "price": 100,
  "quantity": 1,
  "updatedGold": 2940,
  "transactionDate": "2026-03-31T12:05:00Z"
}
```

---

### 6.5 店舗在庫・価格の設定更新 (`PUT /api/v1/economic/shops/{shopId}/inventory`)
ショップオーナー（管理者）が店舗の在庫アイテムの追加・削除、販売価格の設定、および開店/閉店状態を更新します。

- **リクエストボディ**:
```json
{
  "ownerId": "manager_user_01",
  "isOpen": true,
  "inventoryUpdates": [
    {
      "instanceId": "item_inst_1003",
      "customPrice": 500
    }
  ],
  "removedInstanceIds": [
    "item_inst_1002"
  ]
}
```

- **レスポンス (200 OK)**:
```json
{
  "shopId": "shop_dungeon_01_f1_01",
  "ownerId": "manager_user_01",
  "isOpen": true,
  "totalItemCount": 6,
  "updatedAt": "2026-03-31T12:10:00Z"
}
```

---

## 7. エラーハンドリング仕様

経済システムの各 API 呼び出しにおける異常系のエラーコード、HTTP ステータス、および発生条件は以下の通りです。

| エラーコード | HTTP ステータス | 発生条件 | エラーメッセージ（例） |
| :--- | :--- | :--- | :--- |
| `ITEM_NOT_FOUND` | `404 Not Found` | 指定されたアイテムタイプ（`typeId`）またはアイテムインスタンスが存在しない場合。 | 指定されたアイテムが見つかりません。 |
| `SHOP_NOT_FOUND` | `404 Not Found` | 指定された `shopId` のショップが存在しない場合。 | 指定されたショップが見つかりません。 |
| `SHOP_CLOSED` | `400 Bad Request` | 開店していない（`isOpen = false`）ショップで売買を行おうとした場合。 | 対象のショップは現在閉店中です。 |
| `ITEM_NOT_IN_SHOP` | `400 Bad Request` | ショップの在庫一覧に含まれていないアイテムを購入しようとした場合。 | 指定されたアイテムはショップで販売されていません。 |
| `INSUFFICIENT_GOLD` | `400 Bad Request` | 購入に必要なゴールドが不足している場合。 | ゴールドが不足しています。 |
| `INSUFFICIENT_STOCK` | `400 Bad Request` | 購入希望数量がショップの在庫数量を超えている場合。 | ショップの在庫が不足しています。 |
| `INVENTORY_FULL` | `400 Bad Request` | アイテム購入後、プレイヤーの所持品インベントリ枠を超過する場合。 | インベントリ領域が不足しています。 |
| `CIRCULATION_LIMIT_REACHED` | `400 Bad Request` | 新規アイテム配置・補充時に世界全体の存在上限（`maxLimit`）に達している場合。 | アイテムの流通上限に達しているため補充できません。 |
| `UNAUTHORIZED_SHOP_OWNER` | `403 Forbidden` | ショップオーナー以外のユーザーが在庫や価格の設定を変更しようとした場合。 | ショップの管理権限がありません。 |
| `INVALID_PRICE_SETTING` | `400 Bad Request` | 設定価格が 0 以下の不正な値である場合。 | 販売価格は 1 ゴールド以上に設定してください。 |
