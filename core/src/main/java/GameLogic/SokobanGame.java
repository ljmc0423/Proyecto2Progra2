package GameLogic;

public class SokobanGame extends Juego {

    private static final String LEVELS_PATH[] = new String[]{
        "levels/level01.txt",
        "levels/level02.txt",
        "levels/level03.txt",
        "levels/level04.txt",
        "levels/level05.txt",
        "levels/level06.txt",
        "levels/level07.txt"
    };

    public SokobanGame() {
        super(LEVELS_PATH);
    }
}
