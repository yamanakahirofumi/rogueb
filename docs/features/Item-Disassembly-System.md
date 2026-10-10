# アイテム分解・リサイクルシステム仕様 (Item Disassembly & Recycling System)

## 1. 概要と目的
本システムは、ダンジョン探索やモンスタードロップ、調合・錬金等で入手した不要な装備品（武器、防具、指輪）、巻物、杖、資材アイテム等を解体・分解し、クラフト・錬金・建築・倉庫拡張・アイテム強化に利用可能な基本素材およびレア資材へ還元する機能「アイテム分解・リサイクルシステム」の仕様を定義します。

### 導入の目的
- **インベントリ圧迫の解消**: 探索中に蓄積する低ティア・重複・呪われた装備品の有効活用手段を提供します。
- **経済・循環ループの強化**: 不要アイテムを破棄または低価格で売却するだけでなく、[ダンジョン錬金・調合システム](./Item-Synthesis-Alchemy-System.md) や [アイテムエンチャントシステム](./Item-Enchantment-System.md) で必要となる上位素材（`magic_powder`, `elemental_essence_*` 等）の供給源として機能させます。

---

## 2. 分解メカニズムと手段

アイテムの分解を実行するには、持ち運び可能な消費アイテム「分解キット（`disassembly_kit`）」を使用するか、ダンジョン内または街に設置された建築オブジェクト「鍛冶解体炉（`disassembly_workshop`）」を利用します。

### 2.1 分解手段の比較

| 分解手段 | 場所・利用条件 | 素材還元率 (Yield Multiplier) | レア素材ボーナス | 特徴 |
| :--- | :--- | :---: | :---: | :--- |
| **分解キット** (`disassembly_kit`) | インベントリ内（どこでも実行可能） | 70% (0.70) | 0% (1.0x) | 1回につきキットを1個消費。探索中に即座にバッグの空きを確保可能。 |
| **鍛冶解体炉** (`disassembly_workshop`) | 街の工房またはダンジョン内建築タイル | 100% (1.00) | +15% (1.15x) | キット消費なし。拠点等に移動してまとめて解体・高効率還元を行う際に使用。 |

---

## 3. 素材還元・計算式

アイテム分解時に獲得できる素材の「種類」および「獲得個数」は、対象アイテムの**カテゴリ (`TypeEnum`)**、**ティア (`tier`)**、**標準価格 (`standardPrice`)**、および**付与されているエンチャント・属性**に基づいて算出されます。

### 3.1 基本獲得素材テーブル

分解対象のカテゴリに応じて、必ず獲得できる基本素材が決定されます。

| カテゴリ (`TypeEnum`) | 基本獲得素材 ID | 基本素材名称 | 獲得数算出基準 |
| :--- | :--- | :--- | :--- |
| `WEAPON` | `stone_floor` / `iron_ore` | 鉱石・金属資材 | `max(1, floor(standardPrice / 100 * YieldMultiplier))` |
| `ARMOR` | `stone_floor` / `leather_scrap` | 皮革・金属資材 | `max(1, floor(standardPrice / 100 * YieldMultiplier))` |
| `RING` | `magic_powder` | 魔導の粉 | `max(1, floor(tier * 2 * YieldMultiplier))` |
| `SCROLL` | `magic_powder` | 魔導の粉 | `max(1, floor(1 * YieldMultiplier))` |
| `STICK` | `stone_floor` | 杖の削り木 | `max(1, floor(charges * 0.5 * YieldMultiplier))` |
| `MATERIAL` | 同種基本資材 | 各種建材 | `max(1, floor(quantity * 0.5 * YieldMultiplier))` |
| `OTHER` | `magic_powder` | 魔導の粉 | `max(1, floor(tier * YieldMultiplier))` |

### 3.2 レア素材還元確率

ティア 2 以上の装備品、属性付きアイテム、およびエンチャント付与済みアイテムを分解する場合、以下の確率で希少素材が追加獲得されます。

#### 希少素材のドロップ計算
- **魔導の粉 (`magic_powder`)**:
  - 発生確率: `Tier * 20%`
  - 獲得数: `1 〜 Tier` 個
- **エレメント精導石 (`elemental_essence_fire`, `elemental_essence_frost`, `elemental_essence_lightning`)**:
  - 対象: 火・水/氷・風/雷属性（`Fire`, `Water`, `Wind`）が付与されたアイテム
  - 発生確率: `50%`
  - 獲得数: `1` 個
- **増築用資材 (`expansion_material`)**:
  - 対象: ティア 3 以上の `ARMOR` または `WEAPON`
  - 発生確率: `(Tier - 2) * 10%` （例: ティア 3 は 10%、ティア 4 は 20%）
  - 獲得数: `1` 個

### 3.3 呪い・エンチャント補正
- **呪われたアイテム (`isCursed` = true)**:
  - 呪いを解かずに分解可能ですが、素材還元率が **50% ペナルティ（0.5x）** となります。
- **エンチャント保持アイテム**:
  - エンチャントが 1 つ付与されているごとに、`magic_powder` の獲得数が **+1** 確定で増加します。

