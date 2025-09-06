package GameLogic;

public class TileMap {

    private char tileMap[][];

    public static final char WALL = '#';
    public static final char FLOOR = '.';
    public static final char TARGET = '+';
    public static final char BOX = '$';
    public static final char PLAYER = '@';
    public static final char BOX_ON_TARGET = '*';
    public static final char PLAYER_ON_TARGET = '&';

    public TileMap(char tileMap[][]) {
        this.tileMap = tileMap;
    }

    public char getTile(int x, int y) {
        return tileMap[y][x];
    }

    public void setTile(int x, int y, char c) {
        tileMap[y][x] = c;
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < GameConfig.COLS && y >= 0 && y < GameConfig.ROWS;
    }

    public char[][] getTileMap() {
        return tileMap;
    }
}
