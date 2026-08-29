# ダンジョン独自ルール詳細仕様 (Dungeon Custom Rule Specification)

## 1. 概要
本ドキュメントは、ダンジョンランク S において管理者が設定可能な「独自ルール」の技術的な詳細、パラメーター構造、および適用ロジックを定義します。これらのルールは、[ダンジョンランクシステム](./Dungeon-Rank-System.md) に基づき、フロア全体の挙動をカスタマイズするために使用されます。

## 2. ルール定義一覧

各独自ルールは、`DungeonDomain.customRules` 内の `ruleId` と `parameters` マップによって定義されます。

### 2.1 アイテム制限 (`ITEM_RESTRICTION`)
特定のカテゴリのアイテムの使用または持ち込みを制限します。

- **パラメーター (`parameters`)**:
    - `prohibitedTypes` (Array<String>): 使用を禁止する `TypeEnum` のリスト（例: `["SCROLL", "STICK"]`）。
    - `isCarryInDisabled` (Boolean): 持ち込み自体を禁止するかどうか（`true` の場合、入場時に一時的に没収されるか、入場不可となる）。
- **適用ロジック**: 制限されたタイプのアイテムを使用（読む、振る等）しようとした際、システムメッセージを表示してアクションをキャンセルします。

### 2.2 属性特化・抑制 (`ATTRIBUTE_MODIFIER`)
特定の属性のダメージを強化し、相反する属性を抑制します。

- **パラメーター (`parameters`)**:
    - `boostedAttribute` (String): 強化する属性（例: `FIRE`）。
    - `boostRate` (Double): 強化倍率（デフォルト: `1.5`）。
    - `suppressedAttribute` (String): 抑制する属性（例: `WATER`）。
    - `suppressRate` (Double): 抑制倍率（デフォルト: `0.5`）。
- **適用ロジック**: [戦闘システム](./Combat-System.md) のダメージ計算において、最終ダメージにこれらの倍率を乗算します。

### 2.3 スタミナ消費増大 (`STAMINA_COST_EXTENSION`)
移動以外の行動（攻撃、スキル、アイテム使用等）においてもスタミナを消費するように設定します。

- **パラメーター (`parameters`)**:
    - `actionCost` (Integer): 移動以外の 1 アクションごとに消費するスタミナ量（デフォルト: `1`）。
- **適用ロジック**: プレイヤーが攻撃やアイテム使用などの「ターンを消費する行動」を行った際、設定された値をスタミナから減算します。

### 2.4 ターン制限 (`TURN_LIMIT`)
指定したターン数以内にクリアできない場合、ペナルティを課します。

- **パラメーター (`parameters`)**:
    - `maxTurns` (Integer): 最大許容ターン数（例: `500`）。
    - `penaltyType` (String): タイムオーバー時の挙動（`FORCED_EXIT`: 強制帰還, `DEATH`: 死亡扱い）。
- **適用ロジック**: `PlayerDomain.currentStatus.subStep` が `maxTurns` に達した時点で判定を行います。

### 2.5 報酬特化（経験値・ゴールド封印） (`REWARD_ENHANCEMENT_ONLY`)
ダンジョン内でのリソース獲得を制限する代わりに、最終的なクリア報酬を大幅に強化します。

- **パラメーター (`parameters`)**:
    - `isExpGainDisabled` (Boolean): 経験値獲得を無効化するか（デフォルト: `true`）。
    - `isGoldPickUpDisabled` (Boolean): ゴールド拾得を無効化するか（デフォルト: `true`）。
    - `rewardMultiplier` (Double): クリア報酬の倍率（例: `2.0`）。
- **適用ロジック**:
    - 戦闘終了時の経験値加算およびゴールド拾得イベントをスキップします。
    - クリア判定時、`DungeonDomain.clearReward` の内容に `rewardMultiplier` を乗算してプレイヤーに付与します。

## 3. 設定と保存
- **上限数**: 1 つのダンジョン（またはフロア）に設定可能な独自ルールは最大 **3 つ** です。
- **保存形式**: [Dungeon-MongoDB.md](./domain_models/Dungeon-MongoDB.md) の `customRules` フィールドに配列形式で保存されます。

## 4. 今後の拡張
- **天候システム**: 特定のターン周期で視界や属性補正が変化するルール。
- **コンボボーナス**: 特定の条件下で連続して敵を倒すと報酬が増加するルール。

