# ダンジョン救助・墓標システム仕様 (Dungeon Rescue & Tombstone System)

## 1. 概要
本ドキュメントは、ダンジョン探索中にプレイヤーが倒脱（HPが0になり倒れる）した際、即座に全アイテム・ゴールドをロストするのではなく、該当フロア・座標に「墓標（Tombstone）」を生成し、他プレイヤーまたは自己の控えパーティによる救助活動（Rescue Run）を可能にする「ダンジョン救助・墓標システム」の仕様を定義します。

ローグライク特有の「死亡ペナルティの緊迫感」を維持しつつ、マルチプレイヤー間および自身の別ユニットを活用した非同期協力プレイと救済の体験を提供します。

---

## 2. 墓標生成と倒脱判定

### 2.1 倒脱時の処理フロー
1. **倒脱検知**: ダンジョン探索中にプレイヤーの HP が 0 に達した際、通常の死亡ペナルティ処理（ロスト判定）を一時保留します。
2. **墓標の配置**: プレイヤーが倒脱したフロアレベルおよび座標 `(x, y)` に、墓標オブジェクト（`TombstoneDomain`）を設置します。
3. **ロスト物品の保護保管**:
   - 倒脱時に所持していたインベントリ内のアイテム（装備含む）および所持ゴールドの 80% を墓標内に一時封印・保存します。
   - 残り 20% のゴールドおよび消費済みスタミナ・バフ状態は消失します。
4. **救助要請（Rescue Request）の自動発行**:
   - 倒脱したプレイヤーは「救助要請」を発行するか、「自力復活（全ロストを受け入れて帰還）」を選択します。
   - 救助要請を発行した場合、該当ダンジョンセッションは「救助待ち（`PENDING_RESCUE`）」状態となり、本人は拠点で救助を待つか、別キャラクター/別パーティで救助に向かうことができます。

### 2.2 墓標の有効期限と失効
- **有効期限**: 救助要請の発行から **72時間（実時間）** です。
- **要請失効**: 有効期限内に救助が達成されなかった場合、墓標は風化・破壊され、内部に封印されていたアイテム・ゴールドは完全にロストします。

---

## 3. 救助要請と救助隊派遣

### 3.1 救助要請の公開と受託
- 救助要請は BookOfAdventure モジュールおよび World モジュールを通じて掲示板（救助掲示板）へ公開されます。
- 他プレイヤーは救助要請一覧（`GET /api/v1/dungeons/rescue/requests/active`）から任意の要請を受託できます。
- 1つの救助要請を同時に受託できる救助者は **1名** のみです（重複受託の防止）。受託中は「救助進行中（`IN_PROGRESS`）」へステータスが遷移します。

### 3.2 救助隊のダンジョン潜入規則
- 救助者は、対象ダンジョンの該当フロアまで自力で到達するか、特殊アイテム「救助の巻物（`rescue_scroll`）」を使用して対象フロアへ直接ワープできます。
- 救助用フロアに到着すると、通常のモンスターに加えて墓標を守護する「墓標の守護霊（Tombstone Guardian）」が周囲 3x3 マスに自動発生します。

---

## 4. 守護霊戦闘と救助完了

### 4.1 守護霊（Guardian Spirit）の撃破
- 墓標に接近して救助（封印解除）を行うには、墓標の周りを囲む守護霊（倒脱したプレイヤーの階級・探索深度に応じたエリートモンスター）を全滅させる必要があります。
- 補助アイテム「供物のお香（`offering_incense`）」を墓標前で使用すると、守護霊の攻撃力・防御力を 30% 弱体化させることができます。

### 4.2 救助達成と報酬還元
1. **救助完了判定**: 守護霊を全滅させ、墓標へアクセスして救助アクション（`POST /api/v1/dungeons/rescue/execute`）を実行すると救助完了となります。
2. **被救助者への還元**:
   - 墓標に封印されていたアイテムおよびゴールド（80%分）が被救助者の保管箱/インベントリへ還元されます。
