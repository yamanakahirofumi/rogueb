# モンスター特性抽出システム

このドキュメントは、モンスターが保持する個体特性（Individual Traits）を抽出・結晶化し、他のモンスターへ移植可能な消費アイテム「特性オーブ（Trait Orb）」を生成する**モンスター特性抽出システム**の仕様を定義します。

---

## 1. 概要

モンスター特性抽出システムは、プレイヤーが丹念に育てたモンスターや所有するモンスターの「個体特性（`MonsterInstanceDomain.traits`）」を取り出し、汎用的な消費アイテム「特性オーブ」として結晶化する仕組みです。

抽出された特性オーブは、[モンスター特性システム](./Monster-Trait-System.md)で定義される個体特性スロット（最大2枠）に空きがある任意のモンスターに対して使用することで、該当の特性をダイレクトに習得・付与させることができます。
本システムにより、ビルドの試行錯誤や不要になったモンスターの有効活用、特定特性の受け渡しなどの自由度の高いモンスター育成が可能となります。

---

## 2. 抽出条件とプロセス

### 2.1 抽出実行条件
特性抽出を行うには、以下の条件をすべて満たす必要があります。

1. **レベル制限**: 対象モンスターのレベルが **10以上** であること。
2. **個体特性の所有**: 対象モンスターが1つ以上の「個体特性（Individual Trait）」を保持していること。
   - ※ 種族固有の特性（`MonsterDomain.traits`）は抽出対象外です。
3. **必要ゴールド**: 抽出費用として **3,000 Gold** を消費すること。
4. **必要触媒アイテム**: インベントリ内に「抽出の水晶球（`extraction_orb`）」を1つ以上所持していること。

### 2.2 抽出による代償と結果
抽出処理を実行すると、以下の結果が適用されます。

- **モンスターの変化**:
  - 指定された個体特性1つがモンスターから削除されます。
  - 抽出の反動により、モンスターのレベルが **1減少** します（レベル10未満には低下せず、最小レベル10で固定）。
  - モンスターの忠誠度（Loyalty）が **-20** 減少します（下限値0）。親愛状態のモンスターの場合は忠誠度の減少が免除されます。
- **獲得アイテム**:
  - 指定された個体特性と同等の効果を持つ「特性オーブ（`typeId: trait_orb_<trait_id>`）」が生成され、プレイヤーのバッグ（または倉庫）に格納されます。
- **消費資源**:
  - 3,000 Gold および「抽出の水晶球（`extraction_orb`）」1個が消費されます。

---

## 3. 特性オーブのデータ表現とアイテム化

抽出によって生成される「特性オーブ」は、[Objectsモジュール](./domain_models/Objects.md)における `CONSUMABLE`（消費アイテム）として扱われます。

### 3.1 アイテムデータ構造
- **`typeId`**: `trait_orb_<trait_id>` （例: `trait_orb_fire_boost_1`）
- **`category`**: `CONSUMABLE`
- **`name`**: `[特性名]のオーブ` （例: 火炎増幅Iのオーブ）
- **`display`**: `o`
- **`metadata`**:
  ```json
  {
    "targetTraitId": "trait_fire_boost_1",
    "traitLevel": 1,
    "extractedFrom": "monster_inst_9988"
  }
  ```

### 3.2 特性オーブの使用
- 特性オーブをバッグから使用し、対象モンスターを選択することで個体特性を付与できます。
- 付与対象モンスターの個体特性スロット（最大2枠）に空きがない場合、使用は拒否されます。
- すでに同系統の特性を保有している場合、重複付与はできません。

---

## 4. モジュール間連携フロー

1. **PlayerOperations → Monster**: 抽出リクエストを受け、対象モンスターのレベル・所有個体特性・所有権を検証。
2. **PlayerOperations → BookOfAdventure / EconomicSystem**: プレイヤーの所持金（3,000 Gold）および所持アイテム（`extraction_orb`）をチェック・消費。
3. **Monster**: モンスターの指定個体特性を削除し、レベル-1および忠誠度減少を適用して永続化。
4. **Objects**: 対応する特性オーブ（`trait_orb_<trait_id>`）のインスタンスを生成し、プレイヤーのバッグに付与。

---

## 5. API仕様

### 5.1 特性抽出の実行 (`POST /api/v1/monsters/{instanceId}/traits/extract`)

指定したモンスターの個体特性を1つ抽出し、特性オーブに変換します。

#### リクエスト (Request)
```json
{
  "userId": "user_12345",
  "traitId": "trait_fire_boost_1",
  "catalystItemId": "item_inst_ext_001"
}
```

#### レスポンス (Response: 200 OK)
```json
{
  "instanceId": "monster_inst_9988",
  "extractedTraitId": "trait_fire_boost_1",
  "remainingTraits": [
    {
      "traitId": "trait_hp_boost_1",
      "name": "HP増幅I",
      "level": 1
    }
  ],
  "updatedMonster": {
    "level": 14,
    "loyalty": 180
  },
  "createdOrbItem": {
    "instanceId": "item_inst_orb_777",
    "typeId": "trait_orb_fire_boost_1",
    "name": "火炎増幅Iのオーブ",
    "category": "CONSUMABLE",
    "metadata": {
      "targetTraitId": "trait_fire_boost_1",
      "traitLevel": 1,
      "extractedFrom": "monster_inst_9988"
    }
  },
  "goldConsumed": 3000,
  "remainingGold": 47000
}
```

---

## 6. エラーハンドリング仕様

特性抽出処理の実行時に発生する主要なエラーコードと対応するHTTPステータスコードです。

### 6.1 エラーコード一覧

| エラーコード | HTTPステータス | 説明 | 発生条件 |
| :--- | :--- | :--- | :--- |
| `MONSTER_NOT_FOUND` | `404 Not Found` | モンスター非存在 | 指定された `instanceId` のモンスターが存在しない。 |
| `MONSTER_NOT_OWNED` | `403 Forbidden` | モンスター所有権エラー | 指定されたモンスターがリクエストしたプレイヤーの所有物ではない。 |
| `INSUFFICIENT_LEVEL` | `400 Bad Request` | レベル不足 | 対象モンスターのレベルが10未満である。 |
| `TRAIT_NOT_FOUND` | `404 Not Found` | 指定特性非存在 | 指定された `traitId` の個体特性をモンスターが保持していない。 |
| `SPECIES_TRAIT_CANNOT_BE_EXTRACTED` | `400 Bad Request` | 種族特性抽出不可 | 指定された `traitId` が種族固定特性であり抽出できない。 |
| `INSUFFICIENT_GOLD` | `400 Bad Request` | ゴールド不足 | 所持金が抽出コスト（3,000 Gold）に満たない。 |
| `ITEM_NOT_FOUND` | `404 Not Found` | 触媒アイテム非存在 | 指定された `catalystItemId` が存在しない。 |
| `INVALID_ITEM_TYPE` | `400 Bad Request` | 触媒アイテム種別不正 | 指定されたアイテムが「抽出の水晶球（`extraction_orb`）」ではない。 |
| `BAG_FULL` | `400 Bad Request` | インベントリ容量不足 | 特性オーブを受け取るためのバッグの空き容量が存在しない。 |

### 6.2 エラーレスポンス形式例

```json
{
  "errorCode": "INSUFFICIENT_LEVEL",
  "message": "特性の抽出にはモンスターのレベルが10以上必要です。（現在のレベル: 8）",
  "status": 400,
  "timestamp": "2026-03-31T12:00:00Z"
}
```
