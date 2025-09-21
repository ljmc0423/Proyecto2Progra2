package Screens;

import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.Input.Keys.ENTER;
import static com.badlogic.gdx.Input.Keys.ESCAPE;

import GameLogic.GameConfig;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public final class VictoryScreen implements Screen {

    private final Game app;
    private final int level, moves, pushes, totalSeconds, maxAvailableFloor;
    private final BitmapFont font;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;

    private Texture gradientBg; // fondo degradado
    private Texture px;

    public VictoryScreen(Game app, int level, int moves, int pushes, int totalSeconds,
            int maxAvailableFloor, BitmapFont font) {
        this.app = app;
        this.level = level;
        this.moves = moves;
        this.pushes = pushes;
        this.totalSeconds = totalSeconds;
        this.maxAvailableFloor = maxAvailableFloor;
        this.font = font;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.PX_WIDTH, GameConfig.PX_HEIGHT, camera);
        batch = new SpriteBatch();

        Pixmap grad = new Pixmap(1, GameConfig.PX_HEIGHT, Pixmap.Format.RGBA8888);
        for (int y = 0; y < GameConfig.PX_HEIGHT; y++) {
            float t = (float) y / (float) GameConfig.PX_HEIGHT;
            
            float r = 0.05f + 0.05f * t;
            float g = 0.07f + 0.05f * t;
            float b = 0.10f + 0.05f * t;
            grad.setColor(r, g, b, 1f);
            grad.drawPixel(0, y);
        }
        gradientBg = new Texture(grad);
        grad.dispose();

        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(Color.WHITE);
        p.fill();
        px = new Texture(p);
        p.dispose();
    }

    @Override
    public void render(float delta) {
        handleInput();

        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        
        batch.draw(gradientBg, 0, 0, GameConfig.PX_WIDTH, GameConfig.PX_HEIGHT);
        drawPanelAndStats();
        batch.end();
    }

    private void handleInput() {
        if (input.isKeyJustPressed(ENTER)) {
            int next = Math.min(level + 1, maxAvailableFloor);
            app.setScreen(new GameScreen(app, next));
        }
        if (input.isKeyJustPressed(ESCAPE)) {
            app.setScreen(new StageScreen(app));
        }
    }

    private void drawPanelAndStats() {
        float pw = GameConfig.PX_WIDTH - 120f;
        float ph = 160f;
        float x0 = 60f;
        float y0 = GameConfig.PX_HEIGHT - ph - 70f;

        // panel negro translúcido
        batch.setColor(0f, 0f, 0f, 0.6f);
        batch.draw(px, x0, y0, pw, ph);
        batch.setColor(Color.WHITE);

        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);

        float x = x0 + 26f;
        float y = y0 + ph - 28f;

        font.setColor(Color.GOLD);
        font.draw(batch, "¡Nivel " + level + " completado!", x, y);
        y -= 32f;

        font.setColor(Color.WHITE);
        font.draw(batch, "Tiempo: " + timeStr + "   Pasos: " + moves + "   Empujes: " + pushes, x, y);
        y -= 40f;

        font.setColor(Color.valueOf("FFE27A"));
        font.draw(batch, "[ENTER] Siguiente nivel     [ESC] Volver al selector", x, y);
        font.setColor(Color.WHITE);
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
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
        if (px != null) {
            px.dispose();
        }
        if (gradientBg != null) {
            gradientBg.dispose();
        }
    }
}
