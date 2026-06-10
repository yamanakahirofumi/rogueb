# リアルタイム同期システム (Real-time Synchronization System)

## 1. 概要
本ドキュメントは、サーバー側で発生したイベント（戦闘結果、ダンジョン状態の変化、管理者の介入など）をクライアントへリアルタイムに通知するための同期メカニズムの仕様を定義します。本システムは、HTTP 接続を維持したままサーバーからデータをプッシュする **SSE (Server-Sent Events)** を主軸に構成されます。

## 2. 通信プロトコル (SSE: Server-Sent Events)
クライアントは各サービスのエンドポイントに対して接続を確立し、イベントのストリームを受信します。

- **エンドポイント例**:
  - `PlayerOperations`: `/api/stream/player/{userId}` （プレイヤー自身の周囲のイベント）
  - `Dungeon`: `/api/stream/dungeon/{dungeonId}` （ダンジョン全体のイベント、管理者用）

## 3. イベント型とデータ構造
ストリームを通じて送信される各イベントは、`type` フィールドによって識別されます。

### 3.1 管理者メッセージ (`ADMIN_MESSAGE`)
ダンジョン管理者がプレイヤーに対して直接送信する「天の声」です。

- **データ構造**:
  ```json
  {
    "type": "ADMIN_MESSAGE",
    "adminId": "admin-user-01",
    "message": "勇者よ、そこから先は危険だぞ...",
    "timestamp": 1698393600000
  }
  ```
- **用途**: [ダンジョン構築・運営システム](./Dungeon-Construction-System.md) における管理者介入アクション。

### 3.2 戦闘ログイベント (`COMBAT_LOG`)
自分や周囲で行われた戦闘の結果を通知します。

- **データ構造**:
  ```json
  {
    "type": "COMBAT_LOG",
    "sourceId": "player-123",
    "targetId": "monster-456",
    "action": "ATTACK",
    "damage": 15,
    "isCritical": false,
    "message": "Hero の攻撃！ スライムに 15 ダメージを与えた。"
  }
  ```

### 3.3 座標更新イベント (`POSITION_UPDATE`)
視界内の他のエンティティ（モンスター、他プレイヤー）の移動を通知します。

- **データ構造**:
  ```json
  {
    "type": "POSITION_UPDATE",
    "instanceId": "monster-789",
    "from": { "x": 10, "y": 5 },
    "to": { "x": 11, "y": 5 }
  }
  ```

### 3.4 ダンジョン環境変化 (`ENVIRONMENT_CHANGE`)
トラップの作動や地形の変化などを通知します。

- **データ構造**:
  ```json
  {
    "type": "ENVIRONMENT_CHANGE",
    "position": { "x": 12, "y": 8 },
    "changeType": "TRAP_REVEALED",
    "description": "落とし穴の罠が姿を現した！"
  }
  ```

## 4. 信頼性と接続管理
- **キープアライブ**: 接続維持のため、30 秒ごとに空のコメント（`:heartbeat`）または `PING` イベントを送信します。
- **再接続**: クライアントは切断を検知した場合、自動的に再接続を試みます。SSE の標準仕様に基づき、`Last-Event-ID` ヘッダーを用いて未受信のイベントを補完することが可能です。

## 5. 実装上の考慮事項
- **負荷分散**: SSE は長時間接続を維持するため、サーバー側の同時接続数制限に注意が必要です。
- **権限管理**: プレイヤーは自身の視認範囲内のイベントのみを受信できるように、サーバー側でフィルタリングを行います。一方、管理者は自身のダンジョン内の全イベントを受信可能です。

## 6. 関連ドキュメント
- [ダンジョン構築・運営システム](./Dungeon-Construction-System.md)
- [戦闘システム](./Combat-System.md)
- [UI/UX デザイン](./UI-UX-Design.md)