3. **救助者への報酬**:
   - 救助者には、難易度（ダンジョンランク・フロア深度）に応じた「救助ポイント（Rescue Points）」および「功績証（`merit_certificate`）」が授与されます。
   - 救助ポイントはショップで限定装備やレアアイテムと交換可能です。

---

## 5. 関連アイテムとドメイン拡張

### 5.1 救助関連アイテム（Objectsモジュール定義）
| typeId | 名称 | 種別 | 説明 |
| :--- | :--- | :--- | :--- |
| `rescue_scroll` | 救助の巻物 | 消費アイテム | 使用すると、受託中の救助対象が存在するフロアへ即座に移動する。 |
| `offering_incense` | 供物のお香 | 消費アイテム | 墓標の周囲で使用すると、墓標の守護霊を沈静化しステータスを30%低下させる。 |
| `tombstone_key` | 墓標の鍵 | 消費アイテム | 特殊な封印が施された高難易度ダンジョンの墓標を解呪・解封するための鍵。 |

### 5.2 ドメインモデル拡張

#### TombstoneDomain (Dungeon モジュール)
```json
{
  "tombstoneId": "ts_uuid_001",
  "dungeonId": "dungeon_cave_01",
  "floorLevel": 5,
  "position": { "x": 12, "y": 8 },
  "ownerUserId": "user_12345",
  "sealedItems": [
    { "typeId": "iron_sword", "quantity": 1, "metadata": { "atk": 15 } }
  ],
  "sealedGold": 1500,
  "guardianMonsterIds": ["guardian_ghost_01", "guardian_ghost_02"],
  "createdAt": "2026-03-30T10:00:00Z",
  "expiresAt": "2026-04-02T10:00:00Z",
  "isRescued": false
}
```

#### RescueRequestDomain (BookOfAdventure モジュール)
```json
{
  "requestId": "req_rescue_999",
  "tombstoneId": "ts_uuid_001",
  "ownerUserId": "user_12345",
  "rescuerUserId": "user_67890",
  "dungeonId": "dungeon_cave_01",
  "floorLevel": 5,
  "status": "IN_PROGRESS",
  "requestedAt": "2026-03-30T10:05:00Z",
  "completedAt": null
}
```

---

## 6. モジュール間データ連携シーケンス

```
[Player]             [Dungeon]          [BookOfAdventure]       [Objects]
   |                    |                       |                   |
   |--- HP=0 (倒脱) ---->|                       |                   |
   |                    |--- 墓標(Tombstone)生成 ->|                   |
   |                    |--- 80%資材封印 ------>|                   |
   |                    |                       |--- 救助要請登録 ->|
   |                    |                       |                   |
   |-- 救助要請受託(他者)------------------------>|                   |
   |                    |<-- 守護霊生成通知 ------|                   |
   |                    |                       |                   |
   |-- 守護霊撃破&救助 ->|                       |                   |
   |                    |--- 救助完了フラグ更新 ->|                   |
   |                    |                       |--- 封印資材返還 ->|
   |                    |                       |--- 救助報酬付与 ->|
```

---

## 7. REST API 仕様

### 7.1 救助要請の作成 (POST /api/v1/dungeons/rescue/requests)
倒脱したプレイヤーが救助要請を掲示板へ発行します。

#### リクエスト Payload
```json
{
  "userId": "user_12345",
  "dungeonId": "dungeon_cave_01",
  "floorLevel": 5,
  "tombstoneId": "ts_uuid_001",
  "message": "5階の墓標で守護霊に囲まれています！助けてください！"
}
```

#### レスポンス Payload (201 Created)
```json
{
  "requestId": "req_rescue_999",
  "tombstoneId": "ts_uuid_001",
  "ownerUserId": "user_12345",
  "status": "PENDING",
  "expiresAt": "2026-04-02T10:05:00Z",
  "createdAt": "2026-03-30T10:05:00Z"
}
```

### 7.2 アクティブな救助要請一覧の取得 (GET /api/v1/dungeons/rescue/requests/active)
救助可能なアクティブ要請の一覧を取得します。

#### クエリパラメータ
- `dungeonId` (optional): ダンジョンIDフィルタ
- `page` (optional, default: 0): ページ番号
- `size` (optional, default: 20): 取得件数

