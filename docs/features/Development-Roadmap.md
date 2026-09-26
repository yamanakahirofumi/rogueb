# 開発ロードマップ (Development Roadmap)

## 1. 開発フェーズ

### フェーズ 1: MVP (Minimum Viable Product) - **仕様策定完了**
- 基盤となるマイクロサービス群の構築 ([Dungeon](domain_models/Dungeon.md), [World](domain_models/World.md), [Objects](domain_models/Objects.md), [BookOfAdventure](domain_models/Book-Of-Adventure.md), [PlayerOperations](domain_models/Player-Operations.md))
- 基本的な 8 方向移動、アイテム拾得・ドロップ、階段昇降、ステータス管理の実装
- MongoDB による状態永続化およびインデックス最適化 ([Dungeon-MongoDB](domain_models/Dungeon-MongoDB.md), [Objects-MongoDB](domain_models/Objects-MongoDB.md), [Book-Of-Adventure-MongoDB](domain_models/Book-Of-Adventure-MongoDB.md))
- コア API リクエスト・レスポンス JSON スキーマおよびエラーコードマッピング策定

### フェーズ 2: ゲーム性の向上 - **仕様策定完了**
- [戦闘システム](Combat-System.md) (攻撃、投擲、待機、ダメージ計算、状態異常、成長式)
- [アイテム識別システム](Item-Identification-System.md) (未識別状態、識別の巻物、知識継承、店鑑定)
- [アイテムエンチャントシステム](Item-Enchantment-System.md) & [セット装備システム](Equipment-Set-System.md) (能力付与、セット効果)
- [ダンジョン生成システム](Dungeon-Generation-System.md) (部屋・通路型、セル・オートマトン洞窟、迷路、大部屋、シード管理)
- [トラップシステム](Trap-System.md) (作動判定、属性トラップ、設置ルール)
- [スキル・魔法システム](Skill-And-Magic-System.md) (コスト、射程、特効補正、移動・移動系ユーティリティ魔法)
- [経済システム](domain_models/Economic-System.md) & [倉庫システム](Storage-System.md) (市場価格算出、ショップ経営、段階的倉庫拡張)

### フェーズ 3: 高度なシステムとエコシステム - **仕様策定完了**
- **モンスター育成・生態系**:
  - [捕獲](Monster-Capture-System.md), [繁殖](Monster-Breeding-System.md), [進化](Monster-Evolution-System.md), [退化](Monster-Degeneration-System.md), [融合](Monster-Fusion-System.md)
  - [忠誠度](Monster-Loyalty-System.md), [親愛](Monster-Affection-System.md), [遠征](Monster-Expedition-System.md), [作戦指示](Monster-Tactical-Directives-System.md), [図鑑・博物誌](Monster-Encyclopedia-System.md)
- **モンスター特性システム**:
  - [基本特性](Monster-Trait-System.md), [特性強化](Monster-Trait-Enhancement-System.md), [特性抽出](Monster-Trait-Extraction-System.md), [連携特性](Monster-Synergy-Trait-System.md)
- **ダンジョン構築・世界間連携**:
  - [ダンジョン構築・運営](Dungeon-Construction-System.md), [ダンジョンランク](Dungeon-Rank-System.md), [独自ルール](Dungeon-Custom-Rule-Specification.md)
  - [モンスター化・PK](Monster-PK-System.md), [世界間連携](World-Interoperability-System.md)
- **手触り微調整 & 通信最適化**:
  - [UI-UX 設計](UI-UX-Design.md) (先行入力、ヒットストップ、スクリーンシェイク、SE発火タイミング、手触り設定制御)
  - [モジュール間通信最適化](../implementation/Inter-Module-Communication-Optimization.md) (リアクティブ gRPC、共有メモリ)

## 2. 優先順位
1. バックエンドエンジン・コアマイクロサービスの Java 21 / Spring Boot 4.0 による実装および単体・結合テスト
2. リアクティブ gRPC によるモジュール間通信およびイベント同期の実装
3. フロントエンド（クライアント）のプロトタイプ構築およびプレイ感覚（手触り）パラメータの微調整・検証
4. 世界間連携（クロスワールド・マイグレーション）およびトラストポリシーの分散検証
