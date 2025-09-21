package Screens;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.Input.Keys.*;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import GameLogic.Directions;
import GameLogic.GameConfig;
import GameLogic.MovementThread;
import GameLogic.Player;
import GameLogic.SokobanGame;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.elkinedwin.LogicaUsuario.Usuario;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.elkinedwin.LogicaUsuario.AudioBus;
import com.elkinedwin.LogicaUsuario.AudioX;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;

public abstract class BasePlayScreen implements Screen {

    protected final Game app;
    protected final SokobanGame game = new SokobanGame();

    protected OrthographicCamera camera;
    protected FitViewport viewport;
    protected SpriteBatch batch;

    protected final int level;

    protected BlockingQueue<Directions> directionQueue;
    protected MovementThread movementThreadLogic;
    protected Thread movementThread;

    protected BitmapFont font;
    protected FreeTypeFontGenerator fontGenerator;
    protected FreeTypeFontParameter fontParameter;

    protected Texture floorTexture, wallTexture;

    protected Texture[] downFrames, upFrames, leftFrames, rightFrames;

    protected Sound stepSound, resetLevelSound, boxPlacedSound;
    protected Music bgMusic;

    protected int prevX, prevY;
    protected boolean moveRequested = false;

    protected Directions facing = Directions.DOWN;

    protected float playerRatio = 1f;
    protected Player player;

    protected boolean tweenActive = false;
    protected float tweenTime = 0f;
    protected float tweenDuration = 0.165f;
    protected float spriteXStart, spriteYStart, spriteXEnd, spriteYEnd;
    protected float drawPX, drawPY;

    protected final float initialDelay = tweenDuration;
    protected final float repeatRate = tweenDuration;
    protected Directions heldDirection = null;
    protected float holdTimer = 0f;

    protected int kUp, kDown, kLeft, kRight, kReset, kPause;
    protected String sUp, sDown, sLeft, sRight, sReset, sPause;

    protected boolean paused = false;
    private Stage pauseStage;
    private Table pauseRoot, pausePanel;

    private Texture dimTexture, btnTexture;

    protected float timeChronometer = 0f;

    protected int prevPushes;

    public BasePlayScreen(Game app, int level) {
        this.app = app;
        this.level = level;
    }

    protected void setPlayer(Player player) {
        this.player = player;
        this.drawPX = player.getX() * GameConfig.TILE_SIZE;
        this.drawPY = player.getY() * GameConfig.TILE_SIZE;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.PX_WIDTH, GameConfig.PX_HEIGHT, camera);
        batch = new SpriteBatch();

        game.startLevel(level);
        setPlayer(game.getPlayer());

        directionQueue = new ArrayBlockingQueue<>(1);
        movementThreadLogic = new MovementThread(directionQueue, game.getMap(), game.getPlayer());
        movementThread = new Thread(movementThreadLogic);
        movementThread.setDaemon(true);
        movementThread.start();

        prevX = game.getPlayer().getX();
        prevY = game.getPlayer().getY();

        loadCommonAssets();

        pauseStage = new Stage(viewport);
        Label.LabelStyle pauseStyle = new Label.LabelStyle(font, Color.WHITE);

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(0, 0, 0, 0.6f);
        pm.fill();
        dimTexture = new Texture(pm);
        pm.dispose();
        Drawable dimBg = new TextureRegionDrawable(new TextureRegion(dimTexture));

        Pixmap pb = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pb.setColor(Color.WHITE);
        pb.fill();
        btnTexture = new Texture(pb);
        pb.dispose();
        Drawable up = new TextureRegionDrawable(new TextureRegion(btnTexture)).tint(new Color(1f, 1f, 1f, 0.92f));
        Drawable down = new TextureRegionDrawable(new TextureRegion(btnTexture)).tint(new Color(0.90f, 0.90f, 0.90f, 1f));

        TextButtonStyle bs = new TextButtonStyle();
        bs.font = font;
        bs.fontColor = Color.BLACK;
        bs.up = up;
        bs.down = down;

        pauseRoot = new Table();
        pauseRoot.setFillParent(true);
        pauseRoot.setBackground(dimBg);

        pausePanel = new Table();
        pausePanel.pad(24f);
        pausePanel.defaults().pad(8f);
        pausePanel.add(new Label("JUEGO PAUSADO", pauseStyle)).row();
        pausePanel.add(new Label(sPause + ": Reanudar", pauseStyle)).row();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.GRAY;

