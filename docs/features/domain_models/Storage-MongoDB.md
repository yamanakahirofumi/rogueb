# 倉庫システム MongoDBデータ構造

このドキュメントは、**Storage**（倉庫）システムに関連するドメインオブジェクトがMongoDBにどのように永続化されるかについて説明します。

## 1. `playerStorageDomain` コレクション
- **説明:** `PlayerStorageDomain`クラスに対応します。プレイヤー個別の倉庫の状態を保持します。
- **フィールド:**
    - `_id` (String): 倉庫の一意な識別子。
    - `playerId` (String): 所有しているプレイヤーのID。
    - `objectIdList` (Array): 保管されているアイテムのインスタンスIDの配列。
    - `limitSize` (Integer): 倉庫に保管可能なアイテムの最大数（初期値: 50、最大: 200）。
    - `expansionStage` (Integer): 倉庫の拡張段階（0: 初期 50枠、1〜5: 段階拡張）。
    - `updateDate` (Date): 最終更新日時。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

---

## 2. `storageLogDomain` コレクション
- **説明:** `StorageLogDomain` クラスに対応します。倉庫における入庫・出庫・拡張などの操作履歴（監査ログ）を記録します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意な識別子。
    - `playerId` (String): 対象のプレイヤー ID。
    - `actionType` (String): 操作種別 (`DEPOSIT`, `WITHDRAW`, `EXPAND`)。
    - `objectId` (String): 入出庫されたアイテムのインスタンス ID（拡張時は `null`）。
    - `expansionStage` (Integer): 拡張時の段階（1〜5、入出庫時は `null`）。
    - `timestamp` (Date): 操作日時。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

---

## 3. インデックス推奨事項と肥大化対策

### `playerStorageDomain`
- `{"playerId": 1}`: プレイヤーに紐づく倉庫を検索する場合に必須。一意（Unique）である必要があります。

### `storageLogDomain`
- `{"playerId": 1, "timestamp": -1}`: 特定プレイヤーの倉庫操作履歴を時系列で検索するために使用します。
- `{"timestamp": 1}`: (TTL インデックスの検討) 履歴データが肥大化する場合、一定期間（例：90日間）経過した古い操作ログを自動削除するために使用します。
