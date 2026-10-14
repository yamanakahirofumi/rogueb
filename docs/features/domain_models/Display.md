# Displayモジュール ドメインモデル

このドキュメントは、**Display**モジュールのコアとなるドメインモデル、描画レイヤー構造、リアルタイムストリーミング仕様、およびAPI仕様について説明します。

---

## 1. コアコンセプト

Displayモジュールは、ゲーム画面の描画・UI表示に必要なフィールドデータ、エンティティ（プレイヤー、モンスター、アイテム、トラップ等）、視界（Fog of War）、および画面オーバーレイ演出（環境異変・ステータス効果フィルター）を統合・集約するBFF（Backend for Frontend）／描画制御モジュールです。

各マイクロサービス（Dungeon, Objects, Monster, PlayerOperations）から非同期・リアクティブ（Spring WebFlux / SSE）に状態を取得し、クライアント（フロントエンド）に多層レイヤー構造（`DisplayData`）として統合データを提供します。

---

## 2. 主要なドメインオブジェクト

### `DisplayData` (レコード)
- **説明:** 画面上の各セル（座標 `x`, `y`）における重ね合わせ描画情報を保持する集約オブジェクトです。1つの座標に対し、床・トラップ・アイテム・モンスター・プレイヤー・エフェクトといった複数レイヤーの表示要素を含みます。
- **主要なプロパティ:**
    - `x`: フィールド上の X 座標。
    - `y`: フィールド上の Y 座標。
    - `layers`: 描画優先度順（`LayerEnum` 昇順）に整理された表示要素（文字、スプライトID、カラーコード）のマップ。
    - `visibility`: 該当座標の視界状態（`UNEXPLORED`, `EXPLORED`, `VISIBLE`）。

### `LayerEnum` (列挙型)
- **説明:** 画面上での描画重ね合わせ優先度（Zインデックス）を定義する列挙型です。値が大きくなるほど前面に描画されます。
- **定義値:**
    1. `GROUND`: 地形・床・壁（例: `.`, `#`, `~`）
    2. `TRAP`: 設置された罠（例: `^`、可視状態のみ描画）
    3. `OBJECT`: 床上のアイテム・ドロップ品（例: `!`, `/`, `=`）
    4. `MONSTER`: 敵・仲間モンスター（例: `k`, `D`）
    5. `PLAYER`: プレイヤーキャラクター（例: `@`）
    6. `EFFECT`: 攻撃ヒット、スキル演出、ダメージポップアップ等
    7. `OVERLAY`: 環境異変マスク、暗闇・視界制限フィルター等

### `FieldOverlayDomain`
- **説明:** フロア全体の環境異変（太陽フレア、絶対零度、瘴気嵐等）や、視界制限・ステータス効果（暗闇、あかりの巻物効果等）に基づく画面全体の視覚的オーバーレイ・マスク情報を保持します。
- **主要なプロパティ:**
    - `dungeonId`: 対象のダンジョンID。
    - `floorNumber`: フロア階層。
    - `anomalyType`: 発生中の環境異変種別（例: `SOLAR_FLARE`, `MIASMA_STORM`, `NONE`）。
    - `lightLevel`: フロア全体の明るさレベル（`FULL_LIGHT`, `DIM_LIGHT`, `DARKNESS`）。
    - `activeFilterCode`: クライアント表示用のシェーダー／カラーフィルターコード。

### `FogOfWarDomain`
- **説明:** プレイヤー個別のダンジョン踏破状況および現在視界範囲を管理する値オブジェクトです。
- **視界状態区分 (`VisibilityEnum`):**
    - `UNEXPLORED`: 未踏・未発見タイル。黒塗り描画。
    - `EXPLORED`: 過去に通過・可視化された既知タイル。地形のみ薄暗く表示（エンティティ・罠は非表示）。
    - `VISIBLE`: 現在のプレイヤー視界内（半径内）。リアルタイムで全レイヤーを描画。

### `ViewportCoordinateDomain`
- **説明:** クライアント（UI）画面に表示される描画領域（ビューポート: 標準 15×11 セル等）の中心座標（カメラ位置）およびバウンディングボックス情報を表現します。
- **主要なプロパティ:**
    - `centerX`: カメラの中心 X 座標（通常はプレイヤー位置）。
    - `centerY`: カメラの中心 Y 座標（通常はプレイヤー位置）。
    - `viewWidth`: ビューポートの横幅セル数（例: 15）。
    - `viewHeight`: ビューポートの縦幅セル数（例: 11）。
    - `minX`, `maxX`, `minY`, `maxY`: ビューポートの可視境界座標。

