package GameLogic;

public abstract class Juego {

    protected final String levelPaths[];
    protected int currentLevelIndex = 1;

    protected Juego(String levelPaths[]) {
        this.levelPaths = levelPaths;
    }

    public final String getCurrentLevelPath() {
        return levelPaths[currentLevelIndex];
    }

    public final boolean hasNextLevel() {
        return currentLevelIndex + 1 < levelPaths.length;
    }

    public final boolean advanceLevel() {
        if (!hasNextLevel()) {
            return false;
        }
        currentLevelIndex++;
        return true;
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

}
