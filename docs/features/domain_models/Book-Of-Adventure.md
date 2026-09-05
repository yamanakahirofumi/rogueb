# BookOfAdventureモジュール ドメインモデル

このドキュメントは、**BookOfAdventure**モジュールのコアとなるドメインモデルについて説明します。

## 1. コアコンセプト

BookOfAdventureモジュールは、プレイヤーの冒険の記録、キャラクターの状態、および所持アイテム（インベントリ）の永続化を担当します。ゲームのセーブデータを管理する中心的なレポジトリとして機能します。

PlayerOperationsモジュールはこのサービスを利用して、プレイヤーの状態を取得・更新します。

---

## 2. 主要なドメインオブジェクト

### `PlayerDomain`
- **ファイル:** `BookOfAdventure/src/main/java/net/hero/rogueb/bookofadventure/domain/PlayerDomain.java`
- **説明:** プレイヤーの全状態を保持し、データベースに保存されるメインのエンティティです。
- **主要なプロパティ:**
    - `id`: ユーザー（プレイヤー）の一意な識別子。
    - `name`: プレイヤーの名前。
    - `level`: レベル。
    - `exp`: 経験値。
    - `gold`: 所持金。
    - `totalPkCount`: 累計撃破数（プレイヤーキル数）。
    - `currentKillStreak`: 現在の連続撃破数（倒されずに連続して PK した数）。
    - `bounty`: 現在かけられている賞金額（ゴールド）。
    - `namespace`: プレイヤーが属する論理的な領域。
    - `currentStatus`: 現在のステータスを保持するマップ。
        - キー: `hp` (ヒットポイント), `mp` (魔法ポイント), `stamina` (スタミナ), `actionInterval` (行動間隔), `seed` (乱数シード), `subStep` (内部歩数カウンタ)
    - `status`: 基本ステータスを保持するマップ（成長や永続的なバフの影響を受ける前の値）。
        - キー: `atk` (物理攻撃力), `def` (物理防御力), `magicAtk` (魔法攻撃力), `magicDef` (魔法防御力), `dex` (器用さ/命中率), `maxHp` (最大ヒットポイント), `maxMp` (最大魔法ポイント), `attribute` (属性), `mnd` (精神力/状態異常耐性), `maxStamina` (最大スタミナ)
    - `location`: 現在の位置情報を保持するマップ。
        - キー: `dungeonId` (ダンジョンID), `level` (階層), `x` (X座標), `y` (Y座標)
    - `equipment`: 装備中のアイテム情報を保持するマップ。
        - キー: `weapon` (武器), `armor` (防具), `ring1` (指輪1), `ring2` (指輪2)
        - 値: アイテムのインスタンス ID。
    - `skillIds`: 習得しているスキル ID のリスト。詳細は [スキル・魔法システム](../Skill-And-Magic-System.md) を参照してください。
    - `activeMonsterIds`: パーティ（Active Party）に編入されているモンスターのインスタンス ID リスト。
        - **制限**: 同時に編入できるモンスターは最大 **3 体** までです。
    - `statusEffects`: 付与されている状態異常 (`StatusEffectDomain`) のリスト。

### `PlayerObjectDomain`
- **ファイル:** `BookOfAdventure/src/main/java/net/hero/rogueb/bookofadventure/domain/PlayerObjectDomain.java`
- **説明:** プレイヤーが所持しているアイテム（インベントリ）のリストを管理します。
- **主要なプロパティ:**
    - `id`: 一意な識別子。
    - `playerId`: プレイヤーのID。
    - `objectIdList`: プレイヤーが所持しているアイテムのインスタンスIDのリスト。
    - `limitSize`: バッグが保持できるアイテムの最大数（デフォルト値: 23）。詳細は [Player-Operations.md](./Player-Operations.md) を参照。

### `PlayerMonsterDomain`
- **説明:** プレイヤーが所持しているモンスター（捕獲済み、または繁殖で得た個体）のリストを管理します。
- **主要なプロパティ:**
    - `id`: 一意な識別子。
    - `playerId`: プレイヤーのID。
    - `monsterIdList`: プレイヤーが所持しているモンスターのインスタンスIDのリスト。
    - `limitSize`: 所持可能なモンスターの最大数（デフォルト値: 50）。

