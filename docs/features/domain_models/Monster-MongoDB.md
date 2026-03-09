# Monsterモジュール MongoDBデータ構造

このドキュメントは、**Monster**モジュールのドメインオブジェクトがMongoDBにどのように永続化されるかについて説明します。

## 1. `monsterDomain` コレクション
- **説明:** `MonsterDomain`クラスに対応します。モンスターの種族ごとの基本情報を保持します。
- **フィールド:**
    - `_id` (String): 種族の一意な識別子（例：「スライム」「オーク」）。
    - `name` (String): モンスターの名称。
    - `baseHp` (Integer): 基本体力。
    - `baseAtk` (Integer): 基本攻撃力。
    - `baseDef` (Integer): 基本防御力。
    - `baseMagicAtk` (Integer): 基本魔法攻撃力。
    - `baseMagicDef` (Integer): 基本魔法防御力。
    - `baseDex` (Integer): 基本器用さ。
    - `baseMnd` (Integer): 基本精神力。
    - `display` (String): マップ上で表示される文字。
    - `expValue` (Integer): 倒した際に得られる経験値。
    - `type` (String): モンスターの属性やカテゴリ。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## 2. `monsterInstanceDomain` コレクション
- **説明:** `MonsterInstanceDomain`クラスに対応します。特定のモンスター個体の状態を保持します。
- **フィールド:**
    - `_id` (String): 個体の一意な識別子（instanceId）。
    - `monsterId` (String): 関連する`monsterDomain`のID。
    - `level` (Integer): 現在のレベル。
    - `currentHp` (Integer): 現在の体力。
    - `statusEffects` (Array): 付与されている状態異常のリスト。
    - `ownerId` (String): 所有しているプレイヤーのID。
    - `isWild` (Boolean): 野生状態かどうかを示すフラグ。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。
