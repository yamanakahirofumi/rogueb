# モンスター ドメインモデル

このドキュメントは、**Monster** 関連のコアとなるドメインモデルについて説明します。モンスターはダンジョン内でのエネミーとして、またプレイヤーが捕獲して管理する対象として機能します。

## 1. コアコンセプト

モンスターシステムは、ダンジョンに配置される野生のモンスターと、プレイヤーによって捕獲されたモンスターの双方を管理します。モンスターは種族ごとの基本性能を持ち、個体ごとに状態（HP、MP、経験値、状態異常等）が管理されます。

---

## 2. 主要なドメインオブジェクト

### `MonsterDomain`
- **説明:** モンスターの「種族」としての基本情報を定義します。
- **主要なプロパティ:**
    - `id`: 種族の一意な識別子（例：「スライム」「オーク」）。
    - `name`: モンスターの名称。
    - `baseHp`: 基本体力。
    - `baseMp`: 基本魔法力。
    - `baseAtk`: 基本攻撃力。
    - `baseDef`: 基本防御力。
    - `baseMagicAtk`: 基本魔法攻撃力。
    - `baseMagicDef`: 基本魔法防御力。
    - `baseDex`: 基本器用さ（命中率、クリティカル率に影響）。
    - `baseMnd`: 基本精神力（状態異常耐性に影響）。
    - `display`: マップ上で表示される文字（例：スライムは `s`）。
    - `expValue`: 倒した際に得られる基本経験値。
    - `type`: モンスターのカテゴリ。特効（Slayer）の判定に使用されます。
        - 有効な値: `SLIME`, `BEAST`, `UNDEAD`, `DRAGON`, `DEMON`, `SPIRIT`, `HUMANOID`
    - `attribute`: モンスターの属性（Fire, Water, Wind, Earth, Holy, Dark, None）。
    - `traits`: モンスターの種族固有の特性（パッシブ能力）のリスト。詳細は [モンスター特性システム](../Monster-Trait-System.md) を参照。特性は「能力強化」「耐性・無効」「特殊・行動」のカテゴリに分類されています。
    - `aiType`: 行動を制御する AI のタイプ（`NORMAL`, `STATIONARY`, `COWARDLY`, `AGGRESSIVE` 等）。詳細は [モンスターAI詳細仕様](../Monster-AI-Specification.md) を参照。
    - `skillRate`: AI がスキルを使用する基本確率（0 〜 100）。
    - `baseActionInterval`: 基本行動間隔。
    - `placementCost`: ダンジョン配置時のコスト。
    - `baseIncubationSteps`: 孵化に必要な基本歩数。
    - `skillTable`: `MonsterSkillSlot` のリスト（習得可能なスキル）。
    - `dropTable`: `MonsterDropSlot` のリスト。
    - `evolutionTable`: `MonsterEvolutionSlot` のリスト。詳細は [モンスター進化システム](../Monster-Evolution-System.md) を参照。

### `MonsterSkillSlot` (値オブジェクト)
- **説明:** モンスターが習得するスキルとその条件を定義します。
- **プロパティ:**
    - `skillId`: スキルの ID (`SkillAndMagicSystem` の ID)。
    - `level`: そのスキルを習得するレベル。

### `MonsterDropSlot` (値オブジェクト)
- **説明:** モンスターがドロップするアイテムの設定を定義します。
- **プロパティ:**
    - `typeId`: ドロップするアイテムのタイプ ID（`Thing.getId()` に対応）。
    - `weight`: そのアイテムが選ばれる相対的な重み。
    - `minCount`: ドロップする個数の最小値。
    - `maxCount`: ドロップする個数の最大値。

### `MonsterEvolutionSlot` (値オブジェクト)
- **説明:** モンスターの進化条件と進化先を定義します。
- **プロパティ:**
    - `targetMonsterId`: 進化先の種族 ID。
    - `requiredLevel`: 必要なレベル。
    - `requiredItemId`: 必要なアイテムのタイプ ID（任意）。
    - `requiredStats`: 必要なステータス条件（Map<String, Integer>、任意）。
        - 有効なキー: `hp`, `mp`, `atk`, `def`, `magicAtk`, `magicDef`, `dex`, `mnd`, `loyalty`
    - `resetLevel`: 進化後にレベルを 1 に戻すかどうか (boolean)。

