package net.hero.rogueb.dungeon.domain;

/**
 * ダンジョン内で死亡した際のペナルティ設定を保持する値オブジェクト。
 */
public record DeathPenaltyDomain(
        ItemForfeitureType itemForfeitureType,
        GoldLossType goldLossType,
        int goldLossValue,
        StatusResetType statusResetType
) {
    public enum ItemForfeitureType {
        NONE, RANDOM, ALL
    }

    public enum GoldLossType {
        NONE, FIXED, PERCENTAGE, ALL
    }

    public enum StatusResetType {
        NONE, EXP_REDUCTION, LEVEL_RESET
    }
}
