package Screens;

import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.elkinedwin.LogicaUsuario.ArchivoGuardar;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;
import java.io.IOException;

public class MenuScreen extends BaseScreen {

    private final Game game;

    private Label lblTitle;
    private TextButton btnPlay, btnLevels, btnConfig, btnUniverso, btnExit;

    private BitmapFont titleFont, buttonFont;
    private FreeTypeFontGenerator generator;

    private Music bgMusic;

    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        // Fuentes
        generator = new FreeTypeFontGenerator(files.internal("fonts/pokemon_fire_red.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter pt = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pt.size = 136;
        pt.color = Color.valueOf("E6DFC9");
        titleFont = generator.generateFont(pt);

        FreeTypeFontGenerator.FreeTypeFontParameter pb = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pb.size = 72;
        pb.color = Color.valueOf("E6DFC9");
        buttonFont = generator.generateFont(pb);

        // Styles
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = buttonFont;
        btnStyle.fontColor = buttonFont.getColor();

        lblTitle = new Label("SOKOBAN", titleStyle);
        btnPlay = new TextButton("Jugar", btnStyle);
        btnLevels = new TextButton("Niveles", btnStyle);
        btnConfig = new TextButton("Configuraciones", btnStyle);
        btnUniverso = new TextButton("Universo Sokoban", btnStyle);
        btnExit = new TextButton("Cerrar Sesion", btnStyle);

        // Listeners
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new GameScreen(game, 3));
            }
        });
        btnLevels.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new TutorialScreen(game));
            }
        });
        btnConfig.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new ConfigScreen(game));
            }
        });
        btnUniverso.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
            }
        });
        btnExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                try {
                    ArchivoGuardar.guardarTodoCerrarSesion();
                } catch (IOException ioe) {
                    System.out.println("Error al guardar y cerrar sesión: " + ioe.getMessage());
                } finally {
                    ManejoUsuarios.UsuarioActivo = null;
                    game.setScreen(new LoginScreen(game));
                }
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.top().padTop(40f);
        root.add(lblTitle).center().padBottom(60f).row();

        root.defaults().padTop(18f).padBottom(18f).center();
        root.add(btnPlay).row();
        root.add(btnLevels).row();
        root.add(btnConfig).row();
        root.add(btnUniverso).row();
        root.add(btnExit).padTop(36f).row();

        bgMusic = audio.newMusic(files.internal("audios/menu_bg_song.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.5f);
        bgMusic.play();
    }

    @Override
    public void hide() {
        if (bgMusic != null) {
            bgMusic.stop();
            bgMusic.dispose();
            bgMusic = null;
        }
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
        generator.dispose();
        titleFont.dispose();
        buttonFont.dispose();
    }
}
