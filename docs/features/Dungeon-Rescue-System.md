# ダンジョン救助・墓標システム仕様 (Dungeon Rescue & Tombstone System)

## 1. 概要
本ドキュメントは、ダンジョン探索中にプレイヤーが倒伏（HP 0）した際の救済メカニズム、非同期マルチプレイヤー協力プレイ、および遺品回収を担う「ダンジョン救助・墓標システム」の詳細仕様を定義します。

従来のローグライクにおける全損デスペナルティの緊張感を維持しつつ、他プレイヤーとの相互扶助や、自身の遺志（墓標）に宿る残留思念・守護英霊との防衛戦を通じた遺品回収というやり込み要素を提供します。

---

## 2. コアコンセプトと救助ライフサイクル

プレイヤーがダンジョン内で倒伏した際、通常の即時デスペナルティ適用の代わりに、以下の2つの選択肢が提示されます。

```
                     ┌───────────────────┐
                     │  ダンジョン内倒伏  │
                     └─────────┬─────────┘
                               │ 選択
               ┌───────────────┴───────────────┐
               ▼                               ▼
       【救助要請の発行】               【墓標の生成と即時帰還】
   (ダンジョン内で待機/中断)            (街で復活 / 倒伏位置に墓標配置)
               │                               │
               ▼                               ▼
      他プレイヤーによる救助            自己または他者による墓標回収
    (救助隊が遭難フロアへ突入)          (守護英霊戦 / 遺灰供養)
               │                               │
               ▼                               ▼
      その場復活 & 探索継続           喪失アイテム・ゴールドの一部奪還
```

### 2.1 救助要請 (Rescue Request)
- **待機モード**: 救助要請を発行したプレイヤーは、救助が成功するまでそのセッションの探索を一時中断し、街や拠点メニュー（または別キャラクター）の操作のみが可能になります。
- **救助回数制限**: 同一ダンジョン・同一探索につき最大 **3 回** まで要請可能。
- **公開範囲 (Scope)**:
    - `PUBLIC`: ワールド内の全プレイヤーが救助掲示板から受注可能。
    - `FRIEND`: フレンド登録済みのプレイヤー限定。
    - `PASSWORD`: 任意の合言葉（パスワード）を知るプレイヤー限定。
- **救助達成時の効果**:
    - 被救助者: 倒伏したフロア・座標でHP/満腹度全快で蘇生し、アイテム・ゴールド・経験値を一切失うことなくそのフロアから探索を再開。
    - 救助者: 依頼主が設定した報奨金（ゴールド）、名声ポイント、および専用素材「救助の証（Rescue Medal）」を獲得。

### 2.2 墓標システム (Tombstone Entity)
- **墓標の生成**: プレイヤーが救助を要請せず即時撤退（または救助回数上限到達）した場合、倒伏したフロア・座標に `Tombstone`（墓標）エンティティが永続配置されます。
- **墓標の属性・データ**:
    - 刻銘（碑文）: 倒伏日時、プレイヤー名、死因（撃破されたモンスター名やトラップ名）。
    - 封印された遺品: デスペナルティで失われたアイテムおよびゴールドの **50% 〜 80%**。
    - 墓標の守護英霊（Guardian Spirit）: 倒伏時のプレイヤーの幻影（シャドウ）または手持ちモンスターの残留思念が防衛エンティティとして出現。
- **墓標回収プロセス**:
    - 接触判定: フロア上の墓標マスへ到達し、アクションキーまたは「墓標の鍵」「供養のお香」を使用。
    - 守護戦: 接触時に周囲 1 マスに守護英霊（ステータスは倒伏時プレイヤーの 80% 相当）が出現。撃破することで墓標の封印が解除。
    - 回収完了: 封印されたアイテム・ゴールドがドロップし、回収可能となる。

---

## 3. 専用アイテム定義

救助活動および墓標供養を支援する専用アイテムです。[Objectsモジュール ドメインモデル](./domain_models/Objects.md) に登録されます。

| ID | 名称 | カテゴリ | 属性 | 標準価格 | ティア | 上限 | 表示 | 効果・補正 |
| :--- | :--- | :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| `rescue_scroll` | 救助の巻物 | `SCROLL` | Holy | 1500 | 2 | 500 | `?` | `meta_effects: [{"id": "RESCUE_PLAYER"}]`。遭難プレイヤーの元へ即時到達・蘇生を施す。 |
| `offering_incense` | 供養のお香 | `MATERIAL` | None | 800 | 1 | 1000 | `*` | `meta_effects: [{"id": "PURIFY_TOMBSTONE"}]`。墓標の守護英霊を鎮め、無血で遺品を回収する。 |
| `tombstone_key` | 墓標の鍵 | `OTHER` | None | 2000 | 2 | 300 | `k` | 強固に封印された墓標を解錠し、回収率を 100% に引き上げる。 |

