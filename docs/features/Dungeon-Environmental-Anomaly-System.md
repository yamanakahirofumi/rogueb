# ダンジョン環境異変システム仕様 (Dungeon Environmental Anomaly System)

## 1. 概要
本ドキュメントは、ダンジョンの各フロア全体に一時的または恒常的に及ぶ広域環境効果「ダンジョン環境異変（Environmental Anomaly）」に関するシステム仕様を定義します。
ダンジョン環境異変は、ローグライクにおける探索の緊張感、戦闘バランス、属性スキルの優位性、モンスターの発生傾向、およびトラップやドロップ率に動的な変動をもたらします。ダンジョンランクに応じた自然発生に加え、[ダンジョン構築・運営システム](./Dungeon-Construction-System.md) における管理者介入や専用アイテム（天候触媒）によって制御可能です。

---

## 2. 環境異変の種類と効果詳細

本システムで導入される主要な環境異変と、それぞれの戦闘・探索パラメータへの影響は以下の通りです。同時に発生できる環境異変は1フロアにつき最大1種類です。

### 2.1 太陽フレア / 灼熱地獄 (`solar_flare`)
- **概念**: 太陽光の暴走またはマグマ層の上昇により、フロア全体が超高熱に包まれた状態。
- **戦闘影響**:
  - 火属性スキルおよび攻撃のダメージ +50%
  - 氷/水属性スキルおよび攻撃のダメージ -50%
- **探索・パラメータ影響**:
  - プレイヤーおよび仲間モンスターのスタミナ消費速度 1.5 倍
  - マグマタイルの踏破ダメージが 10 から 20 へ増加
  - 氷結状態異常の付与判定を無効化
- **モンスター・その他影響**:
  - 火属性モンスター（サラマンダー等）の移動速度 +1（倍速）

### 2.2 絶対零度 / 極寒氷結 (`freezing_blizzard`)
- **概念**: 吹き荒れる猛吹雪と極低温により、フロア全体の熱量が奪われた状態。
- **戦闘影響**:
  - 氷/水属性スキルのダメージ +50%
  - 火属性スキルのダメージ -50%
  - 攻撃時、5% の確率で対象に「凍結（1ターン行動不能）」を付与
- **探索・パラメータ影響**:
  - 水脈・水たまりタイルが全て通行可能な氷床タイル（滑る床）へと変化
  - 氷耐性を持たないエンティティは 10 ターンごとに 5% のスタミナを喪失
- **モンスター・その他影響**:
  - 水/氷属性モンスターの自然回復量が 2 倍に増加

### 2.3 マナ大奔流 / 魔導濃霧 (`mana_surge`)
- **概念**: 大気中の高濃度マナが凝縮し、濃密な魔導の霧がフロアを覆い尽くした状態。
- **戦闘影響**:
  - 全てのスキル・魔法の消費 MP が 50% 削減（最低 1）
  - 攻撃魔法のクリティカル発動率 +20%
- **探索・パラメータ影響**:
  - 視界範囲（Line of Sight）が全エンティティ一律 2 マスに制限
  - 隠蔽されたトラップの通常視認判定を完全に無効化（「目薬の草」等でのみ開示可能）
- **モンスター・その他影響**:
  - 魔法詠唱型モンスターのスキル使用確率 (`skillRate`) が 1.5 倍に上昇

### 2.4 瘴気嵐 / 暗黒日蝕 (`miasma_storm`)
- **概念**: 冥府の扉が開き、死の瘴気と闇がフロア全体を覆った状態。
- **戦闘影響**:
  - 毒・呪い・恐怖状態の持続ターン数が 2 倍に延長
  - アンデッド（UNDEAD）および悪魔（DEMON）カテゴリの物理攻撃力・防御力 +30%
- **探索・パラメータ影響**:
  - プレイヤーおよび仲間モンスターの自然 HP 回復が完全に停止
  - レアドロップ率およびドロップアイテムの品質補正 +50%