        TextButton exitButton = new TextButton("Salir del Juego", buttonStyle);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(new MenuScreen(app));
            }
        });

        pausePanel.add(exitButton).padTop(20f).row();

        pauseRoot.add(pausePanel).center();
        pauseStage.addActor(pauseRoot);
        pauseRoot.setVisible(false);

        onShowExtra();
    }

    @Override
    public void render(float delta) {
        onUpdate(delta);
        advanceTween(delta);

        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        onDrawMap();
        drawPlayer();
        onDrawHUD();
        batch.end();

        if (paused) {
            pauseStage.act(delta);
            pauseStage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        pauseStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        if (bgMusic != null) {
            bgMusic.stop();
            AudioBus.unregisterMusic(bgMusic);
        }
    }

    @Override
    public void dispose() {
        try {
            if (movementThreadLogic != null) {
                movementThreadLogic.stop();
            }
            if (movementThread != null) {
                movementThread.interrupt();
            }
        } catch (Exception ignored) {
        }
        pauseStage.dispose();
        onDisposeExtra();
        batch.dispose();
        disposeCommonAssets();
    }

    protected abstract void onShowExtra();

    protected abstract void onDrawMap();

    protected abstract void onDrawHUD();

    protected abstract void onDisposeExtra();

    protected void onUpdate(float delta) {

        if (input.isKeyJustPressed(kPause)) {
            paused = !paused;

            if (paused) {
                directionQueue.clear();
                pauseRoot.setVisible(true);
                if (bgMusic != null) {
                    bgMusic.pause();
                }
                input.setInputProcessor(pauseStage);
            } else {
                pauseRoot.setVisible(false);
                if (bgMusic != null) {
                    bgMusic.play();
                }
                input.setInputProcessor(null);
            }
        }

        if (paused) {
            return;
        }

        timeChronometer += delta;

        if (!tweenActive) {
            handleHeldInput(delta);
        }

        detectAndAnimateMovement();

        if (moveRequested && !tweenActive && directionQueue.isEmpty()) {
            Player player = game.getPlayer();
            if (player.getX() == prevX && player.getY() == prevY) {
                moveRequested = false;
            }
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
                    if (front == GameLogic.TileMap.BOX_ON_TARGET && boxPlacedSound != null) {
                        AudioX.play(boxPlacedSound, 1f);
                    }
                }
            }

            AudioX.play(stepSound, 1f);

            startTween(startPX, startPY, endPX, endPY);

            game.recomputeVictory();
            if (game.isVictory()) {
                if (bgMusic != null) {
                    bgMusic.stop();
                }
                int totalSec = (int) timeChronometer;
                int moves = p.getMoveCount();
                int pushes = p.getPushCount();
                if (level == 0) {
                    try {
                        if (ManejoUsuarios.UsuarioActivo != null) {
                            ManejoUsuarios.UsuarioActivo.setTutocomplete(true);
                        }
                    } catch (Exception ignored) {
                    }
                }
                app.setScreen(new VictoryScreen(app, level, moves, pushes, totalSec, 7, 0, font));
                return;
            }

            prevX = cx;
            prevY = cy;
            prevPushes = p.getPushCount();
            moveRequested = false;
        }
    }

    protected void loadCommonAssets() {
        fontGenerator = new FreeTypeFontGenerator(files.internal("fonts/pokemon_fire_red.ttf"));
        fontParameter = new FreeTypeFontParameter();
        fontParameter.size = 20;
        fontParameter.color = Color.WHITE;
        fontParameter.borderWidth = 1;
        fontParameter.borderColor = Color.BLACK;
        font = fontGenerator.generateFont(fontParameter);
        fontGenerator.dispose();

        floorTexture = load("textures/floor.png");
        wallTexture = load("textures/wall.png");

        Texture downWalk1 = load("textures/player_down_walk1.png");
        Texture downIdle = load("textures/player_down_idle.png");
        Texture downWalk2 = load("textures/player_down_walk2.png");
        Texture upWalk1 = load("textures/player_up_walk1.png");
        Texture upIdle = load("textures/player_up_idle.png");
        Texture upWalk2 = load("textures/player_up_walk2.png");
        Texture leftWalk1 = load("textures/player_left_walk1.png");
        Texture leftIdle = load("textures/player_left_idle.png");
        Texture leftWalk2 = load("textures/player_left_walk2.png");
        Texture rightWalk1 = load("textures/player_right_walk1.png");
        Texture rightIdle = load("textures/player_right_idle.png");
        Texture rightWalk2 = load("textures/player_right_walk2.png");

        kUp = getCfgKey("Arriba", Input.Keys.UP);
        kDown = getCfgKey("Abajo", Input.Keys.DOWN);
        kLeft = getCfgKey("Izquierda", Input.Keys.LEFT);
        kRight = getCfgKey("Derecha", Input.Keys.RIGHT);
        kReset = getCfgKey("Reiniciar", Input.Keys.R);
        kPause = getCfgKey("Pausar", Input.Keys.ESCAPE);

        sUp = Input.Keys.toString(kUp);
        sDown = Input.Keys.toString(kDown);
        sLeft = Input.Keys.toString(kLeft);
        sRight = Input.Keys.toString(kRight);
        sReset = Input.Keys.toString(kReset);
        sPause = Input.Keys.toString(kPause);

        downFrames = new Texture[]{downWalk1, downIdle, downWalk2};
        upFrames = new Texture[]{upWalk1, upIdle, upWalk2};
        leftFrames = new Texture[]{leftWalk1, leftIdle, leftWalk2};
        rightFrames = new Texture[]{rightWalk1, rightIdle, rightWalk2};

        stepSound = AudioX.newSound("audios/step.wav");
        resetLevelSound = AudioX.newSound("audios/reset_level.wav");
        bgMusic = AudioX.newMusic("audios/levelSong.mp3");
        bgMusic.setLooping(true);
        bgMusic.play();

        playerRatio = (float) downFrames[1].getHeight() / downFrames[1].getWidth();
    }

    protected Texture load(String path) {
        return new Texture(files.internal(path));
    }

    protected void disposeCommonAssets() {
        floorTexture.dispose();
        wallTexture.dispose();
        btnTexture.dispose();

        for (Texture t : downFrames) {
            t.dispose();
        }
        for (Texture t : upFrames) {
            t.dispose();
        }
        for (Texture t : leftFrames) {
            t.dispose();
        }
        for (Texture t : rightFrames) {
            t.dispose();
        }

        stepSound.dispose();
        resetLevelSound.dispose();

        if (bgMusic != null) {
            AudioBus.unregisterMusic(bgMusic);
            bgMusic.dispose();
        }

        font.dispose();
    }

    protected int getCfgKey(String name, int def) {
        try {
            Usuario usuario = com.elkinedwin.LogicaUsuario.ManejoUsuarios.UsuarioActivo;
            if (usuario != null && usuario.configuracion != null) {
                Integer v = usuario.configuracion.get(name);
                if (v != null && v != 0) {
                    return v;
                }
            }
        } catch (Exception ignored) {
        }
        return def;
    }

    protected Directions readHeldDirection() {
        kUp = getCfgKey("MoverArriba", UP);
        kDown = getCfgKey("MoverAbajo", DOWN);
        kLeft = getCfgKey("MoverIzq", LEFT);
        kRight = getCfgKey("MoverDer", RIGHT);

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

    protected void startTween(float startPX, float startPY, float endPX, float endPY) {
        spriteXStart = startPX;
        spriteYStart = startPY;
        spriteXEnd = endPX;
        spriteYEnd = endPY;

        drawPX = startPX;
        drawPY = startPY;

        tweenTime = 0f;
        tweenActive = true;
    }

    protected void advanceTween(float delta) {
        if (!tweenActive) {
            return;
        }

        tweenTime += delta;
        float t = tweenTime / tweenDuration;
        if (t >= 1f) {
            t = 1f;
            tweenActive = false;
        }
        drawPX = spriteXStart + (spriteXEnd - spriteXStart) * t;
        drawPY = spriteYStart + (spriteYEnd - spriteYStart) * t;
    }

    protected void drawPlayer() {
        Texture frame = pickFrameForFacing();
        float w = GameConfig.TILE_SIZE;
        float h = GameConfig.TILE_SIZE * playerRatio;
        batch.draw(frame, drawPX, drawPY, w, h);
    }

    protected Texture pickFrameForFacing() {
        Texture arr[];
        switch (facing) {
            case UP:
                arr = upFrames;
                break;
            case DOWN:
                arr = downFrames;
                break;
            case LEFT:
                arr = leftFrames;
                break;
            case RIGHT:
                arr = rightFrames;
                break;
            default:
                arr = downFrames;
        }
        if (!tweenActive) {
            return arr[1];
        }
        float t = tweenTime / tweenDuration;
        int idx = (int) (t * 3.0f);
        if (idx > 2) {
            idx = 2;
        }
        return arr[idx];
    }
}
