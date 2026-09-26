# モンスター図鑑・博物誌システム (Monster Encyclopedia System)

## 1. 概要
本ドキュメントは、プレイヤーがダンジョン探索や育成を通じて遭遇・撃パー・捕獲・繁殖・進化・融合した各種モンスター種族の記録・伝承・生態データを収集・閲覧する「モンスター図鑑・博物誌システム」の仕様を定義します。

本システムは、ローグライク・ハックアンドスラッシュとしての収集欲求を満たすエンドコンテンツとして機能し、収集段階に応じた情報開示、モンスター伝承（フレーバーテキスト）の閲覧、コンプリート率に応じたマイルストーン報酬、および特定種族の完全踏破によるパッシブ戦闘補正などを提供します。

## 2. 図鑑登録メカニズムと段階的情報開示

モンスター図鑑の情報は、プレイヤーと対象モンスター種族との関わり（遭遇、撃破、獲得）に応じて段階的に解放（アンロック）されます。

### 2.1 情報開示段階 (Unlock Stages)

| 段階コード | 段階名称 | 解放条件 | 開示情報 |
| :--- | :--- | :--- | :--- |
| `UNENCOUNTERED` | 未遭遇 (Stage 0) | 一度も遭遇していない状態。 | 名前 `???`、カテゴリ `???`、シルエットアイコンのみ表示。ステータスやドロップ品は非開示。 |
| `DISCOVERED` | 遭遇・発見 (Stage 1) | 戦闘中またはマップ視界内で1回以上遭遇した状態。 | 正式名称、種族カテゴリ（Slime, Beast, Undead等）、属性、マップ表示文字（display）、主な生息ダンジョン・フロア範囲。 |
| `DEFEATED` | 討伐 (Stage 2) | ダンジョン内で1体以上撃破した状態。 | 基礎ステータス（HP, MP, 攻撃力, 防御力, 魔法攻撃力, 魔法防御力, 器用さ, 精神力）、ドロップ可能アイテムリスト（アイテム名のみ、ドロップ率は隠蔽）。累計撃破数の記録。 |
| `CAPTURED` | 獲得・仲良し (Stage 3) | [モンスター捕獲システム](./Monster-Capture-System.md)、[繁殖](./Monster-Breeding-System.md)、[進化](./Monster-Evolution-System.md)、[融合](./Monster-Fusion-System.md) のいずれかで手持ちに加えた状態。 | 詳細生態伝承（フレーバーテキスト）、習得可能スキル一覧、ドロップ確率パーセンテージ、種族固有パッシブ特性、進化・融合可能ルート情報。 |
| `MASTERED` | 完全踏破・熟知 (Stage 4) | 累計 10 体以上撃破かつ 1 体以上捕獲/獲得した状態。 | 該当種族に対する追加ダメージ補正（**図鑑熟知ボーナス +5%**）、およびマスターマークの付与。 |

### 2.2 ドロップ確率およびスキル情報の完全開示ルール
- `DEFEATED`（Stage 2）時点ではドロップアイテムの品名のみが表示され、ドロップ率欄は `??.?%` と表記されます。
- `CAPTURED`（Stage 3）に達することで、[モンスタードロップシステム](./Monster-Drop-System.md) で設定されている正確なドロップ確率（例: 5.0%）および流通制限時の代替ドロップ情報が表示されます。

## 3. 図鑑コンプリート率とマイルストーン報酬

全モンスター種族に対する図鑑の収集状況は、プレイヤーごとにリアルタイムで計算され、規定の達成率（マイルストーン）に到達した際に特別報酬を獲得できます。

### 3.1 コンプリート率 (Completion Rate) の計算式

$$\text{コンプリート率 (\%)} = \left( \frac{\text{Stage 2 以上（DEFEATED / CAPTURED / MASTERED）が解放されている種族数}}{\text{ゲーム内に存在する全モンスター種族数}} \right) \times 100$$

※小数点以下第1位まで表示（例: 42.5%）。

### 3.2 マイルストーン達成報酬一覧

