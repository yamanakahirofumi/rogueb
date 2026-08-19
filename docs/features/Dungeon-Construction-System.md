# ダンジョン構築・運営システム (Dungeon Construction & Management System)

## 1. 概要
本ドキュメントは、管理者が自身のダンジョン（マイ・ダンジョン）を構築・デザインし、他のプレイヤーに公開して運営するための仕様を定義します。本システムは、RogueB の「構築と運営」サイクルの中核を担います。

## 2. 建築資材 (Building Materials)
ダンジョンの構成要素を配置するには、特定の「建築資材」が必要です。これらは `Objects` モジュールで `TypeEnum.MATERIAL` として管理されます。

### 2.1 資材の獲得方法
- **探索による調達**: ランダムダンジョン（フロンティア）の宝箱や、特定の壁を掘削することで獲得できます。
- **ショップでの購入**: 他の管理者が運営するショップや、システム提供の資材屋で購入できます。
- **解体**: 既存の設置物を解体することで、一部の資材を回収できます。

### 2.2 資材のカテゴリと配置コスト
資材の配置には、アイテムとしての資材消費に加え、以下の配置コスト（ゴールド）の目安が必要です。

| カテゴリ | 例 | 配置コスト (Gold/枚) | 備考 |
| :--- | :--- | :---: | :--- |
| **地形タイル** | 石の床、水路、溶岩 | 10 〜 200 | 面積を埋める基本的なタイル。 |
| **構造物** | 扉、壊れる壁 | 80 〜 200 | 通行を制御する要素。 |
| **構造物 (強)** | 頑丈な壁 | 500 | 掘削困難な強固な壁。 |
| **トラップ** | 落とし穴、地雷 | 50 〜 200 | [トラップシステム](./Trap-System.md) 準拠。 |
| **施設** | ショップカウンター | 1,000 | 機能を持つ特殊なタイル。 |
| **施設 (高)** | 回復の泉 | 5,000 | 非常に高い回復効果を持つ。 |
| **デコレーション** | 燭台、石像、旗 | 50 〜 300 | 外観を整えるための装飾。 |

### 2.3 特殊タイルの詳細効果 (Detailed Effects of Special Tiles)
特定の地形タイルがエンティティ（プレイヤー・モンスター）に与える影響、および特性との相互作用を以下に定義します。

| タイル名 | 基本効果 | 特性による相互作用 |
| :--- | :--- | :--- |
| **水路 (Waterway)** | 通常移動不可。 | 特性「飛行 (`FLIGHT`)」を持つユニットは、ペナルティなしで通行可能。 |
| **溶岩 (Lava)** | 通行可能だが、進入時および滞在中に **10 HP ダメージ** を受ける。 | 特性「飛行」または「火属性無効 (`FIRE_IMMUNITY`)」を持つユニットは、ダメージを受けずに通行可能。 |
| **落とし穴 (Pitfall/Pit)** | 進入時に「[落とし穴の罠](./Trap-System.md)」として作動する。 | 特性「飛行」を持つユニットは、作動させずに通過可能。 |

## 3. 建築モード (Build Mode)
管理者は、自身の所有するダンジョンにおいて「建築モード」に切り替えることで、リアルタイムにレイアウトを変更できます。

### 3.1 配置ルール
- **グリッドベース**: ダンジョンは格子状のセルで構成され、資材は 1 セル単位で配置します。
- **配置コスト**: 資材の配置には、アイテムとしての「資材」の消費に加え、一定の「建築コスト（ゴールド）」が必要となる場合があります。
- **進入路の確保**: 原則として、入り口（上り階段）から最深部（下り階段/クリア地点）までの通行可能な経路が最低 1 つ存在する必要があります。

