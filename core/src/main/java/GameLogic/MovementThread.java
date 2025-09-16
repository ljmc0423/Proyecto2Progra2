package GameLogic;

import java.util.concurrent.BlockingQueue;

public final class MovementThread implements Runnable {

    private final BlockingQueue<Directions> directionectionQueue;
    private final TileMap map;
    private final Player player;
    private volatile boolean running = true;

    public MovementThread(BlockingQueue<Directions> directionectionQueue, TileMap map, Player player) {
        this.directionectionQueue = directionectionQueue;
        this.map = map;
        this.player = player;
    }

    public void stop() {
        running = false;
        Thread.currentThread().interrupt();
    }

    @Override
    public void run() {
        try {
            while (running) {
                Directions direction = directionectionQueue.take();
                if (direction == Directions.QUIT) {
                    break;
                }
                applyMovement(map, player, direction);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private MoveResult applyMovement(TileMap map, Player player, Directions direction) {
        final int dx = direction.dx, dy = direction.dy;
        final int nx = player.getX() + dx;
        final int ny = player.getY() + dy;

        if (!map.isInBounds(nx, ny)) {
            return MoveResult.BLOCKED;
        }

        char front = map.getTile(nx, ny);
        if (map.isWall(front)) {
            return MoveResult.BLOCKED;
        }

        if (map.isBox(front)) {
            final int bx = nx + dx, by = ny + dy;
            if (!map.isInBounds(bx, by)) {
                return MoveResult.BLOCKED;
            }

            char dest = map.getTile(bx, by);
            if (!map.isFree(dest)) {
                return MoveResult.BLOCKED;
            }

            map.setTile(bx, by, dest == TileMap.TARGET ? TileMap.BOX_ON_TARGET : TileMap.BOX);
            map.setTile(nx, ny, front == TileMap.BOX_ON_TARGET ? TileMap.TARGET : TileMap.FLOOR);

            player.moveTo(nx, ny, true);
            return MoveResult.PUSH;
        }

        if (map.isFree(front)) {
            player.moveTo(nx, ny, false);
            return MoveResult.STEP;
        }

        return MoveResult.BLOCKED;
    }
}
