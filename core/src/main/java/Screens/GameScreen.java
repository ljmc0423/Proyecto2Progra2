package Screens;

import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.Input.Keys.R;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.concurrent.ArrayBlockingQueue;
import com.badlogic.gdx.graphics.Texture;
import GameLogic.GameConfig;
import GameLogic.MovementThread;
import GameLogic.TileMap;
import com.badlogic.gdx.Input.Keys;

public final class GameScreen extends BasePlayScreen {

    // Texturas
    private Texture boxTexture, boxTexturePlaced, targetTexture;

    //para el hud del tutorial
    private int kUp, kDown, kLeft, kRight, kReset;
    private String sUp, sDown, sLeft, sRight, sReset;

    public GameScreen(Game app, int level) {
        super(app, level);
    }

    @Override
    protected void onShowExtra() {
        prevPushes = game.getPlayer().getPushCount();

        boxTexture = load("textures/box.png");
        boxTexturePlaced = load("textures/boxPlaced.png");
        targetTexture = load("textures/target.png");
        boxPlacedSound = loadSound("audios/box_placed.wav");

        kUp = getCfgKey("Arriba", Keys.UP);
        kDown = getCfgKey("Abajo", Keys.DOWN);
        kLeft = getCfgKey("Izquierda", Keys.LEFT);
        kRight = getCfgKey("Derecha", Keys.RIGHT);
        kReset = getCfgKey("Reiniciar", Keys.R);

        sUp = Keys.toString(kUp);
        sDown = Keys.toString(kDown);
        sLeft = Keys.toString(kLeft);
        sRight = Keys.toString(kRight);
        sReset = Keys.toString(kReset);

    }

    @Override
    protected void onUpdate(float delta) {
        super.onUpdate(delta);
        int reiniciarKey = getCfgKey("Reiniciar", R);
        if (input.isKeyJustPressed(reiniciarKey)) {
            resetLevel();
        }
    }

    private void resetLevel() {
        resetLevelSound.play(1.0f);

        try {
            if (movementThreadLogic != null) {
                movementThreadLogic.stop();
            }
            if (movementThread != null) {
                movementThread.interrupt();
            }
        } catch (Exception ignored) {
        }

        // Recargar nivel
        game.startLevel(level);
        setPlayer(game.getPlayer()); // re-sincroniza BasePlayScreen

        timeChronometer = 0f;
        tweenActive = false;
        moveRequested = false;

        prevX = game.getPlayer().getX();
        prevY = game.getPlayer().getY();
        prevPushes = game.getPlayer().getPushCount();

        // Crear nuevo hilo con las nuevas referencias
        directionQueue = new ArrayBlockingQueue<>(1);
        movementThreadLogic = new MovementThread(directionQueue, game.getMap(), game.getPlayer());
        movementThread = new Thread(movementThreadLogic);
        movementThread.setDaemon(true);
        movementThread.start();
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
                    case TileMap.TARGET:
                        batch.draw(targetTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                        break;
                    case TileMap.BOX:
                        batch.draw(boxTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                        break;
                    case TileMap.BOX_ON_TARGET:
                        batch.draw(targetTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                        batch.draw(boxTexturePlaced, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onDrawHUD() {
        int moves = game.getPlayer().getMoveCount();
        int pushes = game.getPlayer().getPushCount();

        int totalSeconds = (int) timeChronometer;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);

        BitmapFont f = font;
        f.draw(batch, "Nivel " + level
                + "  Pasos: " + moves
                + "  Empujes: " + pushes
                + "  Tiempo: " + timeStr,
                6, GameConfig.PX_HEIGHT - 6);

        if (level == 0) {
            float x = 6f, y = 36f;
            font.draw(batch, "Controles:", x, y + 24);
            font.draw(batch, "Arriba: " + sUp + "   Abajo: " + sDown, x, y + 12);
            font.draw(batch, "Izquierda: " + sLeft + "   Derecha: " + sRight + "   Reiniciar: " + sReset, x, y);
        }
    }

    @Override
    protected void onDisposeExtra() {
        boxTexture.dispose();
        boxTexturePlaced.dispose();
        targetTexture.dispose();
        boxPlacedSound.dispose();
    }
}
