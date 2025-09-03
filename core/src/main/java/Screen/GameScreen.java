package Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {

    //dimensiones del mapa y casillas
    public static final int TILE_SIZE = 16;
    public static final int COLS = 16;
    public static final int ROWS = 12;

    //pasos por segundo
    private static final float stepsSeconds = 3.75f;
    private float stepsCoolDown = 0;

    //gráficos
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;

    //clases
    private TileMap tileMap;
    private LevelRenderer levelRenderer;
    private Player player;

    //assets
    private Texture floorTexture;
    private Texture wallTexture;
    private Texture boxTexture;
    private Texture playerTexture;
    private Texture boxPlacedTexture;
    private Texture targetTexture;

    //nivel
    private final String levelData[] = {
        "################",
        "#...........+..#",
        "#..............#",
        "#.....$........#",
        "#..............#",
        "#..+...........#",
        "#..............#",
        "#......@...+...#",
        "#..............#",
        "#............$.#",
        "#..............#",
        "################"
    };

    @Override
    public void show() {
        //inicialización de cosas gráficas
        camera = new OrthographicCamera();
        viewport = new FitViewport(TILE_SIZE * COLS, TILE_SIZE * ROWS, camera);
        batch = new SpriteBatch();

        floorTexture = new Texture("floor.png");
        playerTexture = new Texture("player.png");
        boxTexture = new Texture("box.png");
        boxPlacedTexture = new Texture("boxPlaced.png");
        wallTexture = new Texture("wall.png");
        targetTexture = new Texture("target.png");

        //creo que no hacen nada estos filtros pero los dejo por cualquier cosa
        floorTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        playerTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        boxTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        boxPlacedTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        wallTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        targetTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        tileMap = new TileMap(levelData);

        int start[] = tileMap.findPlayer();
        player = new Player(start[1], start[0]);

        levelRenderer = new LevelRenderer(tileMap, player, TILE_SIZE, wallTexture, floorTexture, boxTexture,
                boxPlacedTexture, targetTexture, playerTexture);
    }

    @Override
    public void render(float delta) {
        stepsCoolDown -= delta; //delta es el tiempo entre frames
        if (stepsCoolDown <= 0f) {
            int dx = 0, dy = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                dx = -1;
                dy = 0;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                dx = 1;
                dy = 0;
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                dx = 0;
                dy = +1;
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                dx = 0;
                dy = -1;
            }

            if (dx != 0 || dy != 0) {
                player.move(dx, dy, COLS, ROWS);
                stepsCoolDown = 1f / stepsSeconds; //reseteo de cooldown
            }
        }

        ScreenUtils.clear(Color.BLACK);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        levelRenderer.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        floorTexture.dispose();
        wallTexture.dispose();
        boxTexture.dispose();
        playerTexture.dispose();
        boxPlacedTexture.dispose();
        targetTexture.dispose();
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

}