---

## 4. データモデルおよび永続化仕様

分解処理の実行履歴は、不正防止およびプレイヤー実績・ログ照会のため MongoDB の `playerDisassemblyLogDomain` コレクションに永続化されます。

### 4.1 `playerDisassemblyLogDomain` MongoDB コレクション構造
- **説明:** プレイヤーのアイテム分解・リサイクル実行履歴を保存します。
- **フィールド:**
    - `_id` (String): 一意なログ ID（UUID 形式）。
    - `userId` (String): 実行したプレイヤー ID。
    - `disassembledItemId` (String): 分解されたアイテムの `instanceId`（破棄前 ID）。
    - `disassembledTypeId` (String): 分解されたアイテムの `typeId`（例: `iron_sword`）。
    - `method` (String): 分解手段（`KIT` または `WORKSHOP`）。
    - `yieldedMaterials` (Array): 獲得した素材アイテムのリスト。
        - `typeId` (String): 素材アイテム ID（例: `magic_powder`）。
        - `quantity` (Integer): 獲得個数。
    - `createdAt` (Date): 実行日時。
    - `_class` (String): Spring Data MongoDB クラス情報。

---

## 5. API仕様 (API Specifications)

### 5.1 アイテム分解実行 (`POST /api/v1/objects/disassemble`)
指定されたアイテムインスタンスを解体・分解し、生成された素材をプレイヤーのインベントリに加算します。

#### リクエスト JSON スキーマ
```json
{
  "userId": "user_772101",
  "targetInstanceId": "obj_inst_998213",
  "method": "KIT",
  "kitInstanceId": "obj_inst_443102"
}
```

#### レスポンス JSON スキーマ (200 OK)
```json
{
  "disassembleLogId": "dis_log_883192",
  "status": "SUCCESS",
  "disassembledItem": {
    "instanceId": "obj_inst_998213",
    "typeId": "iron_sword",
    "name": "鉄の剣"
  },
  "yieldedMaterials": [
    {
      "typeId": "stone_floor",
      "name": "石の床",
      "quantity": 3
    },
    {
      "typeId": "magic_powder",
      "name": "魔導の粉",
      "quantity": 1
    }
  ],
  "timestamp": "2026-03-31T12:00:00Z"
}
```

---

### 5.2 アイテム分解プレビュー (`POST /api/v1/objects/disassemble/preview`)
消費を実行せずに、対象アイテムを分解した場合に予想される獲得素材と還元確率を事前照会します。

#### リクエスト JSON スキーマ
```json
{
  "userId": "user_772101",
  "targetInstanceId": "obj_inst_998213",
  "method": "WORKSHOP"
}
```

#### レスポンス JSON スキーマ (200 OK)
```json
{
  "targetInstanceId": "obj_inst_998213",
  "method": "WORKSHOP",
  "yieldMultiplier": 1.0,
  "expectedMaterials": [
    {
      "typeId": "stone_floor",
      "name": "石の床",
      "guaranteedQuantity": 5,
      "chance": 1.0
    },
    {
      "typeId": "magic_powder",
      "name": "魔導の粉",
      "guaranteedQuantity": 0,
      "chance": 0.20
    }
  ]
}
```

---

## 6. エラーハンドリング仕様 (Error Handling Specification)

API実行時に発生したエラーは、以下のエラーコードおよびHTTPステータスコードにマッピングされて返却されます。

| エラーコード | HTTPステータス | 発生条件・説明 |
| :--- | :---: | :--- |
| `ITEM_NOT_FOUND` | `404 Not Found` | 分解対象または消費キットのアイテムマスタが存在しない場合。 |
| `INSTANCE_NOT_FOUND` | `404 Not Found` | 指定された `targetInstanceId` または `kitInstanceId` がインベントリに存在しない場合。 |
| `ITEM_NOT_DISASSEMBLABLE` | `400 Bad Request` | 分解不可のキーアイテムや特殊オブジェクトを指定した場合。 |
| `INSUFFICIENT_DISASSEMBLY_KIT` | `400 Bad Request` | `method` = `KIT` 指定時に、有効な分解キットが指定されていない場合。 |
| `INVENTORY_FULL` | `409 Conflict` | 還元素材を獲得するためのインベントリ空き枠が不足している場合。 |
| `UNAUTHORIZED_OBJECT_OPERATOR` | `403 Forbidden` | 他のプレイヤーが所持するアイテムの分解を試みた場合。 |

---

## 7. 他システムとの連携

- **[Objectsモジュール](./domain_models/Objects.md)**: 分解ツールおよび各種還元素材アイテムのマスタ定義。
- **[ダンジョン錬金・アイテム調合システム](./Item-Synthesis-Alchemy-System.md)**: 分解で獲得した `magic_powder` や `elemental_essence_*` を調合用触媒として利用。
- **[倉庫システム](./Storage-System.md)**: 分解で獲得した `expansion_material` を用いて倉庫枠を段階的に拡張。
- **[UI-UX 設計](./UI-UX-Design.md)**: 分解実行時の解体アニメーション、素材入手ポップアップ、および成功音 SE 演出。
