package GameLogic;

import java.util.concurrent.BlockingQueue;

public final class MoveLogic implements Runnable {

    private final TileMap map;
    private final BlockingQueue<Directions> moves;
    private volatile boolean running = true;

    //volatile sirve para que ambos hilos vean los cambios inmediatamente
    private volatile int playerX;
    private volatile int playerY;

    public MoveLogic(TileMap map, BlockingQueue<Directions> moves) {
        this.map = map;
        this.moves = moves;

        for (int y = 0; y < GameConfig.ROWS; y++) {
            for (int x = 0; x < GameConfig.COLS; x++) {
                char c = map.getTile(x, y);
                if (c == TileMap.PLAYER || c == TileMap.PLAYER_ON_TARGET) {
                    playerX = x;
                    playerY = y;
                    map.setTile(x, y, (c == TileMap.PLAYER_ON_TARGET) ? TileMap.TARGET : TileMap.FLOOR);
                }
            }
        }
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
                synchronized (map) {
                    applyMove(direction.dx, direction.dy);
                }
            }
        } catch (InterruptedException ignored) {
        } finally {
            running = false;
        }
    }

    private void applyMove(int dx, int dy) {
        int nx = playerX + dx;
        int ny = playerY + dy;

        if (!map.inBounds(nx, ny)) {
            return;
        }

        char front = map.getTile(nx, ny);

        if (front == TileMap.WALL) {
            return;
        }

        if (isBox(front)) {
            int bx = nx + dx;
            int by = ny + dy;
            if (!map.inBounds(bx, by)) {
                return;
            }

            char nextTile = map.getTile(bx, by);
            if (!isFree(nextTile)) {
                return;
            }

            map.setTile(bx, by, (nextTile == TileMap.TARGET) ? TileMap.BOX_ON_TARGET : TileMap.BOX);

            map.setTile(nx, ny, (front == TileMap.BOX_ON_TARGET) ? TileMap.TARGET : TileMap.FLOOR);

            playerX = nx;
            playerY = ny;
            return;
        }

        if (isFree(front)) {
            playerX = nx;
            playerY = ny;
        }
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
}
