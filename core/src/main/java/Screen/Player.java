package Screen;

public class Player {

    private int col;
    private int row;

    public Player(int startCol, int startRow) {
        this.col = startCol;
        this.row = startRow;
    }

    public void move(int dx, int dy, int maxCols, int maxRows) {
        int newCol = col + dx;
        int newRow = row + dy;
        if (newCol < 0 || newCol >= maxCols) {
            return;
        }
        if (newRow < 0 || newRow >= maxRows) {
            return;
        }
        col = newCol;
        row = newRow;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}
