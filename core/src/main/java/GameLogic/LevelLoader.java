package GameLogic;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class LevelLoader {

    private final File levelFile;

    public LevelLoader(String path) {
        this.levelFile = new File(path);
    }

    public LevelData load() throws IOException {
        char[][] data = new char[GameConfig.ROWS][GameConfig.COLS];
        int startX = -1, startY = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(levelFile))) {
            for (int y = 0; y < GameConfig.ROWS; y++) {
                String line = br.readLine();
                for (int x = 0; x < GameConfig.COLS; x++) {
                    char ch = line.charAt(x);

                    if (ch == TileMap.PLAYER || ch == TileMap.PLAYER_ON_TARGET) {
                        startX = x;
                        startY = y;
                        ch = (ch == TileMap.PLAYER_ON_TARGET) ? TileMap.TARGET : TileMap.FLOOR;
                    }

                    data[y][x] = ch;
                }
            }
        }

        return new LevelData(new TileMap(data), startX, startY);
    }
}
