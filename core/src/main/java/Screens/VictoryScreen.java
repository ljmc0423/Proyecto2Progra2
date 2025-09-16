package Screens;

import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.Input.Keys.*;

import GameLogic.GameConfig;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public final class VictoryScreen implements Screen {

    private final Game app;
    private final int level;
    private final int moves;
    private final int pushes;
    private final int totalSeconds;
    private final int maxAvailableFloor;
    private final int selectorLevel;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private final BitmapFont font;

    private Texture overlay;

    public VictoryScreen(Game app, int level, int moves, int pushes, int totalSeconds, int maxAvailableFloor, int selectorLevel,
            BitmapFont font) {
        
        this.app = app;
        this.level = level;
        this.moves = moves;
        this.pushes = pushes;
        this.totalSeconds = totalSeconds;
        this.maxAvailableFloor = maxAvailableFloor;
        this.selectorLevel = selectorLevel;
        this.font = font;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.PX_WIDTH, GameConfig.PX_HEIGHT, camera);
        batch = new SpriteBatch();

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        overlay = new Texture(pm);
        pm.dispose();
    }

    @Override
    public void render(float delta) {
        handleInput();

        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.setColor(0f, 0f, 0f, 1f);
        batch.draw(overlay, 0, 0, GameConfig.PX_WIDTH, GameConfig.PX_HEIGHT);
        batch.setColor(Color.WHITE);

        drawPanel();
        batch.end();
    }

    private void handleInput() {
        if (input.isKeyJustPressed(ENTER)) {
            int next = Math.min(level + 1, maxAvailableFloor);
            app.setScreen(new GameScreen(app, next));
        }
        
        if (input.isKeyJustPressed(ESCAPE)) {
            app.setScreen(new GameScreen(app, selectorLevel));
        }
    }

    private void drawPanel() {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);

        float x = 60;
        float y = GameConfig.PX_HEIGHT - 80;

        font.setColor(Color.WHITE);
        font.draw(batch, "¡Nivel " + level + " completado!", x, y);
        y -= 30;
        font.draw(batch, "Pasos: " + moves + "  Empujes: " + pushes + "  Tiempo: " + timeStr, x, y);
        y -= 40;
        font.draw(batch, "[ENTER] Siguiente [ESC] Selector", x, y);
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
        batch.dispose();
        if (overlay != null) {
            overlay.dispose();
        }
    }
}
