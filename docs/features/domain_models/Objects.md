# Objectsモジュール ドメインモデル

このドキュメントは、**Objects**モジュールのコアとなるドメインモデルについて説明します。

## 1. コアコンセプト

Objectsモジュールは、武器、防具、ポーション、指輪など、すべてのゲーム内アイテムおよびオブジェクトの定義、作成、管理を担当します。ゲームワールドで対話可能なすべての「モノ（`Thing`）」のファクトリおよびリポジトリとして機能します。

---

## 2. コアとなるインターフェースとクラス

### `Thing` (インターフェース)
- **ファイル:** `Objects/src/main/java/net/hero/rogueb/objects/Thing.java`
- **説明:** すべてのゲームオブジェクトの中心となるインターフェースです。すべてのアイテムやオブジェクトが準拠しなければならない共通の規約を定義します。
- **主要なメソッド:**
    - `getId()`: オブジェクトのタイプの一意な識別子を返します（例：「レザーアーマー」のID）。
    - `getDisplay()`: マップ上でオブジェクトを表すために使用される文字を返します（例：防具の場合は`[`、武器の場合は`）`）。
    - `getName()`: オブジェクトの名前を返します（例：「ショートソード」）。
    - `getType()`: オブジェクトのカテゴリを示す`TypeEnum`値を返します。
    - `getStandardPrice()`: オブジェクトの標準価格を返します。
    - `getAttribute()`: オブジェクトの属性（Fire, Water, Wind, Earth, Holy, Dark, None）を返します。
    - `getTier()`: オブジェクトのティア（ランク）を返します（ドロップ置換ロジック等で使用）。
    - `getMaxLimit()`: オブジェクトの世界全体における存在上限数を返します（経済システムの流通管理で使用）。
    - `isMany()`: オブジェクトがスタック可能かどうかを示すブール値。

### `TypeEnum` (列挙型)
- **ファイル:** `Objects/src/main/java/net/hero/rogueb/objects/domain/TypeEnum.java`
- **説明:** オブジェクトのカテゴリを定義する列挙型。
- **値:**
    - `ARMOR`: 防具
    - `RING`: 指輪
    - `SCROLL`: 巻物
    - `WEAPON`: 武器
    - `STICK`: 杖
    - `POTION`: ポーション（薬）
    - `FOOD`: 食料
    - `MATERIAL`: 建築資材
    - `OTHER`: その他

### `ThingInstance` (レコード)
- **ファイル:** `Objects/src/main/java/net/hero/rogueb/objects/ThingInstance.java`
- **説明:** `Thing`の一意で具体的なインスタンスを表します。`Thing`がオブジェクトのタイプ（「力の指輪」など）を定義するのに対し、`ThingInstance`はプレイヤーが発見する可能性のある、独自の一意なインスタンスIDを持つ特定のオブジェクトを表します。
- **注意**: 「ゴールド（金）」は `ThingInstance` としては扱われず、プレイヤーの `gold` フィールドやダンジョンの `GoldCoordinateDomain` として数値で直接管理されます。
- **フィールド:**
    - `instanceId`: このオブジェクトの特定のインスタンスのための一意なID。
    - `typeId`: このインスタンスが派生したベースとなる`Thing`のID。
    - `name`: オブジェクトの名前。
    - `display`: オブジェクトの表示文字。
    - `attribute`: オブジェクトの属性。
    - `standardPrice`: オブジェクトの標準価格。
    - `tier`: オブジェクトのティア。
    - `metadata`: インスタンス固有の動的データ（Map<String, Object>）。
        - 予約済みキーの詳細は **[標準メタデータ仕様](../Standard-Metadata-Specification.md)** を参照してください。

### 2.4 スタック可能なアイテム (`isMany` = true)
- **個数管理**: `ThingInstance.metadata` 内の `quantity` キーによって現在の個数を保持します。
- **同一性の判定**:
    - `isMany` が true のアイテムは、`typeId` およびメタデータの内容（属性、呪いの有無、識別状態等）が完全に一致する場合にのみスタック可能です。
- **インスタンス ID の扱い**:
    - スタックされたアイテム群は、一つの `instanceId` を共有する一つの `ThingInstance` として管理されます。
    - **合算**: アイテムを拾った際、インベントリ内にスタック可能な同種のアイテムが存在する場合、既存のインスタンスの `quantity` を加算し、新たに拾ったインスタンスは破棄されます。
    - **分割**: スタックから一部を取り出す（投げる、置く、消費する等）際、取り出されたアイテムには新しい `instanceId` が割り当てられ、元のスタックの `quantity` が減算されます。

