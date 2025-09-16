package GameLogic;

public class LevelData {

    private final TileMap tileMap;
    private final int playerStartX;
    private final int playerStartY;

    public LevelData(TileMap tileMap, int playerStartX, int playerStartY) {
        this.tileMap = tileMap;
        this.playerStartX = playerStartX;
        this.playerStartY = playerStartY;
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public int getPlayerStartX() {
        return playerStartX;
    }

    public int getPlayerStartY() {
        return playerStartY;
    }
}