### 3.2 モンスターの配置
管理者は、[モンスター捕獲システム](./Monster-Capture-System.md) や [モンスター繁殖システム](./Monster-Breeding-System.md) で獲得したモンスターをダンジョン内に配置できます。
- **モンスター・ストック**: 配置に使用するモンスターは、あらかじめ「ストック（預かり所）」に登録されている必要があります（`MonsterInstanceDomain.state` が `STORAGE` または `PLACED` の個体）。
- **配置制限**: 配置できるモンスターの総数（`placementCost` の合計）には上限があります。この上限（最大配置コスト）は、**ダンジョンのランク**および**フロアの面積（幅 × 高さ）**に基づいて算出されます。
- **巡回設定**: モンスターの待機位置や、特定の範囲を巡回するなどの簡単な行動指針を設定できます。

## 4. ダンジョンの公開と運営
構築したダンジョンを他のプレイヤーに公開することで、収益を得ることができます。

### 4.1 運営設定
- **入場料 (`entryFee`)**: プレイヤーが入場する際に支払う金額を設定します。
- **デスペネルティ (`deathPenalty`)**: プレイヤーが死亡した際のアイテム・ゴールド没収ルールを設定します。詳細は [機能仕様書](./Functional-Specification.md) を参照。
- **クリア条件 (`clearCondition`)**: ダンジョンの「クリア」と判定される条件（階層到達、ボス撃破など）を設定します。
- **クリア報酬 (`clearReward`)**: プレイヤーがクリアした際に与える報酬（ゴールド、アイテム）を設定します。

### 4.2 収益サイクル
1. プレイヤーが入場料を支払い、管理者の所持金が増加する。
2. プレイヤーがダンジョン内で死亡した場合、没収されたアイテムやゴールドが管理者の倉庫/所持金に加算される。
3. プレイヤーがクリアした場合、管理者が設定した報酬がプレイヤーに支払われる（管理者の資産から差し引かれる）。

## 5. リアルタイム介入 (Admin Intervention)
管理者は、自身のダンジョンを攻略中のプレイヤーに対し、リアルタイムで干渉することができます。これらの介入アクションには、悪用防止のために「介入ポイント (Intervention Points: IP)」または「クールタイム」が設定されます。

- **介入ポイント (IP)**:
    - ダンジョンの時間経過や、プレイヤーへのダメージ、プレイヤーの撃破によって蓄積されます。
    - **蓄積ルール**:
        - **時間経過**: 10 秒ごとに 1 IP 蓄積。
        - **与ダメージ**: プレイヤーに 10 ダメージ与えるごとに 1 IP 蓄積。
        - **プレイヤー撃破**: 1 人撃破につき 100 IP 蓄積。
        - **ランクボーナス**: 蓄積される IP には、ダンジョンランクに応じた補正がかかります。
            - `獲得 IP = 基礎 IP * (1.0 + (ランク指数 - 1) * 0.2)`
    - **最大蓄積量**: `500 + (ダンジョンランク指数 * 100)` IP。
- **介入アクション**:
    - **増援の召喚**: ストックしているモンスターを、プレイヤーの視界外に即座に配置します。
        - **消費 IP**: モンスターのティアに応じて変動（Tier 1: 50 / Tier 2: 150 / Tier 3: 400 / Tier 4: 800）。
        - **クールタイム**: 配置したモンスターのティアに依存します（Tier 1: 30秒 / Tier 2: 60秒 / Tier 3: 120秒 / Tier 4: 300秒）。
    - **トラップの手動発動**: プレイヤーが踏んでいないトラップを、管理者の意思で強制的に作動させます。
        - **消費 IP**: 100 IP 固定。
        - **クールタイム**: 15 秒。
    - **メッセージ送信**: 攻略中のプレイヤーに対し、特定のメッセージ（天の声）を表示します。
        - **通信仕様**: [リアルタイム同期システム](../implementation/Real-time-Synchronization.md) の `ADMIN_MESSAGE` イベントを使用して配信されます。
        - **消費 IP**: 10 IP。
        - **クールタイム**: 5 秒。

## 6. プレイヤー活動のフィードバック (Player Activity Feedback)
管理者は、自身のダンジョンをより魅力的に、あるいは戦略的に改善するために、プレイヤーの活動記録を確認できます。

