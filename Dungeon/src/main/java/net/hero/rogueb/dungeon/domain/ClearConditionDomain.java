package net.hero.rogueb.dungeon.domain;

/**
 * ダンジョンのクリア判定基準を定義する値オブジェクト。
 */
public record ClearConditionDomain(
        ClearConditionType type,
        String targetValue
) {
    public enum ClearConditionType {
        FLOOR_REACHED, BOSS_DEFEATED, ITEM_ACQUIRED
    }
}
