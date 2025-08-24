package net.hero.rogueb.dungeon.fields;

public class DungeonLocation {
    private final String dungeonId;
    private final String playerId;
    private final int level;
    private final Coordinate coordinate;

    public DungeonLocation(String dungeonId, String playerId, int level, int x, int y) {
        this.dungeonId = dungeonId;
        this.playerId = playerId;
        this.level = level;
        this.coordinate = new Coordinate2D(x, y);
    }

    public DungeonLocation(String dungeonId, String playerId, int level, Coordinate coordinate) {
        this.dungeonId = dungeonId;
        this.playerId = playerId;
        this.level = level;
        this.coordinate = coordinate;
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