- **モンスター・その他影響**:
  - モンスターの再ポップ間隔（自然発生ターン）が通常の 50% に短縮

### 2.5 局地超重力 / 磁気嵐 (`supergravity`)
- **概念**: 強烈な重力波と磁界の歪みにより、物体や遠距離攻撃の軌道が物理的に抑制された状態。
- **戦闘影響**:
  - 矢・投擲アイテムおよび直線飛翔スキルの射程が最大 2 マスに制限
  - ノックバック効果（吹き飛ばし）を完全に無効化
  - 金属製装備（鉄・鋼等）着用者の物理防御力 +20%、回避率 -15%
- **探索・パラメータ影響**:
  - トラップの作動ダメージおよび効果範囲 +50%
  - 管理者によるトラップ配置コスト -30%

---

## 3. 発動・発生メカニズム

### 3.1 フロア生成時の自然発生
ダンジョン生成時（`POST /api/v1/dungeons/{dungeonId}/floors/generate`）、ダンジョンランク（`DungeonRank`）および階層レベルに応じて一定確率で環境異変が自然発生します。

| ダンジョンランク | 自然発生確率 | 出現可能パラメータ制限 |
| :--- | :--- | :--- |
| Rank F 〜 D | 0% | 自然発生なし |
| Rank C | 5% | severity = 1 のみ |
| Rank B | 15% | severity = 1 〜 2 |
| Rank A | 25% | severity = 1 〜 3 |
| Rank S | 40% | severity = 1 〜 3（独自ルールとの併用可） |

### 3.2 ダンジョン管理者による介入発動
ダンジョン管理者は、[ダンジョン構築・運営システム](./Dungeon-Construction-System.md) の介入アクションの一環として、ダンジョンポイント（Intervention Points）およびゴールドを消費して任意のフロアに環境異変を意図的に引き起こすことができます。

- **消費リソース**:
  - 介入ポイント: 50 IP
  - ゴールド: 2,000 Gold（またはワールド共通通貨）
- **クールダウン**: 介入発動後、同一ダンジョン内での再介入には 30 ターンのクールダウンが発生します。

### 3.3 プレイヤーおよび管理者用触媒アイテム
特定の消費アイテム（宝珠・巻物）をダンジョン内で使用することで、一時的に環境異変を発生・上書き、または解除できます。

| アイテム ID | アイテム名称 | 効果概要 |
| :--- | :--- | :--- |
| `weather_orb_blaze` | 灼熱の宝珠 | フロア環境を `solar_flare`（持続 50 ターン）に変更 |
| `weather_orb_blizzard` | 極寒の宝珠 | フロア環境を `freezing_blizzard`（持続 50 ターン）に変更 |
| `weather_orb_miasma` | 瘴気の宝珠 | フロア環境を `miasma_storm`（持続 50 ターン）に変更 |
| `weather_orb_gravity` | 重力の宝珠 | フロア環境を `supergravity`（持続 50 ターン）に変更 |
| `weather_dispel_scroll` | 凪の巻物 | 現在フロアに発生している環境異変を即座に消滅・無害化 |

### 3.4 持続時間と減衰・解除メカニズム
- **自然発生時**: フロア全体の探索中（階段移動まで）永続、または指定ターン数（例: 100 ターン）。
- **アイテム・介入発動時**: 50 ターン固定。残りターン数はプレイヤーのターン経過とともに 1 ずつ減少します。
- **上書きルール**: 新しい環境異変が発動した場合、既存の環境異変は消滅し、新効果が指定ターン数適用されます。
- **消滅処理**: 残りターン数が 0 になった場合、フロア環境は即座に「通常（`NORMAL`）」へ復帰し、視界やステータス補正が元に戻ります。

---

## 4. ドメインモデルおよびデータ構造

### 4.1 EnvironmentalAnomalyDomain
`Dungeon` モジュール配下でフロアごとの広域環境状態を表す値オブジェクト/ドメインモデルです。