---

## 3. ドメインエンティティ（例）

これらは`Thing`インターフェースの具体的な実装であり、通常はデータベースに保存されます。

### `RingDomain`
- **ファイル:** `Objects/src/main/java/net/hero/rogueb/objects/domain/RingDomain.java`
- **説明:** 指輪のタイプを表す永続的なドメインオブジェクトです。`Thing`インターフェースを実装して、指輪に関する詳細を提供します。
- **実装例:**
    - `getDisplay()`: `=` を返します。
    - `getType()`: `TypeEnum.RING` を返します。

### `ObjectHistoryDomain`
- **ファイル:** `Objects/src/main/java/net/hero/rogueb/objects/domain/ObjectHistoryDomain.java`
- **説明:** オブジェクトインスタンスのライフサイクルや履歴を追跡するためのエンティティです。
- **インスタンス管理の仕組み:**
    - `Objects`モジュールでは、単独の「インスタンス」テーブルは持たず、この履歴情報の初回のIDを`parentId`として利用することでインスタンスを識別します。
    - `ObjectService`は、特定の`parentId`を持つ最新の履歴レコードを取得することで、そのインスタンスの現在の状態（`ThingInstance`）を復元します。
- **主要なプロパティ:**
    - `id`: 履歴エントリの一意なID。
    - `thing`: 対象となった`Thing`オブジェクトの情報。
    - `parentId`: インスタンスを識別するためのID（初回の履歴レコードの`id`がセットされます）。
    - `isIdentified`: 特定のアイテムインスタンスが識別されているかどうかを保持します。詳細は [アイテム識別システム](../Item-Identification-System.md) を参照してください。
    - `description`: 発生したイベント（作成、拾得など）の説明。
    - `createDate`: イベントが発生した日時。
    - `zoneId`: タイムゾーン情報。

### `IdentificationMapDomain`
- **ファイル:** `Objects/src/main/java/net/hero/rogueb/objects/domain/IdentificationMapDomain.java`
- **説明:** ワールドごとのアイテムの外見マッピング（未識別名）を管理します。
- **主要なプロパティ:**
    - `id`: マッピングの一意なID。
    - `worldId`: 対象のワールドID。
    - `typeId`: 対象のアイテムタイプID。
    - `appearanceName`: 未識別時に表示される外見名（例：「青い指輪」）。

### 補足: 効果・補正の記述について
アイテムテーブルの「効果・補正」列では、可読性のために簡略化した記法を使用しています。これらは実装上、**[標準メタデータ仕様](../Standard-Metadata-Specification.md)** で定義された構造化されたメタデータとして `ThingInstance.metadata` または `meta_effects` 配列に格納されます。

#### 記法とメタデータの対応例
| 記法例 | メタデータ構造 (JSON 表現) | 説明 |
| :--- | :--- | :--- |
| `atk: +3` | `{"atk": 3}` | 物理攻撃力補正。 |
| `slayer: DRAGON` | `{"slayer": "DRAGON"}` | 特定カテゴリへの特効。 |
| `HEAL_HP: 30` | `{"meta_effects": [{"id": "HEAL_HP", "params": {"amount": 30}}]}` | HP 回復エフェクト。 |
| `charges: 5` | `{"charges": 5, "maxCharges": 5}` | 杖の使用回数。 |
| `turns: 20` | `{"turns": 20}` | 状態異常等の持続時間（`meta_effects` 内のパラメータ）。 |

---

## 4. 初期実装アイテムデータ

初期の開発・テストにおいて標準的に使用されるアイテムのパラメータを定義します。

