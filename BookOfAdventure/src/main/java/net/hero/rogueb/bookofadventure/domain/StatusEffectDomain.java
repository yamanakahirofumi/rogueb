package net.hero.rogueb.bookofadventure.domain;

import java.util.Objects;

/**
 * プレイヤーやモンスターに付与される状態異常を定義する値オブジェクト。
 */
public record StatusEffectDomain(
        String type,
        int remainingTurns,
        int value
) {
    public StatusEffectDomain {
        Objects.requireNonNull(type);
    }
}
