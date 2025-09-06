package GameLogic;

public class GameConfig {

    public static final int TILE_SIZE = 16; //cuanto mide una casilla
    
    //tamaño del mundo en casillas
    public static final int COLS = 16;
    public static final int ROWS = 12;
    
    //px (en el mundo lógica)
    public static final int PX_WIDTH = TILE_SIZE * COLS;
    public static final int PX_HEIGHT = TILE_SIZE * ROWS;
    
    //lo pongo privado porque al final todo lo que hay acá son constantes
    private GameConfig() {
    }
}
