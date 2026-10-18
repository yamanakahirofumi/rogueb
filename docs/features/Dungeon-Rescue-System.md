# ダンジョン救助・墓標システム仕様

本ドキュメントでは、ダンジョン探索中にプレイヤーが倒伏（HPが0）した際に発生する「墓標生成」と、他のプレイヤーや救助隊に救助を要請する「救助依頼システム」の詳細な仕様について定義します。

---

## 1. システム概要

「ダンジョン救助・墓標システム（Dungeon Rescue & Tombstone System）」は、ダンジョン内で力尽きたプレイヤー（倒伏者）が、所持品やゴールドの全ロストを回避するために他のプレイヤーへ救助を依頼し、他のプレイヤーがそのダンジョンフロアへ潜入して倒伏ポイントに存在する「墓標（Tombstone）」を解放・救助する協力型コンテンツです。

### 1.1 主な特徴
- **墓標（Tombstone）の設置**: プレイヤーが倒伏した座標に `TombstoneEntity` が設置され、ロスト対象のアイテムやゴールドが保護・保管されます。
- **救助依頼（Rescue Request）**: 倒伏者は全倒伏通知板（掲示板）またはフレンド向けに救助依頼を発行できます。
- **守護霊（Guardian Spirit）バトル**: 墓標には倒伏者の残留念が変化した「守護霊モンスター」が憑依している場合があり、救助者は戦闘勝利または特定の供養アイテムによって墓標を浄化・救助します。
- **双方の報酬・メリット**:
  - **被救助者**: ロスト予定だった所持アイテムおよびゴールドの全額（または大部分）を回収可能。
  - **救助者**: 救助勲章（Rescue Medal）、大量の名誉ポイント（Honor Points）、およびダンジョン構築/運営素材を獲得可能。

---

## 2. 墓標生成と救助依頼メカニズム

### 2.1 墓標生成ルール
1. **倒伏時の判定**: プレイヤーの HP が 0 に達した際、通常のデスペナルティ処理が中断され、「救助依頼を発行する」または「そのまま脱出してペナルティを受ける」を選択します。
2. **アイテム/ゴールドの保護**: 選択時、インベントリ内のアイテム（装備含む）および所持ゴールドが `playerTombstoneDomain` 内にエンカプセル封入されます。
3. **墓標の配置**: 倒伏したダンジョン ID、フロア階層、(X, Y) 座標に墓標エンティティが生成され、該当フロアの `DisplayData` 上に墓標アイコンがレンダリングされます。

### 2.2 救助依頼のライフサイクル
救助依頼（`RescueRequestDomain`）は以下のステータスを持ちます。

| ステータスコード | 名称 | 説明 |
| :--- | :--- | :--- |
| `OPEN` | 依頼中 | 救助掲示板に公開され、誰もが探索可能な状態。 |
| `IN_PROGRESS` | 救助進行中 | 他のプレイヤーが該当ダンジョンフロアへ救助目的で潜入中の状態。 |
| `RESCUED` | 救助完了 | 救助者が墓標を浄化・到達し、救助が成功した状態。 |
| `EXPIRED` | 期限切れ | リアルタイムで48時間が経過し、自動的に死亡ペナルティが確定した状態。 |
| `CANCELLED` | 辞退/キャンセル | 倒伏者本人が救助を諦め、あきらめて脱出した状態。 |

---

## 3. 救助探索と守護霊バトル

### 3.1 救助探索の流れ
1. **依頼受諾**: 救助掲示板（`GET /api/v1/dungeons/rescues/requests`）からアクティブな救助依頼を受諾します。
2. **フロア到達**: 該当するダンジョン・フロアへ進入します。「救助の巻物（`rescue_scroll`）」を使用することで、墓標の存在する階層へ直接転送（テレポート）することも可能です。
3. **墓標との接触**: 墓標タイルに隣接・到達することで救助アクションが実行可能になります。

### 3.2 守護霊バトルと浄化アイテム
墓標の周囲には、階層難易度に応じた「守護霊（Guardian Spirit）」が配置されます。

- **通常戦闘**: 守護霊モンスターを撃破することで墓標の封印が解かれます。
- **供養の香（`offering_incense`）**: 戦闘前に使用することで守護霊を鎮静化させ、戦闘なしで即座に救助を完了できます。
- **墓標のカギ（`tombstone_key`）**: 強力な封印が施された高難易度墓標を一発で解除可能です。

---

## 4. ドメインモデル構造

