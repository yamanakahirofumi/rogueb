# 経済システム MongoDBデータ構造

このドキュメントは、**Economic System**（経済システム）のドメインオブジェクトが MongoDB にどのように永続化されるかについて説明します。

## 1. `itemCirculationDomain` コレクション
- **説明:** `ItemCirculationDomain` クラスに対応します。世界全体のアイテム流通量と基本価格を管理します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意な識別子。
    - `worldId` (String): 対象のワールド ID。
    - `typeId` (String): アイテムタイプの ID（`Thing.getId()` に対応）。
    - `currentCount` (Integer): 現在世界内に存在する総数。
    - `maxLimit` (Integer): 世界内に同時に存在できる最大数。
    - `basePrice` (Double): 流通量に基づき算出された現在の基本市場価格（標準価格の 0.1 〜 2.0 倍）。
    - `_class` (String): Spring Data MongoDB が使用するクラス情報。

### インデックス推奨事項
- `{"worldId": 1, "typeId": 1}`: ユニークインデックス。特定のワールドにおけるアイテムの流通情報を高速に検索するために必須です。

---

## 2. `shopDomain` コレクション
- **説明:** `ShopDomain` クラスに対応します。ダンジョン内に設置されたショップの状態を保持します。
- **フィールド:**
    - `_id` (String): ショップの一意な識別子。
    - `ownerId` (String): ショップを経営する管理者のユーザー ID。
    - `dungeonId` (String): 配置されているダンジョンの ID。
    - `level` (Integer): 配置されている階層。
    - `position` (Object): 座標 (`Coordinate`)。
        - `x` (Integer)
        - `y` (Integer)
    - `inventory` (Map): 販売中のアイテムインスタンス ID をキー、管理者設定価格を値とするマップ。
    - `isOpen` (Boolean): 開店状態フラグ。
    - `_class` (String): Spring Data MongoDB が使用するクラス情報。

### インデックス推奨事項
- `{"dungeonId": 1, "level": 1}`: 特定のフロアにあるショップを特定するために使用します。
- `{"ownerId": 1}`: 特定の管理者が所有するショップを一覧表示するために使用します。
- `{"position": "2d"}` または `{"position": "2dsphere"}`: プレイヤーがショップの座標に到達した際の判定を高速化するために検討します。

---

## 3. `transactionHistoryDomain` コレクション
- **説明:** `TransactionHistoryDomain` クラスに対応します。ショップでの売買履歴を記録します。
- **フィールド:**
    - `_id` (String): 一意な識別子。
    - `shopId` (String): 取引が行われたショップの ID。
    - `buyerId` (String): 購入者のユーザー ID。
    - `sellerId` (String): 販売者のユーザー ID。
    - `instanceId` (String): 取意されたアイテムのインスタンス ID。
    - `price` (Integer): 実際の取引価格。
    - `transactionDate` (Date): 取引日時。
    - `_class` (String): Spring Data MongoDB が使用するクラス情報。

### インデックス推奨事項
- `{"shopId": 1, "transactionDate": -1}`: 特定のショップの取引履歴を時系列で取得するために使用します。
- `{"buyerId": 1}`: プレイヤー自身の購入履歴を確認するために使用します。
- `{"sellerId": 1}`: 管理者が自身の売り上げ履歴を確認するために使用します。
- `{"transactionDate": 1}`: (TTL インデックスの検討) 履歴データが肥大化する場合、一定期間（例：30日間）経過したデータを自動削除するために使用します。
