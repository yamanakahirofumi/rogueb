# ダンジョン救助・墓標システム仕様 (Dungeon Rescue & Tombstone System)

## 1. 概要
本仕様は、ダンジョン探索中にプレイヤーが倒没（HPが0）した際に発生する「墓標（Tombstone）」の生成、遺品およびゴールドの保護、他プレイヤーによる救助要請（Rescue Request）、遺品供養・ガーディアン怨霊戦、および救助成功による成果物の還元メカニズムを定義するものです。

マルチプレイヤーおよびクロスワールド環境において、他プレイヤーとの非同期・リアルタイム協力プレイ体験を提供し、倒没時の全ロストリスクを緩和するゲームエコシステムを構築します。

---

## 2. システムメカニズム

### 2.1 倒没と墓標生成 (Collapse & Tombstone Generation)
1. **倒没の検知**:
   - ダンジョン内でプレイヤーの HP が 0 に達した際、通常死亡処理（アイテム・ゴールドの全ロスト）の直前に救助・墓標判定が行われます。
2. **墓標 (Tombstone) の設置**:
   - 倒没した階層（`floorLevel`）および座標（`Coordinate2D`）に、個体エンティティとして `Tombstone` が生成されます。
   - 墓標には倒没時に所持していたインベントリ内のアイテム（一部保護品を除く）およびゴールド（標準 80%）が封入・保持されます。
3. **ガーディアン怨霊の出現**:
   - 墓標生成と同時に、倒没者の怨念やダンジョンの瘴気が実体化したガーディアンモンスター（`Ghost` / `Wight` / `Revenant`）が墓標の周囲 8 タイル内に自動スポーンします。

### 2.2 救助要請と救助受諾 (Rescue Requests & Acceptance)
1. **救助要請の発行**:
   - 倒没したプレイヤーは、街への復帰画面または `BookOfAdventure` から「救助要請」を発行可能です。
   - 救助要請は有効期限（標準: 72時間）を持ち、アクティブな救助要請として公開されます。
2. **救助の受諾**:
   - 他プレイヤー（救助者）は、掲示板または API 経由でアクティブな救助要請を受諾できます。
   - 1 つの救助要請に対して同時に受諾できる救助者は 1 名のみです（受諾後一定時間内に達成されない場合は自動キャンセルされ他者に開放）。

### 2.3 救助実行とガーディアン攻略 (Execution & Guardian Combat)
救助者は、対象ダンジョンの該当フロアに到達し、以下のいずれかの方法で墓標を解放・救助できます。

- **戦闘による解放**: 墓標を守るガーディアン怨霊を撃破することで、墓標の封印が解けます。
- **救助の巻物 (`rescue_scroll`) の使用**: 墓標に隣接して専用消費アイテムを使用することで、無戦闘で即座に救助を完了させます。
- **供養の線香 (`offering_incense`) の使用**: ガーディアンを弱体化・鎮魂させ、遺品回収率（`recoveryRate`）を最大 100% まで向上させます。

### 2.4 成果物の還元と報酬 (Recovery & Savior Rewards)
1. **倒没者への還元**:
   - 救助成功時、墓標に封入されていたアイテムおよびゴールドが倒没者の `BookOfAdventure` 宅配ボックス（Inbox）へ即座に配送されます。
2. **救助者（救世主）への報酬**:
   - 救助者は、感謝の印として「救助ポイント（Rescue Points）」、カルマ値（Karma）、および特別アイテム「救助者の証（Savior Token）」を獲得します。
   - 獲得した救助ポイントは、専用ショップで希少な強化触媒や限定称号と交換可能です。

---

## 3. ドメインモデル仕様

