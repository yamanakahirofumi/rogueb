# 倉庫システム MongoDBデータ構造

このドキュメントは、**Storage**（倉庫）システムに関連するドメインオブジェクトがMongoDBにどのように永続化されるかについて説明します。

## 1. `playerStorageDomain` コレクション
- **説明:** `PlayerStorageDomain`クラスに対応します。プレイヤー個別の倉庫の状態を保持します。
- **フィールド:**
    - `_id` (String): 倉庫の一意な識別子。
    - `playerId` (String): 所有しているプレイヤーのID。
    - `objectIdList` (Array): 保管されているアイテムのインスタンスIDの配列。
    - `limitSize` (Integer): 倉庫に保管可能なアイテムの最大数。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## 2. インデックス推奨事項

### `playerStorageDomain`
- `{"playerId": 1}`: プレイヤーに紐づく倉庫を検索する場合に必須。一意（Unique）である必要があります。