| 達成率 | 獲得報酬名称 | 獲得アイテム / 補正効果 | 概要 |
| :--- | :--- | :--- | :--- |
| **25%** | 見習い博物学者 | 金貨 10,000 Gold + 称号「見習い博物学者」 | 序盤の図鑑収集に対する基本報酬。 |
| **50%** | 探索支援セット | 「遠征の笛」x3 + 「抽出の水晶球」x2 | モンスター遠征および特性抽出に役立つユーティリティアイテム群。 |
| **75%** | 博物学者の眼鏡 | 特殊アクセサリー装備「博物学者の眼鏡」 | 装備時、ダンジョン内で未鑑定アイテムがドロップする確率が +10% 上昇する。 |
| **100%** | 万物探求者の試練 | 固有パッシブ特性「モンスターマスター」+ 称号「万物探求者」 | 全手持ちモンスターの忠誠度減少速度が -50% 緩和され、親愛解放の必要レベルが -5 緩和される。 |

## 4. モジュール間連携とイベントフロー

図鑑システムは、戦闘（Combat）、モンスター管理（Monster）、プレイヤーセーブデータ管理（BookOfAdventure）の各モジュール間イベント連携によって動作します。

```
[プレイヤー行動]
    │
    ├─► 戦闘で敵モンスター発見 ────► [Combat Service] ──► Event: MonsterDiscovered ──┐
    │                                                                             │
    ├─► 戦闘で敵モンスター撃破 ────► [Combat Service] ──► Event: MonsterDefeated ────┼─► [BookOfAdventure Service]
    │                                                                             │   (図鑑エントリ更新・アンロック)
    └─► 捕獲・繁殖・進化・融合達成 ─► [Monster Service] ─► Event: MonsterAcquired ────┘
```

1. **遭遇時 (MonsterDiscovered)**: 戦闘モジュールがプレイヤーの視界内に未遭遇のモンスターを検知した際、非同期イベントを発行して `DISCOVERED`（Stage 1）へ登録します。
2. **撃破時 (MonsterDefeated)**: 戦闘モジュールでモンスターの HP が 0 になった際、累計撃破数をインクリメントし、未解放であれば `DEFEATED`（Stage 2）へ進展させます。
3. **獲得時 (MonsterAcquired)**: 捕獲・孵化・進化・融合成功時、モンスターモジュールからイベントが発行され、`CAPTURED`（Stage 3）以上へ進展させます。

## 5. ドメインモデルおよび MongoDB データ構造

図鑑情報は [BookOfAdventureモジュール](./domain_models/Book-Of-Adventure.md) の `playerMonsterEncyclopediaDomain` コレクションに保存されます。

### 5.1 `playerMonsterEncyclopediaDomain` コレクション構造

- **`_id`** (String): ドキュメントの一意なID（例: `enc_usr_9921_slime_001`）。
- **`userId`** (String): プレイヤーのユーザーID。
- **`monsterTypeId`** (String): モンスター種族ID（例: `slime_001`）。
- **`unlockStage`** (String): 情報開示段階 (`DISCOVERED`, `DEFEATED`, `CAPTURED`, `MASTERED`)。
- **`defeatCount`** (Integer): 累計撃破数。
- **`captureCount`** (Integer): 累計捕獲・獲得数。
- **`firstDiscoveredAt`** (Date): 初遭遇日時。
- **`firstCapturedAt`** (Date): 初獲得日時（未獲得時は null）。
- **`_class`** (String): Spring Data MongoDB クラス識別子。

### 5.2 インデックス推奨事項
- `{"userId": 1}`: ユーザーごとの図鑑一覧・進行度照会。
- `{"userId": 1, "monsterTypeId": 1}`: ユニーク複合インデックス。特定モンスターの図鑑ステータス高速検索用。

## 6. REST API 仕様

### 6.1 図鑑全体進行度・一覧照会 API
指定されたユーザーの図鑑コンプリート率、解放状況サマリー、受取可能なマイルストーン報酬一覧を取得します。

- **HTTP Method**: `GET`
- **Endpoint**: `/api/v1/monsters/encyclopedia/{userId}`

#### レスポンス JSON Schema (`200 OK`)
```json
{
  "userId": "usr_9921",
  "completionRate": 42.5,
  "totalSpeciesCount": 120,
  "unlockedSpeciesCount": 51,
  "masteredSpeciesCount": 12,
  "claimedMilestones": [25],
  "availableMilestones": [50],
  "entries": [
    {
      "monsterTypeId": "slime_001",
      "monsterName": "スライム",
      "category": "Slime",
      "unlockStage": "MASTERED",
      "defeatCount": 24,
      "captureCount": 3,
      "isMastered": true
    },
    {
      "monsterTypeId": "goblin_001",
      "monsterName": "ゴブリン",
      "category": "Humanoid",
      "unlockStage": "DEFEATED",
      "defeatCount": 5,
      "captureCount": 0,
      "isMastered": false
    }
  ]
}
```

