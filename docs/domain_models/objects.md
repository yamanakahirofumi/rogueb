# Objectsモジュール ドメインモデル

このドキュメントは、**Objects**モジュールのコアとなるドメインモデルについて説明します。

## コアコンセプト

Objectsモジュールは、武器、防具、ポーション、指輪など、すべてのゲーム内アイテムおよびオブジェクトの定義、作成、管理を担当します。ゲームワールドで対話可能なすべての「モノ（`Thing`）」のファクトリおよびリポジトリとして機能します。

---

## コアとなるインターフェースとクラス

### `Thing` (インターフェース)
- **ファイル:** `Objects/src/main/java/net/hero/rogueb/objects/Thing.java`
- **説明:** すべてのゲームオブジェクトの中心となるインターフェースです。すべてのアイテムやオブジェクトが準拠しなければならない共通の規約を定義します。
- **主要なメソッド:**
    - `getId()`: オブジェクトのタイプの一意な識別子を返します（例：「レザーアーマー」のID）。
    - `getDisplay()`: マップ上でオブジェクトを表すために使用される文字を返します（例：防具の場合は`[`、武器の場合は`）`）。
    - `getName()`: オブジェクトの名前を返します（例：「ショートソード」）。
    - `getType()`: オブジェクトのカテゴリを示す`TypeEnum`値を返します（例：`WEAPON`、`ARMOR`、`RING`）。
    - `isMany()`: オブジェクトがスタック可能かどうかを示すブール値。

### `ThingInstance` (レコード)
- **ファイル:** `Objects/src/main/java/net/hero/rogueb/objects/ThingInstance.java`
- **説明:** `Thing`の一意で具体的なインスタンスを表します。`Thing`がオブジェクトのタイプ（「力の指輪」など）を定義するのに対し、`ThingInstance`はプレイヤーが発見する可能性のある、独自の一意なインスタンスIDを持つ特定のオブジェクトを表します。
- **フィールド:**
    - `instanceId`: このオブジェクトの特定のインスタンスのための一意なID。
    - `typeId`: このインスタンスが派生したベースとなる`Thing`のID。
    - `name`: オブジェクトの名前。
    - `display`: オブジェクトの表示文字。

---

## ドメインエンティティ（例）

これらは`Thing`インターフェースの具体的な実装であり、通常はデータベースに保存されます。

### `RingDomain`
- **ファイル:** `Objects/src/main/java/net/hero/rogueb/objects/domain/RingDomain.java`
- **説明:** 指輪のタイプを表す永続的なドメインオブジェクトです。`Thing`インターフェースを実装して、指輪に関する詳細を提供します。
- **実装例:**
    - `getDisplay()`: `=` を返します。
    - `getType()`: `TypeEnum.RING` を返します。

### `ObjectHistoryDomain`
- **ファイル:** `Objects/src/main/java/net/hero/rogueb/objects/domain/ObjectHistoryDomain.java`
- **説明:** このエンティティは、オブジェクトインスタンスがいつ作成され、ドロップされ、拾われたかなど、そのライフサイクルや履歴を追跡する可能性があります。これはロギングやデバッグに役立ちます。
- **主要なプロパティ:**
    - `id`: 履歴エントリの一意なID。
    - `thingInstanceId`: この履歴エントリが関連する`ThingInstance`のID。
    - `event`: 発生したイベントの説明。
    - `timestamp`: イベントが発生した日時。
