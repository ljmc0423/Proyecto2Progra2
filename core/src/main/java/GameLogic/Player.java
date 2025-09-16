package GameLogic;

public final class Player {

    private int x;
    private int y;
    private int moveCount;
    private int pushCount;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public int getPushCount() {
        return pushCount;
    }

    public void moveTo(int newX, int newY, boolean pushed) {
        this.x = newX;
        this.y = newY;
        moveCount++;
        
        if (pushed) {
            pushCount++;
        }
    }
}
