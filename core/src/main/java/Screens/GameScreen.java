// ======= Screens/GameScreen.java =======
package Screens;

import GameLogic.Directions;
import GameLogic.GameConfig;
import GameLogic.MapCopy;
import GameLogic.MoveApplier;
import GameLogic.MoveResult;
import GameLogic.MovementThread;
import GameLogic.SharedMovement;
import GameLogic.SokobanGame;
import GameLogic.TileMap;
import GameLogic.Type;

import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.Input.Keys.*;
import com.badlogic.gdx.audio.Sound;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.elkinedwin.LogicaUsuario.ManejoUsuarios;
public final class GameScreen implements Screen {

    //Clases de libgdx
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;

    //juego
    private final SokobanGame game = new SokobanGame();

    //lógica de movimiento
    private final MoveApplier applier = new MoveApplier();
    private final SharedMovement shared = new SharedMovement();
    private final BlockingQueue<Directions> directionQueue = new ArrayBlockingQueue<>(3);
    private final MovementThread movementThreadLogic = new MovementThread(shared, directionQueue);
    private Thread movementThread;

    private Texture floorTexture, wallTexture, boxTexture, boxTexturePlaced, targetTexture;

    //frames
    private Texture downWalk1, downIdle, downWalk2;
    private Texture upWalk1, upIdle, upWalk2;
    private Texture leftWalk1, leftIdle, leftWalk2;
    private Texture rightWalk1, rightIdle, rightWalk2;

    //audios
    private Sound stepSound;
    private Sound boxPlacedSound;
    private Sound resetLevelSound;
    private Music bgMusic;

    //fuente
    private BitmapFont font;

    private Directions facing = Directions.DOWN; //por default, ve hacia abajo
    private float playerRatio = 1f;

    //tween
    private boolean tweenActive = false;
    private float tweenTime = 0f;
    private final float tweenDuration = 0.165f;
    private float spriteXStart, spriteYStart;
    private float spriteXEnd, spriteYEnd;

    private final float initialDelay = tweenDuration;
    private final float repeatRate = tweenDuration;
    private Directions heldDirection = null;
    private float holdTimer = 0f;

