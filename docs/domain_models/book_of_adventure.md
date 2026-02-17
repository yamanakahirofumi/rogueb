# BookOfAdventureモジュール ドメインモデル

このドキュメントは、**BookOfAdventure**モジュールのコアとなるドメインモデルについて説明します。

## コアコンセプト

BookOfAdventureモジュールは、プレイヤーの冒険の記録、キャラクターの状態、および所持アイテム（インベントリ）の永続化を担当します。ゲームのセーブデータを管理する中心的なレポジトリとして機能します。

PlayerOperationsモジュールはこのサービスを利用して、プレイヤーの状態を取得・更新します。

---

## 主要なドメインオブジェクト

### `PlayerDomain`
- **ファイル:** `BookOfAdventure/src/main/java/net/hero/rogueb/bookofadventure/domain/PlayerDomain.java`
- **説明:** プレイヤーの全状態を保持し、データベースに保存されるメインのエンティティです。
- **主要なプロパティ:**
    - `id`: ユーザー（プレイヤー）の一意な識別子。
    - `name`: プレイヤーの名前。
    - `exp`: 経験値。
    - `gold`: 所持金。
    - `namespace`: プレイヤーが属する論理的な領域。
    - `currentStatus`: 現在のステータス（HP、スタミナなど）を保持するマップ。
    - `status`: 基本ステータスを保持するマップ。
    - `location`: 現在の位置情報（ダンジョンID、レベル、座標）を保持するマップ。

### `PlayerObjectDomain`
- **ファイル:** `BookOfAdventure/src/main/java/net/hero/rogueb/bookofadventure/domain/PlayerObjectDomain.java`
- **説明:** プレイヤーが所持しているアイテム（インベントリ）のリストを管理します。
- **主要なプロパティ:**
    - `id`: 一意な識別子。
    - `playerId`: プレイヤーのID。
    - `objectIdList`: プレイヤーが所持しているアイテムのインスタンスIDのリスト。

---

## データ転送オブジェクト (DTO)

### `PlayerDto`
- **ファイル:** `BookOfAdventureClient/src/main/java/net/hero/rogueb/bookofadventureclient/o/PlayerDto.java`
- **説明:** 他のサービス（特にPlayerOperations）との間でプレイヤーの状態をやり取りするための主要なDTOです。`PlayerDomain`とほぼ同じ構造を持ちます。
- **主要なプロパティ:**
    - `id`: プレイヤーの一意な識別子。
    - `name`: プレイヤーの名前。
    - `exp`: 累積経験値。
    - `gold`: 所持金額。
    - `namespace`: プレイヤーが属する論理的な領域。
    - `currentStatus`: 現在のステータス（HP、スタミナなど）を保持するマップ。
    - `status`: 基本ステータスを保持するマップ。
    - `location`: 現在の位置情報（`dungeonId`, `level`, `x`, `y`）を保持するマップ。
- **用途:** `PlayerService`でのビジネスロジック処理や、APIのレスポンスとして使用されます。
