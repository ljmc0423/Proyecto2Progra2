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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

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

    //nivel normal
    private Texture floorTexture, wallTexture, boxTexture, boxTexturePlaced, targetTexture;

    //selector de niveles
    private Texture elevatorTexture, carpetTexture;

    private boolean elevatorOpen = false;
    private int selectedFloor = 0;
    private int maxUnlockedFloor = 1;
    private int maxAvailableFloor = 7;

    // coords del elevador (casilla)
    private int elevatorX = -1, elevatorY = -1;

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
    private FreeTypeFontGenerator generator;
    private FreeTypeFontParameter parameter;

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

    //tiempo transcurrido
    private float timeChronometer;

    //nivel actual
    private int level = 0;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.PX_WIDTH, GameConfig.PX_HEIGHT, camera);

        batch = new SpriteBatch();

        game.startLevel(level);

        elevatorX = -1;
        elevatorY = -1;
        elevatorOpen = false;

        movementThread = new Thread(movementThreadLogic);
        movementThread.start();

        generator = new FreeTypeFontGenerator(files.internal("fonts/pokemon_fire_red.ttf"));
        parameter = new FreeTypeFontParameter();
        parameter.size = 20; // tamaño de la fuente en px
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        font = generator.generateFont(parameter);
        generator.dispose();

        floorTexture = load("textures/floor.png");
        wallTexture = load("textures/wall.png");
        boxTexture = load("textures/box.png");
        boxTexturePlaced = load("textures/boxPlaced.png");
        targetTexture = load("textures/target.png");

        elevatorTexture = load("textures/elevator.png");
        carpetTexture = load("textures/carpet.png");

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
        timeChronometer += delta;

        updateElevatorOverlay();

        if (!elevatorOpen) {
            updateContinuousInput(delta);
            applyMoveResult();
            resetLevel();
            elevatorInput();
            advanceTween(delta);
        }

        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawMap();
        drawElevator();
        drawPlayer();
        drawHUD();
        if (elevatorOpen) {
            drawElevatorOverlay();
        }
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
            elevatorX = -1;
            elevatorY = -1;
            elevatorOpen = false;
            timeChronometer = 0f;
            tweenActive = false;
        }
    }

    private void updateElevatorOverlay() {
        if (!elevatorOpen) {
            return;
        }

        if (input.isKeyJustPressed(UP)) {
            selectedFloor = Math.max(1, selectedFloor - 1);
        }
        if (input.isKeyJustPressed(DOWN)) {
            selectedFloor = Math.min(maxAvailableFloor, selectedFloor + 1);
        }

        selectedFloor = Math.min(selectedFloor, maxUnlockedFloor);

        if (input.isKeyJustPressed(ENTER)) {
            if (selectedFloor <= maxUnlockedFloor) {
                level = selectedFloor;
                elevatorX = -1;
                elevatorY = -1;
                timeChronometer = 0f;
                game.startLevel(level);
                tweenActive = false;
                elevatorOpen = false;
                //sonido de “ding”
            } else {
                //?
            }
            return;
        }

        if (input.isKeyJustPressed(ESCAPE)) {
            elevatorOpen = false;
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

    private void elevatorInput() {
        if (!elevatorOpen && input.isKeyJustPressed(ENTER) && isFacingElevator()) {
            elevatorOpen = true;
            selectedFloor = Math.min(level, maxUnlockedFloor);
        }
    }

    private Directions readHeldDirection() {
        int kUp = getCfgKey("MoverArriba", UP);
        int kDown = getCfgKey("MoverAbajo", DOWN);
        int kLeft = getCfgKey("MoverIzq", LEFT);
        int kRight = getCfgKey("MoverDer", RIGHT);

        if (input.isKeyPressed(kUp)) {
            return Directions.UP;
        }
        if (input.isKeyPressed(kDown)) {
            return Directions.DOWN;
        }
        if (input.isKeyPressed(kLeft)) {
            return Directions.LEFT;
        }
        if (input.isKeyPressed(kRight)) {
            return Directions.RIGHT;
        }

        return null;
    }

    private int getCfgKey(String name, int def) {
        try {
            if (ManejoUsuarios.UsuarioActivo != null && ManejoUsuarios.UsuarioActivo.configuracion != null) {
                Integer v = ManejoUsuarios.UsuarioActivo.configuracion.get(name);
                if (v != null && v != 0) {
                    return v;
                }
            }
        } catch (Exception ignored) {
        }
        return def;
    }

    private boolean isFacingElevator() {
        int px = game.getPlayer().getX();
        int py = game.getPlayer().getY();
        int nx = px + facing.dx;
        int ny = py + facing.dy;

        if (!game.getMap().isInBounds(nx, ny)) {
            return false;
        }
        return game.getMap().isElevator(game.getMap().getTile(nx, ny));
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
                    case TileMap.CARPET:
                        batch.draw(carpetTexture, px, py, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                        break;
                    case TileMap.ELEVATOR:
                        // Guardar la posición del elevador; no dibujar aquí
                        elevatorX = col;
                        elevatorY = row;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void drawElevator() {
        if (elevatorX < 0 || elevatorY < 0) {
            return;
        }
        float px = elevatorX * GameConfig.TILE_SIZE;
        float py = elevatorY * GameConfig.TILE_SIZE;

        float offsetX = (GameConfig.TILE_SIZE - elevatorTexture.getWidth()) / 2f;

        batch.draw(
                elevatorTexture,
                px + offsetX,
                py,
                elevatorTexture.getWidth(),
                elevatorTexture.getHeight()
        );
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

    private void drawElevatorOverlay() {
        // batch.setColor(0, 0, 0, 0.5f);
        // batch.draw(algunaTexturaSemiTransparente, 0, 0, GameConfig.PX_WIDTH, GameConfig.PX_HEIGHT);
        // batch.setColor(Color.WHITE);

        String title = "Elevador: seleccionar piso";
        font.draw(batch, title, 40, GameConfig.PX_HEIGHT - 40);

        int y = GameConfig.PX_HEIGHT - 70;
        int lineStep = 24;

        for (int i = 1; i <= maxAvailableFloor; i++) {
            boolean isSelected = (i == selectedFloor);
            boolean isLocked = (i > maxUnlockedFloor);

            String mark = isSelected ? ">" : " ";
            String label = isLocked ? ("Piso " + i + " (bloqueado)") : ("Piso " + i);

            if (isLocked) {
                font.setColor(Color.GRAY);
            } else {
                font.setColor(Color.WHITE);
            }

            font.draw(batch, mark + " " + label, 60, y);
            y -= lineStep;
        }

        font.setColor(Color.WHITE);
        font.draw(batch, "[ARRIBA/ABAJO] Mover  [ENTER] Entrar  [ESC] Cancelar", 40, 40);
    }

    private void drawHUD() {
        int moves = game.getPlayer().getMoveCount();
        int pushes = game.getPlayer().getPushCount();

        int totalSeconds = (int) timeChronometer;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);

        font.draw(batch, "Nivel " + level
                + "  Pasos: " + moves
                + "  Empujes: " + pushes
                + "  Tiempo: " + timeStr,
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
        elevatorTexture.dispose();
        carpetTexture.dispose();
    }
}
