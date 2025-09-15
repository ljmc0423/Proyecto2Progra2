package GameLogic;

public abstract class Game {
    public abstract void startLevel(int id);

    public abstract boolean isVictory();

    public abstract int getPoints();
}
