# リアルタイム同期プロトコル (Real-time Synchronization Protocol)

## 1. 概要
本ドキュメントは、RogueB におけるマルチプレイヤー間のリアルタイムな状態同期を実現するための通信プロトコルを定義します。本作はターンベースの要素を持ちつつも、複数のプレイヤーや管理者が同一のダンジョンに干渉するため、Server-Sent Events (SSE) を用いた非同期な情報配信を行います。

## 2. 通信プロトコル (SSE: Server-Sent Events)
クライアントは各サービスのエンドポイントに対して接続を確立し、イベントのストリームを受信します。サーバーからクライアントへの単方向通信となります。

- **エンドポイント例**:
  - `PlayerOperations`: `/api/stream/player/{userId}` （プレイヤー自身の周囲のイベント）
  - `Dungeon`: `/api/stream/dungeon/{dungeonId}` （ダンジョン全体のイベント、管理者用）
- **データ形式**: `application/x-ndjson` または `text/event-stream` 形式。

## 3. イベントの種類 (Event Types)
ストリームを通じて送信される各イベントは、`type` フィールドによって識別されます。配信されるイベントは以下のカテゴリに分類されます。

### 3.1 座標更新 (LOCATION_UPDATE)
視界内のエンティティ（他のプレイヤー、モンスター、NPC）の移動を通知します。

- **データ構造例**:
  ```json
  {
    "type": "LOCATION_UPDATE",
    "entityId": "monster-789",
    "entityType": "MONSTER",
    "coordinate": { "x": 11, "y": 5 },
    "from": { "x": 10, "y": 5 },
    "direction": "RIGHT"
  }
  ```

### 3.2 戦闘イベント (COMBAT_EVENT)
攻撃、ダメージ、回避、スキル発動などの戦闘行動を通知します。

- **データ構造例**:
  ```json
  {
    "type": "COMBAT_EVENT",
    "sourceId": "player-123",
    "targetId": "monster-456",
    "actionType": "NORMAL_ATTACK",
    "value": 15,
    "isCritical": false,
    "message": "Hero の攻撃！ スライムに 15 ダメージを与えた。"
  }
  ```

### 3.3 状態変化 (STATUS_CHANGE)
状態異常の付与、解除、レベルアップ、HP/MP の変動を通知します。

- **データ構造例**:
  ```json
  {
    "type": "STATUS_CHANGE",
    "entityId": "player-123",
    "changeType": "HP_CHANGE",
    "value": 85
  }
  ```

### 3.4 環境変化 (ENVIRONMENT_UPDATE)
アイテムの出現・消失、トラップの露出、扉の開閉、階段の出現を通知します。

- **データ構造例**:
  ```json
  {
    "type": "ENVIRONMENT_UPDATE",
    "coordinate": { "x": 12, "y": 8 },
    "changeType": "TRAP_REVEAL",
    "objectId": "trap-001",
    "description": "落とし穴の罠が姿を現した！"
  }
  ```

### 3.5 管理者メッセージ (ADMIN_MESSAGE)
ダンジョン管理者からの介入メッセージ（天の声）を通知します。

- **データ構造例**:
  ```json
  {
    "type": "ADMIN_MESSAGE",
    "adminId": "admin-user-01",
    "message": "勇者よ、そこから先は危険だぞ...",
    "timestamp": 1698393600000
  }
  ```
- **用途**: [ダンジョン構築・運営システム](../features/Dungeon-Construction-System.md) における管理者介入アクション。

## 4. 配信スコープとフィルタリング
サーバー負荷の軽減と、ゲーム上の「視界（Fog of War）」を維持するため、イベントの配信は動的にフィルタリングされます。

### 4.1 視界ベースの配信 (Vision-based Broadcasting)
- **ルール**: 各プレイヤーには、自身の現在の「視界範囲内」で発生したイベントのみが送信されます。
- **視界範囲**: 原則として自身の座標を中心とした 15x15 マス（半径 7 マス）程度とします。
- **管理者の例外**: ダンジョンの管理者は、自身のダンジョン内のすべてのイベントを受信可能です。

### 4.2 フロア内配信
- 階段移動など、フロア全体に影響するイベントは、同一フロアに滞在している全プレイヤーに配信されます。

## 5. 信頼性と接続管理 (Reliability and Connection Management)
- **キープアライブ**: 接続維持のため、30 秒ごとに空のコメント（`:heartbeat`）または `PING` イベントを送信します。
- **再接続 (Reconnection)**: クライアントは切断を検知した場合、即座に再接続を試みます。SSE の標準仕様に基づき、`Last-Event-ID` ヘッダーを用いて未受信のイベントを補完することが可能です。再接続時には、最新のフロア状態（スナップショット）を再取得して状態を同期します。

## 6. 実装上の考慮事項 (Implementation Considerations)
- **負荷分散**: SSE は長時間接続を維持するため、サーバー側の同時接続数制限に注意が必要です。
- **権限管理**: プレイヤーは自身の視認範囲内のイベントのみを受信できるように、サーバー側でフィルタリングを行います。
- **BFF (PlayerOperations) の責務**:
  - 各ドメインサービスから発生したイベントを購読 (Subscribe) し、適切なプレイヤーへルーティングするハブとして機能します。
  - リアクティブストリーム (`Flux`) を用いて、複数のソースからのイベントを統合してクライアントへストリームします。

## 7. 関連ドキュメント
- [ダンジョン構築・運営システム](../features/Dungeon-Construction-System.md)
- [戦闘システム](../features/Combat-System.md)
- [UI/UX デザイン](../features/UI-UX-Design.md)