---

## 4. モジュール間連携フロー

救助要請および墓標回収における主要コンポーネント間のデータ連携シーケンスです。

```
[PlayerOperations]       [Dungeon]             [BookOfAdventure]       [Objects]
       │                    │                         │                    │
       │── 1. 倒伏通知 ────>│                         │                    │
       │   (HP 0 検知)      │── 2. 墓標・要請生成 ───>│                    │
       │                    │   (TombstoneList登録)   │                    │
       │                    │                         │                    │
       │<── 3. 救助要請受付 ──────────────────────────│                    │
       │                                              │                    │
 [救助側Player]                                       │                    │
       │── 4. 救助受注 ──────────────────────────────>│                    │
       │   (POST /rescue/accept)                      │                    │
       │── 5. 特殊ダンジョン突入 ─> [Dungeon]         │                    │
       │   (遭難フロアへ転送)       │                 │                    │
       │── 6. 蘇生アクション ────>│── 7. 蘇生通知 ──>│                    │
       │   (救助の巻物使用)         │                 │── 8. 報酬配布 ────>│
       │                            │                 │  (ゴールド・証)    │
```

---

## 5. API仕様 (API Specifications)

ダンジョン救助および墓標システムが提供するREST APIのエンドポイントです。

### 5.1 救助要請発行 (`POST /api/v1/dungeons/rescue/request`)
プレイヤーがダンジョン内で倒伏した際に、他プレイヤーに向けて救助要請を発行します。

#### リクエスト (Request)
```json
{
  "userId": "usr_778899",
  "dungeonId": "dungeon_cave_01",
  "floorLevel": 8,
  "deathCoordinate": {
    "x": 14,
    "y": 22,
    "z": 0
  },
  "causeOfDeath": "KILLED_BY_MONSTER",
  "monsterTypeId": "arch_dragon",
  "rewardGold": 3000,
  "scope": "PUBLIC",
  "password": null
}
```

#### レスポンス (Response: 201 Created)
```json
{
  "requestId": "res_req_20260401_0012",
  "userId": "usr_778899",
  "dungeonId": "dungeon_cave_01",
  "floorLevel": 8,
  "rewardGold": 3000,
  "scope": "PUBLIC",
  "status": "OPEN",
  "requestedAt": "2026-04-01T10:00:00Z",
  "expiresAt": "2026-04-03T10:00:00Z"
}
```

---

### 5.2 救助要請一覧照会 (`GET /api/v1/dungeons/rescue/requests`)
受注可能な救助要請の一覧を検索・取得します。

#### クエリパラメータ
- `dungeonId`: ダンジョンID（任意）
- `scope`: 公開範囲（`PUBLIC`, `FRIEND`）
- `page`: ページ番号（デフォルト: 0）
- `size`: 取得件数（デフォルト: 20）

#### レスポンス (Response: 200 OK)
```json
{
  "requests": [
    {
      "requestId": "res_req_20260401_0012",
      "requesterUserId": "usr_778899",
      "requesterName": "AdventurerRoy",
      "dungeonId": "dungeon_cave_01",
      "dungeonName": "試練の洞窟",
      "floorLevel": 8,
      "causeOfDeath": "KILLED_BY_MONSTER",
      "rewardGold": 3000,
      "scope": "PUBLIC",
      "status": "OPEN",
      "requestedAt": "2026-04-01T10:00:00Z"
    }
  ],
  "totalCount": 1,
  "page": 0,
  "size": 20
}
```

---

### 5.3 救助要請受諾 (`POST /api/v1/dungeons/rescue/{requestId}/accept`)
救助者が指定された要請を受諾し、救助専用のダンジョン突入権限を発行します。

#### リクエスト (Request)
```json
{
  "rescuerUserId": "usr_112233",
  "password": null
}
```

#### レスポンス (Response: 200 OK)
```json
{
  "requestId": "res_req_20260401_0012",
  "rescuerUserId": "usr_112233",
  "status": "IN_PROGRESS",
  "rescueSessionId": "sess_rescue_991823",
  "acceptedAt": "2026-04-01T10:15:00Z"
}
```

---

### 5.4 救助完了報告 (`POST /api/v1/dungeons/rescue/{requestId}/complete`)
救助者が遭難地点へ到達し、救助アクションを完遂したことを報告します。

