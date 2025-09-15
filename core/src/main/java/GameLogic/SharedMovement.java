package GameLogic;

public final class SharedMovement {
    public volatile MoveResult lastResult = null;

    public MoveResult takeResult() {
        MoveResult moveResult = lastResult;
        lastResult = null;
        return moveResult;
    }
}
