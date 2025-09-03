package Screen;

public class TileMap {

    private final char charMap[][];
    private final int rows;
    private final int cols;

    public TileMap(String[] levelData) {
        rows = levelData.length;
        cols = levelData[0].length();
        charMap = new char[rows][cols];
        
        for (int r = 0; r < rows; r++) {
            charMap[r] = levelData[r].toCharArray();
        }
    }

    public int getRows() {
        return charMap.length;
    }

    public int getCols() {
        return charMap[0].length;
    }

    public char getTile(int row, int col) {
        return charMap[row][col];
    }

    public void setTile(int row, int col, char c) {
        charMap[row][col] = c;
    }
    
    public int[] findPlayer() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (charMap[row][col] == '@') {
                    return new int[] {row,col};
                }
            }
        }
        return null;
    }
}
