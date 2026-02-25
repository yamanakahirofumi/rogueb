# 実装詳細 (Implementation Details)

## 1. 座標系
本プロジェクトでは、`Coordinate2D` レコードを使用して 2 次元平面上の位置を表現します。

- **x**: 水平方向の座標
- **y**: 垂直方向の座標
- **z**: 階層（レベル）を表現する場合に使用

座標は (0, 0) を起点とし、ダンジョン範囲内でのバリデーションが行われます。

## 2. 位置管理 (`DungeonLocation`)
プレイヤーやオブジェクトの場所は `DungeonLocation` DTO で一元管理されます。

- `dungeonId`: 所属するダンジョンの ID
- `playerId`: 対象となるプレイヤーの ID
- `level`: 階層
- `coordinate`: `Coordinate2D` による位置

## 3. リアクティブ・スタック
Spring WebFlux および Project Reactor を採用しており、すべてのサービス間通信および DB アクセスはノンブロッキングで行われます。

- **Mono**: 単一の値を返す非同期ストリーム
- **Flux**: 複数の値を返す非同期ストリーム

## 4. モジュール間連携
BFF である `PlayerOperations` が、各ドメインサービス（Dungeon, Objects 等）をオーケストレーションします。

1. `PlayerOperations` がリクエストを受信
2. `BookOfAdventure` から現在の状態を取得
3. `Dungeon` に対して移動やアクションを要求
4. 成功した場合、`BookOfAdventure` の状態を更新
5. クライアントに結果を返却