### `MonsterInstance`
- **説明:** 特定のモンスター個体を表します。ダンジョン内の野生モンスター、またはプレイヤーが所持しているモンスターとして存在します。
- **主要なプロパティ:**
    - `instanceId`: 個体の一意な識別子.
    - `monsterId`: `MonsterDomain` の ID（種族 ID）.
    - `level`: 現在のレベル.
    - `currentHp`: 現在の体力.
    - `currentMp`: 現在の魔法力.
    - `skillRate`: 個体固有のスキル使用確率（通常は種族の基本値を継承）.
    - `subStep`: 内部歩数カウンタ（状態異常の継続判定や自然回復のタイミング計算に使用）.
    - `experience`: 累積経験値（捕獲後の成長に使用）.
    - `skillIds`: 習得しているスキル ID のリスト. **最大 4 つ**まで保持可能です。
    - `inheritedStatus`: 継承されたステータス補正（Map<String, Integer>）. 繁殖個体の場合に使用.
    - `statusEffects`: 付与されている状態異常 (`StatusEffectDomain`) のリスト.
    - `traits`: 個体固有の特性（パッシブ能力）のリスト。詳細は [モンスター特性システム](../Monster-Trait-System.md) を参照。特性は「能力強化」「耐性・無効」「特殊・行動」のカテゴリに分類されています.
    - `metadata`: 個体固有の動的データ（Map<String, Object>）。ニックネームや特殊な成長記録などに使用.
        - 予約済みキーの詳細は **[標準メタデータ仕様](../Standard-Metadata-Specification.md)** を参照してください.
    - `ownerId`: 所有しているプレイヤーの ID（捕獲済みの場合）.
    - `isWild`: 野生状態かどうかを示すフラグ（`state` が `WILD` の場合のみ `true`）.
    - `state`: モンスター個体の現在の所在・状態.
        - `WILD`: 野生状態。ダンジョン等に自然発生した個体.
        - `PARTY`: プレイヤーのパーティ（手持ち）に入っている状態.
        - `STORAGE`: 預かり所（倉庫）に保管されている状態.
        - `PLACED`: 管理者によってダンジョン内に配置されている状態.
    - `loyalty`: プレイヤーに対する忠誠度（懐き具合）。詳細は [モンスター忠誠度システム](../Monster-Loyalty-System.md) を参照.
    - `lastLoyaltyUpdate`: 最後に忠誠度が更新された（時間経過による減少判定が行われた）タイムスタンプ (Long).
    - `lastBreedingTime`: 最後に繁殖を行ったタイムスタンプ (Long)。[モンスター繁殖システム](../Monster-Breeding-System.md) におけるクールタイム判定に使用.
    - `fusionCount`: 累積融合回数（Integer、最大5）。自身がベース個体として融合を行った累計回数を記録します。詳細は [モンスター融合システム](../Monster-Fusion-System.md) を参照.