### 4.1 墓標ドメイン（`TombstoneDomain`）
- `tombstoneId` (String): 墓標のユニークID
- `dungeonId` (String): 設置対象ダンジョンID
- `floorLevel` (int): 設置フロア階層
- `position` (Coordinate2D): 設置座標
- `ownerUserId` (String): 倒伏したプレイヤーのユーザーID
- `storedItems` (List<ThingInstance>): 保管されている所持アイテムリスト
- `storedGold` (long): 保管されている所持ゴールド
- `guardianSpiritType` (String): 守護霊モンスター種族ID
- `isGuarded` (boolean): 守護霊が健在か否か
- `createdAt` (LocalDateTime): 墓標生成日時

### 4.2 救助依頼ドメイン（`RescueRequestDomain`）
- `requestId` (String): 救助依頼ID
- `tombstoneId` (String): 関連付けられた墓標ID
- `requesterUserId` (String): 依頼者（倒伏者）ID
- `rescuerUserId` (String): 受諾者（救助者）ID（未受諾時は null）
- `status` (RescueStatusEnum): 依頼ステータス (`OPEN`, `IN_PROGRESS`, `RESCUED`, `EXPIRED`, `CANCELLED`)
- `rewardMedals` (int): 成功時に救助者が獲得する救助勲章数
- `expiresAt` (LocalDateTime): 依頼有効期限

---

## 5. モジュール間連携フロー

1. **Combat -> Dungeon / BookOfAdventure**: プレイヤー HP 0 判定時に `TombstoneDomain` および `RescueRequestDomain` を作成。
2. **Display -> Player**: 該当フロアの `DisplayData` オーバーレイレイヤーに墓標オブジェクトを描画。
3. **Dungeon -> Combat**: 救助者が墓標タイルに進入した際、守護霊バトルをトリガー。
4. **BookOfAdventure / Objects**: 救助成功時、BookOfAdventure 経由で倒伏者のポスト/インベントリへアイテム・ゴールドを返還し、救助者へ報酬（勲章・名誉pt）を付与。

---

## 6. REST API 仕様

### 6.1 救助依頼一覧照会
- **エンドポイント**: `GET /api/v1/dungeons/rescues/requests`
- **レスポンス**:
```json
{
  "requests": [
    {
      "requestId": "req_882319",
      "tombstoneId": "tomb_99120",
      "requesterUserId": "usr_alpha",
      "dungeonId": "dungeon_ancient_catacomb",
      "floorLevel": 12,
      "rewardMedals": 50,
      "status": "OPEN",
      "expiresAt": "2026-03-31T15:00:00Z"
    }
  ]
}
```

### 6.2 救助依頼発行
- **エンドポイント**: `POST /api/v1/dungeons/rescues/request`
- **リクエスト**:
```json
{
  "userId": "usr_alpha",
  "dungeonId": "dungeon_ancient_catacomb",
  "floorLevel": 12,
  "position": { "x": 14, "y": 8 }
}
```
- **レスポンス**:
```json
{
  "requestId": "req_882319",
  "tombstoneId": "tomb_99120",
  "status": "OPEN",
  "message": "Rescue request created successfully."
}
```

### 6.3 救助実行・墓標解放
- **エンドポイント**: `POST /api/v1/dungeons/rescues/attempt`
- **リクエスト**:
```json
{
  "rescuerUserId": "usr_beta",
  "requestId": "req_882319",
  "useItemId": "offering_incense"
}
```
- **レスポンス**:
```json
{
  "requestId": "req_882319",
  "status": "RESCUED",
  "rewardMedalsEarned": 50,
  "honorPointsEarned": 120,
  "message": "Player rescued successfully!"
}
```

---

## 7. エラーハンドリング仕様

| エラーコード | HTTP ステータス | 発生条件 |
| :--- | :--- | :--- |
| `RESCUE_REQUEST_NOT_FOUND` | `404 Not Found` | 指定された `requestId` の救助依頼が存在しない場合。 |
| `TOMBSTONE_NOT_FOUND` | `404 Not Found` | 該当する墓標エンティティが存在しない場合。 |
| `ALREADY_RESCUED` | `400 Bad Request` | 既に他の救助者によって救助が完了している場合。 |
| `RESCUE_SELF_NOT_ALLOWED` | `400 Bad Request` | 自分自身の救助依頼を受諾・実行しようとした場合。 |
| `GUARDIAN_SPIRIT_ALREADY_ACTIVE` | `409 Conflict` | 守護霊が未撃破であり、無効化アイテムなしで解放を試みた場合。 |
| `RESCUE_TIMEOUT_EXPIRED` | `410 Gone` | 救助依頼の有効期限（48時間）が切れている場合。 |
| `INSUFFICIENT_RESCUE_ITEM` | `400 Bad Request` | 指定された救助アイテム（供養の香等）を所持していない場合。 |