### 3.1 墓標エンティティ (`TombstoneDomain`)
| 属性名 | 型 | 説明 |
| :--- | :--- | :--- |
| `tombstoneId` | String | 墓標の一意な識別子 (UUID) |
| `fallenPlayerId` | String | 倒没したプレイヤーの ID |
| `dungeonId` | String | 墓標が存在するダンジョンの ID |
| `floorLevel` | Integer | 墓標が存在する階層レベル |
| `coordinate` | Coordinate2D | 墓標の設置座標 `(x, y)` |
| `lostItems` | List\<ThingInstanceDomain\> | 墓標に封入されている遺品アイテムリスト |
| `lostGold` | Long | 墓標に封入されている所持ゴールド |
| `guardianMonsterTypeId` | String | スポーンしたガーディアンのモンスター種族 ID |
| `status` | Enum | 状態 (`ACTIVE`, `IN_RESCUE`, `RESCUED`, `EXPIRED`) |
| `recoveryRate` | Double | 遺品還元率 (0.5 ～ 1.0) |
| `createdAt` | Instant | 墓標の生成日時 |
| `expiresAt` | Instant | 墓標の消滅・期限切れ日時 |

### 3.2 救助要請ドメイン (`RescueRequestDomain`)
| 属性名 | 型 | 説明 |
| :--- | :--- | :--- |
| `requestId` | String | 救助要請の一意な識別子 (UUID) |
| `tombstoneId` | String | 関連付けられた墓標の ID |
| `fallenPlayerId` | String | 倒没したプレイヤーの ID |
| `rescuerPlayerId` | String | 受諾した救助者の ID (未受諾時は null) |
| `status` | Enum | 要請状態 (`PENDING`, `ACCEPTED`, `COMPLETED`, `FAILED`, `EXPIRED`) |
| `requestedAt` | Instant | 要請発行日時 |
| `completedAt` | Instant | 救助完了日時 |

---

## 4. API 仕様

### 4.1 救助要請発行 API (`POST /api/v1/dungeons/rescue/request`)

倒没したプレイヤーが自身の墓標に対する救助要請を発行します。

#### リクエスト Payload 例
```json
{
  "userId": "usr_99887766",
  "tombstoneId": "tomb_11223344",
  "message": "15階の墓標で怨霊に囲まれています！救助をお願いします。"
}
```

#### レスポンス Payload 例 (201 Created)
```json
{
  "success": true,
  "requestId": "res_req_55667788",
  "tombstoneId": "tomb_11223344",
  "status": "PENDING",
  "expiresAt": "2026-03-30T12:00:00Z",
  "message": "救助要請を受理し、公開掲示板へ配信しました。"
}
```

---

### 4.2 アクティブ救助要請一覧照会 API (`GET /api/v1/dungeons/rescue/active`)

世界全体の公開救助要請、または特定ダンジョン内のアクティブな墓標一覧を取得します。

#### クエリパラメータ
- `dungeonId` (任意): ダンジョン ID でフィルタ
- `page` (任意): ページ番号 (デフォルト: 0)
- `size` (任意): 取得件数 (デフォルト: 20)

#### レスポンス Payload 例 (200 OK)
```json
{
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "requests": [
    {
      "requestId": "res_req_55667788",
      "tombstoneId": "tomb_11223344",
      "fallenPlayerId": "usr_99887766",
      "fallenPlayerName": "シレン",
      "dungeonId": "dungeon_ancient_ruins",
      "floorLevel": 15,
      "lostGold": 4500,
      "lostItemCount": 8,
      "message": "15階の墓標で怨霊に囲まれています！救助をお願いします。",
      "guardianMonsterTypeId": "mon_ghost_revenant",
      "status": "PENDING",
      "requestedAt": "2026-03-27T12:00:00Z",
      "expiresAt": "2026-03-30T12:00:00Z"
    }
  ]
}
```

---

### 4.3 救助要請受諾 API (`POST /api/v1/dungeons/rescue/accept`)

救助者が特定の救助要請を受諾し、救助ミッションを開始します。

#### リクエスト Payload 例
```json
{
  "rescuerUserId": "usr_11223344",
  "requestId": "res_req_55667788"
}
```

#### レスポンス Payload 例 (200 OK)
```json
{
  "success": true,
  "requestId": "res_req_55667788",
  "rescuerUserId": "usr_11223344",
  "status": "ACCEPTED",
  "acceptedAt": "2026-03-27T13:00:00Z",
  "targetDungeonId": "dungeon_ancient_ruins",
  "targetFloorLevel": 15,
  "message": "救助ミッションを開始しました。対象フロアへ向かってください。"
}
```

---

