package Screens;

import static com.badlogic.gdx.Gdx.files;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.elkinedwin.LogicaUsuario.ArchivoGuardar;
import com.elkinedwin.LogicaUsuario.AudioBus;
import com.elkinedwin.LogicaUsuario.AudioX;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;
import com.elkinedwin.LogicaUsuario.Usuario;

import java.io.IOException;

public class MenuScreen extends BaseScreen {

    private final Game game;

    private Label lblTitle;
    private TextButton btnPlay, btnLevels, btnConfig, btnUniverso, btnExit;
    // NUEVO
    private TextButton btnHistorial;

    private BitmapFont titleFont, buttonFont;
    private FreeTypeFontGenerator generator;

    private Music bgMusic;

    private Label userLabel;
    private Texture avatarTex;
    private Image avatarImg;

    public MenuScreen(Game game) { this.game = game; }

    @Override
    protected void onShow() {
        generator = new FreeTypeFontGenerator(files.internal("fonts/pokemon_fire_red.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter pt = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pt.size = 136;
        pt.color = Color.valueOf("E6DFC9");
        titleFont = generator.generateFont(pt);

        FreeTypeFontGenerator.FreeTypeFontParameter pb = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pb.size = 72;
        pb.color = Color.valueOf("E6DFC9");
        buttonFont = generator.generateFont(pb);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = buttonFont;
        btnStyle.fontColor = buttonFont.getColor();

        lblTitle = new Label("SOKOBAN", titleStyle);
        btnPlay = new TextButton("Jugar", btnStyle);
        btnLevels = new TextButton("Tutorial", btnStyle);
        btnConfig = new TextButton("Configuraciones", btnStyle);
        // NUEVO botón de historial
        btnHistorial = new TextButton("Historial de Partidas", btnStyle);
        btnUniverso = new TextButton("Universo Sokoban", btnStyle);
        btnExit = new TextButton("Cerrar Sesion", btnStyle);

        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                boolean tutoHecho = false;
                try {
                    if (ManejoUsuarios.UsuarioActivo != null) {
                        tutoHecho = ManejoUsuarios.UsuarioActivo.getTutocomplete();
                    }
                } catch (Exception ignored) {}
                if (!tutoHecho) {
                    game.setScreen(new TutorialScreen(game));
                } else {
                    // Lógica “avanzada”: ir al hub/selector de niveles
                    game.setScreen(new StageScreen(game));
                    // Si prefieres arrancar en el nivel 1, usa:
                    // game.setScreen(new GameScreen(game, 1));
                }
            }
        });

        btnLevels.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new TutorialScreen(game));
            }
        });
        btnConfig.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new ConfigScreen(game));
            }
        });
        // NUEVO listener
        btnHistorial.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new HistorialScreen(game));
            }
        });
        btnUniverso.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { }
        });
        btnExit.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                try { ArchivoGuardar.guardarTodoCerrarSesion(); } catch (IOException ignored) {}
                finally {
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
        // NUEVO botón en el menú
        root.add(btnHistorial).row();
        root.add(btnUniverso).row();
        root.add(btnExit).padTop(36f).row();

        int volCfg = 70;
        try {
            if (ManejoUsuarios.UsuarioActivo != null && ManejoUsuarios.UsuarioActivo.configuracion != null) {
                Integer v = ManejoUsuarios.UsuarioActivo.configuracion.get("Volumen");
                if (v != null) volCfg = v;
            }
        } catch (Exception ignored) {}
        AudioBus.setMasterVolume(volCfg / 100f);

        bgMusic = AudioX.newMusic("audios/menu_bg_song.mp3");
        bgMusic.setLooping(true);
        bgMusic.play();

        Usuario u = ManejoUsuarios.UsuarioActivo;
        String username = (u != null && u.getUsuario() != null) ? u.getUsuario() : "Invitado";

        String avatarPath = "ui/default_avatar.png";
        if (u != null && u.avatar != null && !u.avatar.trim().isEmpty() && files.internal(u.avatar).exists()) {
            avatarPath = u.avatar;
        }

        Label.LabelStyle userStyle = new Label.LabelStyle(buttonFont, Color.WHITE);
        userLabel = new Label(username, userStyle);
        userLabel.setFontScale(1.1f);

        avatarTex = new Texture(avatarPath);
        avatarImg = new Image(avatarTex);
        avatarImg.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new MiPerfilScreen(game));
            }
        });

        Table topRight = new Table();
        topRight.setFillParent(true);
        topRight.top().right().padTop(14f).padRight(16f);
        topRight.add(userLabel).padRight(10f).center();
        topRight.add(avatarImg).size(112f, 112f).center();
        stage.addActor(topRight);
    }

    @Override
    public void hide() {
        if (bgMusic != null) {
            bgMusic.stop();
            com.elkinedwin.LogicaUsuario.AudioBus.unregisterMusic(bgMusic);
            bgMusic.dispose();
            bgMusic = null;
        }
        if (avatarTex != null) { avatarTex.dispose(); avatarTex = null; }
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