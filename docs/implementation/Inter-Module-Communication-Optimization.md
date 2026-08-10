# モジュール間通信最適化仕様 (Inter-Module Communication Optimization)

## 1. 概要
本ドキュメントは、`rogueb` マイクロサービス群におけるサービス間（モジュール間）通信のオーバーヘッドを削減し、システム全体の遅延（レイテンシ）を極限まで低減させるための「モジュール間通信最適化」に関する技術提案仕様です。

本提案は、[検討事項・TODOリスト](../TODO-Details.md)の「モジュール間通信の最適化」課題を解決し、[実装詳細](Implementation-Details.md)および[最適化戦略](Optimization-Strategy.md)に定義された非ブロッキングリアクティブシステムをさらに高度化することを目的とします。

---

## 2. 現状分析と課題
現在、BFF である `PlayerOperations` と各ドメインサービス（`Dungeon`, `Objects`, `BookOfAdventure`）間の通信、およびサービス同士の連携には、主に HTTP/1.1 ベースの REST API（JSON 形式）が使用されています。

### 2.1 現状アーキテクチャの課題
- **HTTP/1.1 のオーバーヘッド**: リクエストごとにコネクションの確立・切断オーバーヘッド（またはキープアライブ制御）が発生し、ヘッダー圧縮が行われないため帯域および CPU 資源を浪費します。
- **シリアライズ・デシリアライズ負荷**: テキストベースの JSON フォーマットは可読性が高い一方で、シリアライズ（オブジェクトから文字列への変換）とデシリアライズ（文字列からオブジェクトへの変換）の処理コストが極めて高く、高頻度な戦闘・ターン更新アクションにおいて CPU ボトルネックとなります。
- **データ重複**: 各マイクロサービス間でお互いのドメインオブジェクトの一部を重複して保持・送信しており、ペイロードサイズが肥大化しています。

---

## 3. 技術選定：REST、gRPC、共有メモリ
通信性能を最大化するため、ユースケースやデプロイ構成に応じて複数の最適化アプローチを使い分けるハイブリッドアプローチを提案します。

| 通信方式 | メリット | デメリット | 主なユースケース |
| :--- | :--- | :--- | :--- |
| **REST (HTTP/1.1 + JSON)** | 実装・デバッグが極めて容易、外部システム（フロントエンド）との高い親和性。 | 低スループット、高遅延、CPUシリアライズ負荷が高い。 | BFF（外部向け公開API）、管理者ツール等の非リアルタイム通信。 |
| **gRPC (HTTP/2 + Protobuf)** | 高効率バイナリプロトコル、双方向ストリーミング、CPU負荷が極めて低い、自動コード生成。 | デバッグ時にツールが必要、ブラウザからの直接接続には中継が必要。 | サービス間（ドメインモジュール間）の高頻度・低遅延内部RPC。 |
| **共有メモリ (Shared Memory)** | ネットワークスタックを完全にバイパス（ゼロコピー）、極限の超低遅延（マイクロ秒以下）。 | 同一ノード（ローカル）配備に限定、共有メモリの排他制御・管理コスト。 | 単一サーバー（ローカル）での密結合モジュール間通信（Dungeon ⇔ Objects）。 |

### 3.1 決定事項 (Architectural Decisions)
1. **マイクロサービス間の内部 RPC 推進**: サービス間連携のプロトコルを REST から **gRPC (Protocol Buffers)** へ段階的に移行します。
2. **ローカル配備時の共有メモリ活用**: 同一コンテナ/VM 上にサービスをデプロイする「シングルノードモード」向けに、Java の `MappedByteBuffer` を活用した**共有メモリ IPC メカニズム**を提案します。

---

## 4. リアクティブ gRPC 設計
本プロジェクトは [技術スタック](../tech/Tech-Stack.md) および[アーキテクチャ設計](../tech/Architecture.md)の通り、Spring WebFlux / Project Reactor を基盤とするリアクティブスタックを採用しています。したがって、gRPC の導入においても Project Reactor とシームレスに統合できる **Reactor gRPC**（または `grpc-kotlin` / `rsocket` の思想を取り入れたライブラリ）を採用します。

### 4.1 リアクティブ・データフロー
非同期ストリームである `Mono` および `Flux` を gRPC の `Unary` および `Streaming` 呼び出しに直接マッピングします。

```
[BFF / PlayerOperations]                 [Dungeon Service (gRPC Server)]
  Mono<PlayerAction> --- (gRPC Unary) --->
                                           Mono<ActionResult>
  Flux<DungeonEvent> <--- (Server Stream) -
```

- **メリット**: スレッドブロッキングを一切発生させず、Project Reactor のバックプレッシャー（Backpressure）制御やリアクティブオペレータ（`map`, `flatMap`, `zip` 等）をそのまま活用可能です。

---

## 5. 共有メモリ（Shared Memory）IPC 設計
同一ノード上で稼働する超高速シナリオ向けに、ネットワークカード（NIC）や OS の TCP/IP ループバックを介さない共有メモリ（Shared Memory）通信を設計します。

### 5.1 アーキテクチャ概要
Java の `java.nio.channels.FileChannel` と `MappedByteBuffer`（オフヒープメモリ）を使用し、固定サイズの循環リングバッファ（Ring Buffer）をメモリマップドファイル上に構築します。

