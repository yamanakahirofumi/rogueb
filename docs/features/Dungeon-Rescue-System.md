# ダンジョン救助・墓標システム仕様 (Dungeon Rescue & Tombstone System Specification)

## 1. 概要
本ドキュメントは、ダンジョン内でプレイヤーが HP 0 に到達し倒伏（力尽きた）した際に、所持アイテムやゴールドの一部を現地に「墓標（Tombstone）」として残し、他プレイヤーや自身の救助モンスターチームによって救助・回収を可能にする**「ダンジョン救助・墓標システム（Dungeon Rescue & Tombstone System）」**の設計と仕様を定義します。

---

## 2. システム基本設計

### 2.1 倒伏と墓標生成メカニズム
1. **倒伏発生**: ダンジョン探索中にプレイヤーの HP が 0 になった際、死亡ペナルティ設定に基づき、所持品・所持ゴールドの一部が現地タイルに固定化された**「墓標エンティティ（`TombstoneDomain`）」**としてドロップ生成されます。
2. **墓標の特性**:
   - 墓標が存在するタイルは進入不可（障壁）となり、専用の演出（墓標グラフィックおよび念動波エフェクト）が表示されます。
   - 墓標周囲 8 マスには「ガーディアン（守護霊・亡霊モンスター）」が低確率で自動スポーンし、墓標を保護します。
   - ダンジョンの該当フロアが次回再生成されるまで、墓標データは永続化保持されます（有効期限: 48時間）。

### 2.2 救助要請の掲載と公開
1. **救助要請の発行**: 倒伏したプレイヤーは、冒険の書（`BookOfAdventure`）システムを通じて**「救助要請（`RescueRequestDomain`）」**を公開ボードに登録できます。
2. **要請公開レベル**:
   - `PUBLIC`: 全プレイヤーに救助要請を公開。
   - `GUILD_FRIENDS`: フレンドまたはギルドメンバー限定で公開。
   - `SELF_MONSTER`: 自身の控えモンスター遠征隊のみによる自己救助。
3. **救助報酬金（デポジット）**: 要請者は救助成功時に救助者へ支払う「感謝報酬金（ゴールド）」を設定・事前預託します（最低 500 G）。

### 2.3 救助探索と浄化・回収フロー
1. **救助任務の受注**: 他プレイヤー（救助者）はアクティブな救助要請を受注し、該当ダンジョン・フロアへの「救助探索」を開始します。
2. **墓標の浄化**:
   - 救助者が墓標タイルに到達し、専用アイテム（`rescue_scroll`, `offering_incense`, `tombstone_key` 等）を使用するか、ガーディアンを撃破することで墓標が**浄化（Purify）**されます。
3. **回収と還元**:
   - 浄化完了時、墓標に保管されていた遺品・所持ゴールドが**「救助報酬ボックス」**へ送られ、要請者の郵便箱/ストレージへ還元されます。
   - 救助者には預託された報酬金、救助経験値、および専用称号の進行度が与えられます。

---

## 3. ドメインモデル構造

### 3.1 TombstoneDomain (Dungeon モジュール)
ダンジョンの特定フロア・座標に存在する墓標エンティティのモデルです。

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `tombstoneId` | String | 墓標の一意な ID (UUID) |
| `dungeonId` | String | 墓標が存在するダンジョン ID |
| `floorLevel` | Integer | 墓標が存在する階層レベル |
| `position` | `Coordinate2D` | 墓標の存在座標 (x, y) |
| `victimUserId` | String | 倒伏したプレイヤーのユーザー ID |
| `victimName` | String | 倒伏したプレイヤーの表示名 |
| `forfeitedGold` | Integer | 墓標内に封印されているゴールド |
| `forfeitedItems` | Array | 墓標内に封印されているアイテム (`ThingInstance` のリスト) |
| `guardianMonsterId` | String | 墓標を守護するガーディアンモンスターの種族 ID (任意) |
| `isPurified` | Boolean | 浄化済みかどうか |
| `createdAt` | String | 墓標生成日時 (ISO-8601) |
| `expiresAt` | String | 墓標消滅日時 (生成から 48 時間後) |

### 3.2 RescueRequestDomain (BookOfAdventure モジュール)
システム全体で共有される救助要請のモデルです。

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `requestId` | String | 救助要請の一意な ID (UUID) |
| `tombstoneId` | String | 関連付けられた墓標 ID |
| `requesterUserId` | String | 救助を要請したユーザー ID |
| `visibility` | String | 要請の公開範囲 (`PUBLIC`, `GUILD_FRIENDS`, `SELF_MONSTER`) |
| `rewardGold` | Integer | 救助成功時に救助者へ支払われる預託ゴールド |
| `status` | String | 要請ステータス (`OPEN`, `IN_PROGRESS`, `RESCUED`, `EXPIRED`, `CANCELLED`) |
| `rescuerUserId` | String | 救助を担当/完了したユーザー ID (未解決時は null) |
| `createdAt` | String | 要請作成日時 (ISO-8601) |
| `resolvedAt` | String | 要請解決日時 (ISO-8601) |

