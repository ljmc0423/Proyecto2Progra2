package GameLogic;

import java.util.concurrent.BlockingQueue;

public final class MovementThread implements Runnable {

    private final SharedMovement shared;
    private final BlockingQueue<Directions> directionQueue;
    private final MoveLogic logic = new MoveLogic();
    private volatile TileMap mapCopy;
    private volatile Position playerPosCopy;
    private volatile boolean running = true;

    public MovementThread(SharedMovement shared, BlockingQueue<Directions> directionQueue) {
        this.shared = shared;
        this.directionQueue = directionQueue;
    }

    public void updateCopies(TileMap mapCopy, Position playerPosCopy) {
        this.mapCopy = mapCopy;
        this.playerPosCopy = playerPosCopy;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Directions direction = directionQueue.take();
                if (direction == Directions.QUIT) {
                    break;
                }
                TileMap m = mapCopy;
                Position p = playerPosCopy;

                MoveResult moveResult = logic.applyMovement(m, p, direction);
                shared.lastResult = moveResult;
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
