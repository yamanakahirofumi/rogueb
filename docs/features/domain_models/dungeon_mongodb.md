# Dungeonモジュール MongoDBデータ構造

このドキュメントは、**Dungeon**モジュールのドメインオブジェクトがMongoDBにどのように永続化されるかについて説明します。

## `dungeonDomain` コレクション
- **説明:** `DungeonDomain`クラスに対応します。各ドキュメントは一つのダンジョンを表します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `name` (String): ダンジョンの名前。
    - `maxLevel` (Integer): 最大フロア数。
    - `itemSeed` (Integer): アイテム生成用シード値。
    - `roomCountSeed` (Integer): 部屋数生成用シード値。
    - `namespace` (String): 論理的な名前空間。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報（例: `net.hero.rogueb.dungeon.domain.DungeonDomain`）。

## `floorDomain` コレクション
- **説明:** `FloorDomain`クラスに対応します。各ドキュメントはダンジョン内の一つのフロアを表します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `dungeonId` (String): 関連する`dungeonDomain`のID。
    - `userId` (String): このフロアが割り当てられているユーザーのID。
    - `level` (Integer): フロアの階層レベル。
    - `upStairs` (Object): 上り階段の座標 (`Coordinate`オブジェクト)。
    - `downStairs` (Object): 下り階段の座標 (`Coordinate`オブジェクト)。
    - `thingList` (Array): フロア上のアイテムのリスト (`ObjectCoordinateDomain`オブジェクトの配列)。
    - `goldList` (Array): フロア上の金のリスト (`GoldCoordinateDomain`オブジェクトの配列)。
    - `tiles` (Array): 2次元のタイル情報（`Tile`オブジェクトの配列の配列）。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報。

## `dungeonPlayerDomain` コレクション
- **説明:** `DungeonPlayerDomain`クラスに対応します。プレイヤーのダンジョン内での現在の階層情報を保持します。
- **フィールド:**
    - `_id` (String): ドキュメントの一意なID。
    - `dungeonId` (String): ダンジョンのID。
    - `playerId` (String): プレイヤーのID。
    - `level` (Integer): 現在の階層レベル。
    - `_class` (String): Spring Data MongoDBが使用するクラス情報（例: `net.hero.rogueb.dungeon.domain.DungeonPlayerDomain`）。
