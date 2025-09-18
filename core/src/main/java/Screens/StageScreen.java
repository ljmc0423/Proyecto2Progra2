package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;

import GameLogic.GameConfig;
import GameLogic.TileMap;
import GameLogic.Elevator;

public final class StageScreen extends BasePlayScreen {

    // Texturas mapa
    private Texture carpetTexture;

    private Texture[] elevatorFrames;

    private Elevator elevator;

    // Posición del elevador (tu '?' estaba en x=16, y=12)
    private final int ex = 16, ey = 12;

    public StageScreen(Game app) {
        super(app, 0);
    }

    @Override
    protected void onShowExtra() {
        prevPushes = game.getPlayer().getPushCount();

        // Carga piso especial, etc.
        carpetTexture = load("textures/carpet.png");

        // Frames de elevador
        Texture elevator0 = load("textures/elevator_0.png");
        Texture elevator1 = load("textures/elevator_1.png");
        Texture elevator2 = load("textures/elevator_2.png");
        Texture elevator3 = load("textures/elevator_3.png");
        elevatorFrames = new Texture[]{elevator0, elevator1, elevator2, elevator3};

        elevator = new Elevator(ex, ey);
    }

    @Override
    protected void onUpdate(float delta) {
        super.onUpdate(delta);
        elevator.update(delta, game.getPlayer());
    }

    @Override
    protected void onDrawMap() {
        TileMap map = game.getMap();
        for (int row = 0; row < GameConfig.ROWS; row++) {
            for (int col = 0; col < GameConfig.COLS; col++) {
                int px = col * GameConfig.TILE_SIZE;
                int py = row * GameConfig.TILE_SIZE;

                batch.draw(floorTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);

                char ch = map.getTile(col, row);
                switch (ch) {
                    case TileMap.WALL:
                        batch.draw(wallTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                        break;
                    case TileMap.CARPET:
                        batch.draw(carpetTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                        break;
                    default:
                        break;
                }
            }
        }

        float p = elevator.getProgress();
        int frameIdx = Math.round(p * (elevatorFrames.length - 1));
        if (frameIdx < 0) {
            frameIdx = 0;
        }
        if (frameIdx > elevatorFrames.length - 1) {
            frameIdx = elevatorFrames.length - 1;
        }

        int texW = elevatorFrames[0].getWidth();
        int texH = elevatorFrames[0].getHeight();

        float scale = 1.42f;
        float drawW = texW * scale;
        float drawH = texH * scale;

        int baseX = elevator.getTileX() * GameConfig.TILE_SIZE;
        int baseY = elevator.getTileY() * GameConfig.TILE_SIZE;

        float drawX = baseX + (GameConfig.TILE_SIZE - drawW) * 0.5f;
        float drawY = baseY;

        batch.draw(elevatorFrames[frameIdx], drawX, drawY, drawW, drawH);
    }

    @Override
    protected void onDrawHUD() {
    }

    @Override
    protected void onDisposeExtra() {
        carpetTexture.dispose();
        for (Texture t : elevatorFrames) {
            t.dispose();
        }
    }
}
