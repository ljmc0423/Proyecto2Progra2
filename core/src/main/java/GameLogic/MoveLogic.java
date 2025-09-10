package GameLogic;

import java.util.concurrent.BlockingQueue;

public final class MoveLogic implements Runnable {

    private final TileMap tileMap;
    private final BlockingQueue<Directions> moves;
    private volatile boolean running = true;

    //volatile sirve para que ambos hilos vean los cambios inmediatamente
    private volatile int playerX;
    private volatile int playerY;

    private volatile int moveCount = 0;
    private volatile int pushCount = 0;

    private volatile boolean levelCompleted = false;

    public MoveLogic(TileMap tileMap, BlockingQueue<Directions> moves) {
        this.tileMap = tileMap;
        this.moves = moves;

        for (int y = 0; y < GameConfig.ROWS; y++) {
            for (int x = 0; x < GameConfig.COLS; x++) {
                char c = tileMap.getTile(x, y);
                if (c == TileMap.PLAYER || c == TileMap.PLAYER_ON_TARGET) {
                    playerX = x;
                    playerY = y;
                    tileMap.setTile(x, y, (c == TileMap.PLAYER_ON_TARGET) ? TileMap.TARGET : TileMap.FLOOR);
                }
            }
        }
    }

    public int getMoveCount() {
        return moveCount;
    }

    public int getPushCount() {
        return pushCount;
    }

    public void stop() {
        running = false;
        moves.clear();
        moves.offer(Directions.QUIT);
    }

    @Override
    public void run() {
        try {
            while (running) {
                Directions direction = moves.take();
                if (direction == Directions.QUIT) {
                    break;
                }
                
                if (levelCompleted) {
                    continue;
                }
                
                boolean moved;
                synchronized (tileMap) {
                    moved = applyMove(direction.dx, direction.dy);
                }
                
                if (moved) {
                    isCompleted();
                }
            }
        } catch (InterruptedException ignored) {
        } finally {
            running = false;
        }
    }

    private void isCompleted() {
        for (int y = 0; y < GameConfig.ROWS; y++) {
            for (int x = 0; x < GameConfig.COLS; x++) {
                if (tileMap.getTile(x, y) == TileMap.TARGET) {
                    return;
                }
            }
        }
        
        levelCompleted = true;
    }

    private boolean applyMove(int dx, int dy) {
        int nx = playerX + dx;
        int ny = playerY + dy;

        if (!tileMap.inBounds(nx, ny)) {
            return false;
        }

        char front = tileMap.getTile(nx, ny);

        if (front == TileMap.WALL) {
            return false;
        }

        if (isBox(front)) {
            int bx = nx + dx;
            int by = ny + dy;
            if (!tileMap.inBounds(bx, by)) {
                return false;
            }

            char nextTile = tileMap.getTile(bx, by);
            if (!isFree(nextTile)) {
                return false;
            }

            tileMap.setTile(bx, by, (nextTile == TileMap.TARGET) ? TileMap.BOX_ON_TARGET : TileMap.BOX);

            tileMap.setTile(nx, ny, (front == TileMap.BOX_ON_TARGET) ? TileMap.TARGET : TileMap.FLOOR);

            playerX = nx;
            playerY = ny;

            moveCount++;
            pushCount++;

            return true;
        }

        if (isFree(front)) {
            playerX = nx;
            playerY = ny;
            moveCount++;
            
            return true;
        }
        
        return false;
    }

    private static boolean isBox(char c) {
        return c == TileMap.BOX || c == TileMap.BOX_ON_TARGET;
    }

    private static boolean isFree(char c) {
        return c == TileMap.FLOOR || c == TileMap.TARGET;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }
    
    public boolean isLevelCompleted() {
        return levelCompleted;
    }
}
