# BookOfAdventureモジュール MongoDBデータ構造

このドキュメントは、**BookOfAdventure**モジュールのドメインオブジェクトがMongoDBにどのように永続化されるかについて説明します。

## 1. `playerDomain` コレクション
- **説明:** `PlayerDomain`クラスに対応します。プレイヤーの基本情報、ステータス、現在地、インベントリ情報を保持します。
- **フィールド:**
    - `_id` (String): ユーザーID（通常はシステムが生成するUUID）。
    - `name` (String): プレイヤー名。
    - `level` (Integer): レベル。
    - `exp` (Integer): 累積経験値。
    - `gold` (Integer): 所持金額。
    - `totalPkCount` (Integer): 累計撃破数。
    - `currentKillStreak` (Integer): 現在の連続撃破数。
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

## 5. `playerMonsterEncyclopediaDomain` コレクション
- **説明:** `PlayerMonsterEncyclopediaDomain`クラスに対応します。プレイヤーごとのモンスター図鑑の解放状況、撃破数、捕獲数、初遭遇日時を保持します。
- **フィールド:**
    - `_id` (String): 一意なID（例: `enc_usr_9921_slime_001`）。
    - `userId` (String): ユーザーID。
    - `monsterTypeId` (String): モンスター種族ID。
    - `unlockStage` (String): 情報開示段階 (`DISCOVERED`, `DEFEATED`, `CAPTURED`, `MASTERED`)。
    - `defeatCount` (Integer): 累計撃破数。
    - `captureCount` (Integer): 累計捕獲・獲得数。
    - `firstDiscoveredAt` (Date): 初遭遇日時。
    - `firstCapturedAt` (Date): 初獲得日時。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## 6. `playerAchievementDomain` コレクション
- **説明:** `PlayerAchievementDomain`クラスに対応します。プレイヤーごとの実績の累積進捗、目標数値、達成フラグ、報酬受領フラグを保持します。
- **フィールド:**
    - `_id` (String): 一意なID (例: `ach_usr_12345_exp_floor_30`)。
    - `userId` (String): ユーザーID。
    - `achievementId` (String): 実績定義ID。
    - `category` (String): 実績カテゴリ (`EXPLORATION`, `MONSTER_MASTER` 等)。
    - `currentProgress` (Long): 現在の進捗累積値。
    - `targetProgress` (Long): 目標達成値。
    - `isCompleted` (Boolean): 達成フラグ。
    - `isClaimed` (Boolean): 報酬受領フラグ。
    - `completedAt` (Date): 達成日時。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## 7. `playerTitleDomain` コレクション
- **説明:** `PlayerTitleDomain`クラスに対応します。プレイヤーごとの解禁済み称号リストおよび現在装着中のアクティブ称号を保持します。
- **フィールド:**
    - `_id` (String): ユーザーID（通常は1ユーザー1ドキュメント）。
    - `userId` (String): ユーザーID。
    - `unlockedTitleIds` (Array): 解禁済み称号IDの配列。
    - `equippedTitleId` (String): 現在装着中のアクティブ称号ID（未装着時は null）。
    - `updatedAt` (Date): 最終更新日時。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## 8. `rescueRequestDomain` コレクション
- **説明:** `RescueRequestDomain`クラスに対応します。救助掲示板で管理される救助リクエストデータを保持します。詳細は [ダンジョン救助＆墓標システム](../Dungeon-Rescue-System.md) を参照。
- **フィールド:**
    - `_id` (String): リクエストの一意なID (`rescueRequestId`)。
    - `tombstoneId` (String): 関連付けられた墓標のID。
    - `requesterUserId` (String): 倒伏プレイヤーのユーザー ID。
    - `helperUserId` (String): 救助者のユーザー ID。
    - `dungeonId` (String): ダンジョン ID。
    - `floorLevel` (Integer): 階層レベル。
    - `rewardGold` (Integer): 報酬金。
    - `privacyType` (String): `PUBLIC`, `GUILD_ONLY`, `FRIEND_ONLY`, `PASSCODE`
    - `passcode` (String): パスフレーズ。
    - `status` (String): `PENDING`, `IN_PROGRESS`, `COMPLETED`, `EXPIRED`
    - `createdAt` (Long): 生成タイムスタンプ。
    - `expiresAt` (Long): 有効期限タイムスタンプ。
    - `_class` (String): Spring Data MongoDB クラス情報。

## 9. インデックス推奨事項

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

### `playerMonsterEncyclopediaDomain`
- `{"userId": 1}`: ユーザーごとの図鑑解放状況を一覧検索するために必須。
- `{"userId": 1, "monsterTypeId": 1}`: ユニーク複合インデックス。特定のモンスター種族の図鑑データ照会・更新用。

### `playerAchievementDomain`
- `{"userId": 1}`: ユーザーごとの実績進捗一覧検索用。
- `{"userId": 1, "achievementId": 1}`: ユニーク複合インデックス。特定実績の進捗更新・受領判定用。

### `playerTitleDomain`
- `{"userId": 1}`: ユニークインデックス。ユーザーごとの所有称号および装着状態照会・更新用。

### `rescueRequestDomain`
- `{"requesterUserId": 1, "status": 1}`: 依頼者のリクエスト状況照会用。
- `{"status": 1, "privacyType": 1}`: 救助掲示板での未受託リクエスト一覧表示用。
- `{"expiresAt": 1}`: 期限切れリクエスト自動クリーンアップ用 TTL インデックス。
