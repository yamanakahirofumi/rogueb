# 標準メタデータ仕様 (Standard Metadata Specification)

## 1. 概要
本ドキュメントは、`ThingInstance` および `MonsterInstanceDomain` の `metadata` フィールドにおける、予約済みキーとそのデータ型、および意味を定義します。これにより、モジュール間での一貫性を保ち、[世界間連携システム](./World-Interoperability-System.md) における未知のオブジェクトの持ち込み（データ継承）を円滑にします。

## 2. アイテム（ThingInstance）用メタデータ
アイテムの性能補正や特殊効果を定義するキーです。

### 2.1 ステータス補正キー
主に `WEAPON`, `ARMOR`, `RING` カテゴリで使用されます。

| キー | 型 | 意味 | 有効範囲 / 例 |
| :--- | :--- | :--- | :--- |
| `atk` | Integer | 物理攻撃力補正 | -999 〜 999 |
| `def` | Integer | 物理防御力補正 | -999 〜 999 |
| `magicAtk` | Integer | 魔法攻撃力補正 | -999 〜 999 |
| `magicDef` | Integer | 魔法防御力補正 | -999 〜 999 |
| `dex` | Integer | 器用さ（命中率等）補正 | -999 〜 999 |
| `mnd` | Integer | 精神力（耐性等）補正 | -999 〜 999 |
| `maxHp` | Integer | 最大 HP 補正 | -9999 〜 9999 |
| `maxMp` | Integer | 最大 MP 補正 | -9999 〜 9999 |
| `maxStamina` | Integer | 最大スタミナ補正 | -100 〜 100 |

### 2.2 特殊効果・カテゴリキー
| キー | 型 | 意味 | 有効範囲 / 例 |
| :--- | :--- | :--- | :--- |
| `slayer` | String | 特効対象カテゴリ | `UNDEAD`, `DRAGON`, `BEAST`, `DEMON` 等 |
| `meta_effects` | Array | 標準化エフェクトのリスト | オブジェクトの配列。詳細は後述。 |

---

## 3. モンスター（MonsterInstanceDomain）用メタデータ
個体特有のデータや、繁殖に関する情報を定義するキーです。

### 3.1 繁殖・ライフサイクル
| キー | 型 | 意味 | 有効範囲 / 例 |
| :--- | :--- | :--- | :--- |
| `incubationSteps` | Integer | 孵化に必要な総歩数 | 0 〜 10000 |
| `currentSteps` | Integer | 現在の蓄積歩数 | 0 〜 10000 |
| `parentAId` | String | 親 A のインスタンス ID | UUID 形式 |
| `parentBId` | String | 親 B のインスタンス ID | UUID 形式 |
| `typeId` | String | 卵から生まれる種族 ID | `slime`, `dragon` 等 |

### 3.2 個体データ
| キー | 型 | 意味 | 有効範囲 / 例 |
| :--- | :--- | :--- | :--- |
| `nickname` | String | 個体に付けられたニックネーム | 最大 16 文字 |
| `inheritanceData` | Object | 未知の種族を持ち込む際の継承データ | 種族基本ステータス、外見、特性等 |

---

## 4. 世界間連携用メタデータ（共通）
未知のアイテムやモンスターを他サーバーへ持ち込む際に付与される共通の拡張情報です。

| キー | 型 | 意味 | 有効範囲 / 例 |
| :--- | :--- | :--- | :--- |
| `meta_visual_asset_id` | String | アセット参照 ID | `asset/v1/sword_01` |
| `meta_sprite_data` | String | スプライト画像データ | Base64 エンコード文字列 |
| `isUnknown` | Boolean | 移動先で未知の種族/タイプとして扱われているか | `true` / `false` |

---

## 5. 標準化エフェクト構造（meta_effects）
`meta_effects` 配列に含まれる各オブジェクトの構造です。

### 5.1 基本構造
```json
{
  "id": "エフェクトID",
  "params": {
    "key": "value"
  }
}
```

### 5.2 エフェクト ID とパラメータ定義
| エフェクト ID | パラメータ | 型 | 意味 |
| :--- | :--- | :--- | :--- |
| `HEAL_HP` | `amount` | Integer | 固定回復量 |
| | `ratio` | Double | 割合回復 (0.0 - 1.0) |
| `HEAL_MP` | `amount` | Integer | 固定回復量 |
| | `ratio` | Double | 割合回復 (0.0 - 1.0) |
| `HEAL_STAMINA` | `amount` | Integer | 固定回復量 |
| `ADD_STATUS` | `status` | String | 状態異常の種類 (`POISON` 等) |
| | `chance` | Double | 付与確率 (0.0 - 1.0) |
| | `turns` | Integer | 持続ターン数 |
| `REMOVE_STATUS` | `status` | String | 解除する状態異常の種類 |
| `DEAL_DAMAGE` | `damage` | Integer | 直接ダメージ量 |
| `EXPLOSION` | `damage` | Integer | 爆発ダメージ量 |
| | `range` | Integer | 爆発範囲 (1: 周囲 8 マス) |
| `TELEPORT` | `range` | Integer | ワープ範囲（任意） |

---

## 6. 実装時のルール
- **数値の正規化**: メタデータに格納される数値は、計算の簡略化のため原則として整数（Integer）または倍精度浮動小数点（Double）を使用します。
- **未知のキー**: システムは定義されていないキーを無視（無視して保持）し、データの欠落を防ぎます。
- **データ型の一貫性**: 同一のキーに対しては、常に同じデータ型を使用しなければなりません。
