# JUnit 5 利用ルール

## 1. 基本方針
本プロジェクトでは、JUnit 5 を標準のテストフレームワークとして使用します。リアクティブなコードのテストには、`StepVerifier` を活用してください。

## 2. 推奨されるライブラリ
- **JUnit 5 (Jupiter)**: テストの実行とアサーション。
- **Mockito**: 依存コンポーネントのモック化。
- **Project Reactor (StepVerifier)**: `Mono` や `Flux` の検証。
- **AssertJ**: より流暢なアサーション記述（任意）。

## 3. テストの記述パターン (AAA)
[テストルール](../tech/Test-Rule.md) に従い、Arrange, Act, Assert の構造で記述します。

```java
@Test
void testMovePlayer() {
    // Arrange
    Player player = new Player("Hero");
    Coordinate destination = new Coordinate(1, 1);

    // Act & Assert (StepVerifier を使用する場合)
    StepVerifier.create(service.move(player, destination))
        .expectNextMatches(loc -> loc.x() == 1 && loc.y() == 1)
        .verifyComplete();
}
```

## 4. モックの活用
外部サービス呼び出し（Client モジュール）を行う場合は、Mockito を使用して適切なレスポンスをシミュレートしてください。

```java
@MockBean
private DungeonClient dungeonClient;

@Test
void testGetDungeon() {
    when(dungeonClient.getDungeon(anyString()))
        .thenReturn(Mono.just(new DungeonDto(...)));
    // ...
}
```

## 5. 命名規則
- テストクラス名: `[テスト対象クラス名]Test`
- テストメソッド名: `test[期待する挙動]` または `[動作]_[条件]_[期待結果]`