```json
{
  "anomalyId": "solar_flare",
  "name": "太陽フレア",
  "severity": 2,
  "remainingTurns": 45,
  "triggeredBy": "PLAYER_ITEM",
  "modifiers": {
    "fireDamageMultiplier": 1.5,
    "iceDamageMultiplier": 0.5,
    "staminaDrainMultiplier": 1.5,
    "magmaDamage": 20
  }
}
```

### 4.2 ドメイン属性一覧
| 属性名 | 型 | 必須 | 説明 |
| :--- | :--- | :--- | :--- |
| `anomalyId` | String | はい | 環境異変識別子 (`solar_flare`, `freezing_blizzard`, `mana_surge`, `miasma_storm`, `supergravity`, `NONE`) |
| `name` | String | はい | 画面表示用名称 |
| `severity` | Integer | はい | 異変の強さレベル（1: 軽度, 2: 中度, 3: 重度） |
| `remainingTurns` | Integer | はい | 残り持続ターン数（`-1` はフロア永続） |
| `triggeredBy` | String | はい | 発動要因 (`NATURAL`, `MANAGER_INTERVENTION`, `PLAYER_ITEM`) |
| `modifiers` | Map | はい | 戦闘・移動計算用各種補正値オブジェクト |

---

## 5. モジュール間連携およびシーケンス

### 5.1 連携フロー概要
1. **環境異変の発生**: `Dungeon` モジュールにて環境異変の生成・発動が決定されます。
2. **計算補正の適用**: `Combat` モジュールおよび `PlayerOperations` モジュールが、ダメージ計算およびスタミナ計算時に `Dungeon` の現在の `EnvironmentalAnomalyDomain` 状態を参照・反映します。
3. **リアルタイム通知**: `Display` モジュールへ SSE（Server-Sent Events）経由で環境異変の変更・終了イベントをパブリッシュし、画面上の天候エフェクト（背景グラフィック、視界遮蔽マスク）および UI アラートを更新します。

### 5.2 SSEリアルタイム配信イベント
環境異変の発生・消滅時には以下のリアルタイムイベントがクライアントへ配信されます。

#### イベント: `ENVIRONMENTAL_ANOMALY_CHANGED`
```json
{
  "eventType": "ENVIRONMENTAL_ANOMALY_CHANGED",
  "dungeonId": "dungeon_001",
  "floorLevel": 5,
  "anomaly": {
    "anomalyId": "freezing_blizzard",
    "name": "絶対零度",
    "severity": 2,
    "remainingTurns": 50,
    "triggeredBy": "PLAYER_ITEM"
  },
  "message": "フロア全体が極寒の吹雪に包まれた！"
}
```

#### イベント: `ENVIRONMENTAL_ANOMALY_EXPIRED`
```json
{
  "eventType": "ENVIRONMENTAL_ANOMALY_EXPIRED",
  "dungeonId": "dungeon_001",
  "floorLevel": 5,
  "message": "極寒の吹雪が収まり、周囲の気候が元に戻った。"
}
```

---

## 6. REST API 仕様

### 6.1 フロア環境異変状態照会
- **HTTP メソッド**: `GET`
- **エンドポイント**: `/api/v1/dungeons/{dungeonId}/floors/{level}/environment`

#### レスポンス JSON 例 (200 OK):
```json
{
  "dungeonId": "dungeon_001",
  "floorLevel": 5,
  "activeAnomaly": {
    "anomalyId": "mana_surge",
    "name": "マナ大奔流",
    "severity": 2,
    "remainingTurns": 32,
    "triggeredBy": "NATURAL",
    "modifiers": {
      "mpCostDiscountRatio": 0.5,
      "spellCriticalBonus": 0.20,
      "lineOfSightRadius": 2
    }
  }
}
```

---

