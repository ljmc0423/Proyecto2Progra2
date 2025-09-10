package Screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class BaseScreen implements Screen {

    protected final Color BACKGROUND = new Color(34 / 255f, 32 / 255f, 52 / 255f, 1f);
    protected Stage stage;

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        onShow();
    }

    protected abstract void onShow();

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BACKGROUND);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int w, int h) {
        stage.getViewport().update(w, h, true);
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
        stage.dispose();
    }
}
