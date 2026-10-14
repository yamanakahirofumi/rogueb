# 倉庫システム MongoDBデータ構造

このドキュメントは、**Storage**（倉庫）システムに関連するドメインオブジェクトがMongoDBにどのように永続化されるかについて説明します。

## 1. `playerStorageDomain` コレクション
- **説明:** `PlayerStorageDomain`クラスに対応します。プレイヤー個別の倉庫の状態を保持します。
- **フィールド:**
    - `_id` (String): 倉庫の一意な識別子。
    - `playerId` (String): 所有しているプレイヤーのID。
    - `objectIdList` (Array): 保管されているアイテムのインスタンスIDの配列。
    - `limitSize` (Integer): 倉庫に保管可能なアイテムの最大数（初期値: 50、最大: 200）。
    - `expansionStage` (Integer): 倉庫の拡張段階（0〜5）。
    - `updateDate` (String): 最終更新日時（ISO-8601形式）。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## 2. `storageLogDomain` コレクション
- **説明:** 倉庫における各種操作（入庫・出庫・拡張）の履歴および監査ログを管理します。
- **フィールド:**
    - `_id` (String): ログの一意な識別子（UUID等）。
    - `playerId` (String): 操作を行ったプレイヤーのID。
    - `actionType` (String): 操作種別（`DEPOSIT`, `WITHDRAW`, `EXPAND`）。
    - `objectId` (String): 対象となったアイテムのインスタンスID（拡張時は `null`）。
    - `details` (Document): 操作に伴う補足情報（消費ゴールド、使用素材数、拡張前後の容量等）。
    - `timestamp` (Date): 操作実行日時。
    - `_class` (String): クラス表記情報。

## 3. インデックス推奨事項およびデータ自動削除設定

### `playerStorageDomain`
- `{"playerId": 1}`: プレイヤーに紐づく倉庫を検索する場合に必須。一意（Unique）である必要があります。

### `storageLogDomain`
- `{"playerId": 1, "timestamp": -1}`: プレイヤーごとの操作履歴を直近順に検索する場合に使用。
- `{"timestamp": 1}`: **TTL (Time To Live) インデックス**。古いログデータの自動クリーンアップ用（例：90日経過後に自動削除）。
  - `expireAfterSeconds: 7776000` (90日間)
