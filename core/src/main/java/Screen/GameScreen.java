package Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {

    public static final int TILE_SIZE = 16;
    public static final int MAX_COLS = 16;
    public static final int MAX_ROWS = 12;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;

    private Texture floorTexture;
    private Texture wallTexture;
    private Texture boxTexture;
    private Texture playerTexture;
    private Sprite playerSprite;

    //mapa 
    // . = floor, @ = player
    private final String[] levelData = {
        "################",
        "#..............#",
        "#..............#",
        "#.....$........#",
        "#..............#",
        "#..............#",
        "#..............#",
        "#......@.......#",
        "#..............#",
        "#............$.#",
        "#..............#",
        "################"
    };

    //posición del player
    private int playerCol;
    private int playerRow;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(MAX_COLS * TILE_SIZE, MAX_ROWS * TILE_SIZE, camera);
        batch = new SpriteBatch();

        floorTexture = new Texture("floor.png");
        playerTexture = new Texture("player.png");
        boxTexture = new Texture("box.png");
        wallTexture = new Texture("wall.png");

        //no le veo la diferencia con este filtro pero por si acaso
        floorTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        playerTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        boxTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        wallTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        //encontrar el @
        int start[] = findPlayer(levelData);
        playerCol = start[0];
        playerRow = start[1];

        playerSprite = new Sprite(playerTexture);
        float w = playerTexture.getWidth();
        float h = playerTexture.getHeight();
        float aspect = h / w; //proporción de la altura por que no es 16*16
        playerSprite.setSize(TILE_SIZE, TILE_SIZE * aspect);
        setPlayerSpriteToGrid();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            movePlayer(-1, 0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            movePlayer(1, 0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            movePlayer(0, -1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            movePlayer(0, 1);
        }

        ScreenUtils.clear(Color.BLACK);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        //dibujar el suelo
        for (int row = 0; row < levelData.length; row++) {
            for (int col = 0; col < levelData[row].length(); col++) {
                int x = col * TILE_SIZE;
                int y = (levelData.length - 1 - row) * TILE_SIZE;
                batch.draw(floorTexture, x, y, TILE_SIZE, TILE_SIZE);

                char tile = levelData[row].charAt(col);

                if (tile == '$') {
                    batch.draw(boxTexture, x, y, TILE_SIZE, TILE_SIZE);
                } else if (tile == '#') {
                    batch.draw(wallTexture, x, y, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        //importante playerSprite va después del suelo para que se pueda ver
        playerSprite.draw(batch);

        batch.end();
    }

    private void movePlayer(int dx, int dy) {
        int newCol = playerCol + dx;
        int newRow = playerRow + dy;

        //para que no salga de pantalla
        if (newCol < 0 || newCol >= MAX_COLS) {
            return;
        }
        if (newRow < 0 || newRow >= MAX_ROWS) {
            return;
        }

        playerCol = newCol;
        playerRow = newRow;
        setPlayerSpriteToGrid();
    }

    private void setPlayerSpriteToGrid() {
        float x = playerCol * TILE_SIZE;
        float y = playerRow * TILE_SIZE;
        playerSprite.setPosition(x, y);
    }

    private int[] findPlayer(String map[]) {
        for (int row = 0; row < map.length; row++) {
            int col = map[row].indexOf('@');
            if (col >= 0) {
                int worldRow = map.length - 1 - row;
                return new int[]{col, worldRow};
            }
        }
        return null;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        floorTexture.dispose();
        playerTexture.dispose();
        boxTexture.dispose();
        wallTexture.dispose();
    }
}