---

## 4. サポートアイテムおよび標準エフェクト定義

### 4.1 専用アイテム (`Objects` モジュール)
| タイプ ID (`typeId`) | 名称 | カテゴリ | 標準価格 | 概要 |
| :--- | :--- | :--- | :--- | :--- |
| `rescue_scroll` | 救助の巻物 | `SCROLL` | 4,000 G | 墓標のガーディアンを即座に退散させ、安全に墓標を浄化回収する巻物。 |
| `offering_incense` | 供養の香 | `CONSUMABLE` | 2,500 G | 墓標で使用すると、封印された遺品回収率を 100% に高める聖なる香。 |
| `tombstone_key` | 墓標の鍵 | `KEY` | 5,000 G | 特殊な呪印が施された高難易度ダンジョンの墓標を開封・回収するための鍵。 |

### 4.2 標準化エフェクト ID (`Standard-Metadata-Specification.md`)
- `RESCUE_PLAYER`: 救助要請の発行および墓標データの回収処理を行う。
- `PURIFY_TOMBSTONE`: 墓標タイルに対して浄化エフェクトを発火し、遺品およびゴールドの封印を解除する。

---

## 5. REST API 仕様

### 5.1 救助要請発行 (`POST /api/v1/dungeons/rescue/request`)

倒伏したプレイヤーが自身の墓標に対して救助要請を発行・公開します。

#### リクエスト (Request)
```json
{
  "userId": "user_victim_101",
  "tombstoneId": "ts_778899_01",
  "visibility": "PUBLIC",
  "rewardGold": 1000
}
```

#### レスポンス (Response: 200 OK)
```json
{
  "requestId": "req_rescue_99001",
  "tombstoneId": "ts_778899_01",
  "requesterUserId": "user_victim_101",
  "visibility": "PUBLIC",
  "rewardGold": 1000,
  "status": "OPEN",
  "createdAt": "2026-03-31T14:00:00Z"
}
```

---

### 5.2 アクティブ救助要請一覧照会 (`GET /api/v1/dungeons/rescue/active`)

公開されているアクティブな救助要請の一覧を取得します。

#### クエリパラメータ
- `visibility`: 公開範囲フィルタ (`PUBLIC`, `GUILD_FRIENDS`)
- `page`: ページ番号 (デフォルト: 0)
- `size`: 取得件数 (デフォルト: 20)

#### レスポンス (Response: 200 OK)
```json
{
  "requests": [
    {
      "requestId": "req_rescue_99001",
      "requesterName": "勇者アリス",
      "dungeonName": "試練の洞窟",
      "floorLevel": 7,
      "rewardGold": 1000,
      "createdAt": "2026-03-31T14:00:00Z"
    }
  ],
  "totalCount": 1,
  "page": 0,
  "size": 20
}
```

---

### 5.3 墓標浄化・救助完了申請 (`POST /api/v1/dungeons/rescue/claim`)

救助者が現地で墓標を浄化し、救助完了および報酬受取を確定します。

#### リクエスト (Request)
```json
{
  "rescuerUserId": "user_hero_202",
  "requestId": "req_rescue_99001",
  "tombstoneId": "ts_778899_01",
  "usedItemId": "rescue_scroll"
}
```

#### レスポンス (Response: 200 OK)
```json
{
  "requestId": "req_rescue_99001",
  "status": "RESCUED",
  "rescuerUserId": "user_hero_202",
  "rewardGoldEarned": 1000,
  "recoveredGoldToVictim": 1500,
  "recoveredItemCount": 3,
  "resolvedAt": "2026-03-31T14:30:00Z"
}
```

---

## 6. エラーハンドリング仕様

| エラーコード | HTTPステータス | 説明 | 発生条件 |
| :--- | :--- | :--- | :--- |
| `RESCUE_REQUEST_NOT_FOUND` | `404 Not Found` | 救助要請非存在 | 指定された `requestId` の救助要請が存在しない。 |
| `TOMBSTONE_NOT_FOUND` | `404 Not Found` | 墓標非存在 | 指定された `tombstoneId` の墓標が存在しない、またはすでに消滅している。 |
| `TOMBSTONE_ALREADY_PURIFIED` | `409 Conflict` | 二重救助エラー | 墓標がすでに他の救助者によって浄化・回収済みである。 |
| `INSUFFICIENT_RESCUE_FEE` | `400 Bad Request` | 預託金不足 | 救助要請発行に必要な預託ゴールドが不足している。 |
| `UNAUTHORIZED_RESCUER` | `403 Forbidden` | 救助権限エラー | 非公開設定の救助要請に対して権限のないプレイヤーが救助を試みた。 |
| `TOMBSTONE_EXPIRED` | `410 Gone` | 墓標有効期限切れ | 墓標の保持期限（48時間）が経過し、消滅している。 |
