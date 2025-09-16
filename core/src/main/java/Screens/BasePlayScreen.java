package Screens;

import static com.badlogic.gdx.Gdx.audio;
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
import GameLogic.Player;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public abstract class BasePlayScreen implements Screen {

    protected final Game app;

    //clases de libgdx
    protected OrthographicCamera camera;
    protected FitViewport viewport;
    protected SpriteBatch batch;

    //Font
    protected BitmapFont font;
    protected FreeTypeFontGenerator fontGenerator;
    protected FreeTypeFontParameter fontParameter;

    //Texturas del mapa
    protected Texture floorTexture, wallTexture;

    // Frames por dirección: [walk1, idle, walk2]
    protected Texture[] downFrames, upFrames, leftFrames, rightFrames;

    //Audio
    protected Sound stepSound, resetLevelSound;
    protected Music bgMusic;

    // Estado de animación
    protected Directions facing = Directions.DOWN;

    protected float playerRatio = 1f;

    protected Player player;

    protected void setPlayer(Player player) {
        this.player = player;
        this.drawPX = player.getX() * GameConfig.TILE_SIZE;
        this.drawPY = player.getY() * GameConfig.TILE_SIZE;
    }

    //tween = in between
    protected boolean tweenActive = false;
    protected float tweenTime = 0f;
    protected float tweenDuration = 0.165f;
    protected float spriteXStart, spriteYStart, spriteXEnd, spriteYEnd;
    protected float drawPX, drawPY;

    protected final float initialDelay = tweenDuration;
    protected final float repeatRate = tweenDuration;
    protected Directions heldDirection = null;
    protected float holdTimer = 0f;

    // Cronómetro
    protected float timeChronometer = 0f;

    public BasePlayScreen(Game app) {
        this.app = app;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.PX_WIDTH, GameConfig.PX_HEIGHT, camera);
        batch = new SpriteBatch();

        loadCommonAssets();
        onShowExtra();
    }

    @Override
    public void render(float delta) {
        timeChronometer += delta;

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
    }

    @Override
    public void dispose() {
        onDisposeExtra();
        batch.dispose();
        disposeCommonAssets();
    }

    protected abstract void onShowExtra();

    protected abstract void onUpdate(float delta);

    protected abstract void onDrawMap();

    protected abstract void onDrawHUD();

    protected abstract void onDisposeExtra();

    protected Texture load(String path) {
        return new Texture(files.internal(path));
    }

    protected Sound loadSound(String path) {
        return audio.newSound(files.internal(path));
    }

    protected void loadCommonAssets() {
        fontGenerator = new FreeTypeFontGenerator(files.internal("fonts/pokemon_fire_red.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 20;
        fontParameter.color = Color.WHITE;
        fontParameter.borderWidth = 1;
        fontParameter.borderColor = Color.BLACK;
        font = fontGenerator.generateFont(fontParameter);
        fontGenerator.dispose();

        // Mapa
        floorTexture = load("textures/floor.png");
        wallTexture = load("textures/wall.png");

        // Frames
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

        downFrames = new Texture[]{downWalk1, downIdle, downWalk2};
        upFrames = new Texture[]{upWalk1, upIdle, upWalk2};
        leftFrames = new Texture[]{leftWalk1, leftIdle, leftWalk2};
        rightFrames = new Texture[]{rightWalk1, rightIdle, rightWalk2};

        // Audio
        stepSound = loadSound("audios/step.wav");
        resetLevelSound = loadSound("audios/reset_level.wav");

        bgMusic = audio.newMusic(files.internal("audios/levelSong.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.25f);
        bgMusic.play();

        playerRatio = (float) downFrames[1].getHeight() / downFrames[1].getWidth();
    }

    protected void disposeCommonAssets() {
        floorTexture.dispose();
        wallTexture.dispose();

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
        bgMusic.dispose();

        font.dispose();
    }

    protected int getCfgKey(String name, int def) {
        try {
            var usuario = com.elkinedwin.LogicaUsuario.ManejoUsuarios.UsuarioActivo;
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
        int kUp = getCfgKey("MoverArriba", UP);
        int kDown = getCfgKey("MoverAbajo", DOWN);
        int kLeft = getCfgKey("MoverIzq", LEFT);
        int kRight = getCfgKey("MoverDer", RIGHT);

        if (input.isKeyPressed(kUp)) {
            return Directions.UP;
        } else if (input.isKeyPressed(kDown)) {
            return Directions.DOWN;
        } else if (input.isKeyPressed(kLeft)) {
            return Directions.LEFT;
        } else if (input.isKeyPressed(kRight)) {
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
