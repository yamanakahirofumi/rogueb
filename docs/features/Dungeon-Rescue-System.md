# ダンジョン救助・墓標システム仕様書 (Dungeon Rescue & Tombstone System Specification)

## 1. 概要
「ダンジョン救助・墓標システム」は、プレイヤーがダンジョン探索中に力尽きた（倒伏した）際に、その位置に「墓標（Tombstone）」エンティティを動的に配置し、他のプレイヤーまたは自分自身（再挑戦時）による救助・遺品回収を可能とするシステムです。

救助依頼（Rescue Request）を発行することで、他プレイヤーのダンジョン内に墓標が出現し、墓標を保護する「守護霊（Guardian Spirit）」との戦闘や解放の儀式を経て、倒伏したプレイヤーの失われたゴールドや装備・アイテムを安全に回収・返還することができます。

---

## 2. コアメカニズム

### 2.1 倒伏と墓標の生成
1. **倒伏発生**:
   - プレイヤーの HP が 0 になった際、通常の死亡ペナルティ処理の前に「救助要請」の選択ダイアログが表示されます。
   - 救助要請を選択した場合、その階層の倒伏座標 $(x, y)$ に `TombstoneDomain` エンティティが生成され、`playerTombstoneDomain` コレクションへ永続化されます。
2. **遺品の保管**:
   - 倒伏時に所持していたゴールドの一部（または全額）およびインベントリ・装備アイテムのデータが墓標エンティティに封入されます。

### 2.2 救助依頼の発行と受付
1. **救助パスコード（レスキューコード）**:
   - 救助要請を発行すると、一意のレスキューコード（8桁の英数字）が生成されます。
   - プレイヤーはレスキューコードを他者に共有するか、公開救助掲示板（BookOfAdventureモジュール管理）へ登録します。
2. **救助者（レスキュープレイヤー）の受託**:
   - 救助者は同じダンジョンの該当階層に到達した際、墓標エンティティに遭遇します。

### 2.3 墓標の解放と守護霊戦
1. **守護霊（Guardian Spirit）**:
   - 墓標に接近すると、倒伏者のレベルや装備性能に応じた「守護霊」が出現し、戦闘が発生します。
   - 供養用アイテム「供養の香（`offering_incense`）」を消費することで、守護霊の攻撃力・防御力を低下させることができます。
2. **墓標の解錠**:
   - 守護霊を撃破するか、消費アイテム「墓標の鍵（`tombstone_key`）」を使用することで、墓標の封印が解除されます。
3. **成果の返還と報酬**:
   - 墓標解放成功時、倒伏者には「救助完了通知」が送信され、次回ログイン時に遺品が返還されます。
   - 救助者には「名誉ポイント」「救助感謝状アイテム」およびダンジョン評価ボーナスが与えられます。

---

## 3. ドメインモデル構造

### 3.1 TombstoneDomain (墓標エンティティ)
```json
{
  "tombstoneId": "ts_99823411_f05",
  "userId": "user_12345",
  "userName": "BraveExplorer",
  "dungeonId": "dungeon_ancient_ruins",
  "floorLevel": 5,
  "coordinate": {
    "x": 12,
    "y": 8
  },
  "storedGold": 1500,
  "storedItems": [
    {
      "instanceId": "item_sword_99",
      "typeId": "iron_sword",
      "quantity": 1
    }
  ],
  "guardianSpirit": {
    "monsterTypeId": "guardian_spirit_warrior",
    "level": 15,
    "hp": 250,
    "maxHp": 250,
    "attack": 45,
    "defense": 30
  },
  "status": "SEALED",
  "createdAt": "2026-03-30T10:00:00Z",
  "expiresAt": "2026-04-02T10:00:00Z"
}
```

### 3.2 RescueRequestDomain (救助依頼エンティティ)
```json
{
  "requestId": "req_881203",
  "rescueCode": "RC881203",
  "requesterUserId": "user_12345",
  "requesterName": "BraveExplorer",
  "dungeonId": "dungeon_ancient_ruins",
  "floorLevel": 5,
  "tombstoneId": "ts_99823411_f05",
  "status": "OPEN",
  "rescuerUserId": null,
  "createdAt": "2026-03-30T10:00:00Z",
  "completedAt": null
}
```

