package net.hero.rogueb.dungeon.fields;

public class DungeonLocation {
    private final String dungeonId;
    private final String playerId;
    private final int level;
    private final Coordinate2D coordinate2D;

    public DungeonLocation(String dungeonId, String playerId, int level, int x, int y) {
        this.dungeonId = dungeonId;
        this.playerId = playerId;
        this.level = level;
        this.coordinate2D = new Coordinate2D(x, y);
    }

    public DungeonLocation(String dungeonId, String playerId, int level, Coordinate2D coordinate2D) {
        this.dungeonId = dungeonId;
        this.playerId = playerId;
        this.level = level;
        this.coordinate2D = coordinate2D;
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

    public Coordinate2D getCoordinate2D() {
        return coordinate2D;
    }
}