### 6.1 ログの収集と閲覧
- **活動ログ**: プレイヤーの入場、クリア、および死亡の記録が匿名化された状態で保存されます。
- **ヒートマップ**: プレイヤーがどの座標で最も多く立ち止まったか、あるいはどの場所で戦闘不能になったかの統計データを提供します。
- **クリア率の算出**: 階層ごとの到達率や、最終的なクリア率を可視化し、難易度の調整に役立てます。

## 7. モジュール間連携

```mermaid
sequenceDiagram
    participant Admin as 管理者
    participant PO as PlayerOperations
    participant D as Dungeon Module
    participant M as Monster Module
    participant OBJ as Objects Module
    participant BA as BookOfAdventure

    Note over Admin, BA: 建築フェーズ
    Admin->>PO: 資材配置リクエスト (Tile, x, y)
    PO->>OBJ: 資材アイテムの消費確認
    PO->>D: フロア情報の更新 (tiles/thingList)
    D-->>PO: 更新完了
    PO-->>Admin: 配置成功

    Note over Admin, BA: モンスター配置
    Admin->>PO: モンスター配置リクエスト (instanceId, x, y)
    PO->>BA: モンスター所有権の確認
    PO->>D: FloorDomain.monsterList への追加
    D-->>PO: 配置完了

    Note over Admin, BA: 運営開始
    Admin->>PO: 公開設定 (EntryFee, Penalty)
    PO->>D: DungeonDomain の設定更新
```

## 8. APIリクエスト・フローとエラーハンドリング

### 8.1 APIリクエスト仕様
ダンジョン構築・運営における主要アクション（資材・構造物配置、モンスター配置、リアルタイム介入）のエンドポイントおよびリクエスト/レスポンスボディのJSON構造を定義します。

#### 1) 資材・構造物配置 (`POST /api/v1/dungeons/build/place`)
- **Request Body (JSON)**:
```json
{
  "userId": "admin_uuid_12345",
  "dungeonId": "dungeon_uuid_67890",
  "floorNumber": 1,
  "coordinate": {
    "x": 5,
    "y": 10
  },
  "tileTypeId": "breakable_wall"
}
```

- **Response Body (JSON - 成功時)**:
```json
{
  "success": true,
  "result": "SUCCESS",
  "message": "壊れる壁の配置に成功しました。",
  "dungeonId": "dungeon_uuid_67890",
  "floorNumber": 1,
  "placedTile": {
    "x": 5,
    "y": 10,
    "tileTypeId": "breakable_wall"
  },
  "consumedGold": 100,
  "consumedMaterialId": "material_stone"
}
```

#### 2) モンスター配置 (`POST /api/v1/dungeons/build/place-monster`)
- **Request Body (JSON)**:
```json
{
  "userId": "admin_uuid_12345",
  "dungeonId": "dungeon_uuid_67890",
  "floorNumber": 1,
  "monsterInstanceId": "monster_uuid_11223",
  "coordinate": {
    "x": 8,
    "y": 12
  },
  "patrolType": "PATROL_ROOM"
}
```

- **Response Body (JSON - 成功時)**:
```json
{
  "success": true,
  "result": "SUCCESS",
  "message": "モンスター（オーク）の配置に成功しました。",
  "dungeonId": "dungeon_uuid_67890",
  "floorNumber": 1,
  "monster": {
    "instanceId": "monster_uuid_11223",
    "monsterId": "orc",
    "coordinate": {
      "x": 8,
      "y": 12
    },
    "patrolType": "PATROL_ROOM"
  },
  "currentTotalPlacementCost": 15,
  "maxPlacementCost": 50
}
```

#### 3) リアルタイム介入 (`POST /api/v1/dungeons/intervene`)
- **Request Body (JSON)**:
```json
{
  "userId": "admin_uuid_12345",
  "dungeonId": "dungeon_uuid_67890",
  "targetPlayerUserId": "player_uuid_99887",
  "actionType": "SUMMON_REINFORCEMENT",
  "parameters": {
    "monsterInstanceId": "monster_uuid_33445",
    "coordinate": {
      "x": 12,
      "y": 15
    }
  }
}
```

