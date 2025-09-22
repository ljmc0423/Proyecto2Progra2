package GameLogic;

import java.io.IOException;

public final class SokobanGame extends Game {

    private TileMap map;
    private Player player;
    private int currentLevel = 0;
    private boolean victory;

    @Override
    public void startLevel(int id) {
        this.currentLevel = id;
        String path = "levels/level0" + id + ".txt";
        LevelLoader loader = new LevelLoader(path);
        try {
            LevelData data = loader.load();
            this.map = data.getTileMap();
            this.player = new Player(data.getPlayerStartX(), data.getPlayerStartY());
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
        for (int y = 0; y < GameConfig.ROWS; y++) {
            for (int x = 0; x < GameConfig.COLS; x++) {
                if (map.getTile(x, y) == TileMap.BOX || map.getTile(x, y) == TileMap.ELEVATOR
                        || map.getTile(x, y) == TileMap.ELEVATOR_BUTTONS) {
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
