package GameLogic;

public final class MoveResult {
    private final Type type;
    private final int newX;
    private final int newY;

    private final int boxDestX;
    private final int boxDestY;

    private final char previousFront;
    private final char previousDest;

    private MoveResult(Type type, int newX, int newY, int boxDestX, int boxDestY, char previousFront, char previousDest) {
        this.type = type;
        this.newX = newX;
        this.newY = newY;
        this.boxDestX = boxDestX;
        this.boxDestY = boxDestY;
        this.previousFront = previousFront;
        this.previousDest = previousDest;
    }

    public static MoveResult blocked() {
        return new MoveResult(Type.BLOCKED, 0, 0, 0, 0, '\0', '\0'); 
    }

    public static MoveResult step(int newX, int newY) {
        return new MoveResult(Type.STEP, newX, newY, 0, 0, '\0', '\0');
    }

    public static MoveResult push(int newX, int newY, int boxDestX, int boxDestY,
            char previousFront, char previousDest) {
        return new MoveResult(Type.PUSH, newX, newY, boxDestX, boxDestY, previousFront, previousDest);
    }

    public Type getType() {
        return type;
    }

    public int getNewX() {
        return newX;
    }

    public int getNewY() {
        return newY;
    }

    public int getBoxDestX() {
        return boxDestX;
    }

    public int getBoxDestY() {
        return boxDestY;
    }

    public char getPreviousFront() {
        return previousFront;
    }

    public char getPreviousDest() {
        return previousDest;
    }

    public boolean hasMoved() {
        return type != Type.BLOCKED;
    }
}
