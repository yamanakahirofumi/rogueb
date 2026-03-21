package net.hero.rogueb.dungeon.fields;

public record DungeonLocation(String dungeonId, String playerId, int level, Coordinate coordinate) {

    public DungeonLocation(String dungeonId, String playerId, int level, int x, int y) {
        this(dungeonId, playerId, level, new Coordinate2D(x, y));
    }

    public String getDungeonId() {
        return dungeonId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getLevel() {
        return level;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
