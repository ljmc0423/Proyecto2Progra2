package GameLogic;

public class TileMap {

    private char tileMap[][] = new char[GameConfig.ROWS][GameConfig.COLS];

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

    public int getMapWidth() {
        return GameConfig.COLS;
    }

    public int getMapHeight() {
        return GameConfig.ROWS;
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < GameConfig.COLS && y >= 0 && y < GameConfig.ROWS;
    }

    public char getTile(int x, int y) {
        return tileMap[y][x];
    }

    public void setTile(int x, int y, char ch) {
        tileMap[y][x] = ch;
    }

    public boolean isBox(char ch) {
        return ch == BOX || ch == BOX_ON_TARGET;
    }

    public boolean isFree(char ch) {
        return ch == FLOOR || ch == TARGET;
    }

    public boolean isWall(char ch) {
        return ch == WALL;
    }
}
