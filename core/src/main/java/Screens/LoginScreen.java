package Screens;

import com.badlogic.gdx.Game;
import static com.badlogic.gdx.Gdx.app;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import GameLogic.SokobanGame;

public class LoginScreen extends BaseScreen {

    private final Game game;

    private Texture titleTex, loginTex, createPlayerTex, exitTex;
    private Image titleImg, loginImg, createPlayerImg, exitImg;

    public LoginScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        // cargar assets
        titleTex = new Texture("ui/logintitle.png");
        loginTex = new Texture("ui/login.png");
        createPlayerTex = new Texture("ui/createplayer.png");
        exitTex = new Texture("ui/exit.png");

        //añadirlos a las imagenes de los botones
        titleImg = new Image(titleTex);
        loginImg = new Image(loginTex);
        createPlayerImg = new Image(createPlayerTex);
        exitImg = new Image(exitTex);

        loginImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        createPlayerImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
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
        stage.addActor(root);

        root.top().padTop(40f);
        root.add(titleImg).center().padBottom(70f).row();

        root.defaults().padTop(14f).padBottom(14f);
        root.add(loginImg).center().row();
        root.add(createPlayerImg).center().row();
        root.add(exitImg).center().padTop(28f).row();
    }

    @Override
    public void dispose() {
        super.dispose();
        titleTex.dispose();
        loginTex.dispose();
        createPlayerTex.dispose();
        exitTex.dispose();
    }
}
