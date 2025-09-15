package GameLogic;

public final class Player {

    private int x;
    private int y;
    private int moveCount;
    private int pushCount;

    public Player(Position start) {
        this.x = start.getX();
        this.y = start.getY();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Position getPosition() {
        return new Position(x, y);
    }

    public int getMoveCount() {
        return moveCount;
    }

    public int getPushCount() {
        return pushCount;
    }

    public void applyStep(int newXPos, int newYPos) {
        this.x = newXPos;
        this.y = newYPos;
        this.moveCount++;
    }

    public void applyPush(int newXPos, int newYPos) {
        this.x = newXPos;
        this.y = newYPos;
        this.moveCount++;
        this.pushCount++;
    }
}