    //nivel actual
    private int level = 1;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.PX_WIDTH, GameConfig.PX_HEIGHT, camera);

        batch = new SpriteBatch();

        game.startLevel(level);

        movementThread = new Thread(movementThreadLogic);
        movementThread.start();

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        floorTexture = load("textures/floor.png");
        wallTexture = load("textures/wall.png");
        boxTexture = load("textures/box.png");
        boxTexturePlaced = load("textures/boxPlaced.png");
        targetTexture = load("textures/target.png");

        downWalk1 = load("textures/player_down_walk1.png");
        downIdle = load("textures/player_down_idle.png");
        downWalk2 = load("textures/player_down_walk2.png");

        upWalk1 = load("textures/player_up_walk1.png");
        upIdle = load("textures/player_up_idle.png");
        upWalk2 = load("textures/player_up_walk2.png");

        leftWalk1 = load("textures/player_left_walk1.png");
        leftIdle = load("textures/player_left_idle.png");
        leftWalk2 = load("textures/player_left_walk2.png");

        rightWalk1 = load("textures/player_right_walk1.png");
        rightIdle = load("textures/player_right_idle.png");
        rightWalk2 = load("textures/player_right_walk2.png");

        stepSound = loadSound("audios/step.wav");
        boxPlacedSound = loadSound("audios/box_placed.wav");
        resetLevelSound = loadSound("audios/reset_level.wav");


        //el sprite del player no es 16*16

        bgMusic = audio.newMusic(files.internal("audios/levelSong.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.25f); //0.25 para que no le quite protagonismo a los otros efectos de sonidos
        bgMusic.play();

        //esto es importante, pues el sprite del player no es 16*16

        playerRatio = (float) downIdle.getHeight() / downIdle.getWidth();
    }

    private Texture load(String path) {
        return new Texture(files.internal(path));
    }

    private Sound loadSound(String path) {
        return audio.newSound(files.internal(path));
    }

    @Override
    public void render(float delta) {
        updateContinuousInput(delta);

        applyMoveResult();

        resetLevel();

        advanceTween(delta);

        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawMap();
        drawPlayer();
        drawHUD();
        batch.end();
    }

    private void applyMoveResult() {
        MoveResult moveResult = shared.takeResult();

        if (moveResult == null || !moveResult.hasMoved()) {
            return;
        }

        //posición en píxeles antes de moverse
        float startPX = game.getPlayer().getX() * GameConfig.TILE_SIZE;
        float startPY = game.getPlayer().getY() * GameConfig.TILE_SIZE;

        applier.apply(game.getMap(), game.getPlayer(), moveResult);
        game.recomputeVictory();

        float endPX = game.getPlayer().getX() * GameConfig.TILE_SIZE;
        float endPY = game.getPlayer().getY() * GameConfig.TILE_SIZE;

        stepSound.play(1.0f);

        if (moveResult.getType() == Type.PUSH
                && game.getMap().getTile(moveResult.getBoxDestX(), moveResult.getBoxDestY()) == TileMap.BOX_ON_TARGET) {
            boxPlacedSound.play(1.0f);
        }

        startTween(startPX, startPY, endPX, endPY);
    }

    private void startTween(float startPX, float startPY, float endPX, float endPY) {
        spriteXStart = startPX;
        spriteYStart = startPY;
        spriteXEnd = endPX;
        spriteYEnd = endPY;
        tweenTime = 0f;
        tweenActive = true;
    }

    private void resetLevel() {
        int reiniciarKey = getCfgKey("Reiniciar", R);
        if (input.isKeyJustPressed(reiniciarKey)) {
            resetLevelSound.play(1.0f);
            game.startLevel(level);
            tweenActive = false;
        }
    }

    private void advanceTween(float delta) {
        if (!tweenActive) {
            return;
        }
        tweenTime += delta;
        if (tweenTime >= tweenDuration) {
            tweenActive = false;
        }
    }

    private void updateContinuousInput(float delta) {
        if (tweenActive) {
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
            return;
        }

        holdTimer += delta;
        if (holdTimer >= initialDelay) {
            float over = holdTimer - initialDelay;
            while (over >= repeatRate) {
                enqueueDirection(heldDirection);
                over -= repeatRate;
            }
            holdTimer = initialDelay + over;
        }
    }

    // === AHORA lee las teclas según el HashMap del usuario activo ===
    private Directions readHeldDirection() {
        int kUp    = getCfgKey("MoverArriba", UP);
        int kDown  = getCfgKey("MoverAbajo",  DOWN);
        int kLeft  = getCfgKey("MoverIzq",    LEFT);
        int kRight = getCfgKey("MoverDer",    RIGHT);

        if (input.isKeyPressed(kUp))    return Directions.UP;
        if (input.isKeyPressed(kDown))  return Directions.DOWN;
        if (input.isKeyPressed(kLeft))  return Directions.LEFT;
        if (input.isKeyPressed(kRight)) return Directions.RIGHT;

        return null;
    }

    private int getCfgKey(String name, int def) {
        try {
            if (ManejoUsuarios.UsuarioActivo != null && ManejoUsuarios.UsuarioActivo.configuracion != null) {
                Integer v = ManejoUsuarios.UsuarioActivo.configuracion.get(name);
                if (v != null && v != 0) return v;
            }
        } catch (Exception ignored) {}
        return def;
    }

    private void enqueueDirection(Directions direction) {
        if (directionQueue.size() >= 1) {
            return;
        }
        facing = direction;
        movementThreadLogic.updateCopies(
                MapCopy.copy(game.getMap()),
                game.getPlayer().getPosition()
        );
        directionQueue.offer(direction);
    }

    private void drawMap() {
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

    private void drawPlayer() {
        float drawX, drawY;
        if (tweenActive) {
            float t = tweenTime / tweenDuration;
            drawX = spriteXStart + (spriteXEnd - spriteXStart) * t;
            drawY = spriteYStart + (spriteYEnd - spriteYStart) * t;
        } else {
            drawX = game.getPlayer().getX() * GameConfig.TILE_SIZE;
            drawY = game.getPlayer().getY() * GameConfig.TILE_SIZE;
        }

        Texture frame = pickFrameForFacing();

        float w = GameConfig.TILE_SIZE;
        float h = GameConfig.TILE_SIZE * playerRatio;
        batch.draw(frame, drawX, drawY, w, h);
    }

    private void drawHUD() {
        // Texto simple arriba-izquierda
        int moves = game.getPlayer().getMoveCount();
        int pushes = game.getPlayer().getPushCount();

        font.setColor(Color.WHITE);
        font.draw(batch, "Nivel " + level + "  Pasos: " + moves + "  Empujes: " + pushes,
                6, GameConfig.PX_HEIGHT - 6);
    }

    private Texture pickFrameForFacing() {
        if (!tweenActive) {
            switch (facing) {
                case UP:
                    return upIdle;
                case DOWN:
                    return downIdle;
                case LEFT:
                    return leftIdle;
                case RIGHT:
                    return rightIdle;
                default:
                    return downIdle;
            }
        }

        float t = tweenTime / tweenDuration;
        if (t < 1f / 3f) {
            switch (facing) {
                case UP:
                    return upWalk1;
                case DOWN:
                    return downWalk1;
                case LEFT:
                    return leftWalk1;
                case RIGHT:
                    return rightWalk1;
                default:
                    return downWalk1;
            }
        } else if (t < 2f / 3f) {
            switch (facing) {
                case UP:
                    return upIdle;
                case DOWN:
                    return downIdle;
                case LEFT:
                    return leftIdle;
                case RIGHT:
                    return rightIdle;
                default:
                    return downIdle;
            }
        } else {
            switch (facing) {
                case UP:
                    return upWalk2;
                case DOWN:
                    return downWalk2;
                case LEFT:
                    return leftWalk2;
                case RIGHT:
                    return rightWalk2;
                default:
                    return downWalk2;
            }
        }
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
        directionQueue.offer(Directions.QUIT);
        movementThreadLogic.stop();
    }

    @Override
    public void dispose() {
        batch.dispose();
        floorTexture.dispose();
        wallTexture.dispose();
        boxTexture.dispose();
        boxTexturePlaced.dispose();
        targetTexture.dispose();
        downWalk1.dispose();
        downIdle.dispose();
        downWalk2.dispose();
        upWalk1.dispose();
        upIdle.dispose();
        upWalk2.dispose();
        leftWalk1.dispose();
        leftIdle.dispose();
        leftWalk2.dispose();
        rightWalk1.dispose();
        rightIdle.dispose();
        rightWalk2.dispose();
        stepSound.dispose();
        boxPlacedSound.dispose();
        resetLevelSound.dispose();
        bgMusic.dispose();
        font.dispose();
    }
}
