package net.hero.rogueb.bookofadventureclient.o;

import java.util.Objects;

/**
 * プレイヤーやモンスターに付与される状態異常を定義するデータ転送オブジェクト。
 */
public record StatusEffectDto(
        String type,
        int remainingTurns,
        int value
) {
    public StatusEffectDto {
        Objects.requireNonNull(type);
    }
}
