# Monsterモジュール MongoDBデータ構造

このドキュメントは、**Monster**モジュールのドメインオブジェクトがMongoDBにどのように永続化されるかについて説明します。

## 1. `monsterDomain` コレクション
- **説明:** `MonsterDomain`クラスに対応します。モンスターの種族ごとの基本情報を保持します。
- **フィールド:**
    - `_id` (String): 種族の一意な識別子（例：「スライム」「オーク」）。
    - `name` (String): モンスターの名称。
    - `baseHp` (Integer): 基本体力。
    - `baseMp` (Integer): 基本魔法力。
    - `baseAtk` (Integer): 基本攻撃力。
    - `baseDef` (Integer): 基本防御力。
    - `baseMagicAtk` (Integer): 基本魔法攻撃力。
    - `baseMagicDef` (Integer): 基本魔法防御力。
    - `baseDex` (Integer): 基本器用さ。
    - `baseMnd` (Integer): 基本精神力。
    - `display` (String): マップ上で表示される文字。
    - `expValue` (Integer): 倒した際に得られる経験値。
    - `type` (String): モンスターのカテゴリ。
    - `attribute` (String): モンスターの属性（Fire, Water, Wind, Earth, None）。
    - `baseActionInterval` (Integer): 基本行動間隔。
    - `placementCost` (Integer): 配置コスト。
    - `baseIncubationSteps` (Integer): 基本孵化歩数。
    - `skillTable` (Array): `MonsterSkillSlot` オブジェクトの配列。
        - `skillId` (Integer)
        - `level` (Integer)
    - `dropTable` (Array): `MonsterDropSlot` オブジェクトの配列。
        - `typeId` (String)
        - `weight` (Integer)
        - `minCount` (Integer)
        - `maxCount` (Integer)
    - `evolutionTable` (Array): `MonsterEvolutionSlot` オブジェクトの配列。
        - `targetMonsterId` (String)
        - `requiredLevel` (Integer)
        - `requiredItemId` (String)
        - `requiredStats` (Map)
        - `resetLevel` (Boolean)
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## 2. `monsterInstanceDomain` コレクション
- **説明:** `MonsterInstanceDomain`クラスに対応します。特定のモンスター個体の状態を保持します。
- **フィールド:**
    - `_id` (String): 個体の一意な識別子（instanceId）。
    - `monsterId` (String): 関連する`monsterDomain`のID。
    - `level` (Integer): 現在のレベル。
    - `currentHp` (Integer): 現在の体力。
    - `currentMp` (Integer): 現在の魔法力。
    - `subStep` (Integer): 内部歩数カウンタ。
    - `experience` (Long): 累積経験値。
    - `skillIds` (Array): 習得しているスキル ID の配列。
    - `inheritedStatus` (Map): 継承されたステータス補正。
    - `statusEffects` (Array): 付与されている状態異常の配列。
        - `type` (String)
        - `remainingTurns` (Integer)
        - `value` (Integer)
    - `metadata` (Object): 個体固有の動的データ（Map<String, Object>）。
    - `ownerId` (String): 所有しているプレイヤーのID。
    - `isWild` (Boolean): 野生状態かどうかを示すフラグ。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## 3. インデックス推奨事項

### `monsterDomain`
- `{"name": 1}`: 名前による検索。
- `{"type": 1}`: カテゴリ（種族系）による検索。

### `monsterInstanceDomain`
- `{"ownerId": 1}`: プレイヤーが所持するモンスターを検索する場合に必須。
- `{"isWild": 1}`: 野生モンスターのみを抽出する場合に使用します。
