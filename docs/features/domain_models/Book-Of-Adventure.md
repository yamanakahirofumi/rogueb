# BookOfAdventureモジュール ドメインモデル

このドキュメントは、**BookOfAdventure**モジュールのコアとなるドメインモデルについて説明します。

## 1. コアコンセプト

BookOfAdventureモジュールは、プレイヤーの冒険の記録、キャラクターの状態、および所持アイテム（インベントリ）の永続化を担当します。ゲームのセーブデータを管理する中心的なレポジトリとして機能します。

PlayerOperationsモジュールはこのサービスを利用して、プレイヤーの状態を取得・更新します。

---

## 2. 主要なドメインオブジェクト

### `PlayerDomain`
- **ファイル:** `BookOfAdventure/src/main/java/net/hero/rogueb/bookofadventure/domain/PlayerDomain.java`
- **説明:** プレイヤーの全状態を保持し、データベースに保存されるメインのエンティティです。
- **主要なプロパティ:**
    - `id`: ユーザー（プレイヤー）の一意な識別子。
    - `name`: プレイヤーの名前。
    - `exp`: 経験値。
    - `gold`: 所持金。
    - `namespace`: プレイヤーが属する論理的な領域。
    - `currentStatus`: 現在のステータスを保持するマップ。
        - キー: `hp` (ヒットポイント), `mp` (魔法ポイント), `stamina` (スタミナ), `actionInterval` (行動間隔), `seed` (乱数シード), `subStep` (内部歩数カウンタ)
    - `status`: 基本ステータスを保持するマップ（成長や永続的なバフの影響を受ける前の値）。
        - キー: `atk` (物理攻撃力), `def` (物理防御力), `magicAtk` (魔法攻撃力), `magicDef` (魔法防御力), `dex` (器用さ/命中率), `maxHp` (最大ヒットポイント), `maxMp` (最大魔法ポイント), `attribute` (属性), `mnd` (精神力/状態異常耐性)
    - `location`: 現在の位置情報を保持するマップ。
        - キー: `dungeonId` (ダンジョンID), `level` (階層), `x` (X座標), `y` (Y座標)
    - `equipment`: 装備中のアイテム情報を保持するマップ。
        - キー: `weapon` (武器), `armor` (防具), `ring1` (指輪1), `ring2` (指輪2)
        - 値: アイテムのインスタンス ID。
    - `statusEffects`: 付与されている状態異常 (`StatusEffectDomain`) のリスト。

### `PlayerObjectDomain`
- **ファイル:** `BookOfAdventure/src/main/java/net/hero/rogueb/bookofadventure/domain/PlayerObjectDomain.java`
- **説明:** プレイヤーが所持しているアイテム（インベントリ）のリストを管理します。
- **主要なプロパティ:**
    - `id`: 一意な識別子。
    - `playerId`: プレイヤーのID。
    - `objectIdList`: プレイヤーが所持しているアイテムのインスタンスIDのリスト。

### `PlayerKnowledgeDomain`
- **ファイル:** `BookOfAdventure/src/main/java/net/hero/rogueb/bookofadventure/domain/PlayerKnowledgeDomain.java`
- **説明:** プレイヤー（ユーザー）ごとのアイテム知識（識別状況）を管理します。
- **主要なプロパティ:**
    - `id`: 一意な識別子。
    - `userId`: プレイヤー（ユーザー）のID。
    - `worldId`: ワールドのID。
    - `typeId`: アイテムタイプのID。
    - `isIdentified`: そのアイテムタイプが識別されているかどうかを示すブール値。

### `StatusEffectDomain` (値オブジェクト)
- **説明:** プレイヤーやモンスターに付与される状態異常を定義します。
- **プロパティ:**
    - `type`: 状態異常の種類（例: `Poison`, `Confusion`, `Paralysis`, `Sleep`, `Seal`）。
    - `remainingTurns`: 残りの継続ターン数または歩数。
    - `value`: 効果に関連する補助的な数値（例: 毒のダメージ量）。

---

## 3. データ転送オブジェクト (DTO)

### `PlayerDto`
- **ファイル:** `BookOfAdventureClient/src/main/java/net/hero/rogueb/bookofadventureclient/o/PlayerDto.java`
- **説明:** 他のサービス（特にPlayerOperations）との間でプレイヤーの状態をやり取りするための主要なDTOです。`PlayerDomain`とほぼ同じ構造を持ちます。
- **主要なプロパティ:**
    - `id`: プレイヤーの一意な識別子。
    - `name`: プレイヤーの名前。
    - `exp`: 累積経験値。
    - `gold`: 所持金額。
    - `namespace`: プレイヤーが属する論理的な領域。
    - `currentStatus`: 現在のステータスを保持するマップ。
        - キー: `hp` (ヒットポイント), `mp` (魔法ポイント), `stamina` (スタミナ), `actionInterval` (行動間隔), `seed` (乱数シード), `subStep` (内部歩数カウンタ)
    - `status`: 基本ステータスを保持するマップ。
        - キー: `atk` (物理攻撃力), `def` (物理防御力), `magicAtk` (魔法攻撃力), `magicDef` (魔法防御力), `dex` (器用さ/命中率), `maxHp` (最大ヒットポイント), `maxMp` (最大魔法ポイント), `attribute` (属性), `mnd` (精神力/状態異常耐性)
    - `location`: 現在の位置情報を保持するマップ。
        - キー: `dungeonId` (ダンジョンID), `level` (階層), `x` (X座標), `y` (Y座標)
    - `equipment`: 装備中のアイテム情報を保持するマップ。
    - `statusEffects`: 付与されている状態異常 (`StatusEffectDomain`) のリスト。
- **用途:** `PlayerService`でのビジネスロジック処理や、APIのレスポンスとして使用されます。
