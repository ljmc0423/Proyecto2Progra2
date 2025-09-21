package Screens;

import GameLogic.GameConfig;
import GameLogic.TileMap;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public final class ElevatorScreen extends BasePlayScreen {

    private Texture elevatorFloor, elevatorButtons, elevatorWall, carpet, blackTexture;
    private Pixmap pixmap;

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
        super.onUpdate(delta);
        if (paused) {
            return;
        }

    }

    @Override
    protected void onDrawMap() {
        TileMap map = game.getMap();

        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                int px = col * GameConfig.TILE_SIZE;
                int py = row * GameConfig.TILE_SIZE;

                batch.draw(elevatorFloor, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);

                char ch = map.getTile(col, row);
                if (ch == TileMap.WALL) {
                    batch.draw(wallTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                } else if (ch == TileMap.CARPET) {
                    batch.draw(carpet, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                } else if (ch == TileMap.BLACK) {
                    batch.draw(blackTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                }
            }
        }

        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                int px = col * GameConfig.TILE_SIZE;
                int py = row * GameConfig.TILE_SIZE;

                char ch = map.getTile(col, row);
                if (ch == TileMap.ELEVATOR_BUTTONS) {
                    float drawW = 16f, drawH = 49f;
                    float seamX = px + GameConfig.TILE_SIZE;
                    float drawX = seamX - drawW;
                    float drawY = py;
                    batch.draw(elevatorButtons, drawX, drawY, drawW, drawH);
                } else if (ch == TileMap.ELEVATOR_WALL) {
                    float drawW = 65f, drawH = 49f;
                    float seamX = px;
                    float drawX = seamX;
                    float drawY = py;
                    batch.draw(elevatorWall, drawX, drawY, drawW, drawH);
                }
            }
        }
    }

    @Override
    protected void onDrawHUD() {
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
