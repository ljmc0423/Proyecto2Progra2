package GameLogic;

public class MapCopy {

    public static TileMap copy(TileMap originalMap) {
        char mapCopy[][] = new char[GameConfig.ROWS][GameConfig.COLS];
        
        //basicamente esto crea una copia
        for (int y = 0; y < GameConfig.ROWS; y++) {
            for (int x = 0; x < GameConfig.COLS; x++) {
                mapCopy[y][x] = originalMap.getTile(x, y);
            }
        }
        return new TileMap(mapCopy);
    }
}