```
+--------------------------------------------------------------+
|                    Shared Memory File                        |
|                                                              |
|  +---------------------+           +---------------------+   |
|  |    Ring Buffer A    |           |    Ring Buffer B    |   |
|  |  (Request Queue)    |           |  (Response Queue)   |   |
|  +---------------------+           +---------------------+   |
+--------------------------------------------------------------+
          ^                                       |
          | (Write)                               | (Read)
   [PlayerOperations]                      [Dungeon Service]
```

### 5.2 排他制御とデータ転送
- **LMAX Disruptor パターン**: ロックレス（CAS操作）なシーケンスバリアントを利用し、1プロデューサ・1コンシューマ構成でオーバーヘッドを最小化します。
- **データ形式**: バッファ内には Protobuf でバイナリシリアライズされたデータを直接格納し、共有メモリ上でのシリアライズ・デシリアライズ負荷も最小限に抑えます。

---

## 6. プロトタイプ API 定義 (Protobuf)
内部最適化のために最初に移行対象とする、主要サービス間の API の Protocol Buffers (`.proto`) 定義案です。

### 6.1 `dungeon_service.proto`
```protobuf
syntax = "proto3";

package net.hero.rogueb.dungeon.grpc;

option java_multiple_files = true;
option java_package = "net.hero.rogueb.dungeon.grpc";
option java_outer_classname = "DungeonServiceProto";

// ダンジョンの位置表現
message Coordinate2DProto {
  int32 x = 1;
  int32 y = 2;
}

// プレイヤー位置同期リクエスト
message SyncLocationRequest {
  string player_id = 1;
  string dungeon_id = 2;
  int32 level = 3;
  Coordinate2DProto coordinate = 4;
}

// 同期レスポンス
message SyncLocationResponse {
  bool success = 1;
  string message = 2;
  repeated string visible_monster_ids = 3;
}

// ダンジョン生成シード設定
message GenerateFloorRequest {
  string dungeon_id = 1;
  int32 level = 2;
  int32 room_count_seed = 3;
  int32 item_seed = 4;
  int32 monster_seed = 5;
  int32 trap_seed = 6;
}

message GenerateFloorResponse {
  bool success = 1;
  int32 width = 2;
  int32 height = 3;
}

service DungeonGrpcService {
  // プレイヤーの位置情報を超高速に同期 (Unary)
  rpc SyncPlayerLocation (SyncLocationRequest) returns (SyncLocationResponse);

  // 階層のシード値生成リクエスト
  rpc GenerateFloor (GenerateFloorRequest) returns (GenerateFloorResponse);
}
```

---

## 7. 移行計画とフォールバック戦略
システムの安定稼働を確保するため、一括移行ではなく「ハイブリッド移行」および「段階的切替」を推奨します。

### 7.1 ハイブリッド移行フェーズ
1. **ダブルプロトコルサポート**: 各ドメインサービスにおいて、既存の HTTP/REST コントローラと、新しい gRPC サービスエンドポイントを同時に立ち上げます。
2. **選択的ルーティング**: BFF (`PlayerOperations`) 内の `Client` クラスにおいて、プロパティ設定（例：`communication.protocol=grpc`）により、同一インターフェースから REST もしくは gRPC 呼び出しを動的に切り替えられる構成にします。

```
              +-------------------------+
              |   PlayerOperations BFF  |
              +-------------------------+
                           |
            +--------------+--------------+
            | (grpc)                      | (rest)
            v                             v
+-----------------------+     +-----------------------+
|  DungeonService gRPC  |     |  DungeonService REST  |
+-----------------------+     +-----------------------+
```

### 7.2 自動フォールバック（レジリエンス設計）
- gRPC サーバーの起動失敗、または接続タイムアウト（ネットワーク瞬断等）を検知した場合、システムは自動的かつ瞬時に既存の HTTP/REST エンドポイントへの呼び出しに縮退運転（フォールバック）します。これにより、インフラトラブル時にもゲームプレイへの影響を最小限に抑えます。

---

## 8. ベンチマーク・測定手法
最適化の効果を定量的に実証するため、以下の測定手法に基づいて評価を行います。

### 8.1 測定指標 (Metrics)
- **スループット (Throughput)**: 1秒あたりの処理完了リクエスト数 (RPS)。
- **レイテンシ (Latency)**: API 呼び出しの平均時間、およびパーセンタイル値（p50, p95, p99）。
- **CPU/メモリ使用率**: 通信負荷増大時における JVM 内の GC 発生頻度とシリアライズに伴う CPU 負荷割合。

### 8.2 テスト実施計画
1. **JMeter / ghz による負荷テスト**:
   gRPC 専用のベンチマークツールである `ghz` を用い、10,000回以上の高並列リクエストを同時に投入。
2. **測定シナリオ**:
   - シナリオA: 既存の WebClient + REST (JSON)
   - シナリオB: Reactor gRPC (Protobuf)
   - シナリオC: 共有メモリ IPC
3. **期待される効果**:
   gRPC の適用により、REST 構成と比較して、レイテンシは **40% 以上削減**、スループットは **2倍以上向上** することを見込みます。

---

## 9. 今後の検討事項
- **シリアライズの最適化**: Protobuf 以外にも、シリアライズゼロを謳う FlatBuffers の採用検討。
- **K8s デプロイ連携**: Kubernetes 環境下での gRPC ロードバランシング（Linkerd や Envoy などのサービスメッシュ連携）の設計。
