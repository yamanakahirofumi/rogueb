# Dungeonモジュール MongoDBデータ構造

このドキュメントは、**Dungeon**モジュールのドメインオブジェクトがMongoDBにどのように永続化されるかについて説明します。

## 1. `dungeonDomain` コレクション
- **説明:** `DungeonDomain`クラスに対応します。各ドキュメントは一つのダンジョンを表します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `name` (String): ダンジョンの名前。
    - `adminId` (String): 管理者のユーザー ID。
    - `entryFee` (Integer): 入場料。
    - `maxLevel` (Integer): 最大フロア数。
    - `rank` (String): ダンジョンランク。
    - `dungeonExp` (Long): 累計経験値。
    - `itemSeed` (Integer): アイテム生成用シード値。
    - `monsterSeed` (Integer): モンスター生成用シード値。
    - `roomCountSeed` (Integer): 部屋数生成用シード値。
    - `namespace` (String): 論理的な名前空間。
    - `isIntrusionEnabled` (Boolean): 乱入許可フラグ。
    - `interventionPoints` (Integer): 介入ポイント。
    - `environmentalEffects` (Array): 環境効果のリスト (Stringの配列: `DARKNESS`, `MIASMA` 等)。
    - `lastActivityDate` (Long): 最終アクティビティ日時のタイムスタンプ。
    - `lastBossDefeatDate` (Long): 最終ボス撃破日時のタイムスタンプ。
    - `deathPenalty` (Object): デスペナルティの設定 (`DeathPenaltyDomain`)。
        - `itemForfeitureType` (String): `NONE`, `RANDOM`, `ALL`
        - `goldLossType` (String): `NONE`, `FIXED`, `PERCENTAGE`, `ALL`
        - `goldLossValue` (Double)
        - `statusResetType` (String): `NONE`, `EXP_REDUCTION`, `LEVEL_RESET`
    - `clearCondition` (Object): クリア条件 (`ClearConditionDomain`)。
        - `type` (String): `FLOOR_REACHED`, `BOSS_DEFEATED`, `ITEM_ACQUIRED`
        - `targetValue` (String)
    - `clearReward` (Object): クリア報酬。
        - `gold` (Integer)
        - `itemInstanceIds` (Array)
    - `_class` (String): Spring Data MongoDBが使用するクラス情報（例: `net.hero.rogueb.dungeon.domain.DungeonDomain`）。

## 2. `floorDomain` コレクション
- **説明:** `FloorDomain`クラスに対応します。各ドキュメントはダンジョン内の一つのフロアを表します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `dungeonId` (String): 関連する`dungeonDomain`のID。
    - `userId` (String): このフロアが割り当てられているユーザーのID。
    - `level` (Integer): フロアの階層レベル。
    - `upStairs` (Object): 上り階段の座標 (`Coordinate`オブジェクト)。
    - `downStairs` (Object): 下り階段の座標 (`Coordinate`オブジェクト)。
    - `thingList` (Array): フロア上のアイテムのリスト (`ObjectCoordinateDomain`オブジェクトの配列)。
        - `position` (Object): 座標 (`Coordinate`)。
        - `instanceId` (String): アイテムのインスタンス ID。
    - `goldList` (Array): フロア上の金のリスト (`GoldCoordinateDomain`オブジェクトの配列)。
    - `monsterList` (Array): フロア上のモンスターのリスト (`MonsterCoordinateDomain`オブジェクトの配列)。
        - `position` (Object): 座標 (`Coordinate`)。
        - `instanceId` (String): モンスターのインスタンス ID。
    - `shopList` (Array): フロア上のショップのリスト。
        - `position` (Object): 座標 (`Coordinate`)。
        - `shopId` (String): ショップの ID。
    - `trapList` (Array): フロア上のトラップのリスト (`TrapCoordinateDomain`オブジェクトの配列)。
        - `position` (Object): 座標 (`Coordinate`)。
        - `trapId` (Integer): トラップの種別 ID（[トラップシステム](../Trap-System.md) の ID に対応）。
        - `isRevealed` (Boolean): 露出フラグ。
    - `tiles` (Array): 2次元のタイル情報（`Tile`オブジェクトの配列の配列）。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## 3. `dungeonPlayerDomain` コレクション
- **説明:** `DungeonPlayerDomain`クラスに対応します。プレイヤーのダンジョン内での現在の階層情報を保持します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `dungeonId` (String): ダンジョンのID。
    - `playerId` (String): プレイヤーのID。
    - `level` (Integer): 現在の階層レベル。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報（例: `net.hero.rogueb.dungeon.domain.DungeonPlayerDomain`）。

## 4. インデックス推奨事項

### `dungeonDomain`
- `{"name": 1}`: ダンジョンを名前で検索する場合。
- `{"namespace": 1}`: ネームスペースによるグルーピングを行う場合。

### `floorDomain`
- `{"dungeonId": 1, "level": 1}`: 特定のダンジョンの特定の階層を検索するために必須です。
- `{"userId": 1, "dungeonId": 1}`: ユーザーごとのダンジョンインスタンスを管理する場合に使用します。

### `dungeonPlayerDomain`
- `{"playerId": 1}`: プレイヤーの現在の滞在ダンジョンを特定するために使用します。
- `{"dungeonId": 1}`: 特定のダンジョンにいるプレイヤーを一覧表示するために使用します。