---

## 4. REST API 仕様

### 4.1 救助依頼発行 API
- **Endpoint**: `POST /api/v1/dungeons/rescue/request`
- **Request Body**:
```json
{
  "userId": "user_12345",
  "dungeonId": "dungeon_ancient_ruins",
  "floorLevel": 5,
  "coordinate": {
    "x": 12,
    "y": 8
  },
  "storedGold": 1500,
  "itemInstanceIds": ["item_sword_99"]
}
```
- **Response Body (201 Created)**:
```json
{
  "requestId": "req_881203",
  "rescueCode": "RC881203",
  "tombstoneId": "ts_99823411_f05",
  "status": "OPEN",
  "createdAt": "2026-03-30T10:00:00Z"
}
```

### 4.2 救助依頼検索・取得 API
- **Endpoint**: `GET /api/v1/dungeons/rescue/requests/{rescueCode}`
- **Response Body (200 OK)**:
```json
{
  "requestId": "req_881203",
  "rescueCode": "RC881203",
  "requesterName": "BraveExplorer",
  "dungeonId": "dungeon_ancient_ruins",
  "floorLevel": 5,
  "status": "OPEN",
  "createdAt": "2026-03-30T10:00:00Z"
}
```

### 4.3 墓標解放・救助実行 API
- **Endpoint**: `POST /api/v1/dungeons/rescue/unlock`
- **Request Body**:
```json
{
  "rescuerUserId": "user_67890",
  "tombstoneId": "ts_99823411_f05",
  "usedItemId": "tombstone_key"
}
```
- **Response Body (200 OK)**:
```json
{
  "tombstoneId": "ts_99823411_f05",
  "status": "RESCUED",
  "recoveredGold": 1500,
  "recoveredItemCount": 1,
  "rewardGold": 300,
  "rescuerHonorPoints": 50,
  "message": "墓標の封印を解き、救助に成功しました。"
}
```

---

## 5. エラーハンドリング仕様

| エラーコード | HTTPステータス | 原因・発生条件 | 補足・対処 |
| :--- | :--- | :--- | :--- |
| `RESCUE_REQUEST_NOT_FOUND` | `404 Not Found` | 指定されたレスキューコードまたは `requestId` が存在しない | コード入力ミスの確認 |
| `TOMBSTONE_NOT_FOUND` | `404 Not Found` | 指定された `tombstoneId` が存在しない | 既に解放済みまたは期限切れ |
| `TOMBSTONE_ALREADY_UNLOCKED` | `400 Bad Request` | 既に解放・救助完了済みの墓標に対して解放APIを呼び出した | 解放済みステータスを返却 |
| `RESCUE_EXPIRED` | `400 Bad Request` | 救助有効期限（例: 72時間）を超過した | 遺品の一部が散逸した状態で通常死亡処理へ移行 |
| `GUARDIAN_SPIRIT_ALIVE` | `400 Bad Request` | 守護霊を討伐せず、かつ鍵アイテムも未所持で墓標を解錠しようとした | 守護霊戦を促すメッセージ表示 |
| `SELF_RESCUE_NOT_ALLOWED` | `400 Bad Request` | 自分の救助依頼を他者救助モードで解放しようとした | 再挑戦自力回収モードを利用 |

---

## 6. モジュール間データ連携フロー

```
[Player / Dungeon]
       │
       ├─ (倒伏発生) ──> POST /api/v1/dungeons/rescue/request ──> [BookOfAdventure / Dungeon]
       │                                                                  │
       │                                                       (墓標・依頼の永続化)
       │                                                                  │
[Rescuer Player]                                                          ▼
       ├─ (階層到達・墓標遭遇) ──> GET /api/v1/dungeons/rescue/requests ──┤
       │                                                                  │
       ├─ (守護霊撃破 or 鍵使用) ──> POST /api/v1/dungeons/rescue/unlock ─┘
       │                                                                  │
       ▼                                                                  ▼
[救助報酬獲得] <───────────────────────────────────────────────── [倒伏者へ遺品返還通知]
```
