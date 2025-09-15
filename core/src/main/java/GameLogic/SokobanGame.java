package GameLogic;

import java.io.IOException;

public final class SokobanGame extends Game {

    private TileMap map;
    private Player player;
    private int currentLevel = 1;
    private boolean victory;

    @Override
    public void startLevel(int id) {
        this.currentLevel = id;
        String path = "levels/level0" + id + ".txt";
        LevelLoader loader = new LevelLoader(path);
        try {
            LevelData data = loader.load();
            this.map = data.getTileMap();
            this.player = new Player(data.getPlayerInitialPosition());
            this.victory = checkVictory();
        } catch (IOException e) {
            throw new RuntimeException("Error cargando nivel: " + path, e); 
        }
    }

    @Override
    public boolean isVictory() {
        return victory;
    }

    @Override
    public int getPoints() {
        return 0;
    }

    public void recomputeVictory() {
        this.victory = checkVictory();
    }

    private boolean checkVictory() {
        //si hay cajas, significa que aún no se ha completado el nivel
        for (int y = 0; y < GameConfig.ROWS; y++) {
            for (int x = 0; x < GameConfig.COLS; x++) {
                if (map.getTile(x, y) == TileMap.BOX) {
                    return false;
                }
            }
        }
        return true;
    }

    public TileMap getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
