package Screens;

import GameLogic.GameConfig;
import GameLogic.TileMap;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.Gdx.input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public final class ElevatorScreen extends BasePlayScreen {

    private Texture elevatorFloor, elevatorButtons, elevatorWall, carpet, blackTexture;
    private Pixmap pixmap;

    private boolean nearButtons = false;
    private boolean selecting = false;
    private boolean ignoreEnter = false;
    private int selectedIndex = 0;
    private final int[] levelList = {1, 2, 3, 4, 5, 6, 7};

    public ElevatorScreen(Game app) {
        super(app, 9);
    }

    @Override
    protected void onShowExtra() {
        prevPushes = game.getPlayer().getPushCount();
        carpet = load("textures/carpet.png");
        elevatorFloor = load("textures/elevator_floor.png");
        elevatorWall = load("textures/elevator_wall.png");
        elevatorButtons = load("textures/elevator_buttons.png");

        pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 1);
        pixmap.fill();
        blackTexture = new Texture(pixmap);
    }

    @Override
    protected void onUpdate(float delta) {
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
                int levelId = levelList[selectedIndex];
                app.setScreen(new GameScreen(app, levelId));
                return;
            }
        }
    }

    @Override
    protected GameLogic.Directions readHeldDirection() {
        if (selecting) {
            return null;
        }
        return super.readHeldDirection();
    }

    @Override
    protected void onDrawMap() {
        TileMap map = game.getMap();

        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                int dx = col * GameConfig.TILE_SIZE;
                int dy = row * GameConfig.TILE_SIZE;

                batch.draw(elevatorFloor, dx, dy, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);

                char ch = map.getTile(col, row);
                if (ch == TileMap.WALL) {
                    batch.draw(wallTexture, dx, dy, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                } else if (ch == TileMap.CARPET) {
                    batch.draw(carpet, dx, dy, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
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
        if (nearButtons && !selecting) {
            float tx = 10f;
            float ty = 12f;
            font.draw(batch, "ENTER: Seleccionar nivel", tx, ty);
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
    }

    @Override
    protected void onDisposeExtra() {
        carpet.dispose();
        elevatorFloor.dispose();
        elevatorWall.dispose();
        elevatorButtons.dispose();
        blackTexture.dispose();
        pixmap.dispose();
    }
}
