# 技術スタック

本プロジェクトの開発に使用される技術、ライブラリ、およびツールの一覧です。

## 1. 使用技術一覧

| 分類 | 技術・ツール | バージョン | 備考 |
| :--- | :--- | :--- | :--- |
| 言語 | Java | 21 (LTS) | |
| フレームワーク | Spring Boot | 3.5.5 | WebFlux (Reactive) |
| データベース | MongoDB | - | Reactive Streams |
| データベース | H2 Database | - | Worldモジュール等で使用 |
| ORM / DB Library | Spring Data MongoDB | - | |
| ORM / DB Library | MyBatis | 2.1.2 | Worldモジュールで使用 |
| Migration | Flyway | - | |
| ライブラリ | Project Reactor | - | Reactive Streams 実装 |
| ライブラリ | JUnit 5 | 5.11.x | (Spring Boot Starter 依存) |
| ライブラリ | JaCoCo | - | カバレッジ測定 |
| ビルド | Maven | 3.9 | |
| ツール | Docker / Docker Compose | - | 開発環境（MongoDB等）の構築 |
| 配布 | jlink / jpackage | JDK 標準 | 配布用ランタイム作成 |