### 6.2 個別モンスター図鑑詳細照会 API
指定されたモンスター種族の図鑑詳細情報（開示レベルに応じたステータス・ドロップ率・フレーバーテキスト等）を取得します。

- **HTTP Method**: `GET`
- **Endpoint**: `/api/v1/monsters/encyclopedia/{userId}/{monsterTypeId}`

#### レスポンス JSON Schema (`200 OK`)
```json
{
  "userId": "usr_9921",
  "monsterTypeId": "slime_001",
  "monsterName": "スライム",
  "display": "s",
  "category": "Slime",
  "attribute": "Water",
  "unlockStage": "CAPTURED",
  "defeatCount": 15,
  "captureCount": 2,
  "habitatDungeons": ["初心者ダンジョン 1F-5F", "湿地帯洞窟 1F-3F"],
  "loreText": "柔らかい身体を持つぷにぷにしたモンスター。水分と栄養を求めてダンジョンの浅層を漂っている。",
  "baseStats": {
    "hp": 20,
    "mp": 5,
    "atk": 8,
    "def": 4,
    "magicAtk": 2,
    "magicDef": 3,
    "dex": 5,
    "mnd": 3
  },
  "dropItems": [
    {
      "typeId": "herb",
      "itemName": "薬草",
      "dropRatePercentage": 15.0
    },
    {
      "typeId": "slime_jelly",
      "itemName": "スライムゼリー",
      "dropRatePercentage": 5.0
    }
  ],
  "learnedSkills": [
    {
      "skillId": 101,
      "skillName": "たいあたり",
      "requiredLevel": 1
    }
  ],
  "masteryBonusActive": false
}
```

### 6.3 マイルストーン達成報酬受け取り API
図鑑コンプリート率マイルストーン（25%, 50%, 75%, 100%）の達成報酬を申請・獲得します。

- **HTTP Method**: `POST`
- **Endpoint**: `/api/v1/monsters/encyclopedia/claim-reward`

#### リクエスト JSON Schema
```json
{
  "userId": "usr_9921",
  "milestonePercentage": 50
}
```

#### レスポンス JSON Schema (`200 OK`)
```json
{
  "userId": "usr_9921",
  "claimedMilestone": 50,
  "rewardTitle": "探索支援セット",
  "grantedItems": [
    {
      "typeId": "expedition_whistle",
      "count": 3
    },
    {
      "typeId": "extraction_orb",
      "count": 2
    }
  ],
  "grantedGold": 0,
  "claimedAt": "2026-03-31T12:00:00Z"
}
```

## 7. ビジネスルール・エラーハンドリング

| エラーコード | HTTP ステータス | 発生条件 |
| :--- | :--- | :--- |
| `PLAYER_NOT_FOUND` | `404 Not Found` | 指定された `userId` のプレイヤーが存在しない。 |
| `MONSTER_TYPE_NOT_FOUND` | `404 Not Found` | 指定された `monsterTypeId` の種族データが存在しない。 |
| `ENTRY_NOT_UNLOCKED` | `400 Bad Request` | 未遭遇（`UNENCOUNTERED`）のモンスターの図鑑詳細を照会しようとした。 |
| `MILESTONE_NOT_REACHED` | `422 Unprocessable Entity` | 現在のコンプリート率が申請されたマイルストーン値（例: 50%）に達していない。 |
| `REWARD_ALREADY_CLAIMED` | `409 Conflict` | 既に受け取り済みのマイルストーン報酬を重ねて申請した。 |
| `INVENTORY_FULL` | `400 Bad Request` | インベントリ（バッグ）に空き枠がないため報酬アイテムを付与できない。 |

## 8. 今後の拡張

- **アイテム図鑑 (Item Encyclopedia) との統合**: モンスター図鑑に加え、入手した武具・消費アイテム・貴重品を記録・図鑑化する統合「冒険博物誌」画面の構築。
- **3D/2D モデルビューアーおよびモーション鑑賞**: 獲得済みモンスターの 3D/2D アニメーション（攻撃、ダメージ、待機モーション）を鑑賞できるギャラリーモードの追加。