### 4.4 救助完了・遺品還元 API (`POST /api/v1/dungeons/rescue/claim`)

墓標に到達しガーディアン撃破または専用アイテム使用により救助を完了させます。

#### リクエスト Payload 例
```json
{
  "rescuerUserId": "usr_11223344",
  "tombstoneId": "tomb_11223344",
  "method": "ITEM_USE",
  "usedItemId": "item_inst_rescue_scroll_01"
}
```

#### レスポンス Payload 例 (200 OK)
```json
{
  "success": true,
  "tombstoneId": "tomb_11223344",
  "fallenPlayerId": "usr_99887766",
  "status": "RESCUED",
  "recoveredItemsCount": 8,
  "recoveredGold": 4500,
  "rescuerRewards": {
    "rescuePointsGained": 150,
    "karmaGained": 20,
    "tokenGained": "savior_token_bronze"
  },
  "completedAt": "2026-03-27T14:30:00Z",
  "message": "救助が正常に完了しました。倒没者の宅配ボックスへ遺品を配送しました。"
}
```

---

## 5. ビジネスルールエラーハンドリング仕様

| エラーコード | HTTPステータス | 原因・発生条件 | UI表示・対処方法 |
| :--- | :--- | :--- | :--- |
| `TOMBSTONE_NOT_FOUND` | `404 Not Found` | 指定された `tombstoneId` の墓標が存在しない、または既に削除されている。 | トースト: 「対象の墓標が存在しません。」 |
| `RESCUE_REQUEST_NOT_FOUND` | `404 Not Found` | 指定された `requestId` の救助要請が存在しない。 | ダイアログ: 「該当の救助要請は見つかりませんでした。」 |
| `RESCUE_ALREADY_IN_PROGRESS` | `409 Conflict` | 対象の救助要請が既に他のプレイヤーによって受諾されている。 | ダイアログ: 「この救助要請は他の救世主が受諾済みです。」 |
| `RESCUE_ALREADY_COMPLETED` | `409 Conflict` | 対象の墓標は既に救助完了している。 | トースト: 「この墓標は既に救助・供養されています。」 |
| `RESCUE_EXPIRED` | `410 Gone` | 救助要請の有効期限（72時間）が経過し、失効している。 | ダイアログ: 「救助要請の期限が切れています。」 |
| `CANNOT_RESCUE_SELF` | `422 Unprocessable Entity` | プレイヤーが自分自身の墓標に対して救助受諾を行おうとした。 | トースト: 「自身の墓標を自身で救助することはできません。」 |
| `INSUFFICIENT_RESCUE_ITEM` | `422 Unprocessable Entity` | `ITEM_USE` 方式での完了指定時に必要なアイテム (`rescue_scroll` 等) を所有していない。 | トースト: 「救助に必要なアイテムを所有していません。」 |
| `GUARDIAN_NOT_DEFEATED` | `422 Unprocessable Entity` | ガーディアン怨霊が生存した状態で `COMBAT` 方式の救助を試みた。 | ダイアログ: 「周囲のガーディアン怨霊を全て撃破する必要があります。」 |
| `UNAUTHORIZED_RESCUER` | `403 Forbidden` | 他者が受諾中の救助ミッションに対して完了申請を行った。 | トースト: 「この救助ミッションを行う権限がありません。」 |

---

## 6. シーケンスフロー

```
[Fallen Player] -> [PlayerOperations/Dungeon]: 倒没 (HP=0)
[Dungeon] -> [Dungeon-MongoDB]: playerTombstoneDomain 作成
[Fallen Player] -> [Rescue API]: POST /rescue/request
[Rescue System] -> [World/SSE]: イベント通知 (RESCUE_REQUEST_BROADCAST)
[Rescuer Player] -> [Rescue API]: POST /rescue/accept
[Rescuer Player] -> [Dungeon]: フロア到達・ガーディアン撃破/アイテム使用
[Rescuer Player] -> [Rescue API]: POST /rescue/claim
[Rescue System] -> [BookOfAdventure]: 倒没者宅配ボックスへ遺品配送
[Rescue System] -> [BookOfAdventure]: 救助者へ RescuePoints & Rewards 付与
```
