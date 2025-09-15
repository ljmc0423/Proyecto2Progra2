package GameLogic;

public class LevelData {

    private final TileMap tileMap;
    private final Position playerInitialPosition;

    public LevelData(TileMap tileMap, Position playerInitialPosition) {
        this.tileMap = tileMap;
        this.playerInitialPosition = playerInitialPosition;
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public Position getPlayerInitialPosition() {
        return playerInitialPosition;
    }
}
