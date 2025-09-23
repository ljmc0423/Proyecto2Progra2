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
        
        final Texture bgTexture = new Texture("ui/menuscreen.png");
        Image bgImage = new Image(bgTexture);
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        generator = new FreeTypeFontGenerator(files.internal("fonts/pokemon_fire_red.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter pt = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pt.size = 136;
        pt.color = Color.BLACK;
        BitmapFont rawTitleFont = generator.generateFont(pt);

        FreeTypeFontGenerator.FreeTypeFontParameter pb = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pb.size = 52;
        pb.color = Color.BLACK;
        BitmapFont rawButtonFont = generator.generateFont(pb);

        titleFont = new BitmapFont(rawTitleFont.getData(), rawTitleFont.getRegions(), false);
        titleFont.getData().setScale(0.6f);

        buttonFont = new BitmapFont(rawButtonFont.getData(), rawButtonFont.getRegions(), false);
        buttonFont.getData().setScale(0.6f);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = buttonFont;
        btnStyle.fontColor = buttonFont.getColor();

        lblTitle = new Label("SOKOBAN", titleStyle);
        btnPlay = new TextButton("Jugar", btnStyle);
        btnLevels = new TextButton("Tutorial", btnStyle);
        btnConfig = new TextButton("Opciones", btnStyle);
        btnHistorial = new TextButton("Historial", btnStyle);
        btnUniverso = new TextButton("Leaderboard", btnStyle);
        btnExit = new TextButton("Cerrar Sesion", btnStyle);

        // Subtitle below title
        BitmapFont subtitleFont = new BitmapFont(buttonFont.getData(), buttonFont.getRegions(), false);
        subtitleFont.getData().setScale(0.9f);
        Label.LabelStyle subtitleStyle = new Label.LabelStyle(subtitleFont, Color.BLACK);
        Label lblSubtitle = new Label("Edicion Machoke", subtitleStyle);

        // Button listeners
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                boolean tutoHecho = false;
                try {
                    if (ManejoUsuarios.UsuarioActivo != null) {
                        tutoHecho = ManejoUsuarios.UsuarioActivo.getTutocomplete();
                    }
                } catch (Exception ignored) {}
                
                game.setScreen(new StageScreen(game));
                
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
        btnHistorial.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new HistorialScreen(game));
            }
        });
        btnUniverso.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) { }
        });
        btnExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                try { ArchivoGuardar.guardarTodoCerrarSesion(); } catch (IOException ignored) {}
                finally {
                    ManejoUsuarios.UsuarioActivo = null;
                    game.setScreen(new LoginScreen(game));
                }
            }
        });

        // Root table
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Use centered alignment
        root.top().padTop(40f);

        // Title & subtitle
        root.add(lblTitle).center().row();
        root.add(lblSubtitle).center().padBottom(60f).row(); // space below subtitle

        // Buttons
        root.defaults().padTop(24f).padBottom(24f).center();
        root.add(btnPlay).row();
        root.add(btnLevels).row();
        root.add(btnConfig).row();
        root.add(btnHistorial).row();
        root.add(btnUniverso).row();
        root.add(btnExit).padTop(36f).row();


        // Volume
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

        Label.LabelStyle userStyle = new Label.LabelStyle(buttonFont, Color.BLACK);
        userLabel = new Label(username, userStyle);
        userLabel.setFontScale(0.6f);

        avatarTex = new Texture(avatarPath);
        avatarImg = new Image(avatarTex);
        avatarImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
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