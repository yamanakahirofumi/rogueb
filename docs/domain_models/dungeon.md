# Dungeonモジュール ドメインモデル

このドキュメントは、**Dungeon**モジュールのコアとなるドメインモデルについて説明します。

## コアコンセプト

Dungeonモジュールは、ダンジョンの構造、内容、状態の管理を担当します。主要な概念は、`Dungeon`自体、ダンジョンを構成する`Floor`、およびその中に見つかる様々な`Thing`（モノ）です。

---

## 主要なドメインオブジェクト

これらはDungeonモジュールによって管理される主要なエンティティであり、通常はデータベースに保存されます。

### `DungeonDomain`
- **ファイル:** `Dungeon/src/main/java/net/hero/rogueb/dungeon/domain/DungeonDomain.java`
- **説明:** ダンジョン全体を表します。ダンジョンの全体的なプロパティと設定を保持します。これはルートエンティティです。
- **主要なプロパティ:**
    - `id`: ダンジョンの一意な識別子。
    - `name`: ダンジョンの名前（例：「試練の洞窟」）。
    - `maxLevel`: ダンジョン内のフロアの総数。
    - `itemSeed`: ランダムなアイテム生成のためのシード値。
    - `roomCountSeed`: ランダムな部屋生成のためのシード値。
    - `namespace`: ダンジョンの論理的なグルーピング。

### `FloorDomain`
- **ファイル:** `Dungeon/src/main/java/net/hero/rogueb/dungeon/domain/FloorDomain.java`
- **説明:** ダンジョン内の単一のレベルまたはフロアを表します。マップのレイアウト、階段、アイテム、および金を含みます。
- **主要なプロパティ:**
    - `id`: フロアの一意な識別子。
    - `dungeonId`: このフロアが属する`DungeonDomain`のID。
    - `userId`: このフロアインスタンスがどのユーザーのためのものかを示すID（ユーザーごとのダンジョンインスタンスを示唆）。
    - `level`: フロアのレベル番号（例：1、2、...）。
    - `upStairs`: 上り階段の`Coordinate`。
    - `downStairs`: 下り階段の`Coordinate`。
    - `thingList`: フロアに存在するオブジェクト/アイテムのリスト (`ObjectCoordinateDomain`のリスト)。
    - `goldList`: フロアに存在する金の山のリスト (`GoldCoordinateDomain`のリスト)。
    - `tiles`: フロアのマップレイアウトを表す2Dリスト（`Tile`オブジェクト）。

### `ObjectCoordinateDomain`
- **ファイル:** `Dungeon/src/main/java/net/hero/rogueb/dungeon/domain/ObjectCoordinateDomain.java`
- **説明:** フロア上の特定のアイテムの位置を保持します。
- **プロパティ:**
    - `position`: アイテムの座標 (`Coordinate`)。
    - `objectId`: アイテムのインスタンスID。

### `GoldCoordinateDomain`
- **ファイル:** `Dungeon/src/main/java/net/hero/rogueb/dungeon/domain/GoldCoordinateDomain.java`
- **説明:** フロア上の金の山の位置と量を保持します。
- **プロパティ:**
    - `position`: 金の座標 (`Coordinate`)。
    - `gold`: 金の量。

### `DungeonPlayerDomain`
- **ファイル:** `Dungeon/src/main/java/net/hero/rogueb/dungeon/domain/DungeonPlayerDomain.java`
- **説明:** 特定のダンジョンにおけるプレイヤーの現在の状態（主に滞在フロア）を管理します。
- **主要なプロパティ:**
    - `id`: 一意な識別子。
    - `dungeonId`: プレイヤーが滞在しているダンジョンのID。
    - `playerId`: プレイヤーのID。
    - `level`: プレイヤーが現在滞在しているフロアのレベル。

---

## 値オブジェクトとフィールド

これらのクラスは、ドメインオブジェクト内で使用されるデータ構造と値オブジェクトを表します。

### `Coordinate` (インターフェース)
- **ファイル:** `Dungeon/src/main/java/net/hero/rogueb/dungeon/fields/Coordinate.java`
- **説明:** ダンジョン内の位置を定義するために使用される3D座標のインターフェース。
- **メソッド:**
    - `x()`: X座標を返します。
    - `y()`: Y座標を返します。
    - `z()`: Z座標を返します（多くの場合、フロアレベルに対応）。
    - `minus(Coordinate)`: 座標の差分を計算します。
    - `plus(Coordinate)`: 座標の加算を計算します。
    - `area()`: 面積（x * y）を返します。

### `Coordinate2D` (レコード)
- **ファイル:** `Dungeon/src/main/java/net/hero/rogueb/dungeon/fields/Coordinate2D.java`
- **説明:** `Coordinate`インターフェースの2D実装。`z`は常に0を返します。

### `Tile` (インターフェース)
- **ファイル:** `Dungeon/src/main/java/net/hero/rogueb/dungeon/fields/Tile.java`
- **説明:** マップ上の単一のタイルを表します。これはジェネリックインターフェースであり、さまざまな表示表現を可能にします。
- **メソッド:**
    - `display()`: タイルの表示表現（例：壁の場合は`#`のような文字）を返します。

### `Tile2D` (レコード)
- **ファイル:** `Dungeon/src/main/java/net/hero/rogueb/dungeon/fields/Tile2D.java`
- **説明:** `Tile`インターフェースの具体的な実装。表示文字と、タイルの種類を示す`PointType`を保持します。

### `Gold`
- **ファイル:** `Dungeon/src/main/java/net/hero/rogueb/dungeon/fields/Gold.java`
- **説明:** フロア上の金の山を表す値オブジェクト。
- **主要なプロパティ/メソッド:**
    - `gold`: 金の量。
    - `getDisplay()`: マップ上に表示するための文字（例：`$`）を返します。

### `DisplayData` (レコード)
- **ファイル:** `Dungeon/src/main/java/net/hero/rogueb/dungeon/fields/DisplayData.java`
- **説明:** 座標と、その座標に関連付けられた表示用データのリストを保持するジェネリックレコード。主に周辺視界の描画データ伝達に使用されます。
- **フィールド:**
    - `position`: データの中心となる座標 (`Coordinate`)。
    - `data`: 表示する文字列やその他のデータのリスト。

### `ThingOverviewType` (列挙型)
- **ファイル:** `DungeonBase/src/main/java/net/hero/rogueb/dungeon/base/o/ThingOverviewType.java`
- **説明:** プレイヤーの足元にあるものの種類を示す列挙型。内部的に整数値のタイプを持ちます。
- **値:**
    - `None` (0): 何もない。
    - `Gold` (1): 金がある。
    - `Object` (2): アイテムがある。
