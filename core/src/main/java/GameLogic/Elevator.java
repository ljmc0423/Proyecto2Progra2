// GameLogic/Elevator.java
package GameLogic;

public final class Elevator {

    public enum State {
        CLOSED, OPENING, OPEN, CLOSING
    }

    private final int tileX, tileY;
    private State state = State.CLOSED;
    private float progress = 0f;
    private final float speed = 4.0f;

    private boolean lockClose = false; // evita reabrir durante cierre

    public Elevator(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public void forceOpen() {
        lockClose = false;
        state = State.OPEN;
        progress = 1f;
    }

    public void beginClosing() {
        lockClose = true;
        if (state != State.CLOSED) {
            state = State.CLOSING;
        }
    }

    public void update(float delta, Player player) {
        boolean inFront = (player.getX() == tileX && player.getY() == tileY - 1);
        boolean onTile = (player.getX() == tileX && player.getY() == tileY);

        boolean shouldOpen = (inFront || onTile) && !lockClose;
        State target = shouldOpen ? State.OPEN : State.CLOSED;

        if (target == State.OPEN) {
            if (state == State.CLOSED || state == State.CLOSING) {
                state = State.OPENING;
            }
        } else {
            if (state == State.OPEN || state == State.OPENING) {
                state = State.CLOSING;
            }
        }

        if (state == State.OPENING) {
            progress += speed * delta;
            if (progress >= 1f) {
                progress = 1f;
                state = State.OPEN;
            }
        } else if (state == State.CLOSING) {
            progress -= speed * delta;
            if (progress <= 0f) {
                progress = 0f;
                state = State.CLOSED;
                lockClose = false;
            }
        }
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public State getState() {
        return state;
    }

    public float getProgress() {
        return progress;
    }
}
