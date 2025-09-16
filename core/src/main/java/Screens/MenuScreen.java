package Screens;

import com.badlogic.gdx.Game;
import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

// Guardado / usuarios
import com.elkinedwin.LogicaUsuario.ArchivoGuardar;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;

public class MenuScreen extends BaseScreen {

    private final Game game;

    private Texture titleTex, playTex, levelTex, configTex, sokobanUniverseTex, exitTex;
    private Image titleImg, playImg, levelImg, configImg, sokobanUniverseImg, exitImg;
    
    private Music bgMusic;

    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        titleTex = new Texture("ui/title.png");
        playTex = new Texture("ui/play.png");
        levelTex = new Texture("ui/levels.png");
        configTex = new Texture("ui/config.png");
        sokobanUniverseTex = new Texture("ui/sokobanuniverse.png");
        exitTex = new Texture("ui/logout.png");

        titleImg = new Image(titleTex);
        playImg = new Image(playTex);
        levelImg = new Image(levelTex);
        configImg = new Image(configTex);
        sokobanUniverseImg = new Image(sokobanUniverseTex);
        exitImg = new Image(exitTex);
        
        bgMusic = audio.newMusic(files.internal("audios/menu_bg_song.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.3f);
        bgMusic.play();

        playImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                bgMusic.stop();
                game.setScreen(new GameScreen(game, 0));
            }
        });

        levelImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                bgMusic.stop();
                game.setScreen(new GameScreen(game, 0));
            }
        });

        configImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new ConfigScreen(game));
            }
        });

        sokobanUniverseImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                // futuro: otra pantalla
            }
        });

        exitImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                try {
                    // Guarda en datos.bin, progreso.bin, config.bin y partidas.bin
                    ArchivoGuardar.guardarTodoCerrarSesion();
                } catch (Exception ex) {
                    // opcional: logging
                } finally {
                    ManejoUsuarios.UsuarioActivo = null;
                    game.setScreen(new LoginScreen(game));
                }
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.top().padTop(50f);
        root.add(titleImg).center().padBottom(60f).row();

        root.defaults().padTop(12f).padBottom(12f);
        root.add(playImg).center().row();
        root.add(levelImg).center().row();
        root.add(configImg).center().row();
        root.add(sokobanUniverseImg).center().row();
        root.add(exitImg).center().padTop(24f).row();
    }

    @Override
    public void dispose() {
        super.dispose();
        titleTex.dispose();
        playTex.dispose();
        levelTex.dispose();
        configTex.dispose();
        sokobanUniverseTex.dispose();
        exitTex.dispose();
        bgMusic.dispose();
    }
}