### `PlayerKnowledgeDomain`
- **ファイル:** `BookOfAdventure/src/main/java/net/hero/rogueb/bookofadventure/domain/PlayerKnowledgeDomain.java`
- **説明:** プレイヤー（ユーザー）ごとのアイテム知識（識別状況）を管理します。
- **主要なプロパティ:**
    - `id`: 一意な識別子。
    - `userId`: プレイヤー（ユーザー）のID。
    - `worldId`: ワールドのID。
    - `typeId`: アイテムタイプのID。
    - `isIdentified`: そのアイテムタイプが識別されているかどうかを示すブール値。

### `StatusEffectDomain` (値オブジェクト)
- **説明:** プレイヤーやモンスターに付与される状態異常を定義します。
- **プロパティ:**
    - `type`: 状態異常の種類（例: `Poison`, `Confusion`, `Paralysis`, `Sleep`, `Seal`, `Anger`, `Bind`, `Invisibility`, `Haste`）。
    - `remainingTurns`: 残りの継続ターン数または歩数。
    - `value`: 効果に関連する補助的な数値（例: 毒のダメージ量）。

---

## 3. データ転送オブジェクト (DTO)

### `PlayerDto`
- **ファイル:** `BookOfAdventureClient/src/main/java/net/hero/rogueb/bookofadventureclient/o/PlayerDto.java`
- **説明:** 他のサービス（特にPlayerOperations）との間でプレイヤーの状態をやり取りするための主要なDTOです。`PlayerDomain`とほぼ同じ構造を持ちます。
- **主要なプロパティ:**
    - `id`: プレイヤーの一意な識別子。
    - `name`: プレイヤーの名前。
    - `level`: レベル。
    - `exp`: 累積経験値。
    - `gold`: 所持金額。
    - `totalPkCount`: 累計撃破数。
    - `currentKillStreak`: 現在の連続撃破数。
    - `bounty`: 現在の賞金額。
    - `namespace`: プレイヤーが属する論理的な領域。
    - `currentStatus`: 現在のステータスを保持するマップ。
        - キー: `hp` (ヒットポイント), `mp` (魔法ポイント), `stamina` (スタミナ), `actionInterval` (行動間隔), `seed` (乱数シード), `subStep` (内部歩数カウンタ)
    - `status`: 基本ステータスを保持するマップ。
        - キー: `atk` (物理攻撃力), `def` (物理防御力), `magicAtk` (魔法攻撃力), `magicDef` (魔法防御力), `dex` (器用さ/命中率), `maxHp` (最大ヒットポイント), `maxMp` (最大魔法ポイント), `attribute` (属性), `mnd` (精神力/状態異常耐性), `maxStamina` (最大スタミナ)
    - `location`: 現在の位置情報を保持するマップ。
        - キー: `dungeonId` (ダンジョンID), `level` (階層), `x` (X座標), `y` (Y座標)
    - `equipment`: 装備中のアイテム情報を保持するマップ。
    - `skillIds`: 習得しているスキル ID のリスト。
    - `activeMonsterIds`: パーティ（Active Party）に編入されているモンスターのインスタンス ID リスト。
    - `statusEffects`: 付与されている状態異常 (`StatusEffectDomain`) のリスト。
- **用途:** `PlayerService`でのビジネスロジック処理や、APIのレスポンスとして使用されます。

---

## 4. 初期プレイヤーデータ (Initial Player Data)

新規ゲーム開始時（キャラクター作成時）の標準的な初期ステータスおよび所持品を定義します。

### 4.1 初期ステータス
| カテゴリ | キー | 初期値 | 備考 |
| :--- | :--- | :---: | :--- |
| **基本情報** | `level` | 1 | |
| | `exp` | 0 | |
| | `gold` | 500 | |
| **基本ステータス (`status`)** | `atk` | 10 | |
| | `def` | 10 | |
| | `magicAtk` | 8 | |
| | `magicDef` | 8 | |
| | `dex` | 12 | |
| | `mnd` | 10 | |
| | `maxHp` | 20 | |
| | `maxMp` | 10 | |
| | `maxStamina` | 100 | |
| | `attribute` | `None` | |
| **現在の状態 (`currentStatus`)** | `hp` | 20 | |
| | `mp` | 10 | |
| | `stamina` | 100 | |
| | `actionInterval` | 1000 | ms 単位。 |
| | `subStep` | 0 | |