### 6.2 ダンジョン管理者による環境異変介入発動
- **HTTP メソッド**: `POST`
- **エンドポイント**: `/api/v1/dungeons/{dungeonId}/floors/{level}/environment/trigger`

#### リクエスト JSON 例:
```json
{
  "managerUserId": "usr_manager_01",
  "anomalyId": "solar_flare",
  "severity": 2,
  "durationTurns": 50
}
```

#### レスポンス JSON 例 (200 OK):
```json
{
  "success": true,
  "dungeonId": "dungeon_001",
  "floorLevel": 5,
  "activeAnomaly": {
    "anomalyId": "solar_flare",
    "name": "太陽フレア",
    "severity": 2,
    "remainingTurns": 50,
    "triggeredBy": "MANAGER_INTERVENTION"
  },
  "consumedResource": {
    "interventionPoints": 50,
    "gold": 2000
  }
}
```

---

### 6.3 フロア環境異変の解除・鎮静
- **HTTP メソッド**: `POST`
- **エンドポイント**: `/api/v1/dungeons/{dungeonId}/floors/{level}/environment/dispel`

#### リクエスト JSON 例:
```json
{
  "operatorUserId": "usr_player_01",
  "dispelSource": "ITEM_WEATHER_DISPEL_SCROLL"
}
```

#### レスポンス JSON 例 (200 OK):
```json
{
  "success": true,
  "dungeonId": "dungeon_001",
  "floorLevel": 5,
  "previousAnomalyId": "solar_flare",
  "currentAnomalyId": "NONE",
  "message": "環境異変が解呪され、通常の状態に戻った。"
}
```

---

## 7. エラーハンドリング

環境異変に関する API および処理実行時における異常系エラーコード、HTTP ステータスコード、および表示メッセージのマッピングは以下の通りです。

| エラーコード | HTTP ステータス | 発生原因 | 画面表示メッセージ例 |
| :--- | :--- | :--- | :--- |
| `DUNGEON_NOT_FOUND` | 404 Not Found | 指定されたダンジョン ID が存在しない | ダンジョンが見つかりません。 |
| `FLOOR_NOT_FOUND` | 404 Not Found | 指定されたフロア階層が存在しない | 該当のフロア階層が見つかりません。 |
| `INVALID_ANOMALY_TYPE` | 400 Bad Request | 無効な環境異変 ID を指定した | 指定された環境異変の種別が無効です。 |
| `ANOMALY_ALREADY_ACTIVE` | 409 Conflict | 同一または競合する異変がすでに発動中 | 既に強力な環境異変が発動しています。 |
| `INSUFFICIENT_INTERVENTION_POINTS` | 400 Bad Request | 管理者の介入ポイント不足 | 介入ポイントが不足しています。 |
| `INSUFFICIENT_GOLD` | 400 Bad Request | 管理者またはプレイヤーのゴールド不足 | 所持ゴールドが不足しています。 |
| `UNAUTHORIZED_MANAGER` | 403 Forbidden | ダンジョン所有権のないユーザーによる介入 | ダンジョンの管理権限がありません。 |
| `ANOMALY_NOT_ACTIVE` | 400 Bad Request | 解除しようとしたフロアに異変が存在しない | 解除対象の環境異変が存在しません。 |

---

## 8. ゲームバランス制限および注意事項

- **複数発動の禁止**: 1つのフロアに共存できる環境異変は常に1種類のみです。別の環境異変が発動した場合、既存の効果は即時解除され新しい効果へ置換されます。
- **ボスフロアにおける制限**: ボス階（5階層ごと等の特定フロア）では自然発生確率が 0% に設定され、管理者介入およびアイテムによる上書きも無効化（`ACTION_BLOCKED_BY_BOSS_FLOOR` エラー）されます。
- **耐性・特性とのシナジー**: モンスター特性（[モンスター特性システム](./Monster-Trait-System.md)）や装備の属性耐性と重複適用され、最大 80% のダメージ軽減キャップが適用されます。
