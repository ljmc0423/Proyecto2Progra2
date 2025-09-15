package GameLogic;

public final class GameConfig {

    //tamaño de una casilla
    public static final int TILE_SIZE = 16;

    //tamaño del mundo en casillas
    public static final int COLS = 32;
    public static final int ROWS = 18;

    //tamaño del mundo en píxeles
    public static final int PX_WIDTH = TILE_SIZE * COLS;
    public static final int PX_HEIGHT = TILE_SIZE * ROWS;

    private GameConfig() {} //lo dejo en privado pues no debería usarse
}