### 4.2 初期装備・所持品
- **装備 (`equipment`)**: なし（初期状態では未装備）。
- **インベントリ (`Bag`)**:
    - `wooden_sword` (木の剣) × 1
    - `leather_armor` (皮の鎧) × 1
    - `bread` (パン) × 2
- **所持モンスター**: なし。

---

## 5. API仕様 (API Specifications)

BookOfAdventureモジュールが提供するREST APIのエンドポイント、リクエスト・レスポンスのデータ構造です。

### 5.1 ユーザー存在チェック (`GET /api/v1/user/name/{userName}/exist`)
指定したユーザー名（プレイヤー名）のセーブデータが存在するか確認します。

#### レスポンス JSON スキーマ
```json
{
  "userName": "HeroPlayer",
  "exists": true
}
```

---

### 5.2 新規プレイヤーデータ作成 (`POST /api/v1/user/name/{userName}`)
新しいプレイヤーキャラクターのセーブデータを初期化・生成します。

#### リクエスト JSON スキーマ
```json
{
  "namespace": "world_alpha",
  "currentStatus": {
    "hp": 20,
    "mp": 10,
    "stamina": 100,
    "actionInterval": 1000,
    "subStep": 0
  }
}
```

#### レスポンス JSON スキーマ (201 Created)
```json
{
  "userId": "usr_998877665544",
  "name": "HeroPlayer",
  "namespace": "world_alpha",
  "createdAt": "2026-03-31T12:00:00Z"
}
```

---

### 5.3 プレイヤーセーブデータ照会 (`GET /api/v1/user/id/{userId}`)
指定したユーザーIDのプレイヤー状態（ステータス、位置情報、装備、パーティ編成等）を取得します。

#### レスポンス JSON スキーマ (200 OK)
```json
{
  "id": "usr_998877665544",
  "name": "HeroPlayer",
  "level": 1,
  "exp": 0,
  "gold": 500,
  "totalPkCount": 0,
  "currentKillStreak": 0,
  "bounty": 0,
  "namespace": "world_alpha",
  "currentStatus": {
    "hp": 20,
    "mp": 10,
    "stamina": 100,
    "actionInterval": 1000,
    "subStep": 0
  },
  "status": {
    "atk": 10,
    "def": 10,
    "magicAtk": 8,
    "magicDef": 8,
    "dex": 12,
    "mnd": 10,
    "maxHp": 20,
    "maxMp": 10,
    "maxStamina": 100,
    "attribute": "None"
  },
  "location": {
    "dungeonId": "dungeon_cave_01",
    "level": 1,
    "x": 5,
    "y": 8
  },
  "equipment": {
    "weapon": "item_sword_wood_001",
    "armor": "item_armor_leather_001",
    "ring1": null,
    "ring2": null
  },
  "skillIds": [101, 102],
  "activeMonsterIds": ["mon_inst_001", "mon_inst_002"],
  "statusEffects": [
    {
      "type": "Poison",
      "remainingTurns": 5,
      "value": 2
    }
  ]
}
```

---

### 5.4 プレイヤーセーブデータ保存・更新 (`PUT /api/v1/user/id/{userId}`)
ダンジョン探索中や拠点帰還時に、プレイヤーの現在の状態（HP、所持金、位置情報、装備等）を一括保存・更新します。

