# BookOfAdventureモジュール MongoDBデータ構造

このドキュメントは、**BookOfAdventure**モジュールのドメインオブジェクトがMongoDBにどのように永続化されるかについて説明します。

## 1. `playerDomain` コレクション
- **説明:** `PlayerDomain`クラスに対応します。プレイヤーの基本情報、ステータス、現在地、インベントリ情報を保持します。
- **フィールド:**
    - `_id` (String): ユーザーID（通常はシステムが生成するUUID）。
    - `name` (String): プレイヤー名。
    - `exp` (Integer): 累積経験値。
    - `gold` (Integer): 所持金額。
    - `totalPkCount` (Integer): 累計 PK 数。
    - `currentKillStreak` (Integer): 現在の連続 PK 数。
    - `bounty` (Integer): 賞金額。
    - `namespace` (String): 所属するワールドやネームスペース。
    - `currentStatus` (Map): 現在の変動するステータス。
        - キー: `hp`, `mp`, `stamina`, `actionInterval`, `seed`, `subStep`
    - `status` (Map): 固定または基本のステータス情報。
        - キー: `atk`, `def`, `magicAtk`, `magicDef`, `dex`, `maxHp`, `maxMp`, `attribute`, `mnd`, `maxStamina`
    - `location` (Map): `dungeonId`, `level`, `x`, `y` を含む位置情報マップ。
    - `equipment` (Map): `weapon`, `armor`, `ring1`, `ring2` をキーとし、アイテムインスタンス ID を値とするマップ。
    - `skillIds` (Array): 習得しているスキル ID の配列。
    - `statusEffects` (Array): 付与されている状態異常の配列。
        - `type` (String)
        - `remainingTurns` (Integer)
        - `value` (Integer)
    - `_class` (String): Spring Data MongoDBが使用するクラス情報（例: `net.hero.rogueb.bookofadventure.domain.PlayerDomain`）。

## 2. `playerObjectDomain` コレクション
- **説明:** `PlayerObjectDomain`クラスに対応します。プレイヤーの所持アイテム一覧を保持します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `playerId` (String): プレイヤーのID。
    - `objectIdList` (Array): アイテムのインスタンスIDの配列。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報（例: `net.hero.rogueb.bookofadventure.domain.PlayerObjectDomain`）。

## 3. `playerMonsterDomain` コレクション
- **説明:** `PlayerMonsterDomain`クラスに対応します。プレイヤーの所持モンスター一覧を保持します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `playerId` (String): プレイヤーのID。
    - `monsterIdList` (Array): モンスターのインスタンスIDの配列。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## 4. `playerKnowledgeDomain` コレクション
- **説明:** `PlayerKnowledgeDomain`クラスに対応します。プレイヤーごとのアイテム識別状況を保持します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `userId` (String): ユーザーID。
    - `worldId` (String): ワールドID。
    - `typeId` (String): アイテムタイプID。
    - `isIdentified` (Boolean): 識別済みかどうか。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報（例: `net.hero.rogueb.bookofadventure.domain.PlayerKnowledgeDomain`）。

## 4. インデックス推奨事項

### `playerDomain`
- `{"name": 1}`: プレイヤー名によるユニーク検索に必須。
- `{"namespace": 1}`: ワールド内や特定の領域のプレイヤーを一覧する場合。

### `playerObjectDomain`
- `{"playerId": 1}`: プレイヤーの所持アイテムを検索するために必須。

### `playerMonsterDomain`
- `{"playerId": 1}`: プレイヤーの所持モンスターを検索するために必須。

### `playerKnowledgeDomain`
- `{"userId": 1, "worldId": 1}`: ユーザーが特定のワールドで持っている知識を一覧するために必須。
- `{"userId": 1, "worldId": 1, "typeId": 1}`: ユニークインデックス。
