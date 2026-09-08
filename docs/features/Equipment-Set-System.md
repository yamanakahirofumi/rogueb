# セット装備システム仕様 (Equipment Set System Specification)

## 1. 概要

本ドキュメントは、プレイヤーが特定組み合わせの装備品（武器、防具、指輪等）を同時に装備した際に発動する特殊ボーナス「セット効果（Equipment Set Bonus）」の設計と仕様を定義します。

セット装備システムは、単体のステータス性能だけでなく、装備の組み合わせによる戦術的相乗効果（シナジー）をプレイヤーに提供し、装備収集とビルド構築の奥深さを向上させることを目的とします。

---

## 2. セット効果の発動条件と判定メカニズム

### 2.1 基本発動ルール
1. **装備スロットの参照**: プレイヤー（`PlayerDomain`）が現在装備しているアイテム（武器、防具、指輪1、指輪2等）の `ThingInstance.metadata` に含まれる `setId` を参照します。
2. **必要個数（セットカウント）の判定**: 同一の `setId` を持つ装備品の個数をカウントし、セット定義（`SetEquipmentDomain`）に定められた必要装着数（例: 2部位、3部位）を満たしている場合に該当段階のセットボーナスが活性化します。
3. **段階的ボーナス（Partial Set Bonus）**: 3部位構成のセット装備において、2部位装着時に「2部位効果」、3部位装着時に「3部位効果（フルセットボーナス）」が重複して適用（または上位効果へ上書き）されます。

### 2.2 複数セットの重複発動
- **独立適用**: 異なる `setId` を持つセット効果は、それぞれの発動条件を満たしている限り、すべて同時に重複して発動します（例: 「鉄の騎士セット (2部位)」と「剛力の指輪ペア (2部位)」の双方の効果を同時享受可能）。
- **効果の累積**: 同一ステータス補正（例: 攻撃力 +5）が複数のセットから付与された場合、それらの値は加算されます。

---

## 3. 初期定義セット装備一覧

初期実装として導入される標準的なセット装備の定義およびその効果は以下の通りです。

