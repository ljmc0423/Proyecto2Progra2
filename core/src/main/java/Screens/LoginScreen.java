package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.app;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.elkinedwin.LogicaUsuario.ArchivoGuardar;
import com.elkinedwin.LogicaUsuario.LeerArchivo;
import com.elkinedwin.LogicaUsuario.ManejoArchivos;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;
import static com.elkinedwin.LogicaUsuario.ManejoUsuarios.UsuarioActivo;

public class LoginScreen extends BaseScreen {

    private final Game game;
    private Skin skin;
    private Label lblTitle;
    private TextButton btnLogin, btnCrear, btnSalir;

    public LoginScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        ManejoArchivos.iniciarCpadre();
        skin = buildSkin("fonts/pokemon_fire_red.ttf");

        lblTitle = new Label("INICIO DE SESION", skin, "lblTitle");
        btnLogin = new TextButton("Iniciar sesion", skin);
        btnCrear = new TextButton("Crear jugador", skin);
        btnSalir = new TextButton("Salir", skin);

        btnLogin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                showLogin();
            }
        });
        btnCrear.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                showCreate();
            }
        });
        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                app.exit();
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        root.top().padTop(40f);
        root.add(lblTitle).padBottom(70f).row();
        root.defaults().pad(8f);
        root.add(btnLogin).row();
        root.add(btnCrear).row();
        root.add(btnSalir).padTop(28f).row();
    }

    private void showLogin() {
        final Dialog loginDialog = new Dialog("", skin) {
            private TextField usernameField;
            private TextField passwordField;

            @Override
            protected void result(Object object) {
                if (!(object instanceof Boolean) || !((Boolean) object)) {
                    return;
                }

                String username = usernameField.getText().trim();
                String password = passwordField.getText().trim();

                try {
                    String userPath = com.elkinedwin.LogicaUsuario.ManejoArchivos.buscarUsuario(username);
                    if (userPath == null) {
                        new Dialog("", skin).text("Usuario no existe.").button("OK", true).show(getStage());
                    }

                    ManejoArchivos.setArchivo(username);
                    long now = System.currentTimeMillis();
                    ManejoUsuarios.UsuarioActivo = new com.elkinedwin.LogicaUsuario.Usuario(username, "", "", now);

                    LeerArchivo.cargarUsuario();

                    String savedPassword = UsuarioActivo.getContrasena();
                    if (savedPassword == null || !savedPassword.equals(password)) {
                        UsuarioActivo = null;
                        new Dialog("", skin).text("Contraseña incorrecta.").button("OK", true).show(getStage());
                    }

                    Long previous = UsuarioActivo.getUltimaSesion();
                    if (previous == null) {
                        previous = 0L;
                    }

                    UsuarioActivo.sesionAnterior = previous;
                    UsuarioActivo.sesionActual = now;
                    ArchivoGuardar.guardarFechas();

                    hide();
                    game.setScreen(new MenuScreen(game));
                } catch (Exception ex) {
                    new Dialog("", skin).text("Error: verifique que haya ingresado los datos correctamente").button("OK", true).show(getStage());
                }
            }

            @Override
            public Dialog show(Stage stage) {
                Table content = getContentTable();
                content.pad(16f);
                content.defaults().width(340f).pad(6f).fillX();

                usernameField = new TextField("", skin, "tfSmall");
                usernameField.setMessageText("Usuario");

                passwordField = new TextField("", skin, "tfSmall");
                passwordField.setMessageText("Contrasena");
                passwordField.setPasswordMode(true);
                passwordField.setPasswordCharacter('*');

                content.add(usernameField).row();
                content.add(passwordField).row();

                getButtonTable().defaults().space(40f);
                button("Cancelar", false);
                button("Entrar", true);

                Dialog shown = super.show(stage);
                stage.setKeyboardFocus(usernameField);
                return shown;
            }

        };

        loginDialog.show(stage);
    }

    private void showCreate() {
        final Dialog createDialog = new Dialog("", skin) {
            private TextField usernameField;
            private TextField nameField;
            private TextField passwordField;

            @Override
            protected void result(Object object) {
                if (!(object instanceof Boolean) || !((Boolean) object)) {
                    return;
                }

                String username = usernameField.getText().trim();
                String name = nameField.getText().trim();
                String password = passwordField.getText().trim();

                try {
                    String already = ManejoArchivos.buscarUsuario(username);
                    if (already != null) {
                        new Dialog("", skin).text("Usuario ya existe.").button("OK", true).show(getStage());
                    }

                    ManejoArchivos.crearUsuario(name, username, password);

                    hide();
                    new Dialog("", skin).text("Usuario creado correctamente.").button("OK", true).show(getStage());
                } catch (Exception ex) {
                    new Dialog("", skin).text("Error al crear el usuario: verifique que haya ingresado los datos correctamente.").button("OK", true).show(getStage());
                }
            }

            @Override
            public Dialog show(Stage stage) {
                Table content = getContentTable();
                content.pad(16f);
                content.defaults().width(340f).pad(6f).fillX();

                usernameField = new TextField("", skin, "tfSmall");
                usernameField.setMessageText("Usuario");

                nameField = new TextField("", skin, "tfSmall");
                nameField.setMessageText("Nombre");

                passwordField = new TextField("", skin, "tfSmall");
                passwordField.setMessageText("Contrasena");
                passwordField.setPasswordMode(true);
                passwordField.setPasswordCharacter('*');

                content.add(usernameField).row();
                content.add(nameField).row();
                content.add(passwordField).row();

                getButtonTable().defaults().space(40f);
                button("Cancelar", false);
                button("Crear", true);

                Dialog shown = super.show(stage);
                stage.setKeyboardFocus(usernameField);
                return shown;
            }

        };

        createDialog.show(stage);
    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
    }

    private static Skin buildSkin(String ttfPath) {
        Skin skin = new Skin();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(files.internal(ttfPath));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();

        p.size = 66;
        p.color = Color.valueOf("E6DFC9");
        BitmapFont ui = generator.generateFont(p);

        p.size = 38;
        p.color = Color.valueOf("E6DFC9");
        BitmapFont uiSmall = generator.generateFont(p);

        p.size = 136;
        p.color = Color.valueOf("E6DFC9");
        BitmapFont lblTitle = generator.generateFont(p);
        generator.dispose();

        skin.add("ui", ui, BitmapFont.class);
        skin.add("uiSmall", uiSmall, BitmapFont.class);
        skin.add("lblTitlefont", lblTitle, BitmapFont.class);

        Pixmap pm = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        Texture white = new Texture(pm);
        pm.dispose();
        TextureRegionDrawable whiteDraw = new TextureRegionDrawable(new TextureRegion(white));
        skin.add("white", white);

        // Label
        Label.LabelStyle ls = new Label.LabelStyle();
        ls.font = uiSmall;
        ls.fontColor = ui.getColor();
        skin.add("default", ls);
        Label.LabelStyle lst = new Label.LabelStyle();
        lst.font = lblTitle;
        lst.fontColor = lblTitle.getColor();
        skin.add("lblTitle", lst);
        Label.LabelStyle lse = new Label.LabelStyle();
        lse.font = uiSmall;
        lse.fontColor = Color.SALMON;
        skin.add("error", lse);

        // TextButton
        TextButton.TextButtonStyle bs = new TextButton.TextButtonStyle();
        bs.font = ui;
        bs.fontColor = ui.getColor();
        skin.add("default", bs);

        //textfields
        TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
        tfs.font = ui;
        tfs.fontColor = Color.WHITE;
        tfs.cursor = whiteDraw.tint(Color.WHITE);
        tfs.selection = whiteDraw.tint(new Color(1, 1, 1, 0.25f));
        tfs.background = whiteDraw.tint(new Color(1f, 1f, 1f, 0.15f));
        skin.add("default", tfs);

        TextField.TextFieldStyle tfsSmall = new TextField.TextFieldStyle(tfs);
        tfsSmall.font = uiSmall;
        skin.add("tfSmall", tfsSmall);

        // Window/Dialog
        Window.WindowStyle ws = new Window.WindowStyle();
        ws.titleFont = uiSmall;
        ws.titleFontColor = Color.WHITE;
        ws.background = whiteDraw.tint(new Color(0, 0, 0, 1));
        skin.add("default", ws);

        return skin;
    }

}
