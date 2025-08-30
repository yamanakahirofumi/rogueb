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
    - `thingList`: フロアに存在するオブジェクト/アイテムのリスト。
    - `goldList`: フロアに存在する金の山のリスト。
    - `tiles`: フロアのマップレイアウトを表す2Dリスト（`Tile`オブジェクト）。

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

### `Tile` (インターフェース)
- **ファイル:** `Dungeon/src/main/java/net/hero/rogueb/dungeon/fields/Tile.java`
- **説明:** マップ上の単一のタイルを表します。これはジェネリックインターフェースであり、さまざまな表示表現を可能にします。
- **メソッド:**
    - `display()`: タイルの表示表現（例：壁の場合は`#`のような文字）を返します。

### `Gold`
- **ファイル:** `Dungeon/src/main/java/net/hero/rogueb/dungeon/fields/Gold.java`
- **説明:** フロア上の金の山を表す値オブジェクト。
- **主要なプロパティ/メソッド:**
    - `gold`: 金の量。
    - `getDisplay()`: マップ上に表示するための文字（例：`$`）を返します。