| セットID | セット名称 | 構成アイテム (typeId) | 必要数 | 発動効果・ボーナス詳細 |
| :--- | :--- | :--- | :---: | :--- |
| `dragon_slayer_set` | ドラゴンキラーセット | `dragon_killer` (武器)<br>`dragon_shield` (防具)<br>`fire_stone` (素材/指輪) | 2部位<br><br>3部位 | ドラゴン系モンスターへの与ダメージ +15%<br>竜の鱗 (Dragon Scales): ドラゴン系からの被ダメージ -20%、火属性耐性 +30% |
| `iron_knight_set` | 鉄の騎士セット | `iron_sword` (武器)<br>`chain_mail` (防具)<br>`def_ring` (指輪) | 2部位<br><br>3部位 | 防御力 (def) +5<br>騎士の誇り (Knight's Pride): 物理ダメージ -10%、ノックバック無効 |
| `high_wizard_set` | 大魔導士セット | `magic_robe` (防具)<br>`fire_stick` または `swap_stick` (杖)<br>`mana_potion` (消費/触媒) | 2部位<br><br>3部位 | 魔法防御力 (magicDef) +8<br>魔力循環 (Mana Circulation): スキル/魔法の消費 MP -20% |
| `shadow_assassin_set` | 影の暗殺者セット | `silver_sword` (武器)<br>`leather_armor` (防具)<br>`speed_potion` (消費/触媒) | 2部位<br><br>3部位 | 回避率 (Evasion) +10%<br>急所狙い (Vital Strike): クリティカル発生率 +15%、クリティカル倍率 1.5x -> 2.0x |
| `sacred_guardian_set` | 聖なる守護者セット | `holy_sword` (武器)<br>`plate_armor` (防具)<br>`holy_stone` (素材/指輪) | 2部位<br><br>3部位 | 闇属性耐性 +20%<br>聖なる加護 (Holy Protection): 聖属性与ダメージ +20%、アンデッド系攻撃時 HP 5% 吸収 |

---

## 4. データ構造とメタデータ表現

### 4.1 アイテムメタデータへの記述 (`ThingInstance.metadata`)
セット装備対象のアイテムには、`metadata` フィールドに以下のキーが保持されます。

```json
{
  "setId": "dragon_slayer_set",
  "setPieceType": "WEAPON"
}
```

### 4.2 セット効果マスター定義 (`SetEquipmentDomain`)
セット効果の計算および表示用のデータモデル構造です。

```json
{
  "setId": "dragon_slayer_set",
  "setName": "ドラゴンキラーセット",
  "description": "竜を狩る者のための伝説的装備セット。",
  "pieces": [
    { "typeId": "dragon_killer", "pieceName": "ドラゴンキラー" },
    { "typeId": "dragon_shield", "pieceName": "ドラゴンシールド" },
    { "typeId": "fire_stone", "pieceName": "炎の石" }
  ],
  "bonuses": [
    {
      "requiredCount": 2,
      "bonusName": "竜狩りの心得",
      "effects": [
        { "type": "SLAYER_BONUS", "targetCategory": "DRAGON", "value": 0.15 }
      ]
    },
    {
      "requiredCount": 3,
      "bonusName": "竜の鱗",
      "effects": [
        { "type": "DAMAGE_REDUCTION_CATEGORY", "targetCategory": "DRAGON", "value": 0.20 },
        { "type": "ELEMENTAL_RESIST", "element": "FIRE", "value": 0.30 }
      ]
    }
  ]
}
```

---

## 5. モジュール間データ連携フロー

セット装備効果の判定・適用は、プレイヤーの装備変更時および戦闘計算時に以下のフローで連携されます。

```
[PlayerOperations モジュール]
  │
  ├─ 1. 装備変更 (Equip/Unequip) アクション検出
  │
  ▼
[Objects モジュール]
  │
  ├─ 2. 装備中アイテムの ThingInstance.metadata から setId 一覧を抽出
  ├─ 3. SetEquipmentDomain と照合し、活性化中のセット効果リストを算出
  │
  ▼
[CombatSystem / PlayerOperations モジュール]
  │
  └─ 4. 戦闘計算 (攻撃力/防御力補正、特効倍率、属性耐性等) にセットボーナスを反映
```

---

## 6. ゲームバランスと制限事項

1. **効果適用上限**: 1つのプレイヤーキャラクターが受けられるセットボーナスの合計枠数に上限はありませんが、部位スロットの制限（武器1、防具1、指輪2等）により自然なバランスが維持されます。
2. **モンスター化/PK時の適用**: [モンスター化・PKシステム](./Monster-PK-System.md) において、プレイヤーがモンスターに変身している間はセット装備効果は一時的に無効化され、モンスター固有のステータスおよび特性が優先されます。
3. **エンチャントとの相乗効果**: [アイテムエンチャントシステム](./Item-Enchantment-System.md) による個別スロットのエンチャント効果と、セット装備ボーナスは重複して計算されます。

---

## 7. API仕様

### 7.1 プレイヤーの活性化中セット効果照会

#### リクエスト
`GET /api/v1/players/{userId}/equipment-sets`

#### レスポンス JSON スキーマ (`200 OK`)
```json
{
  "userId": "user_98765",
  "activeSets": [
    {
      "setId": "dragon_slayer_set",
      "setName": "ドラゴンキラーセット",
      "equippedCount": 3,
      "totalPieces": 3,
      "activeBonuses": [
        {
          "requiredCount": 2,
          "bonusName": "竜狩りの心得",
          "description": "ドラゴン系モンスターへの与ダメージ +15%"
        },
        {
          "requiredCount": 3,
          "bonusName": "竜の鱗",
          "description": "ドラゴン系からの被ダメージ -20%、火属性耐性 +30%"
        }
      ]
    }
  ]
}
```

### 7.2 セット装備マスター定義一覧照会

#### リクエスト
`GET /api/v1/objects/equipment-sets`

#### レスポンス JSON スキーマ (`200 OK`)
```json
{
  "equipmentSets": [
    {
      "setId": "dragon_slayer_set",
      "setName": "ドラゴンキラーセット",
      "description": "竜を狩る者のための伝説的装備セット。",
      "totalPieces": 3,
      "bonusesCount": 2
    },
    {
      "setId": "iron_knight_set",
      "setName": "鉄の騎士セット",
      "description": "堅固な守りを誇る重騎士の標準装備。",
      "totalPieces": 3,
      "bonusesCount": 2
    }
  ]
}
```

---

## 8. エラーハンドリング

セット効果照会および装備処理において発生する異常系エラーの定義です。

| エラーコード | HTTP ステータス | 発生条件 | 解決策 / レスポンス仕様 |
| :--- | :---: | :--- | :--- |
| `PLAYER_NOT_FOUND` | `404 Not Found` | 指定された `userId` のプレイヤーが存在しない場合。 | `{"code": "PLAYER_NOT_FOUND", "message": "指定されたプレイヤーが見つかりません。"}` |
| `INVALID_EQUIPMENT_SLOT` | `400 Bad Request` | 無効な装備スロット番号または未装備スロットを参照した場合。 | `{"code": "INVALID_EQUIPMENT_SLOT", "message": "装備スロットが正しくありません。"}` |
| `EQUIPMENT_SET_NOT_FOUND` | `404 Not Found` | 存在しない `setId` がリクエストされた場合。 | `{"code": "EQUIPMENT_SET_NOT_FOUND", "message": "指定されたセット定義が存在しません。"}` |
