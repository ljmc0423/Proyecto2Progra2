package Screen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LevelRenderer {

    private final TileMap map;
    private final Player player;
    private final int tileSize;

    private final Texture wall, floor, box, target, boxPlaced;

    private final Sprite playerSprite;

    public LevelRenderer(TileMap map, Player player, int tileSize, Texture wall, Texture floor, Texture box,
            Texture boxPlaced, Texture target, Texture playerTex) {

        this.map = map;
        this.player = player;
        this.tileSize = tileSize;
        this.wall = wall;
        this.floor = floor;
        this.box = box;
        this.boxPlaced = boxPlaced;
        this.target = target;

        this.playerSprite = new Sprite(playerTex);

        float playerWidth = playerTex.getWidth();
        float playerHeight = playerTex.getHeight();
        float ratio = playerHeight / playerWidth;

        float newWidth = tileSize;
        float newHeight = tileSize * ratio;
        playerSprite.setSize(newWidth, newHeight);
    }

    public void render(SpriteBatch batch) {
        int rows = map.getRows();
        int cols = map.getCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char character = map.getTile(r, c);

                int x = c * tileSize;
                int y = (rows - 1 - r) * tileSize;

                batch.draw(floor, x, y, tileSize, tileSize);

                switch (character) {
                    case '#':
                        batch.draw(wall, x, y, tileSize, tileSize);
                        break;
                    case '$':
                        batch.draw(box, x, y, tileSize, tileSize);
                        break;
                    case '+':
                        batch.draw(target, x, y, tileSize, tileSize);
                        break;
                    default:
                        break;
                }
            }
        }

        int cellX = player.getCol() * tileSize;
        int cellY = (rows - 1 - player.getRow()) * tileSize;

        float drawX = cellX + (tileSize - playerSprite.getWidth()) * 0.5f;
        float drawY = cellY;
        playerSprite.setPosition(drawX, drawY);
        playerSprite.draw(batch);
    }
}