#### リクエスト (Request)
```json
{
  "rescueSessionId": "sess_rescue_991823",
  "rescuerUserId": "usr_112233"
}
```

#### レスポンス (Response: 200 OK)
```json
{
  "requestId": "res_req_20260401_0012",
  "status": "COMPLETED",
  "requesterUserId": "usr_778899",
  "rescuerUserId": "usr_112233",
  "rewardGold": 3000,
  "rewardItemInstanceIds": ["item_medal_rescue_001"],
  "completedAt": "2026-04-01T10:35:00Z"
}
```

---

### 5.5 墓標詳細照会 (`GET /api/v1/dungeons/tombstones/{tombstoneId}`)
指定された墓標エンティティの碑文、守護状態、および封印遺品概要を取得します。

#### レスポンス (Response: 200 OK)
```json
{
  "tombstoneId": "tomb_usr_778899_fl8_01",
  "dungeonId": "dungeon_cave_01",
  "floorLevel": 8,
  "position": {
    "x": 14,
    "y": 22,
    "z": 0
  },
  "deceasedUserId": "usr_778899",
  "deceasedName": "AdventurerRoy",
  "epitaph": "2026-04-01、アークドラゴンの灼熱の業火に屈す。",
  "isGuardianDefeated": false,
  "sealedGold": 1500,
  "sealedItemCount": 3,
  "status": "SEALED"
}
```

---

### 5.6 墓標回収・遺灰供養 (`POST /api/v1/dungeons/tombstones/{tombstoneId}/recover`)
守護戦撃破後、または供養アイテムを使用して墓標の封印を解除し、アイテム・ゴールドを回収します。

#### リクエスト (Request)
```json
{
  "userId": "usr_778899",
  "usedItemId": "tombstone_key"
}
```

#### レスポンス (Response: 200 OK)
```json
{
  "tombstoneId": "tomb_usr_778899_fl8_01",
  "status": "RECOVERED",
  "recoveredGold": 1500,
  "recoveredItems": [
    {
      "instanceId": "item_sword_iron_009",
      "name": "鉄の剣+3"
    },
    {
      "instanceId": "item_shield_dragon_002",
      "name": "ドラゴンシールド"
    }
  ],
  "recoveredAt": "2026-04-01T11:00:00Z"
}
```

---

## 6. エラーハンドリング仕様 (Error Handling Specification)

ダンジョン救助・墓標システムのAPI実行時に発生するエラーコードと対応するHTTPステータスコードです。

### 6.1 エラーレスポンス形式
```json
{
  "errorCode": "RESCUE_ALREADY_ACCEPTED",
  "message": "この救助要請はすでに他の救助者によって受諾されています。",
  "timestamp": "2026-04-01T10:16:00Z"
}
```

### 6.2 エラーコード一覧およびマッピング

| エラーコード | HTTPステータス | 説明 | 発生条件 |
| :--- | :---: | :--- | :--- |
| `RESCUE_REQUEST_NOT_FOUND` | `404 Not Found` | 要請非存在 | 指定された `requestId` の救助要請が存在しない。 |
| `RESCUE_ALREADY_ACCEPTED` | `409 Conflict` | 受諾済みエラー | 他のプレイヤーによって既に受注・進行中である。 |
| `CANNOT_RESCUE_SELF` | `400 Bad Request` | 自己救助禁止 | 自身の発行した救助要請を受諾しようとした。 |
| `MAX_RESCUE_LIMIT_EXCEEDED` | `400 Bad Request` | 救助回数制限超過 | 同一ダンジョン探索における救助上限（3回）を超えている。 |
| `INVALID_RESCUE_PASSWORD` | `403 Forbidden` | 合言葉不一致 | パスワード制の救助要請で誤った合言葉が指定された。 |
| `RESCUE_REQUEST_EXPIRED` | `410 Gone` | 要請期限切れ | 救助要請の有効期限（発行から48時間）が切れている。 |
| `TOMBSTONE_NOT_FOUND` | `404 Not Found` | 墓標非存在 | 指定された `tombstoneId` の墓標が存在しない。 |
| `TOMBSTONE_ALREADY_RECOVERED`| `409 Conflict` | 回収済みエラー | すでに遺品が回収され消滅した墓標である。 |
| `GUARDIAN_BATTLE_ACTIVE` | `400 Bad Request` | 守護戦未完了 | 守護英霊が撃破されておらず、供養アイテムも使用されていない。 |
| `INSUFFICIENT_KEY_ITEM` | `400 Bad Request` | 必要アイテム不足 | 解錠に必要な `tombstone_key` または `offering_incense` を所持していない。 |