- **Response Body (JSON - 成功時)**:
```json
{
  "success": true,
  "result": "SUCCESS",
  "message": "介入アクション「増援の召喚」を実行しました。",
  "actionType": "SUMMON_REINFORCEMENT",
  "consumedIP": 150,
  "remainingIP": 350,
  "cooldownSeconds": 60
}
```

#### 4) エラーレスポンス共通フォーマット (JSON - 異常時)
```json
{
  "success": false,
  "result": "ERROR",
  "errorCode": "PATH_BLOCKED",
  "message": "通行可能経路が遮断されるため、この位置に構造物を配置することはできません。"
}
```

### 8.2 エラーハンドリング (Error Handling)
ダンジョン構築およびリアルタイム介入処理の過程で異常が検出された場合、システムは以下のエラーコードと適切なHTTPステータスを返却します。

| エラーコード | 発生条件 | レスポンス HTTP ステータス | 戻り値のメッセージ例 |
| :--- | :--- | :---: | :--- |
| `DUNGEON_NOT_FOUND` | 指定された `dungeonId` のダンジョンが存在しない。 | 404 Not Found | 指定されたダンジョンが見つかりません。 |
| `FLOOR_NOT_FOUND` | 指定された `floorNumber` のフロアが存在しない。 | 404 Not Found | 指定されたフロアが見つかりません。 |
| `INVALID_COORDINATE` | 指定された座標 `(x, y)` がフロアの有効範囲外である。 | 400 Bad Request | 無効な座標が指定されています。 |
| `INSUFFICIENT_MATERIAL` | 建築に必要な資材アイテムがプレイヤーの所有物/インベントリに不足している。 | 400 Bad Request | 建築に必要な資材アイテムが不足しています。 |
| `INSUFFICIENT_GOLD` | 資材の配置に必要なゴールドが不足している。 | 400 Bad Request | 所持ゴールドが不足しています。 |
| `PATH_BLOCKED` | 構造物の配置により、入り口から最深部までの通過可能な経路が遮断される。 | 400 Bad Request | 通行可能経路が遮断されるため、配置できません。 |
| `TILE_OCCUPIED` | 解体不可能な固定構造物や他の施設が存在するセルに配置しようとした。 | 400 Bad Request | 既に設置物が存在するセルには配置できません。 |
| `MONSTER_NOT_FOUND` | 指定された `monsterInstanceId` のモンスターが存在しない。 | 404 Not Found | 指定されたモンスターが見つかりません。 |
| `MONSTER_NOT_OWNED` | 指定されたモンスターが管理者の所有ストックに存在しない。 | 400 Bad Request | 指定されたモンスターを所有していないか、ストック状態ではありません。 |
| `PLACEMENT_COST_EXCEEDED` | モンスターの配置により、ダンジョンランク・フロア面積で規定された最大配置コストを超過する。 | 400 Bad Request | モンスターの最大配置コスト制限を超過しています。 |
| `ACTIVE_SESSION_NOT_FOUND` | 介入対象のプレイヤーセッションが存在しない、または既にダンジョンを退出している。 | 404 Not Found | 対象のプレイヤーはダンジョン内に存在しません。 |
| `INSUFFICIENT_INTERVENTION_POINTS` | 介入アクションに必要な IP（介入ポイント）が不足している。 | 400 Bad Request | 介入ポイント(IP)が不足しています。 |
| `ACTION_COOLDOWN_ACTIVE` | 前回の介入アクション実行によるクールタイムが経過していない。 | 400 Bad Request | クールタイム中のため介入アクションを実行できません。 |
| `INVALID_TARGET_TILE` | プレイヤーの直接視界内や進入不可マスなど、不適切な位置への介入アクション実行。 | 400 Bad Request | 指定された位置には介入アクションを実行できません。 |

## 9. 今後の拡張
- **[ダンジョンランク](./Dungeon-Rank-System.md)**: プレイヤーの評価や攻略難易度に基づき、ダンジョンのランクが上昇する仕組み。
- **設計図 (Blueprint)**: 他の管理者が作成した優れたレイアウトを、設計図として売買・利用できる機能。
- **自動生成の統合**: ベースとなるレイアウトを自動生成し、そこから管理者が微調整を行うハイブリッド建築。
