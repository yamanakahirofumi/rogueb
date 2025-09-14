# REST API Endpoints

This document lists the REST API endpoints for the application.

## PlayerOperations

Base Path: `/api`

| Method | Path                                       | Parameters | Description                   |
|--------|--------------------------------------------|------------|-------------------------------|
| GET    | `/user/name/{userName}/exist`              | `userName` (String) | ユーザーが存在するかどうかを確認します。          |
| POST   | `/user/name/{userName}`                    | `userName` (String) | 新しいユーザーを作成します。                |
| GET    | `/player/{userId}`                         | `userId` (String) | プレイヤー情報を取得します。                |
| POST   | `/player/{userId}/command/dungeon/default` | `userId` (String) | プレイヤーをデフォルトのダンジョンに移動します。      |
| PUT    | `/player/{userId}/command/top`             | `userId` (String) | プレイヤーを上に移動します。                |
| PUT    | `/player/{userId}/command/down`            | `userId` (String) | プレイヤーを下に移動します。                |
| PUT    | `/player/{userId}/command/right`           | `userId` (String) | プレイヤーを右に移動します。                |
| PUT    | `/player/{userId}/command/left`            | `userId` (String) | プレイヤーを左に移動します。                |
| PUT    | `/player/{userId}/command/top-right`       | `userId` (String) | プレイヤーを右上に移動します。              |
| PUT    | `/player/{userId}/command/top-left`        | `userId` (String) | プレイヤーを左上に移動します。              |
| PUT    | `/player/{userId}/command/down-right`      | `userId` (String) | プレイヤーを右下に移動します。              |
| PUT    | `/player/{userId}/command/down-left`       | `userId` (String) | プレイヤーを左下に移動します。              |
| PUT    | `/player/{userId}/command/pickup`          | `userId` (String) | プレイヤーがアイテムを拾います。              |
| PUT    | `/player/{userId}/command/downStairs`      | `userId` (String) | プレイヤーが階段を降ります。                |
| PUT    | `/player/{userId}/command/upStairs`        | `userId` (String) | プレイヤーが階段を上ります。                |
| GET    | `/fields/{userId}`                         | `userId` (String) | プレイヤーのフィールド情報を取得します。          |
| GET    | `/fields/{userId}/now`                     | `userId` (String) | プレイヤーの現在のフィールド情報を取得します。      |
| GET    | `/fields/{userId}/info`                    | `userId` (String) | ダンジョン情報を取得します。                |

## Objects

Base Path: `/api/objects`

| Method | Path                           | Parameters | Description                     |
|--------|--------------------------------|------------|---------------------------------|
| GET    | `/instance/{id}`               | `id` (String) | オブジェクト情報を取得します。              |
| POST   | `/list`                        | Body: `Collection<String>` | オブジェクトのリストを取得します。            |
| POST   | `/create/count/{count}`        | `count` (int), Body: `String` | オブジェクトを作成します。                |
| POST   | `/instance/{id}/`              | `id` (String), Body: `String` | オブジェクトの履歴を追加します。            |

## Book of Adventure

Base Path: `/api/user`

| Method | Path                      | Parameters | Description                |
|--------|---------------------------|------------|----------------------------|
| GET    | `/name/{userName}/exist`  | `userName` (String) | ユーザーが存在するかどうかを確認します。     |
| PUT    | `/id/{userId}`            | `userId` (String), Body: `PlayerDomain` | プレイヤーデータを保存します。           |
| POST   | `/name/{userName}`        | `userName` (String), Body: `Map<String, Object>` | 新しいユーザーを作成します。           |
| POST   | `/id/{userId}/items`      | `userId` (String), Body: `List<String>` | プレイヤーのアイテムを変更します。         |
| GET    | `/id/{userId}`            | `userId` (String) | プレイヤー情報を取得します。           |
| GET    | `/id/{userId}/items`      | `userId` (String) | プレイヤーのアイテムリストを取得します。     |

## Dungeon

Base Path: `/api/dungeon/`

| Method | Path                                                      | Parameters | Description                               |
|--------|-----------------------------------------------------------|------------|-------------------------------------------|
| POST   | `/{dungeonId}/go/{playerId}`                              | `dungeonId` (String), `playerId` (String) | プレイヤーをダンジョンに移動します。                    |
| POST   | `/{dungeonId}/move/{playerId}/{level}/{fromX}/{fromY}/{toX}/{toY}` | `dungeonId` (String), `playerId` (String), `level` (int), `fromX` (int), `fromY` (int), `toX` (int), `toY` (int) | プレイヤーを移動します。                          |
| GET    | `/{dungeonId}/what/{playerId}/{level}/{x}/{y}`            | `dungeonId` (String), `playerId` (String), `level` (int), `x` (int), `y` (int) | プレイヤーの足元にあるものを確認します。                |
| POST   | `/{dungeonId}/upstairs/{playerId}/{level}/{x}/{y}`        | `dungeonId` (String), `playerId` (String), `level` (int), `x` (int), `y` (int) | プレイヤーが階段を上ります。                      |
| POST   | `/{dungeonId}/downstairs/{playerId}/{level}/{x}/{y}`      | `dungeonId` (String), `playerId` (String), `level` (int), `x` (int), `y` (int) | プレイヤーが階段を降ります。                      |
| POST   | `/{dungeonId}/pickup/gold/{playerId}/{level}/{x}/{y}`     | `dungeonId` (String), `playerId` (String), `level` (int), `x` (int), `y` (int) | プレイヤーがゴールドを拾います。                    |
| POST   | `/{dungeonId}/pickup/object/{playerId}/{level}/{x}/{y}`   | `dungeonId` (String), `playerId` (String), `level` (int), `x` (int), `y` (int) | プレイヤーがオブジェクトを拾います。                  |
| GET    | `/{dungeonId}/name`                                       | `dungeonId` (String) | ダンジョン名を取得します。                        |
| GET    | `/{dungeonId}/display/{playerId}/{level}/{x}/{y}`         | `dungeonId` (String), `playerId` (String), `level` (int), `x` (int), `y` (int) | 表示データを取得します。                          |
| GET    | `/name/{dungeonName}`                                     | `dungeonName` (String) | ダンジョン名で検索します。                        |
| PUT    | `/name/{dungeonName}`                                     | `dungeonName` (String) | ダンジョン情報を保存します。                      |

## World

Base Path: `/api/world`

| Method | Path                | Parameters | Description                |
|--------|---------------------|------------|----------------------------|
| GET    | `/dungeon/init`     | -          | スタートダンジョン情報を取得します。     |
| POST   | `/service`          | Body: `ServiceInfo` | サービスを登録します。             |
