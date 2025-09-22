package Screens;

import GameLogic.GameConfig;
import GameLogic.TileMap;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.Gdx.input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;

public final class ElevatorScreen extends BasePlayScreen {

    private Texture elevatorFloor, elevatorButtons, elevatorWall, blackTexture;
    private Pixmap pixmap;

    private boolean nearButtons = false;
    private boolean selecting = false;
    private boolean ignoreEnter = false;
    private int selectedIndex = 0;
    private final int levelList[] = {1, 2, 3, 4, 5, 6, 7};

    // transición
    private boolean transitioning = false;
    private float transitionTime = 0f;
    private int pendingLevel = -1;
    private Sound sMoving, sFinished;
    private boolean movingPlayed = false;

    public ElevatorScreen(Game app) {
        super(app, 9);
    }

    @Override
    protected void onShowExtra() {
        prevPushes = game.getPlayer().getPushCount();
        elevatorFloor = load("textures/elevator_floor.png");
        elevatorWall = load("textures/elevator_wall.png");
        elevatorButtons = load("textures/elevator_buttons.png");

        pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 1);
        pixmap.fill();
        blackTexture = new Texture(pixmap);

        // sonidos de transición
        sMoving = com.elkinedwin.LogicaUsuario.AudioX.newSound("audios/elevator_moving.wav");
        sFinished = com.elkinedwin.LogicaUsuario.AudioX.newSound("audios/elevator_finished.wav");
    }

    @Override
    protected void onUpdate(float delta) {
        // si estamos en transición, solo temporizamos
        if (transitioning) {
            if (!movingPlayed) {
                movingPlayed = true;
                sMoving.play(1f);
                if (bgMusic != null) {
                    bgMusic.pause();
                }
                directionQueue.clear();
                heldDirection = null;
                moveRequested = false;
                tweenActive = false;
            }
            transitionTime += delta;
            if (transitioning && pendingLevel >= 0) {
                transitionTime += delta;

                if (transitionTime >= 5.0f && transitionTime < 5.1f) {
                    sMoving.stop();
                    sFinished.play(1f);
                }
                if (transitionTime >= 9f) {
                    app.setScreen(new GameScreen(app, pendingLevel));
                    return;
                }
                return;
            }

            return;
        }

        if (selecting && input.isKeyJustPressed(Keys.ESCAPE)) {
            selecting = false;
            return;
        }

        super.onUpdate(delta);
        if (paused) {
            return;
        }

        nearButtons = false;
        TileMap map = game.getMap();
        int px = game.getPlayer().getX();
        int py = game.getPlayer().getY();

        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                if (map.getTile(col, row) == TileMap.ELEVATOR_BUTTONS) {
                    if (px == col && py == row - 1) {
                        nearButtons = true;
                        break;
                    }
                }
            }
            if (nearButtons) {
                break;
            }
        }

        if (!selecting && nearButtons && Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            selecting = true;
            selectedIndex = 0;
            ignoreEnter = true;
        }

        if (selecting) {
            if (ignoreEnter) {
                if (!input.isKeyPressed(Keys.ENTER)) {
                    ignoreEnter = false;
                }
                return;
            }
            if (input.isKeyJustPressed(Keys.UP)) {
                selectedIndex = (selectedIndex - 1 + levelList.length) % levelList.length;
            } else if (input.isKeyJustPressed(Keys.DOWN)) {
                selectedIndex = (selectedIndex + 1) % levelList.length;
            } else if (input.isKeyJustPressed(Keys.ENTER)) {
                pendingLevel = levelList[selectedIndex];
                selecting = false;
                transitioning = true;
                transitionTime = 0f;
                movingPlayed = false;
                return;
            }
        }
    }

    @Override
    protected GameLogic.Directions readHeldDirection() {
        if (selecting || transitioning) {
            return null;
        }
        return super.readHeldDirection();
    }

    @Override
    protected void onDrawMap() {
        TileMap map = game.getMap();

        // fondo
        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                int dx = col * GameConfig.TILE_SIZE;
                int dy = row * GameConfig.TILE_SIZE;

                batch.draw(elevatorFloor, dx, dy, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);

                char ch = map.getTile(col, row);
                if (ch == TileMap.WALL) {
                    batch.draw(wallTexture, dx, dy, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                } else if (ch == TileMap.BLACK) {
                    batch.draw(blackTexture, dx, dy, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                }
            }
        }

        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                int dx = col * GameConfig.TILE_SIZE;
                int dy = row * GameConfig.TILE_SIZE;

                char ch = map.getTile(col, row);
                if (ch == TileMap.ELEVATOR_BUTTONS) {
                    float drawW = 16f, drawH = 49f;
                    float seamX = dx + GameConfig.TILE_SIZE;
                    float drawX = seamX - drawW;
                    float drawY = dy;
                    batch.draw(elevatorButtons, drawX, drawY, drawW, drawH);
                } else if (ch == TileMap.ELEVATOR_WALL) {
                    float drawW = 65f, drawH = 49f;
                    float seamX = dx;
                    float drawX = seamX;
                    float drawY = dy;
                    batch.draw(elevatorWall, drawX, drawY, drawW, drawH);
                }
            }
        }
    }

    @Override
    protected void onDrawHUD() {
        if (!transitioning) {
            if (nearButtons && !selecting) {
                font.draw(batch, "ENTER: Seleccionar nivel", 10f, 52f);
            }
            if (selecting) {
                batch.setColor(1f, 1f, 1f, 0.6f);
                batch.draw(blackTexture, 0, 0, GameConfig.PX_WIDTH, GameConfig.PX_HEIGHT);
                batch.setColor(1f, 1f, 1f, 1f);

                float panelX = GameConfig.PX_WIDTH * 0.5f - 140f;
                float panelY = GameConfig.PX_HEIGHT * 0.5f + 100f;

                font.draw(batch, "Selecciona nivel (ESC para salir)", panelX, panelY);

                float y = panelY - 24f;
                for (int i = 0; i < levelList.length; i++) {
                    String prefix = (i == selectedIndex) ? ">" : " ";
                    font.draw(batch, prefix + " Nivel " + levelList[i], panelX, y);
                    y -= 20f;
                }
            }
        } else {
            batch.setColor(1f, 1f, 1f, 1f);
            batch.draw(blackTexture, 0, 0, GameConfig.PX_WIDTH, GameConfig.PX_HEIGHT);
        }
    }

    @Override
    protected void onDisposeExtra() {
        elevatorFloor.dispose();
        elevatorWall.dispose();
        elevatorButtons.dispose();
        blackTexture.dispose();
        pixmap.dispose();
        if (sMoving != null) {
            sMoving.dispose();
        }
        if (sFinished != null) {
            sFinished.dispose();
        }
    }
}
