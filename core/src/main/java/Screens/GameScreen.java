package Screens;

import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.Input.Keys.R;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import GameLogic.Directions;
import GameLogic.GameConfig;
import GameLogic.MovementThread;
import GameLogic.SokobanGame;
import GameLogic.TileMap;
import GameLogic.Player;

public final class GameScreen extends BasePlayScreen {

    private final SokobanGame game = new SokobanGame();

    // Hilo y cola
    private BlockingQueue<Directions> directionQueue;
    private MovementThread movementThreadLogic;
    private Thread movementThread;

    // Nivel actual
    private final int level;

    // Texturas
    private Texture boxTexture, boxTexturePlaced, targetTexture;

    // Sonidos
    private Sound boxPlacedSound;

    // Para detectar movimiento entre frames
    private int prevX, prevY;
    private int prevPushes;

    private boolean moveRequested = false;

    public GameScreen(Game app, int level) {
        super(app);
        this.level = level;
    }

    @Override
    protected void onShowExtra() {
        game.startLevel(level);

        // Enlazar jugador para BasePlayScreen (para dibujarlo)
        setPlayer(game.getPlayer());
        prevPushes = game.getPlayer().getPushCount();

        boxTexture = load("textures/box.png");
        boxTexturePlaced = load("textures/boxPlaced.png");
        targetTexture = load("textures/target.png");
        boxPlacedSound = loadSound("audios/box_placed.wav");

        directionQueue = new ArrayBlockingQueue<>(1);
        movementThreadLogic = new MovementThread(directionQueue, game.getMap(), game.getPlayer());
        movementThread = new Thread(movementThreadLogic);
        movementThread.setDaemon(true);
        movementThread.start();

        prevX = game.getPlayer().getX();
        prevY = game.getPlayer().getY();
    }

    @Override
    protected void onUpdate(float delta) {
        if (!tweenActive) {
            handleHeldInput(delta);
        }

        detectAndAnimateMovement();

        if (moveRequested && !tweenActive && directionQueue.isEmpty()) {
            Player p = game.getPlayer();
            if (p.getX() == prevX && p.getY() == prevY) {
                moveRequested = false;
            }
        }

        int reiniciarKey = getCfgKey("Reiniciar", R);
        if (input.isKeyJustPressed(reiniciarKey)) {
            resetLevel();
        }
    }

    private void handleHeldInput(float delta) {
        if (tweenActive || moveRequested) {
            return;
        }
        Directions currentHeld = readHeldDirection();
        if (currentHeld == null) {
            heldDirection = null;
            holdTimer = 0f;
            return;
        }

        if (heldDirection == null || currentHeld != heldDirection) {
            heldDirection = currentHeld;
            holdTimer = 0f;
            enqueueDirection(heldDirection);
            facing = heldDirection;
            return;
        }

        holdTimer += delta;
        if (holdTimer >= initialDelay) {
            float over = holdTimer - initialDelay;
            while (over >= repeatRate && !moveRequested) {
                enqueueDirection(heldDirection);
                over -= repeatRate;
            }
            holdTimer = initialDelay + over;
        }
    }

    private void enqueueDirection(Directions dir) {
        if (tweenActive || moveRequested) {
            return;
        }
        if (directionQueue.offer(dir)) {
            moveRequested = true;
        }
    }

    private void detectAndAnimateMovement() {
        Player p = game.getPlayer();
        int cx = p.getX();
        int cy = p.getY();

        if (moveRequested && (cx != prevX || cy != prevY)) {
            float startPX = prevX * GameConfig.TILE_SIZE;
            float startPY = prevY * GameConfig.TILE_SIZE;
            float endPX = cx * GameConfig.TILE_SIZE;
            float endPY = cy * GameConfig.TILE_SIZE;

            boolean pushed = (p.getPushCount() > prevPushes);

            int dx = Integer.compare(cx, prevX);
            int dy = Integer.compare(cy, prevY);

            if (pushed) {
                int fx = cx + dx, fy = cy + dy;
                if (game.getMap().isInBounds(fx, fy)) {
                    char front = game.getMap().getTile(fx, fy);
                    if (front == TileMap.BOX_ON_TARGET && boxPlacedSound != null) {
                        boxPlacedSound.play(1f);
                    }
                }
            }

            stepSound.play(1f);

            startTween(startPX, startPY, endPX, endPY);

            game.recomputeVictory();
            if (game.isVictory()) {
                bgMusic.stop();
                int totalSec = (int) timeChronometer;
                int moves = p.getMoveCount();
                int pushes = p.getPushCount();
                app.setScreen(new VictoryScreen(app, level, moves, pushes, totalSec, 7, 0, font));
                return;
            }

            prevX = cx;
            prevY = cy;
            prevPushes = p.getPushCount();
            moveRequested = false;
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
    }

    @Override
    protected void onDisposeExtra() {
        try {
            if (movementThreadLogic != null) {
                movementThreadLogic.stop();
            }
            if (movementThread != null) {
                movementThread.interrupt();
            }
        } catch (Exception ignored) {
        }

        boxTexture.dispose();
        boxTexturePlaced.dispose();
        targetTexture.dispose();
        boxPlacedSound.dispose();
    }
}
