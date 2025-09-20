package Screens;

import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.elkinedwin.LogicaUsuario.ArchivoGuardar;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;
import com.elkinedwin.LogicaUsuario.Usuario;

import java.io.IOException;

public class MenuScreen extends BaseScreen {

    private final Game game;

    private Label lblTitle;
    private TextButton btnPlay, btnLevels, btnConfig, btnUniverso, btnExit;

    private BitmapFont titleFont, buttonFont;
    private FreeTypeFontGenerator generator;

    private Music bgMusic;

    // UI Usuario (arriba derecha)
    private Label userLabel;
    private Texture avatarTex;
    private Image avatarImg;

    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        
        BitmapFont fallbackTitle = null, fallbackButton = null;
        if (files.internal("fonts/pokemon_fire_red.ttf").exists()) {
            generator = new FreeTypeFontGenerator(files.internal("fonts/pokemon_fire_red.ttf"));

            FreeTypeFontGenerator.FreeTypeFontParameter pt = new FreeTypeFontGenerator.FreeTypeFontParameter();
            pt.size = 136;
            pt.color = Color.valueOf("E6DFC9");
            titleFont = generator.generateFont(pt);

            FreeTypeFontGenerator.FreeTypeFontParameter pb = new FreeTypeFontGenerator.FreeTypeFontParameter();
            pb.size = 72;
            pb.color = Color.valueOf("E6DFC9");
            buttonFont = generator.generateFont(pb);
        } else {
            fallbackTitle = new BitmapFont();
            fallbackButton = new BitmapFont();
            fallbackTitle.setColor(Color.valueOf("E6DFC9"));
            fallbackButton.setColor(Color.valueOf("E6DFC9"));
            fallbackTitle.getData().setScale(2.5f);
            fallbackButton.getData().setScale(1.5f);
            titleFont = fallbackTitle;
            buttonFont = fallbackButton;
            generator = null; 
        }

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = buttonFont;
        btnStyle.fontColor = buttonFont.getColor();

        // ====== Controles principales ======
        lblTitle   = new Label("SOKOBAN", titleStyle);
        btnPlay    = new TextButton("Jugar", btnStyle);

        btnLevels  = new TextButton("Tutorial", btnStyle);
        btnConfig  = new TextButton("Configuraciones", btnStyle);
        btnUniverso= new TextButton("Universo Sokoban", btnStyle);
        btnExit    = new TextButton("Cerrar Sesion", btnStyle);

       
        btnPlay.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new GameScreen(game, 3));
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
        btnUniverso.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                
            }
        });
        btnExit.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
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

        // ====== Layout principal ======
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

      
        if (files.internal("audios/menu_bg_song.mp3").exists()) {
            bgMusic = audio.newMusic(files.internal("audios/menu_bg_song.mp3"));
            bgMusic.setLooping(true);
            bgMusic.setVolume(0.5f);
            bgMusic.play();
        } else {
            bgMusic = null;
        }

        
        Usuario u = ManejoUsuarios.UsuarioActivo;
        String username = (u != null && u.getUsuario() != null && !u.getUsuario().trim().isEmpty())
                ? u.getUsuario()
                : "Invitado";

        Label.LabelStyle userStyle = new Label.LabelStyle(buttonFont, Color.WHITE);
        userLabel = new Label(username, userStyle);
        userLabel.setFontScale(1.1f);

       
        String avatarPath = "ui/default_avatar.png";
        if (u != null && u.avatar != null && !u.avatar.trim().isEmpty() && files.internal(u.avatar).exists()) {
            avatarPath = u.avatar;
        }
        if (files.internal(avatarPath).exists()) {
            avatarTex = new Texture(avatarPath);
            avatarImg = new Image(avatarTex);
            avatarImg.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new MiPerfilScreen(game));
                }
            });

            Table topRight = new Table();
            topRight.setFillParent(true);
            topRight.top().right().padTop(14f).padRight(16f);
            topRight.add(userLabel).padRight(10f).center();
            topRight.add(avatarImg).size(112f, 112f).center();
            stage.addActor(topRight);
        } else {
           
            Table topRight = new Table();
            topRight.setFillParent(true);
            topRight.top().right().padTop(14f).padRight(16f);
            topRight.add(userLabel).center();
            stage.addActor(topRight);
            avatarTex = null;
            avatarImg = null;
        }
    }

    @Override
    public void hide() {
      
        if (bgMusic != null) {
            bgMusic.stop();
            bgMusic.dispose();
            bgMusic = null;
        }
        if (avatarTex != null) {
            avatarTex.dispose();
            avatarTex = null;
        }
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
       
        if (generator != null) generator.dispose();
        if (titleFont != null)  titleFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
        
        if (bgMusic != null) {
            bgMusic.stop();
            bgMusic.dispose();
            bgMusic = null;
        }
       
        if (avatarTex != null) {
            avatarTex.dispose();
            avatarTex = null;
        }
    }
}
