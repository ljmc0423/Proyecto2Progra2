package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.Gdx.app;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import GameLogic.SokobanGame;


public class MenuScreen implements Screen {

    private final Game game;
    private final Color BACKGROUND = new Color(34 / 255f, 32 / 255f, 52 / 255f, 1f);

    private Stage stage;

    private Texture titleTex;
    private Image titleImg;

    private Texture playTex;
    private Texture exitTex;
    private Image playImg;
    private Image exitImg;

    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        titleTex = new Texture("ui/title.png");
        playTex = new Texture("ui/play.png");
        exitTex = new Texture("ui/exit.png");

        titleImg = new Image(titleTex);
        playImg = new Image(playTex);
        exitImg = new Image(exitTex);

        playImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new GameScreen(new SokobanGame()));
            }
        });

        exitImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                app.exit();
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(32f);
        stage.addActor(root);

        root.add(titleImg).center().padBottom(114f).row();
        root.add(playImg).center().pad(38f).row();
        root.add(exitImg).center().pad(38f).row();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BACKGROUND);
        stage.act(Math.min(delta, 1 / 30f));
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
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        titleTex.dispose();
        playTex.dispose();
        exitTex.dispose();

    }
}