### `StatusEffectDomain` (値オブジェクト)
- **説明:** [BookOfAdventureモジュール](./Book-Of-Adventure.md#statuseffectdomain-値オブジェクト) にて定義。プレイヤーやモンスターに付与される状態異常を表します。

### `MonsterCoordinateDomain`
- **説明:** [Dungeonモジュール](./Dungeon.md#monstercoordinatedomain) にて定義。フロア上のモンスターの位置を保持します。

---

## 3. ステータス計算とAIアルゴリズム

### 3.1 ステータス成長の計算式
モンスターのレベルに応じた各ステータスは、以下の式で算出されます。

`現在のステータス = (基本ステータス + 継承補正) * (1 + (レベル - 1) * 0.1) * 忠誠度補正 * エリート補正`

- **継承補正**: 繁殖によって生まれた個体の場合、`inheritedStatus` に保持されている値が基本ステータスに加算されます。野生個体の場合は 0 です。
- **忠誠度補正 (Loyalty Bonus)**: プレイヤーとの絆による補正です。詳細は [モンスター忠誠度システム](../Monster-Loyalty-System.md) を参照。
    - 忠誠度 200 〜 250: **1.03** (HP, MP を除く)
    - 忠誠度 251 〜 255: **1.05** (HP, MP を除く)
- **エリート補正 (Elite Correction)**: ダンジョンランク A 以上の「エリート個体」に適用される補正です。
    - エリート個体: **1.2** (全ステータス)
- HP および MP は、これらの補正（エリート補正等）を含めて算出された値を最大値として扱います。

### 3.2 配置コスト (Placement Cost) の目安
ダンジョン管理者がモンスターを配置する際の `placementCost` は、モンスターのティア（強さの段階）に基づいて以下の範囲で設定されます。

| ティア | 配置コストの目安 | 備考 |
| :--- | :---: | :--- |
| **1** | 10 〜 50 | スライム、コボルト等の初期モンスター。 |
| **2** | 51 〜 150 | オーク、ワーウルフ等の中堅モンスター。 |
| **3** | 151 〜 400 | ドラゴン、デーモン等の上級モンスター。 |
| **4** | 401 〜 1,000 | 準ボス級の強力なモンスター。 |
| **5 (Boss)** | 2,000 〜 | ユニーク個体、ダンジョンボス。 |

### 3.3 レベルアップに必要な経験値
モンスターがレベルアップするために必要な累計経験値は、プレイヤーと同じ計算式を使用します。
`累計必要経験値(L) = 10 * (L - 1)^2`
- L は目標とするレベルを表します。

### 3.4 捕獲済みモンスターの経験値獲得ルール
捕獲されたモンスターは、以下の方法で経験値を獲得します。
- **戦闘参加**: プレイヤーの戦闘に参加し、敵を撃破した際に得られる経験値の 50% を獲得します。
- **同行ボーナス**: プレイヤーが経験値を獲得した際、控えとして同行しているモンスターは、プレイヤーが獲得した経験値の 10% を「学習経験値」として獲得します。
- **アイテムによる付与**: 「不思議な飴」などのアイテムを使用することで、直接経験値を与えることができます。

### 3.5 スキル枠の制限と管理
モンスターが個体として保持できるスキル（`skillIds`）は、**最大 4 つ**に制限されます。

- **新規習得時の挙動**: レベルアップや進化、卵からの「孵化」、または「融合（合体）」によって 5 つ目のスキルを習得（継承）しようとした場合、プレイヤーは手動で「現在のスキルを忘れて新しいスキルを習得する」か「新しいスキルの習得を諦める」かを選択する必要があります。
- **スキルの上書き**: 既存のスキルを選択して、新しいスキルで上書きすることが可能です。一度忘れたスキルを再度習得するには、そのスキルを習得できるレベルに再度到達（進化によるレベルリセット等）するか、特定のアイテムを使用する必要があります。

### 3.6 基本AIアルゴリズム
野生モンスターは、自身のターンにおいて行動タイプ（`aiType`）に基づいたアルゴリズムに従って行動を決定します。

基本（`NORMAL`）タイプの場合、以下の優先順位で行動を決定します。

1. **攻撃範囲内の確認**: 隣接 8 方向（または特殊射程内）にプレイヤーが存在する場合、攻撃アクションを実行します。
2. **追跡**: 視界内にプレイヤーが存在し、かつ隣接していない場合、プレイヤーに近づく方向へ移動します。
3. **ランダム移動**: 視界内にプレイヤーがいない、または移動不可能な場合、周囲の通行可能なタイルへランダムに移動します。

詳細は [モンスターAI詳細仕様](../Monster-AI-Specification.md) を参照してください。

### 3.7 モンスター表示文字の規約 (Monster Display Character Convention)
マップ上でモンスターを表す表示文字（`display`）は、原則として以下の規約に従って割り当てられます。

- **ティア 1 〜 2**: 小文字（例: スライム `s`、ドラゴンスライム `d`）。
- **ティア 3 以上 / ユニーク**: 大文字（例: ドラゴン `D`、古代龍 `A`、賞金稼ぎ `H`）。
- **競合の解決**: 同じ文字を使用する種族が複数存在し、かつ同じティア区分である場合は、一方が大文字（または別の文字）を使用することで重複を避けます。
  - 例: スライム（Tier 1）が `s` を使用するため、スケルトン（Tier 1）は `S` を使用します。
  - 例: ウルフ（Tier 1）が `w` を使用するため、ウィンドスピリット（Tier 2）は `W` を使用します。

---

## 4. モジュール間連携

### 4.1 Dungeon モジュールとの連携
- ダンジョンのフロア生成時、`MonsterDomain` に基づいて野生モンスターが生成され、`FloorDomain` の `monsterList`（後述の拡張）に配置されます。
- プレイヤーの移動に伴い、モンスターの AI（移動・攻撃ロジック）が実行されます。

### 4.2 BookOfAdventure モジュールとの連携
- プレイヤーがモンスターを捕獲した場合、そのモンスターの `MonsterInstance` はプレイヤーの所持モンスターとして `BookOfAdventure` に保存されます。
- 繁殖（Breeding）によって新しいモンスターが生成される際も、このモジュールを通じて永続化されます。

### 4.3 Combat システムとの連携
- 戦闘発生時、`MonsterInstance` のステータスと `MonsterDomain` の基本性能を用いてダメージ計算が行われます。詳細は [戦闘システム](../Combat-System.md) を参照してください。
- プレイヤーによるモンスターの捕獲処理が行われます。詳細は [モンスター捕獲システム](../Monster-Capture-System.md) を参照してください。
- モンスターの死亡時、プレイヤーへの経験値付与やアイテムドロップの判定が行われます。詳細は [モンスタードロップシステム](../Monster-Drop-System.md) を参照してください。

---

## 5. 初期実装モンスターデータ

初期の開発・テストにおいて標準的に使用されるモンスターのパラメータを定義します。

| ID | 名称 | カテゴリ | ティア | 属性 | コスト | EXP | HP | MP | ATK | DEF | MATK | MDEF | DEX | MND | 表示 | 特性 (Traits) | 初期スキル | AI | スキル率 | 間隔 (ms) | 孵化 (歩) | 進化先 (Lv) |
| :--- | :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| `slime` | [スライム](#species-slime) | `SLIME` | 1 | None | 10 | 5 | 10 | 0 | 5 | 5 | 2 | 2 | 5 | 5 | `s` | `SLIME_BODY` | - | NORMAL | 20 | 1000 | 500 | `dragon_slime` (Lv 15 + `fire_stone`, Reset: No) |
| `kobold` | [コボルト](#species-kobold) | `HUMANOID` | 1 | None | 25 | 12 | 15 | 0 | 8 | 6 | 2 | 4 | 10 | 6 | `k` | - | - | NORMAL | 20 | 1000 | 1000 | `orc` (Lv 12, Reset: Yes) |
| `orc` | [オーク](#species-orc) | `HUMANOID` | 2 | None | 80 | 45 | 40 | 10 | 18 | 15 | 5 | 8 | 12 | 10 | `o` | `AGGRESSIVE` | 101 | AGGRESSIVE | 25 | 1000 | 1500 | `demon` (Lv 25 + `dark_stone`, Reset: Yes) |
| `dragon` | [ドラゴン](#species-dragon) | `DRAGON` | 3 | Fire | 250 | 120 | 100 | 30 | 35 | 25 | 20 | 15 | 15 | 12 | `D` | `FIRE_IMMUNITY` | 201 | NORMAL | 30 | 1500 | 5000 | `ancient_dragon` (Lv 30 + `bounty_hunter_proof`, Reset: Yes) |
| `lich` | [リッチ](#species-lich) | `UNDEAD` | 4 | None | 600 | 300 | 150 | 100 | 20 | 20 | 45 | 40 | 18 | 30 | `L` | `UNDEAD_SOUL`, `STATUS_IMMUNITY`, `MIASMA_RESISTANCE` | 204, 307, 501 | NORMAL | 40 | 1200 | 5000 | - |
| `wolf` | [ウルフ](#species-wolf) | `BEAST` | 1 | None | 35 | 18 | 20 | 0 | 12 | 8 | 2 | 5 | 15 | 8 | `w` | `TRACKING` | 105 | AGGRESSIVE | 25 | 1000 | 1000 | `griffin` (Lv 20 + `wind_stone` + `wolf_soul_gem`, Reset: Yes) |
| `eagle` | [イーグル](#species-eagle) | `BEAST` | 1 | Wind | 40 | 20 | 18 | 10 | 10 | 7 | 5 | 8 | 18 | 10 | `e` | `FLIGHT` | - | NORMAL | 20 | 1000 | 1000 | `griffin` (Lv 20 + `wind_stone`, Reset: No) |
| `zombie` | [ゾンビ](#species-zombie) | `UNDEAD` | 1 | None | 30 | 15 | 30 | 0 | 10 | 10 | 0 | 2 | 4 | 12 | `z` | `UNDEAD_SOUL`, `MIASMA_RESISTANCE` | - | NORMAL | 15 | 1000 | 1500 | `lich` (Lv 20, Reset: Yes) |
| `skeleton` | [スケルトン](#species-skeleton) | `UNDEAD` | 1 | None | 35 | 20 | 25 | 0 | 14 | 8 | 0 | 4 | 12 | 6 | `S` | `UNDEAD_SOUL`, `MIASMA_RESISTANCE` | - | NORMAL | 20 | 1000 | 1500 | - |
| `fire_spirit` | [ファイアスピリット](#species-fire_spirit) | `SPIRIT` | 2 | Fire | 120 | 60 | 35 | 50 | 10 | 10 | 25 | 20 | 15 | 15 | `f` | `FIRE_IMMUNITY` | 201 | NORMAL | 30 | 1000 | 2000 | `dragon` (Lv 20 + `fire_stone`, Reset: Yes) |
| `water_spirit` | [ウォータースピリット](#species-water_spirit) | `SPIRIT` | 2 | Water | 120 | 60 | 35 | 50 | 10 | 10 | 25 | 20 | 15 | 15 | `u` | - | 202 | NORMAL | 30 | 1000 | 2000 | `mist_spirit` (Lv 20 + `water_stone`, Reset: Yes) |
| `wind_spirit` | [ウィンドスピリット](#species-wind_spirit) | `SPIRIT` | 2 | Wind | 120 | 60 | 35 | 50 | 10 | 10 | 25 | 20 | 15 | 15 | `W` | - | 401 | NORMAL | 30 | 1000 | 2000 | `griffin` (Lv 25 + `wolf_soul_gem`, Reset: Yes) |
| `earth_spirit` | [アーススピリット](#species-earth_spirit) | `SPIRIT` | 2 | Earth | 120 | 60 | 40 | 40 | 15 | 15 | 15 | 15 | 10 | 20 | `t` | - | 203 | NORMAL | 30 | 1000 | 2000 | - |
| `dragon_slime` | [ドラゴンスライム](#species-dragon_slime) | `DRAGON` | 2 | Fire | 150 | 80 | 50 | 20 | 20 | 18 | 15 | 15 | 12 | 12 | `d` | `SLIME_BODY`, `FIRE_IMMUNITY` | 201 | NORMAL | 25 | 1000 | 2500 | - |
| `griffin` | [グリフォン](#species-griffin) | `BEAST` | 3 | Wind | 300 | 150 | 80 | 30 | 30 | 22 | 15 | 18 | 20 | 15 | `G` | `FLIGHT` | 401 | AGGRESSIVE | 30 | 1200 | 3000 | - |
| `demon` | [デーモン](#species-demon) | `DEMON` | 3 | Dark | 300 | 150 | 120 | 50 | 35 | 25 | 30 | 30 | 12 | 15 | `V` | - | 201 | NORMAL | 35 | 1500 | 5000 | - |
| `mist_spirit` | [ミストスピリット](#species-mist_spirit) | `SPIRIT` | 3 | Water | 350 | 180 | 70 | 80 | 15 | 15 | 35 | 30 | 25 | 20 | `M` | - | 202, 302, 304 | NORMAL | 40 | 1200 | 3000 | - |
| `ancient_dragon` | [古代龍](#species-ancient_dragon) | `DRAGON` | 5 | Fire | 2500 | 1000 | 300 | 100 | 80 | 60 | 50 | 40 | 25 | 20 | `A` | `FIRE_IMMUNITY`, `STATUS_IMMUNITY`, `MIASMA_RESISTANCE` | 201, 401, 403 | NORMAL | 50 | 1500 | 10000 | - |
| `town_guardian` | [拠点衛兵](#species-town_guardian) | `HUMANOID` | - | None | - | 0 | 500 | 200 | 100 | 100 | 80 | 80 | 50 | 50 | `g` | `STATUS_IMMUNITY`, `SEE_INVISIBILITY` | 303 | STATIONARY | 100 | 800 | - | - |
| `bounty_hunter` | [賞金稼ぎ](#species-bounty_hunter) | `HUMANOID` | 4 | None | - | 0 | (Scaling) | (Scaling) | (Scaling) | (Scaling) | (Scaling) | (Scaling) | (Scaling) | (Scaling) | `H` | `TRACKING`, `SEE_INVISIBILITY` | - | AGGRESSIVE | 40 | 1000 | - | [詳細](../Monster-PK-System.md#742-賞金稼ぎ-npc-bounty-hunter) |

- **賞金稼ぎ (Rank S) の補正**: ランク S の賞金稼ぎは、1.1 倍のステータス補正を受け、追加の特性として `REGENERATION_II`（毎ターン 5% 回復）および `STATUS_IMMUNITY`（全状態異常無効）を保持します。
- **エリート個体 (Elite) の補正**: ダンジョンランク A 以上で出現するエリート個体は、通常のモンスターと比較して **1.2 倍** のステータス補正（HP, MP, ATK, DEF, MATK, MDEF, DEX, MND）を受けます。

### 5.2 各種族の習得スキルテーブル

各種族がレベルアップ時に習得するスキルの詳細を定義します。スキル ID の詳細は [スキル・魔法システム](../Skill-And-Magic-System.md) を参照してください。

| 種族 ID | 習得スキル (Lv) |
| :--- | :--- |
| <a id="species-slime"></a>`slime` | `302`: ポイズンガス (10), `101`: パワーアタック (20), `105`: かみつき (30), `602`: 分裂 (35) |
| <a id="species-kobold"></a>`kobold` | `101`: パワーアタック (8), `103`: シールドバッシュ (15) |
| <a id="species-orc"></a>`orc` | `101`: パワーアタック (1), `102`: 回転斬り (15), `103`: シールドバッシュ (25) |
| <a id="species-dragon"></a>`dragon` | `201`: ファイアボール (1), `403`: トルネード (25), `601`: 自爆 (50) |
| <a id="species-lich"></a>`lich` | `204`: ダークブレス (1), `307`: サイレス (1), `501`: テレポート (1), `305`: コンフューズ (20), `404`: クエイク (40) |
| <a id="species-wolf"></a>`wolf` | `105`: かみつき (1), `101`: パワーアタック (10), `310`: 遠吠え (25) |
| <a id="species-eagle"></a>`eagle` | `401`: ウィンドブレス (15), `403`: トルネード (35) |
| <a id="species-zombie"></a>`zombie` | `302`: ポイズンガス (12), `101`: パワーアタック (20) |
| <a id="species-skeleton"></a>`skeleton` | `101`: パワーアタック (5), `103`: シールドバッシュ (18) |
| <a id="species-fire_spirit"></a>`fire_spirit` | `201`: ファイアボール (1), `302`: ポイズンガス (15) |
| <a id="species-water_spirit"></a>`water_spirit` | `202`: アイスブレス (1), `304`: スリープクラウド (15) |
| <a id="species-wind_spirit"></a>`wind_spirit` | `401`: ウィンドブレス (1), `309`: インビジブル (20) |
| <a id="species-earth_spirit"></a>`earth_spirit` | `203`: アースニードル (1), `101`: パワーアタック (10), `404`: クエイク (30) |
| <a id="species-dragon_slime"></a>`dragon_slime` | `201`: ファイアボール (1), `302`: ポイズンガス (10) |
| <a id="species-demon"></a>`demon` | `201`: ファイアボール (1), `204`: ダークブレス (15), `305`: コンフューズ (15), `501`: テレポート (30) |
| <a id="species-griffin"></a>`griffin` | `401`: ウィンドブレス (1), `102`: 回転斬り (20) |
| <a id="species-mist_spirit"></a>`mist_spirit` | `202`: アイスブレス (1), `302`: ポイズンガス (1), `304`: スリープクラウド (1), `602`: 分裂 (40) |
| <a id="species-ancient_dragon"></a>`ancient_dragon` | `201`: ファイアボール (1), `401`: ウィンドブレス (1), `403`: トルネード (1), `308`: ヘイスト (50) |
| <a id="species-town_guardian"></a>`town_guardian` | `303`: 捕縛 (1) |
| <a id="species-bounty_hunter"></a>`bounty_hunter` | `101`: パワーアタック (1), `303`: 捕縛 (1) |

## 6. 今後の拡張
- **連携攻撃**: 複数のモンスターが協力して発動する強力な攻撃アクション。
