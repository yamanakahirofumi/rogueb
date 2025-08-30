# PlayerOperationsモジュール ドメインモデル

このドキュメントは、**PlayerOperations**モジュールのコアとなるドメインモデルについて説明します。

## コアコンセプト

PlayerOperationsモジュールは、プレイヤーキャラクターの状態とインベントリの管理を担当します。プレイヤーが何であるか、何を持っているかを定義します。このモジュールは、プレイヤーによって開始されたアクションを調整し、Dungeon（移動用）やObjects（アイテム管理用）などの他のサービスと対話する可能性があります。

---

## 主要なドメインオブジェクト

### `Player` (インターフェース)
- **ファイル:** `PlayerOperations/src/main/java/net/hero/rogueb/character/Player.java`
- **説明:** プレイヤーキャラクターの基本的な規約を定義する単純なインターフェースです。
- **主要なメソッド:**
    - `getName()`: プレイヤーの名前を返します。
    - `isMoved()`: プレイヤーの移動状態に関連するブール値のフラグ。

### `Human`
- **ファイル:** `PlayerOperations/src/main/java/net/hero/rogueb/character/Human.java`
- **説明:** `Player`インターフェースの具体的な実装です。人間のプレイヤーキャラクターを表します。
- **主要なプロパティ:**
    - `name`: 人間キャラクターの名前。
    - `bag`: プレイヤーのインベントリを表す`Bag`クラスのインスタンス。

### `Bag`
- **ファイル:** `PlayerOperations/src/main/java/net/hero/rogueb/bag/Bag.java`
- **説明:** プレイヤーのインベントリ、または「バッグ」を表します。プレイヤーが運んでいるアイテムを管理します。
- **主要なプロパティとメソッド:**
    - `contents`: `ThingSimple`オブジェクトのリスト。`ThingSimple`は`ObjectsClient`モジュールからのデータ転送オブジェクト（DTO）であり、このモジュールが`Objects`サービスによって管理されるアイテムへの参照を保持していることを示します。
    - `limitSize`: バッグが保持できるアイテムの最大数。
    - `addContents(ThingSimple thing)`: スペースがあればバッグにアイテムを追加します。
    - `getThingIdList()`: バッグ内のアイテムのインスタンスIDのリストを返します。
