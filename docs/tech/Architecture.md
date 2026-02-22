# アーキテクチャ設計

本プロジェクトでは、2Dオープンワールドの複雑な状態管理と描画、およびユーザー入力を効率的に処理するため、MVC (Model-View-Controller) パターンをベースに、ECS (Entity Component System) 的な考え方を取り入れた設計を採用します。

## 1. ディレクトリ・パッケージ構造
標準的な Maven 構造および JavaFX のモジュール・システム（JPMS）に準拠した構成を推奨します。

```
.
├── pom.xml                # プロジェクト構成 (Maven)
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── module-info.java  # モジュール定義
│   │   │   └── com.example.terraria
│   │   │       ├── Main.java     # エントリーポイント
│   │   │       ├── model/        # ゲームの状態・ロジック
│   │   │       │   ├── world/    # タイル、バイオーム、生成
│   │   │       │   ├── entity/   # プレイヤー、モンスター
│   │   │       │   └── item/     # アイテム、インベントリ、クラフト
│   │   │       ├── view/         # JavaFX UI コンポーネント・レンダリング
│   │   │       ├── controller/   # 入力（キーボード・マウス）制御
│   │   │       ├── service/      # セーブ・ロード、設定管理
│   │   │       └── util/         # 数学ユーティリティ、定数
│   │   └── resources
│   │       └── com.example.terraria
│   │           ├── sprites/      # タイル・アイテム・エンティティ画像
│   │           └── css/          # UI スタイルシート
│   └── test
│       └── java
│           └── com.example.terraria # ユニットテスト
```

## 2. 主要コンポーネントの責務

### 2.1 Model 層
- **World**：タイルの 2次元グリッドを保持し、時間経過、地形更新、衝突判定ロジックを担当します。
- **Entity**：プレイヤーやモンスターの抽象クラス。HP、座標、速度などの属性と、移動や攻撃の基本ロジックを持ちます。
- **Item/Inventory**：アイテムの定義と、インベントリの管理（追加、削除、移動）を行います。

### 2.2 View 層
- **GameRenderer**：JavaFX の `Canvas` を使用し、ワールドのタイル、エンティティ、エフェクトをフレームごとに描画します。
- **UIManager**：HPバー、インベントリスロット、ホットバーなどの UI要素を JavaFX のコンポーネント（FXML等）で管理します。

### 2.3 Controller 層
- **InputController**：キーボードおよびマウスイベントを監視し、移動、ジャンプ、アイテム使用、採掘などのアクションをモデルへ通知します。
- **GameLoop**：`AnimationTimer` を使用し、一定間隔で `model.update()` と `view.render()` を呼び出します。

## 3. デザイン方針
- **ECS (Entity Component System) の検討**：エンティティが増えた場合の拡張性を考慮し、機能（物理演算、AI、描画）をコンポーネント化することを検討します。
- **不変性 (Immutability)**：アイテムデータなどの定義データについては `record` を活用し、不変性を担保します。
- **JavaFX のスレッドルール**：ゲームロジックの更新と描画は UI スレッド（JavaFX Application Thread）で行いますが、地形生成やセーブ・ロードなどの重い処理は別スレッドで行います。