## 5. APIリクエスト・フローとエラーハンドリング

### 5.1 APIリクエスト仕様
ダンジョン管理者による独自ルールの設定、変更、取得、削除に関するAPIエンドポイントおよびJSON形式を定義します。

#### 1) 独自ルール一覧取得 (`GET /api/v1/dungeons/{dungeonId}/custom-rules`)
- **Response Body (JSON - 成功時)**:
```json
{
  "dungeonId": "dungeon_uuid_67890",
  "dungeonRank": "S",
  "customRules": [
    {
      "ruleId": "ITEM_RESTRICTION",
      "parameters": {
        "prohibitedTypes": ["SCROLL", "STICK"],
        "isCarryInDisabled": true
      }
    },
    {
      "ruleId": "ATTRIBUTE_MODIFIER",
      "parameters": {
        "boostedAttribute": "FIRE",
        "boostRate": 1.5,
        "suppressedAttribute": "WATER",
        "suppressRate": 0.5
      }
    }
  ]
}
```

#### 2) 独自ルールの設定・更新 (`POST /api/v1/dungeons/{dungeonId}/custom-rules`)
- **Request Body (JSON)**:
```json
{
  "userId": "admin_uuid_12345",
  "ruleId": "TURN_LIMIT",
  "parameters": {
    "maxTurns": 500,
    "penaltyType": "FORCED_EXIT"
  }
}
```

- **Response Body (JSON - 成功時)**:
```json
{
  "success": true,
  "result": "SUCCESS",
  "message": "独自ルール「TURN_LIMIT」を設定しました。",
  "dungeonId": "dungeon_uuid_67890",
  "appliedRule": {
    "ruleId": "TURN_LIMIT",
    "parameters": {
      "maxTurns": 500,
      "penaltyType": "FORCED_EXIT"
    }
  },
  "currentRuleCount": 3,
  "maxRuleLimit": 3
}
```

#### 3) 独自ルールの削除 (`DELETE /api/v1/dungeons/{dungeonId}/custom-rules/{ruleId}`)
- **Response Body (JSON - 成功時)**:
```json
{
  "success": true,
  "result": "SUCCESS",
  "message": "独自ルール「TURN_LIMIT」を削除しました。",
  "dungeonId": "dungeon_uuid_67890",
  "removedRuleId": "TURN_LIMIT",
  "currentRuleCount": 2
}
```

#### 4) エラーレスポンス共通フォーマット (JSON - 異常時)
```json
{
  "success": false,
  "result": "ERROR",
  "errorCode": "INSUFFICIENT_DUNGEON_RANK",
  "message": "独自ルールを設定するにはダンジョンランクが S である必要があります。"
}
```

### 5.2 エラーハンドリング (Error Handling)
独自ルールの設定・変更および削除処理中に異常が検出された場合、システムは以下のエラーコードと適切なHTTPステータスを返却します。

| エラーコード | 発生条件 | レスポンス HTTP ステータス | 戻り値のメッセージ例 |
| :--- | :--- | :---: | :--- |
| `DUNGEON_NOT_FOUND` | 指定された `dungeonId` のダンジョンが存在しない。 | 404 Not Found | 指定されたダンジョンが見つかりません。 |
| `INSUFFICIENT_DUNGEON_RANK` | ダンジョンのランクが S 未満であるため独自ルールを設定・変更できない。 | 400 Bad Request | 独自ルールを設定するにはダンジョンランクが S である必要があります。 |
| `CUSTOM_RULE_LIMIT_EXCEEDED` | 設定済みの独自ルールが上限の3つに達しており、新たなルールを追加できない。 | 400 Bad Request | 独自ルールの設定上限（最大3つ）を超過しています。 |
| `INVALID_RULE_TYPE` | 定義されていない無効な `ruleId` が指定された。 | 400 Bad Request | 無効な独自ルールタイプが指定されています。 |
| `INVALID_RULE_PARAMETER` | `parameters` に不正な値（例: 負のターン数、未定義の属性名等）が含まれている。 | 400 Bad Request | ルールパラメーターの値が不正です。 |
| `DUPLICATE_CUSTOM_RULE` | 既に設定済みの `ruleId` を重複して設定しようとした。 | 400 Bad Request | 指定された独自ルールは既に設定されています。 |
| `RULE_NOT_FOUND` | 削除対象として指定された `ruleId` が設定されていない。 | 404 Not Found | 指定された独自ルールは設定されていません。 |
