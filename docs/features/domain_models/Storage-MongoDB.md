# 倉庫システム MongoDBデータ構造

このドキュメントは、**Storage**（倉庫）システムに関連するドメインオブジェクトがMongoDBにどのように永続化されるかについて説明します。

## 1. `playerStorageDomain` コレクション
- **説明:** `PlayerStorageDomain`クラスに対応します。プレイヤー個別の倉庫の状態を保持します。
- **フィールド:**
    - `_id` (String): 倉庫の一意な識別子。
    - `playerId` (String): 所有しているプレイヤーのID。
    - `objectIdList` (Array): 保管されているアイテムのインスタンスIDの配列。
    - `limitSize` (Integer): 倉庫に保管可能なアイテムの最大数（初期値: 50、最大値: 200）。
    - `expansionStage` (Integer): 現在の倉庫拡張段階（0〜5。初期値: 0）。
    - `updateDate` (Date): 倉庫の最終更新日時（UTC）。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## 2. `storageLogDomain` コレクション
- **説明:** 倉庫に対する操作（入庫、出庫、拡張）の履歴を監査・トラブルシューティング目的で保持します。
- **フィールド:**
    - `_id` (String): ログレコードの一意な識別子。
    - `playerId` (String): 操作を行ったプレイヤーのID。
    - `actionType` (String): 操作種別（`DEPOSIT`, `WITHDRAW`, `EXPAND`）。
    - `objectId` (String, Optional): 移動対象となったアイテムのインスタンスID（入出庫時）。
    - `detail` (Object): 操作に応じた追加情報（拡張前後の容量、消費ゴールド、素材数等）。
    - `timestamp` (Date): 操作実行日時（UTC）。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## 3. インデックス推奨事項

### `playerStorageDomain`
- `{"playerId": 1}`: プレイヤーに紐づく倉庫を検索する場合に必須。一意（Unique）である必要があります。

### `storageLogDomain`
- `{"playerId": 1, "timestamp": -1}`: プレイヤーごとの操作履歴を時系列降順に検索・監査するための複合インデックス。
- `{"timestamp": 1}`: 保存期間を過ぎたログを自動削除するための TTL インデックス（例: 90日経過後に自動削除 `expireAfterSeconds: 7776000`）。
