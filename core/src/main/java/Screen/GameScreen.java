package Screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {

    public static final int MAX_COLS = 16;
    public static final int MAX_ROWS = 12;
    //lo de arriba son la cantidad de casillas que mide el mundo

    private OrthographicCamera camera;
    private FitViewport viewport;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(MAX_COLS, MAX_ROWS, camera);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        camera.update();
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
    }
}
