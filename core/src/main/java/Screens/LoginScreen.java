package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.badlogic.gdx.Gdx.app;

public class LoginScreen extends BaseScreen {

    private final Game game;

    private Texture titleTex, loginTex, createPlayerTex, exitTex;
    private Image titleImg, loginImg, createPlayerImg, exitImg;

    private Skin skin;

    public LoginScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        com.elkinedwin.LogicaUsuario.ManejoArchivos.iniciarAlmacenamiento();

        titleTex = new Texture("ui/logintitle.png");
        loginTex = new Texture("ui/login.png");
        createPlayerTex = new Texture("ui/createplayer.png");
        exitTex = new Texture("ui/exit.png");

        titleImg = new Image(titleTex);
        loginImg = new Image(loginTex);
        createPlayerImg = new Image(createPlayerTex);
        exitImg = new Image(exitTex);

        skin = buildMinimalSkin();

        loginImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                loginDialog();
            }
        });

        createPlayerImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                createPlayerDialog();
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
        if (titleTex != null) titleTex.dispose();
        if (loginTex != null) loginTex.dispose();
        if (createPlayerTex != null) createPlayerTex.dispose();
        if (exitTex != null) exitTex.dispose();
        if (skin != null) skin.dispose();
    }

    private void loginDialog() {
        final Dialog dialog = new Dialog("Iniciar sesión", skin);
        Table content = dialog.getContentTable();
        content.pad(16f);
        content.defaults().pad(6f).fillX();

        final TextField user = new TextField("", skin);
        user.setMessageText("Usuario");
        user.setTextFieldFilter(onlyAlnumFilter());

        final TextField password = new TextField("", skin);
        password.setMessageText("Contraseña");
        password.setPasswordMode(true);
        password.setPasswordCharacter('*');
        password.setTextFieldFilter(onlyAlnumFilter());

        final Label error = new Label("", skin);
        error.setColor(Color.SALMON);

        content.add(new Label("Usuario", skin)).left().row();
        content.add(user).width(340f).row();
        content.add(new Label("Contraseña", skin)).left().row();
        content.add(password).width(340f).row();
        content.add(error).left().row();

        TextButton cancelBtn = new TextButton("Cancelar", skin);
        TextButton okBtn     = new TextButton("Entrar", skin);

        cancelBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        okBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                String u = user.getText().trim();
                String p = password.getText().trim();

                if (!isAlnum(u) || !isAlnum(p)) {
                    error.setText("Solo letras o números.");
                    return;
                }

                try {
                    String path = com.elkinedwin.LogicaUsuario.ManejoArchivos.buscarArchivoUsuario(u);
                    if (path == null) {
                        error.setText("Usuario no existe.");
                        return;
                    }

                    com.elkinedwin.LogicaUsuario.ManejoArchivos.abrirArchivo(path);
                    com.elkinedwin.LogicaUsuario.LeerArchivo.usarArchivo(
                            com.elkinedwin.LogicaUsuario.ManejoArchivos.archivoAbierto
                    );

                    long ahora = System.currentTimeMillis();
                    com.elkinedwin.LogicaUsuario.ManejoUsuarios.UsuarioActivo =
                            new com.elkinedwin.LogicaUsuario.Usuario(u, "", "", ahora);

                    com.elkinedwin.LogicaUsuario.LeerArchivo.cargarUsuario();

                    String passArchivo = com.elkinedwin.LogicaUsuario.ManejoUsuarios.UsuarioActivo.getContrasena();
                    if (passArchivo == null || !passArchivo.equals(p)) {
                        com.elkinedwin.LogicaUsuario.ManejoUsuarios.UsuarioActivo = null;
                        error.setText("Contraseña incorrecta.");
                        return;
                    }

                    Long anterior = com.elkinedwin.LogicaUsuario.ManejoUsuarios.UsuarioActivo.getUltimaSesion();
                    if (anterior == null) anterior = 0L;
                    com.elkinedwin.LogicaUsuario.ManejoUsuarios.UsuarioActivo.sesionAnterior = anterior;
                    com.elkinedwin.LogicaUsuario.ManejoUsuarios.UsuarioActivo.sesionActual = System.currentTimeMillis();

                    com.elkinedwin.LogicaUsuario.ArchivoGuardar.usarArchivo(
                            com.elkinedwin.LogicaUsuario.ManejoArchivos.archivoAbierto
                    );
                    com.elkinedwin.LogicaUsuario.ArchivoGuardar.guardarFechas();

                    dialog.hide();
                    game.setScreen(new MenuScreen(game));

                } catch (Exception ex) {
                    error.setText("Fallo.");
                }
            }
        });

        dialog.getButtonTable().pad(0, 16f, 16f, 16f).defaults().width(120f).pad(6f);
        dialog.getButtonTable().add(cancelBtn);
        dialog.getButtonTable().add(okBtn);

        dialog.show(stage);
        stage.setKeyboardFocus(user);
    }

    private void createPlayerDialog() {
        final Dialog dialog = new Dialog("Crear jugador", skin);
        Table content = dialog.getContentTable();
        content.pad(16f);
        content.defaults().pad(6f).fillX();

        final TextField user = new TextField("", skin);
        user.setMessageText("Usuario");
        user.setTextFieldFilter(onlyAlnumFilter());

        final TextField name = new TextField("", skin);
        name.setMessageText("Nombre");
        name.setTextFieldFilter(onlyAlnumFilter());

        final TextField password = new TextField("", skin);
        password.setMessageText("Contraseña");
        password.setPasswordMode(true);
        password.setPasswordCharacter('*');
        password.setTextFieldFilter(onlyAlnumFilter());

        final Label error = new Label("", skin);
        error.setColor(Color.SALMON);

        content.add(new Label("Usuario", skin)).left().row();
        content.add(user).width(340f).row();
        content.add(new Label("Nombre", skin)).left().row();
        content.add(name).width(340f).row();
        content.add(new Label("Contraseña", skin)).left().row();
        content.add(password).width(340f).row();
        content.add(error).left().row();

        TextButton cancelBtn = new TextButton("Cancelar", skin);
        TextButton createBtn = new TextButton("Crear", skin);

        cancelBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        createBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                String u = user.getText().trim();
                String n = name.getText().trim();
                String p = password.getText().trim();

                if (!isAlnum(u) || !isAlnum(n) || !isAlnum(p)) {
                    error.setText("Solo letras o números (sin espacios ni símbolos).");
                    return;
                }

                try {
                    String existe = com.elkinedwin.LogicaUsuario.ManejoArchivos.buscarArchivoUsuario(u);
                    if (existe != null) {
                        error.setText("El usuario ya existe.");
                        return;
                    }

                    com.elkinedwin.LogicaUsuario.ManejoArchivos.crearUsuario(n, u, p);

                    dialog.hide();
                    new Dialog("Listo", skin)
                            .text("Usuario creado correctamente.")
                            .button("OK", true)
                            .show(stage);

                } catch (Exception ex) {
                    error.setText("Fallo.");
                }
            }
        });

        dialog.getButtonTable().pad(0, 16f, 16f, 16f).defaults().width(120f).pad(6f);
        dialog.getButtonTable().add(cancelBtn);
        dialog.getButtonTable().add(createBtn);

        dialog.show(stage);
        stage.setKeyboardFocus(user);
    }

    private Skin buildMinimalSkin() {
        Skin skin = new Skin();

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.25f);
        skin.add("defaultfont", font, BitmapFont.class);

        Pixmap pixMap = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        pixMap.setColor(Color.WHITE);
        pixMap.fill();
        Texture white = new Texture(pixMap);
        pixMap.dispose();
        skin.add("white", white);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
        tfs.font = font;
        tfs.fontColor = Color.WHITE;
        tfs.cursor = skin.newDrawable("white", Color.WHITE);
        tfs.selection = skin.newDrawable("white", new Color(1, 1, 1, 0.25f));
        tfs.background = skin.newDrawable("white", new Color(0f, 0f, 0f, 1f));
        skin.add("default", tfs);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = skin.newDrawable("white", new Color(1f, 1f, 1f, 0.85f));
        buttonStyle.over = skin.newDrawable("white", new Color(1f, 1f, 1f, 0.95f));
        buttonStyle.down = skin.newDrawable("white", new Color(0.90f, 0.90f, 0.90f, 1f));
        buttonStyle.fontColor = Color.BLACK;
        skin.add("default", buttonStyle);

        Window.WindowStyle ws = new Window.WindowStyle();
        ws.titleFont = font;
        ws.titleFontColor = Color.WHITE;
        ws.background = skin.newDrawable("white", new Color(0f, 0f, 0f, 1f));
        skin.add("default", ws);

        return skin;
    }

    private TextField.TextFieldFilter onlyAlnumFilter() {
        return (textField, c) -> Character.isLetterOrDigit(c);
    }

    private boolean isAlnum(String s) {
        return s != null && s.matches("[A-Za-z0-9]+");
    }
}
