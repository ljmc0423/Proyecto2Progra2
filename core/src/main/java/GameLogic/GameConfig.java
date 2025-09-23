package GameLogic;

public final class GameConfig {

    public static final int TILE_SIZE = 16;

    //tamaño de nivel
    public static final int COLS = 32;
    public static final int ROWS = 18;

    //tamaño de nivel en pixeles
    public static final int PX_WIDTH = TILE_SIZE * COLS;
    public static final int PX_HEIGHT = TILE_SIZE * ROWS;

    private GameConfig() {}
}
