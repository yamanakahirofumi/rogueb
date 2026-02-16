# PlayerOperationsモジュール ドメインモデル

このドキュメントは、**PlayerOperations**モジュールのコアとなるドメインモデルについて説明します。

## コアコンセプト

PlayerOperationsモジュールは、プレイヤーキャラクターの状態とインベントリの管理を担当します。プレイヤーが何であるか、何を持っているかを定義します。このモジュールは、プレイヤーによって開始されたアクションを調整し、Dungeon（移動用）やObjects（アイテム管理用）などの他のサービスと対話する可能性があります。

---

## 主要なドメインオブジェクト

### `PlayerDto`
- **ファイル:** `BookOfAdventureClient/src/main/java/net/hero/rogueb/bookofadventureclient/o/PlayerDto.java`
- **説明:** **PlayerOperations**におけるプレイヤー状態のやり取りに使用される中心的なDTOです。プレイヤー名、経験値、所持金、および現在のステータスや位置情報を保持します。
- **詳細:** 詳細な構造については、**[BookOfAdventureモジュール ドメインモデル](./book_of_adventure.md)**を参照してください。

### `Player` (インターフェース)
- **ファイル:** `PlayerOperations/src/main/java/net/hero/rogueb/character/Player.java`
- **説明:** プレイヤーキャラクターの基本的な規約を定義するインターフェースです。
- **主要なメソッド:**
    - `getName()`: プレイヤーの名前を返します。
    - `isMoved()`: プレイヤーの移動状態に関連するブール値のフラグ。

### `Human`
- **ファイル:** `PlayerOperations/src/main/java/net/hero/rogueb/character/Human.java`
- **説明:** `Player`インターフェースの具体的な実装です。
- **主要なプロパティ:**
    - `name`: キャラクターの名前。
    - `bag`: プレイヤーのインベントリを表す`Bag`クラスのインスタンス。

### `Bag`
- **ファイル:** `PlayerOperations/src/main/java/net/hero/rogueb/bag/Bag.java`
- **説明:** プレイヤーのインベントリ、または「バッグ」を表します。プレイヤーが運んでいるアイテムを管理します。
- **主要なプロパティとメソッド:**
    - `contents`: `ThingSimple`オブジェクトのリスト。
    - `limitSize`: バッグが保持できるアイテムの最大数（デフォルト値: 23）。
    - `addContents(ThingSimple thing)`: スペースがあればバッグにアイテムを追加します。
    - `getThingIdList()`: バッグ内のアイテムのインスタンスIDのリストを返します。

### `ThingSimple` (レコード)
- **ファイル:** `ObjectsClient/src/main/java/net/hero/rogueb/objectclient/o/ThingSimple.java`
- **説明:** `Objects`サービスから取得したアイテムの基本情報を保持するためのDTO。
- **フィールド:**
    - `instanceId`: アイテムのインスタンスID。
    - `objectId`: アイテムの種類を示すID。
    - `display`: マップ上での表示文字。

---

## 主要なサービス

### `FieldsService`
- **ファイル:** `PlayerOperations/src/main/java/net/hero/rogueb/services/FieldsService.java`
- **説明:** プレイヤー周辺のフィールド情報をリアクティブに提供するサービス。
- **主要な機能:**
    - `getFields(userId)`: `Flux.interval(Duration.ofSeconds(20))` を用いて20秒間隔での定期更新、または即時更新 (`getFieldsNow`) を提供します。

---

## 値オブジェクトと列挙型

### `MoveEnum` (列挙型)
- **ファイル:** `DungeonClient/src/main/java/net/hero/rogueb/dungeonclient/o/MoveEnum.java`
- **説明:** プレイヤーの移動方向を定義する列挙型。
- **値:** `Top`, `Down`, `Left`, `Right`, `TopLeft`, `TopRight`, `DownLeft`, `DownRight`