---

## 3. API仕様 (API Specifications)

Displayモジュールが提供する描画用APIエンドポイントのリクエスト・レスポンス定義です。

### 3.1 プレイヤー周辺表示データ取得 (`GET /api/v1/display/fields/{userId}`)
指定したプレイヤーの現在のビューポート領域内における、多層レイヤー構造の描画データ（`DisplayData`）を取得します。

#### レスポンス JSON スキーマ (200 OK)
```json
{
  "userId": "user_12345",
  "dungeonId": "dungeon_frontier_01",
  "floorNumber": 3,
  "viewport": {
    "centerX": 10,
    "centerY": 15,
    "viewWidth": 15,
    "viewHeight": 11,
    "minX": 3,
    "maxX": 17,
    "minY": 10,
    "maxY": 20
  },
  "cells": [
    {
      "x": 10,
      "y": 15,
      "visibility": "VISIBLE",
      "layers": {
        "GROUND": { "display": ".", "color": "#707070" },
        "PLAYER": { "display": "@", "color": "#FFFFFF" }
      }
    },
    {
      "x": 11,
      "y": 15,
      "visibility": "VISIBLE",
      "layers": {
        "GROUND": { "display": ".", "color": "#707070" },
        "OBJECT": { "display": "!", "color": "#FF5555" }
      }
    }
  ]
}
```

---

### 3.2 画面オーバーレイ情報取得 (`GET /api/v1/display/overlay/{userId}`)
指定したプレイヤーの現在フロアにおける環境異変および視界（Fog of War）フィルター情報を取得します。

#### レスポンス JSON スキーマ (200 OK)
```json
{
  "userId": "user_12345",
  "dungeonId": "dungeon_frontier_01",
  "floorNumber": 3,
  "overlay": {
    "anomalyType": "SOLAR_FLARE",
    "lightLevel": "FULL_LIGHT",
    "activeFilterCode": "FILTER_SUNNY_RED",
    "description": "太陽フレアにより炎属性威力が増加し、マップ全域が明るく見えています。"
  }
}
```

---

### 3.3 リアルタイム描画ストリーミング (`GET /api/v1/display/stream/{userId}`)
SSE (Server-Sent Events) を使用し、プレイヤーの移動、戦闘アクション、モンスター行動、および環境変化に伴う画面更新イベントをリアクティブにストリーミング配信します。

#### SSE レスポンス イベント例
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache
Connection: keep-alive

event: displayUpdate
data: {"userId":"user_12345","timestamp":1774958400000,"changedCells":[{"x":11,"y":15,"visibility":"VISIBLE","layers":{"GROUND":{"display":"."},"MONSTER":{"display":"k","color":"#00FF00"}}}]}
```

---

## 4. エラーハンドリング仕様 (Error Handling Specification)

DisplayモジュールのAPI実行時に発生する主要なエラーコードおよび対応するHTTPステータスコードの一覧です。

### 4.1 エラーコード一覧およびマッピング

| エラーコード | HTTPステータス | 発生条件・説明 |
| :--- | :---: | :--- |
| `PLAYER_NOT_FOUND` | `404 Not Found` | 指定された `userId` のプレイヤーが存在しない場合。 |
| `VIEWPORT_OUT_OF_BOUNDS` | `400 Bad Request` | 要求されたビューポート座標がダンジョンフロア範囲を超過している場合。 |
| `DUNGEON_NOT_ACTIVE` | `422 Unprocessable Entity` | プレイヤーが現在アクティブなダンジョンに滞在していない場合。 |
| `STREAM_CONNECTION_FAILED` | `500 Internal Server Error` | SSE ストリーミングセッションの確立またはデータ送信に失敗した場合。 |
| `UNAUTHORIZED_DISPLAY_REQUEST` | `403 Forbidden` | 他プレイヤーの非公開ビューポート情報への不正アクセスを検知した場合。 |

### 4.2 エラーレスポンス共通 JSON スキーマ例
```json
{
  "errorCode": "DUNGEON_NOT_ACTIVE",
  "message": "プレイヤーは現在ダンジョン内に滞在していないため、描画データを取得できません。",
  "status": 422,
  "timestamp": "2026-03-31T12:00:00Z"
}
```
