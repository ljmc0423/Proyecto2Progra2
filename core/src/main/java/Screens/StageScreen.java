package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import GameLogic.GameConfig;
import GameLogic.TileMap;
import GameLogic.Elevator;
import GameLogic.Directions;
import GameLogic.Phase;

public final class StageScreen extends BasePlayScreen {

    private Texture carpet;
    private Texture[] elevatorFrames;
    private Elevator elevator;

    private final int elevatorX = 16, elevatorY = 12;

    private Phase phase = Phase.NONE;
    private float phaseTime = 0f;

    private float hiddenPlayerRatio = -1f;
    private static final float CLOSE_SPEED = 5.0f;

    public StageScreen(Game app) { super(app, 8); }

    @Override
    protected void onShowExtra() {
        prevPushes = game.getPlayer().getPushCount();

        carpet = load("textures/carpet.png");

        Texture f0 = load("textures/elevator_0.png");
        Texture f1 = load("textures/elevator_1.png");
        Texture f2 = load("textures/elevator_2.png");
        Texture f3 = load("textures/elevator_3.png");
        elevatorFrames = new Texture[]{f0, f1, f2, f3};

        elevator = new Elevator(elevatorX, elevatorY);
    }

    @Override
    protected void onUpdate(float delta) {
        super.onUpdate(delta);
        if (paused) return;

        switch (phase) {
            case NONE: {
                elevator.update(delta, game.getPlayer());
                boolean standing = !tweenActive && !moveRequested;
                if (standing && game.getPlayer().getX() == elevatorX && game.getPlayer().getY() == elevatorY) {
                    directionQueue.clear();
                    heldDirection = null;
                    moveRequested = false;
                    tweenActive = false;

                    drawPX = elevatorX * GameConfig.TILE_SIZE;
                    drawPY = elevatorY * GameConfig.TILE_SIZE;

                    if (bgMusic != null) bgMusic.stop();

                    elevator.forceOpen();
                    phase = Phase.WAIT_OPEN;
                    phaseTime = 0f;
                }
                break;
            }
            case WAIT_OPEN: {
                phaseTime += delta;
                if (phaseTime >= 1.0f) {
                    if (hiddenPlayerRatio < 0f) hiddenPlayerRatio = playerRatio;
                    playerRatio = 0f;
                    elevator.beginClosing();
                    phase = Phase.CLOSING;
                    phaseTime = 0f;
                }
                break;
            }
            case CLOSING: {
                elevator.update(delta * CLOSE_SPEED, game.getPlayer());
                boolean closed = elevator.getState() == Elevator.State.CLOSED && elevator.getProgress() == 0f;
                if (closed) {
                    phase = Phase.POST_CLOSE;
                    phaseTime = 0f;
                }
                break;
            }
            case POST_CLOSE: {
                phaseTime += delta;
                if (phaseTime >= 2.0f) {
                    app.setScreen(new ElevatorScreen(app));
                    return;
                }
                break;
            }
        }
    }

    @Override
    protected Directions readHeldDirection() {
        if (phase != Phase.NONE) return null;
        return super.readHeldDirection();
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
                if (ch == TileMap.WALL) {
                    batch.draw(wallTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                } else if (ch == TileMap.CARPET) {
                    batch.draw(carpet, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                }
            }
        }

        int fi = Math.max(0, Math.min(Math.round(elevator.getProgress() * (elevatorFrames.length - 1)),
                                      elevatorFrames.length - 1));
        int texW = elevatorFrames[0].getWidth();
        int texH = elevatorFrames[0].getHeight();

        float scale = 2.1f;
        float drawW = texW * scale;
        float drawH = texH * scale;

        float baseX = elevatorX * GameConfig.TILE_SIZE;
        float baseY = elevatorY * GameConfig.TILE_SIZE;

        float drawX = baseX + (GameConfig.TILE_SIZE - drawW) * 0.5f;
        float drawY = baseY;

        batch.setColor(1, 1, 1, 1);
        batch.draw(elevatorFrames[fi], drawX, drawY, drawW, drawH);
    }

    @Override protected void onDrawHUD() {}

    @Override
    protected void onDisposeExtra() {
        carpet.dispose();
        for (Texture t : elevatorFrames) t.dispose();
        if (hiddenPlayerRatio >= 0f) playerRatio = hiddenPlayerRatio;
    }
}
