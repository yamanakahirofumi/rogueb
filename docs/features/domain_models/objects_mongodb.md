# Objectsモジュール MongoDBデータ構造

このドキュメントは、**Objects**モジュールのドメインオブジェクトがMongoDBにどのように永続化されるかについて説明します。

## `ringDomain` コレクション
- **説明:** `RingDomain`クラスに対応します。各ドキュメントは指輪の「種類」を表します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `name` (String): 指輪の名前。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報（例: `net.hero.rogueb.objects.domain.RingDomain`）。

## `objectHistoryDomain` コレクション
- **説明:** `ObjectHistoryDomain`クラスに対応します。オブジェクトのライフサイクルイベントを記録します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `thing` (Object): イベントの対象となった`Thing`オブジェクトの情報。
    - `parentId` (String): インスタンスを識別するためのID。初回の履歴レコードの`id`がセットされ、以降の更新でも同じ値を引き継ぐことで同一インスタンスであることを示します。
    - `isIdentified` (Boolean): インスタンスが識別済みかどうか。
    - `description` (String): イベントの詳細な説明。
    - `createDate` (Date): イベントの発生日時。
    - `zoneId` (String): タイムゾーン情報。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## `identificationMapDomain` コレクション
- **説明:** `IdentificationMapDomain`クラスに対応します。ワールドごとのアイテムタイプと外見（未識別名）の対応を管理します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `worldId` (String): ワールドID。
    - `typeId` (String): アイテムタイプID。
    - `appearanceName` (String): 外見名。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報（例: `net.hero.rogueb.objects.domain.IdentificationMapDomain`）。