#### レスポンス Payload (200 OK)
```json
{
  "requests": [
    {
      "requestId": "req_rescue_999",
      "tombstoneId": "ts_uuid_001",
      "ownerUserId": "user_12345",
      "ownerName": "アリス",
      "dungeonId": "dungeon_cave_01",
      "floorLevel": 5,
      "status": "PENDING",
      "message": "5階の墓標で守護霊に囲まれています！助けてください！",
      "expiresAt": "2026-04-02T10:05:00Z"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

### 7.3 救助の実行 (POST /api/v1/dungeons/rescue/execute)
救助者が墓標に到達し守護霊撃破後に救助を完了させます。

#### リクエスト Payload
```json
{
  "rescuerUserId": "user_67890",
  "requestId": "req_rescue_999",
  "tombstoneId": "ts_uuid_001"
}
```

#### レスポンス Payload (200 OK)
```json
{
  "requestId": "req_rescue_999",
  "tombstoneId": "ts_uuid_001",
  "status": "COMPLETED",
  "rescuedUserId": "user_12345",
  "rescuerUserId": "user_67890",
  "rewardRescuePoints": 350,
  "rewardItems": [
    {
      "typeId": "merit_certificate",
      "quantity": 1
    }
  ],
  "completedAt": "2026-03-30T14:20:00Z"
}
```

### 7.4 救助報酬・資材の受け取り (POST /api/v1/dungeons/rescue/claim)
被救助者が救助完了後に封印資材を受け取ります。

#### リクエスト Payload
```json
{
  "userId": "user_12345",
  "requestId": "req_rescue_999"
}
```

#### レスポンス Payload (200 OK)
```json
{
  "requestId": "req_rescue_999",
  "recoveredGold": 1500,
  "recoveredItems": [
    {
      "typeId": "iron_sword",
      "quantity": 1,
      "metadata": { "atk": 15 }
    }
  ],
  "claimedAt": "2026-03-30T14:25:00Z"
}
```

---

## 8. エラーハンドリングとビジネスルール違反マッピング

| エラーコード | HTTP ステータス | 発生条件 | クライアント処置 |
| :--- | :--- | :--- | :--- |
| `PLAYER_NOT_DEFEATED` | `400 Bad Request` | 倒脱状態でないプレイヤーが救助要請を作成しようとした場合。 | プレイヤー状態を確認の上リクエストを中止。 |
| `RESCUE_REQUEST_NOT_FOUND` | `404 Not Found` | 指定された `requestId` の要請が存在しない場合。 | 要請IDの再確認・要請一覧の再取得。 |
| `TOMBSTONE_NOT_FOUND` | `404 Not Found` | 指定された `tombstoneId` の墓標が存在しない場合。 | 墓標の存在状況を再照会。 |
| `RESCUE_ALREADY_IN_PROGRESS` | `409 Conflict` | 他の救助者が既に受託中の救助要請を受託しようとした場合。 | 最新の要請一覧を取得して別の要請を選択。 |
| `GUARDIAN_NOT_DEFEATED` | `400 Bad Request` | 墓標周囲の守護霊（Guardian Spirit）が残存している状態で救助完了を実行しようとした場合。 | 周囲の守護霊を全て討伐後に再実行。 |
| `RESCUE_EXPIRED` | `410 Gone` | 72時間の有効期限が切れた救助要請・墓標へアクセスした場合。 | 要請一覧を最新化しダイアログ表示。 |
| `CANNOT_RESCUE_SELF` | `400 Bad Request` | 同一ユーザーのメインキャラクターで自身の要請を受託しようとした場合（※別サブパーティは可）。 | 救援パーティを変更するか他者に依頼。 |
| `UNAUTHORIZED_RESCUER` | `403 Forbidden` | 受託を行っていない第三者が救助実行 API を呼び出した場合。 | 受託処理（Accept）を完了させてから実行。 |
| `RECOVERY_ALREADY_CLAIMED` | `400 Bad Request` | 既に受け取り済みの救助結果から重ねて回収を行おうとした場合。 | 保管箱・インベントリの確認。 |