| ID | 名称 | カテゴリ | 属性 | 価格 | ティア | 上限 | 表示 | 効果・補正 |
| :--- | :--- | :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| `wooden_sword` | 木の剣 | `WEAPON` | None | 100 | 1 | 1000 | `)` | `atk: +3` |
| `iron_sword` | 鉄の剣 | `WEAPON` | None | 500 | 1 | 1000 | `)` | `atk: +5` |
| `leather_armor` | 皮の鎧 | `ARMOR` | None | 150 | 1 | 1000 | `[` | `def: +2` |
| `chain_mail` | チェインメイル | `ARMOR` | None | 600 | 2 | 500 | `[` | `def: +4` |
| `plate_armor` | プレートアーマー | `ARMOR` | None | 1200 | 3 | 200 | `[` | `def: +7` |
| `magic_robe` | 魔術師のローブ | `ARMOR` | None | 800 | 2 | 500 | `[` | `def: +2, magicDef: +5` |
| `healing_potion` | 癒しの薬 | `POTION` | None | 50 | 1 | 5000 | `!` | `HEAL_HP: 30` |
| `mana_potion` | 魔力の薬 | `POTION` | None | 50 | 1 | 5000 | `!` | `HEAL_MP: 20` |
| `medicinal_herb` | 薬草 | `POTION` | None | 30 | 1 | 5000 | `!` | `REMOVE_STATUS: POISON` |
| `bitter_medicine` | 苦い薬 | `POTION` | None | 50 | 1 | 5000 | `!` | `REMOVE_STATUS: ALL, MODIFY_LOYALTY: -10` |
| `speed_potion` | 加速の薬 | `POTION` | None | 150 | 2 | 1000 | `!` | `ADD_STATUS: HASTE, turns: 20` |
| `bread` | パン | `FOOD` | None | 30 | 1 | 5000 | `%` | `HEAL_STAMINA: 50` |
| `capture_capsule` | 捕獲カプセル | `OTHER` | None | 500 | 1 | 2000 | `0` | モンスターを捕獲する。 |
| `great_capsule` | 高性能カプセル | `OTHER` | None | 1500 | 2 | 500 | `0` | 捕獲成功率: 1.5x |
| `ultra_capsule` | 最高性能カプセル | `OTHER` | None | 5000 | 3 | 100 | `0` | 捕獲成功率: 2.0x |
| `elemental_capsule` | 属性カプセル | `OTHER` | None | 3000 | 2 | 200 | `0` | 属性一致時、捕獲成功率: 2.5x |
| `monster_soul_gem` | モンスターソウルジェム | `OTHER` | None | 1000 | 2 | 500 | `*` | モンスターに変身し、乱入する。 |
| `wolf_soul_gem` | ウルフのソウルジェム | `OTHER` | None | 1200 | 2 | 300 | `*` | ウルフの魂が宿る宝石。進化用触媒。 |
| `id_scroll` | 識別の巻物 | `SCROLL` | None | 200 | 1 | 2000 | `?` | `IDENTIFY` |
| `uncurse_scroll` | 解呪の巻物 | `SCROLL` | None | 300 | 1 | 1000 | `?` | `REMOVE_CURSE` |
| `loyalty_cookie` | 懐きクッキー | `FOOD` | None | 400 | 1 | 1000 | `%` | `MODIFY_LOYALTY: +20` |
| `wonder_candy` | 不思議な飴 | `FOOD` | None | 2000 | 3 | 100 | `%` | `LEVEL_UP: +1` |
| `fire_stone` | 炎の石 | `MATERIAL` | Fire | 500 | 2 | 300 | `*` | 進化用触媒 |
| `water_stone` | 水の石 | `MATERIAL` | Water | 500 | 2 | 300 | `*` | 進化用触媒 |
| `wind_stone` | 風の石 | `MATERIAL` | Wind | 500 | 2 | 300 | `*` | 進化用触媒 |
| `earth_stone` | 土の石 | `MATERIAL` | Earth | 500 | 2 | 300 | `*` | 進化用触媒 |
| `holy_stone` | 聖なる石 | `MATERIAL` | Holy | 800 | 3 | 100 | `*` | 進化用触媒 |
| `dark_stone` | 闇の石 | `MATERIAL` | Dark | 800 | 3 | 100 | `*` | 進化用触媒 |
| `breeding_incense` | 繁殖のお香 | `MATERIAL` | None | 1000 | 2 | 300 | `*` | 繁殖用触媒 |
| `bounty_hunter_proof` | 賞金稼ぎの証 | `OTHER` | None | 2000 | 3 | 100 | `"` | 強敵撃破の証 |
| `egg` | モンスターの卵 | `OTHER` | None | 1000 | 2 | 500 | `*` | 孵化してモンスターが生まれる |
| `dragon_killer` | ドラゴンキラー | `WEAPON` | None | 4000 | 3 | 50 | `)` | `atk: 12`, `slayer: DRAGON` |
| `beast_buster` | ビーストバスター | `WEAPON` | None | 2500 | 2 | 100 | `)` | `atk: 8`, `slayer: BEAST` |
| `demon_slayer` | 魔物斬り | `WEAPON` | None | 3500 | 3 | 50 | `)` | `atk: 10`, `slayer: DEMON` |
| `silver_sword` | 銀の剣 | `WEAPON` | None | 1500 | 2 | 200 | `)` | `atk: 6`, `slayer: UNDEAD` |
| `holy_sword` | 聖なる剣 | `WEAPON` | Holy | 2000 | 2 | 200 | `)` | `atk: 8, attribute: Holy, slayer: UNDEAD` |
| `str_ring` | 剛力の指輪 | `RING` | None | 1000 | 2 | 500 | `=` | `atk: +3` |
| `def_ring` | 守りの指輪 | `RING` | None | 1000 | 2 | 500 | `=` | `def: +3` |
| `tp_scroll` | 高飛びの巻物 | `SCROLL` | None | 150 | 1 | 2000 | `?` | `TELEPORT` |
| `swap_stick` | 場所替えの杖 | `STICK` | None | 1000 | 2 | 500 | `/` | `SWAP_POSITION, charges: 5` |
| `fire_stick` | 火炎の杖 | `STICK` | Fire | 1200 | 2 | 500 | `/` | `DEAL_DAMAGE: 20, charges: 5` |
| `sleep_stick` | 睡眠の杖 | `STICK` | None | 1000 | 2 | 500 | `/` | `ADD_STATUS: SLEEP, charges: 5` |
| `sample_ring` | 試作の指輪 | `RING` | None | 500 | 1 | 1000 | `=` | 識別の動作確認用サンプル。 |
| `sample_stick` | 試作の杖 | `STICK` | None | 800 | 1 | 1000 | `/` | 識別の動作確認用サンプル。 |
| `stone_floor` | 石の床 | `MATERIAL` | None | 50 | 1 | 10000 | `.` | 建築用資材 |
| `waterway` | 水路 | `MATERIAL` | Water | 100 | 1 | 5000 | `~` | 建築用資材、進入不可 |
| `lava` | 溶岩 | `MATERIAL` | Fire | 200 | 2 | 2000 | `~` | 建築用資材、進入時ダメージ |
| `strong_wall` | 頑丈な壁 | `MATERIAL` | None | 150 | 1 | 5000 | `#` | 建築用資材、掘削困難 |
| `door` | 扉 | `MATERIAL` | None | 100 | 1 | 3000 | `+` | 建築用資材、開閉可能 |
| `breakable_wall` | 壊れる壁 | `MATERIAL` | None | 80 | 1 | 5000 | `#` | 建築用資材、攻撃で破壊可能 |
| `shop_counter` | ショップカウンター | `MATERIAL` | None | 500 | 2 | 500 | `S` | ショップ設置用 |
| `healing_spring` | 回復の泉 | `MATERIAL` | Holy | 1000 | 3 | 100 | `W` | 建築用資材、HP/MP回復 |
| `trap_pitfall` | 落とし穴の種 | `MATERIAL` | None | 200 | 1 | 1000 | `^` | 設置で「落とし穴」になる |
| `trap_poison_arrow` | 毒矢の罠の種 | `MATERIAL` | None | 150 | 1 | 1000 | `^` | 設置で「毒矢の罠」になる |
| `trap_landmine` | 地雷の種 | `MATERIAL` | None | 300 | 2 | 500 | `^` | 設置で「地雷」になる |
| `trap_sleep_gas` | 睡眠ガスの種 | `MATERIAL` | None | 250 | 2 | 500 | `^` | 設置で「睡眠ガス」になる |
| `trap_unequip` | 装備外しの罠の種 | `MATERIAL` | None | 400 | 3 | 200 | `^` | 設置で「装備外しの罠」になる |
| `trap_hunger` | 空腹の罠の種 | `MATERIAL` | None | 100 | 1 | 2000 | `^` | 設置で「空腹の罠」になる |
| `trap_summon` | 召喚の罠の種 | `MATERIAL` | None | 500 | 3 | 200 | `^` | 設置で「召喚の罠」になる |
| `trap_search_scroll` | 罠探しの巻物 | `SCROLL` | None | 150 | 1 | 2000 | `?` | `REVEAL_TRAPS` |
| `mapping_scroll` | あかりの巻物 | `SCROLL` | None | 200 | 2 | 1000 | `?` | `MAPPING` |
| `candlestick` | 燭台 | `MATERIAL` | Fire | 100 | 1 | 5000 | `i` | 建築用デコレーション |
| `stone_statue` | 石像 | `MATERIAL` | None | 300 | 2 | 1000 | `I` | 建築用デコレーション |
| `flag` | 旗 | `MATERIAL` | None | 150 | 1 | 3000 | `F` | 建築用デコレーション |

## 5. 今後の拡張
- **エンチャントシステム**: アイテムにランダムな追加効果（「攻撃力+1」「火属性」等）を付与する仕組み。
- **セット装備**: 特定の装備を組み合わせることで発動する強力なボーナス効果。
