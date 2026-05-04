package net.hero.rogueb.dungeon.domain;

import java.util.List;

/**
 * ダンジョンクリア時にプレイヤーに与えられる報酬を定義する値オブジェクト。
 */
public record ClearRewardDomain(
        int gold,
        List<String> itemInstanceIds
) {
}
