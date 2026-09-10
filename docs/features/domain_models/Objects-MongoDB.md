# Objectsモジュール MongoDBデータ構造

このドキュメントは、**Objects**モジュールのドメインオブジェクトがMongoDBにどのように永続化されるかについて説明します。

## 1. `ringDomain` コレクション
- **説明:** `RingDomain`クラスに対応します。各ドキュメントは指輪の「種類」を表します。これは `Thing` インターフェースの具体的な実装例の一つです。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `name` (String): 指輪の名前。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報（例: `net.hero.rogueb.objects.domain.RingDomain`）。

## 2. `objectHistoryDomain` コレクション
- **説明:** `ObjectHistoryDomain`クラスに対応します。オブジェクトのライフサイクルイベントを記録します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `thing` (Object): イベントの対象となった`Thing`オブジェクトの情報。
    - `parentId` (String): インスタンスを識別するためのID。初回の履歴レコードの`id`がセットされ、以降の更新でも同じ値を引き継ぐことで同一インスタンスであることを示します。
    - `isIdentified` (Boolean): インスタンスが識別済みかどうか。
    - `tier` (Integer): インスタンスのティア。
    - `metadata` (Object): インスタンス固有の動的データ（Map<String, Object>）。
    - `description` (String): イベントの詳細な説明。
    - `createDate` (Date): イベントの発生日時。
    - `zoneId` (String): タイムゾーン情報。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

### インデックス推奨事項と肥大化対策
- `{"parentId": 1, "createDate": -1}`: インスタンスの最新の状態を復元するために必須のインデックスです。
- **肥大化対策**: `ObjectHistoryDomain` はアイテムの全履歴を保持するため、時間の経過とともにデータ量が膨大になります。
    - **TTL インデックス**: 古い履歴（例：1年以上前）を自動的に削除することを検討します。ただし、アイテムがまだ存在する場合は、最初のレコード（parentId の元）と最新のレコードを保持する必要があります。
    - **アーカイブ戦略**: 使用されなくなったアイテム（持ち主がいない、破壊された等）の履歴を別のコレクションやコールドストレージへ移動するバッチ処理を検討します。

## 3. `identificationMapDomain` コレクション
- **説明:** `IdentificationMapDomain`クラスに対応します。ワールドごとのアイテムタイプと外見（未識別名）の対応を管理します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `worldId` (String): ワールドID。
    - `typeId` (String): アイテムタイプID。
    - `appearanceName` (String): 外見名。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報（例: `net.hero.rogueb.objects.domain.IdentificationMapDomain`）。

### インデックス推奨事項
- `{"worldId": 1, "typeId": 1}`: ユニークインデックス。特定のワールドにおけるアイテムの外見マッピングを一意に保つために必須です。

## 4. `setEquipmentDomain` コレクション
- **説明:** `SetEquipmentDomain`クラスに対応します。セット装備のマスター定義（構成パーツおよび段階的発動ボーナス）を管理します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。通常は `setId` と同一の値が使用されます。
    - `setId` (String): セット定義の識別子（例: `dragon_slayer_set`）。
    - `setName` (String): セット装備の名称（例: 「ドラゴンキラーセット」）。
    - `description` (String): セット装備のフレーバーテキスト・解説。
    - `pieces` (Array): セットを構成するパーツ情報の配列。
        - `typeId` (String): アイテムの種別 ID（例: `dragon_killer`）。
        - `pieceName` (String): パーツの名称。
    - `bonuses` (Array): 必要装備数に応じた段階的ボーナス定義の配列。
        - `requiredCount` (Integer): ボーナス発動に必要な装備数（例: 2, 3）。
        - `bonusName` (String): ボーナス効果の名称。
        - `effects` (Array): 標準化されたエフェクト構造（`type`, `targetCategory`, `element`, `value` 等）の配列。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報（例: `net.hero.rogueb.objects.domain.SetEquipmentDomain`）。

### インデックス推奨事項
- `{"setId": 1}`: ユニークインデックス。セットIDによる高速なマスター検索を保証します。
