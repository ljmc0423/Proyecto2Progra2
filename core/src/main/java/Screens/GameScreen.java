package Screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

import GameLogic.GameConfig;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import GameLogic.Directions;
import GameLogic.LevelLoader;
import GameLogic.MoveLogic;
import GameLogic.TileMap;
import static com.badlogic.gdx.Gdx.input;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class GameScreen implements Screen {

    //gráficos
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;

    //assets
    private Texture floorTexture;
    private Texture wallTexture;
    private Texture boxTexture;
    private Texture playerTexture;
    private Texture boxPlacedTexture;
    private Texture targetTexture;
    private Sprite playerSprite;

    //otros
    private BlockingQueue<Directions> moves;
    private MoveLogic logic;
    private Thread logicThread;
    private TileMap tileMap;

    @Override
    public void show() {
        //inicialización de las cosas gráficas
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.PX_WIDTH, GameConfig.PX_HEIGHT, camera);

        batch = new SpriteBatch();

        floorTexture = new Texture("textures/floor.png");
        playerTexture = new Texture("textures/player.png");
        boxTexture = new Texture("textures/box.png");
        boxPlacedTexture = new Texture("textures/boxPlaced.png");
        wallTexture = new Texture("textures/wall.png");
        targetTexture = new Texture("textures/target.png");

        //se supone que esto es mejor para pixelart, pero no veo diferencia la verdad (igual lo dejo)
        floorTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        wallTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        boxTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        boxPlacedTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        playerTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        targetTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        playerSprite = new Sprite(playerTexture);
        int tile = GameConfig.TILE_SIZE;
        float w = playerTexture.getWidth();
        float h = playerTexture.getHeight();
        float ratio = h / w;
        playerSprite.setSize(tile, tile * ratio);

        //cargar nivel
        LevelLoader loader = new LevelLoader("levels/level01.txt");
        char levData[][] = loader.LevelCharData();
        tileMap = new TileMap(levData);

        //la cola de movimientos
        moves = new ArrayBlockingQueue<>(1);

        //hilo
        logic = new MoveLogic(tileMap, moves);
        logicThread = new Thread(logic, "logic-thread");
        logicThread.start();
    }

    @Override
    public void render(float delta) {

        if (input.isKeyJustPressed(Input.Keys.UP)) {
            moves.offer(Directions.UP);
        }
        if (input.isKeyJustPressed(Input.Keys.DOWN)) {
            moves.offer(Directions.DOWN);
        }
        if (input.isKeyJustPressed(Input.Keys.LEFT)) {
            moves.offer(Directions.LEFT);
        }
        if (input.isKeyJustPressed(Input.Keys.RIGHT)) {
            moves.offer(Directions.RIGHT);
        }

        //la idea es que lo de arriba se haga con delta en el futuro
        ScreenUtils.clear(Color.BLACK); //limpia la pantalla

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        synchronized (tileMap) {
            for (int row = 0; row < GameConfig.ROWS; row++) {
                for (int col = 0; col < GameConfig.COLS; col++) {
                    int px = col * GameConfig.TILE_SIZE;
                    int py = row * GameConfig.TILE_SIZE;

                    char c = tileMap.getTile(col, row);

                    batch.draw(floorTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);

                    if (c == TileMap.WALL) {
                        batch.draw(wallTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                    } else if (c == TileMap.TARGET) {
                        batch.draw(targetTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                    } else if (c == TileMap.BOX) {
                        batch.draw(boxTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                    } else if (c == TileMap.BOX_ON_TARGET) {
                        batch.draw(boxPlacedTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                    }
                }
            }

            int px = logic.getPlayerX();
            int py = logic.getPlayerY();
            playerSprite.setPosition(px * GameConfig.TILE_SIZE, py * GameConfig.TILE_SIZE);
        }
        playerSprite.draw(batch);

        batch.end();

    }

    @Override
    public void dispose() {
        hide();

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
        if (logic != null) {
            logic.stop();
        }

        if (logicThread != null) {
            logicThread.interrupt();
        }
    }

}