#### リクエスト JSON スキーマ
```json
{
  "id": "usr_998877665544",
  "name": "HeroPlayer",
  "level": 2,
  "exp": 120,
  "gold": 650,
  "totalPkCount": 0,
  "currentKillStreak": 0,
  "bounty": 0,
  "namespace": "world_alpha",
  "currentStatus": {
    "hp": 18,
    "mp": 8,
    "stamina": 85,
    "actionInterval": 1000,
    "subStep": 120
  },
  "status": {
    "atk": 11,
    "def": 10,
    "magicAtk": 8,
    "magicDef": 8,
    "dex": 12,
    "mnd": 10,
    "maxHp": 22,
    "maxMp": 10,
    "maxStamina": 100,
    "attribute": "None"
  },
  "location": {
    "dungeonId": "dungeon_cave_01",
    "level": 2,
    "x": 12,
    "y": 15
  },
  "equipment": {
    "weapon": "item_sword_wood_001",
    "armor": "item_armor_leather_001",
    "ring1": null,
    "ring2": null
  },
  "skillIds": [101, 102],
  "activeMonsterIds": ["mon_inst_001", "mon_inst_002"],
  "statusEffects": []
}
```

#### レスポンス JSON スキーマ (200 OK)
```json
{
  "userId": "usr_998877665544",
  "status": "SUCCESS",
  "updatedAt": "2026-03-31T12:05:00Z"
}
```

---

### 5.5 所持アイテム一覧照会 (`GET /api/v1/user/id/{userId}/items`)
指定したプレイヤーがインベントリ（バッグ）内に所持しているアイテムインスタンスIDの一覧を取得します。

#### レスポンス JSON スキーマ (200 OK)
```json
{
  "playerId": "usr_998877665544",
  "limitSize": 23,
  "objectIdList": [
    "item_sword_wood_001",
    "item_armor_leather_001",
    "item_bread_001",
    "item_bread_002"
  ]
}
```

---

### 5.6 所持アイテム一覧更新 (`POST /api/v1/user/id/{userId}/items`)
拾得・消費・破棄等によるインベントリのアイテムインスタンスIDリストを更新します。

#### リクエスト JSON スキーマ
```json
{
  "objectIdList": [
    "item_sword_wood_001",
    "item_armor_leather_001",
    "item_bread_001",
    "item_potion_hp_001"
  ]
}
```

#### レスポンス JSON スキーマ (200 OK)
```json
{
  "playerId": "usr_998877665544",
  "itemCount": 4,
  "limitSize": 23,
  "status": "SUCCESS"
}
```

---

## 6. エラーハンドリング仕様 (Error Handling Specification)

BookOfAdventureモジュールのAPI実行時にビジネスルール違反やリクエスト異常が発生した場合、以下の統一エラーフォーマットに従ってエラーレスポンスを返却します。

### 6.1 エラーレスポンス共通 JSON スキーマ
```json
{
  "errorCode": "PLAYER_NOT_FOUND",
  "message": "指定されたユーザーIDのプレイヤーデータが存在しません。",
  "timestamp": "2026-03-31T12:00:00Z"
}
```

### 6.2 エラーコード一覧およびマッピング

| エラーコード | HTTPステータス | 発生条件・説明 |
| :--- | :---: | :--- |
| `PLAYER_NOT_FOUND` | `404 Not Found` | 指定された `userId` または `userName` に該当するプレイヤーデータが存在しない場合。 |
| `DUPLICATE_USERNAME` | `409 Conflict` | 新規作成時、指定された `userName` がすでに他のプレイヤーで使用されている場合。 |
| `INVALID_USER_NAME` | `400 Bad Request` | プレイヤー名に使用不可文字が含まれている、または文字数制限（1〜16文字）を超過している場合。 |
| `INVENTORY_LIMIT_EXCEEDED` | `400 Bad Request` | `POST /items` 更新時、送付されたアイテム数がバッグ上限（デフォルト23）を超えている場合。 |
| `INVALID_STATUS_DATA` | `400 Bad Request` | 保存用データの属性値（HP負数、不正なレベル、不正な座標値等）がドメイン制約に違反している場合。 |
| `PARTY_SIZE_EXCEEDED` | `400 Bad Request` | アクティブパーティ (`activeMonsterIds`) に設定されたモンスター数が上限（最大3体）を超えている場合。 |
| `UNAUTHORIZED_USER_ACCESS` | `403 Forbidden` | 他ユーザーのセーブデータに対するアクセスや更新権限がない場合。 |
