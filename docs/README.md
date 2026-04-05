# ドキュメント一覧

このディレクトリには、`rogueb` プロジェクトに関する詳細なドキュメントが格納されています。

## 1. フォルダ構成と配置

ドキュメントは内容に応じて以下のいずれかに分類して配置します。

- **`docs/features/`**：機能仕様、ビジネスルール、ドメインモデル、コンポーネント構成など、ユーザーの要求やシステムの全体像に近い内容。
- **`docs/tech/`**：技術スタック、アーキテクチャ、コーディング規約、CI/CDなど、一般的な技術・開発設定に関する内容。
- **`docs/implementation/`**：特定機能の実装方法、データ構造、最適化手法など、詳細な実装に関する内容。

---

## 2. 機能・仕様 (`docs/features/`)
- [コンポーネント構成](features/Components.md)：各モジュールの役割と相互作用
- [ドメインモデル一覧](features/domain_models/Index.md)：各サービスのコアエンティティとデータ構造
- [ゲーム機能概要](features/Game-Features.md)：ゲームの全体コンセプトと基本操作
- [機能仕様書](features/Functional-Specification.md)：二種類のダンジョン、モンスター、経済システムなどの詳細仕様
- [動作環境](features/System-Requirements.md)：必要な技術スタックとスペック
- [UI-UX 設計](features/UI-UX-Design.md)：画面構成、操作フィードバック、デザイン規約
- [開発ロードマップ](features/Development-Roadmap.md)：開発フェーズと優先順位
- [アイテム識別システム](features/Item-Identification-System.md)：アイテムの識別状態とプロセスに関する仕様
- [トラップシステム](features/Trap-System.md)：ダンジョン内の罠の種類と効果に関する仕様
- [モンスタードロップシステム](features/Monster-Drop-System.md)：モンスター撃破時のドロップと流通制限に関する仕様
- [モンスター繁殖システム](features/Monster-Breeding-System.md)：モンスターの繁殖、継承、および孵化に関する仕様
- [スキル・魔法システム](features/Skill-And-Magic-System.md)：スキル、魔法のカテゴリ、コスト、および効果に関する仕様
- [戦闘システム](features/Combat-System.md)：ダメージ計算、状態異常、戦闘アクションの詳細仕様
- [モンスター捕獲システム](features/Monster-Capture-System.md)：モンスターの捕獲、アイテム、および成功率に関する仕様
- [モンスター進化システム](features/Monster-Evolution-System.md)：進化の条件、プロセス、およびステータス・スキルの継承
- [ダンジョン構築・運営システム](features/Dungeon-Construction-System.md)：建築資材、建築モード、および管理者介入に関する仕様

### 2.1 主要ドメインモデル
- [Dungeon](features/domain_models/Dungeon.md) / [MongoDB](features/domain_models/Dungeon-MongoDB.md)
- [World](features/domain_models/World.md)
- [Objects](features/domain_models/Objects.md) / [MongoDB](features/domain_models/Objects-MongoDB.md)
- [Monster](features/domain_models/Monster.md) / [MongoDB](features/domain_models/Monster-MongoDB.md)
- [BookOfAdventure](features/domain_models/Book-Of-Adventure.md) / [MongoDB](features/domain_models/Book-Of-Adventure-MongoDB.md)
- [PlayerOperations](features/domain_models/Player-Operations.md)
- [経済システム](features/domain_models/Economic-System.md) / [MongoDB](features/domain_models/Economic-System-MongoDB.md)

## 3. 一般的な技術・開発設定 (`docs/tech/`)
- [アーキテクチャ設計](tech/Architecture.md)：システムのパッケージ構造と主要クラスの責務
- [エラーハンドリング方針](tech/Error-Handling-Policy.md)：基本方針と各ケースでの対応
- [ロギング方針](tech/Logging-Policy.md)：デバッグおよび保守のためのログ出力指針
- [技術スタック](tech/Tech-Stack.md)：使用している言語、ライブラリ、ツールなどの情報
- [DB 比較ガイド](tech/Database-Comparison-Guide.md)：MongoDB と RDB の特性比較と選定基準
- [CI 設定](tech/CI-Setting.md)：GitHub Actions を利用した自動ビルドとテストの設定について
- [テストルール](tech/Test-Rule.md)：テストケース作成の一般的なガイドライン
- [品質方針](tech/Quality-Policy.md)：フェーズ（仕様未確定/確定）に応じた品質の考え方と到達目標
- [配布方法](tech/Distribution-Method.md)：カスタム JRE による配布パッケージの作成について
- [コーディング規約](tech/Coding-Convention.md)：クラス作成基準（record, final の使用等）について
- [仕様書の書き方ルール](tech/Specification-Rule.md)：本プロジェクトにおけるドキュメント作成基準
- [TODOリストの書き方ルール](tech/TODO-Rule.md)：検討事項の追加・更新ルール

## 4. 特定機能の実装方法 (`docs/implementation/`)
- [実装詳細](implementation/Implementation-Details.md)：座標系やモジュール間連携の詳細
- [リアルタイム同期プロトコル](implementation/Real-time-Synchronization.md)：SSE を用いた状態同期の仕様
- [JUnit 5 利用ルール](implementation/JUnit-Rule.md)：テストの実装方針と記述例
- [最適化戦略](implementation/Optimization-Strategy.md)：通信、DB、計算処理におけるパフォーマンス向上のための指針


## 5. 検討事項（TODOリスト）
開発を進めるにあたって検討・具体化が必要な事項のリストです。
追加・変更を含む詳細な内容は [検討事項・TODOリスト](TODO-Details.md) を参照してください。
以降の検討事項の更新は、詳細ファイルのみで行います。
