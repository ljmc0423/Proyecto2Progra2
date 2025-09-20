package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.Gdx.app;
import static com.badlogic.gdx.Gdx.files;

import com.elkinedwin.LogicaUsuario.ArchivoGuardar;
import com.elkinedwin.LogicaUsuario.LeerArchivo;
import com.elkinedwin.LogicaUsuario.ManejoArchivos;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;

public class LoginScreen extends BaseScreen {

    private final Game game;

    private Texture titleTex, loginTex, createPlayerTex, exitTex;
    private Image titleImg, loginImg, createPlayerImg, exitImg;

    private Skin skin;
    private Label lblTitle;
    private TextButton btnLogin, btnCrear, btnSalir;

    public LoginScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        ManejoArchivos.iniciarCpadre();

        if (files.internal("fonts/pokemon_fire_red.ttf").exists()) {
            skin = buildSkin("fonts/pokemon_fire_red.ttf");
        } else {
            skin = buildMinimalSkin();
        }

        if (files.internal("ui/logintitle.png").exists()) titleTex = new Texture("ui/logintitle.png");
        if (files.internal("ui/login.png").exists()) loginTex = new Texture("ui/login.png");
        if (files.internal("ui/createplayer.png").exists()) createPlayerTex = new Texture("ui/createplayer.png");
        if (files.internal("ui/exit.png").exists()) exitTex = new Texture("ui/exit.png");

        if (titleTex != null) titleImg = new Image(titleTex);
        if (loginTex != null) loginImg = new Image(loginTex);
        if (createPlayerTex != null) createPlayerImg = new Image(createPlayerTex);
        if (exitTex != null) exitImg = new Image(exitTex);

        lblTitle = new Label("INICIO DE SESION", skin, skin.has("lblTitle", Label.LabelStyle.class) ? "lblTitle" : "default");
        btnLogin = new TextButton("Iniciar sesion", skin);
        btnCrear = new TextButton("Crear jugador", skin);
        btnSalir = new TextButton("Salir", skin);

        ClickListener loginListener = new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { loginDialog(); }
        };
        ClickListener createListener = new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { createPlayerDialog(); }
        };
        ClickListener exitListener = new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { app.exit(); }
        };

        if (loginImg != null) loginImg.addListener(loginListener);
        if (createPlayerImg != null) createPlayerImg.addListener(createListener);
        if (exitImg != null) exitImg.addListener(exitListener);

        btnLogin.addListener(loginListener);
        btnCrear.addListener(createListener);
        btnSalir.addListener(exitListener);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.top().padTop(40f);
        if (titleImg != null) root.add(titleImg).center().padBottom(70f).row();
        else root.add(lblTitle).center().padBottom(70f).row();

        root.defaults().padTop(14f).padBottom(14f);
        if (loginImg != null) root.add(loginImg).center().row(); else root.add(btnLogin).center().row();
        if (createPlayerImg != null) root.add(createPlayerImg).center().row(); else root.add(btnCrear).center().row();
        if (exitImg != null) root.add(exitImg).center().padTop(28f).row(); else root.add(btnSalir).center().padTop(28f).row();
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
        final Dialog dialog = new Dialog("Iniciar sesion", skin);
        Table content = dialog.getContentTable();
        content.pad(16f);
        content.defaults().pad(6f).fillX();

        final TextField user = new TextField("", skin, skin.has("tfSmall", TextField.TextFieldStyle.class) ? "tfSmall" : "default");
        user.setMessageText("Usuario");
        user.setTextFieldFilter(onlyAlnumFilter());

        final TextField password = new TextField("", skin, skin.has("tfSmall", TextField.TextFieldStyle.class) ? "tfSmall" : "default");
        password.setMessageText("Contrasena");
        password.setPasswordMode(true);
        password.setPasswordCharacter('*');
        password.setTextFieldFilter(onlyAlnumFilter());

        final Label error = new Label("", skin, skin.has("error", Label.LabelStyle.class) ? "error" : "default");
        if (!skin.has("error", Label.LabelStyle.class)) error.setColor(Color.SALMON);

        content.add(new Label("Usuario", skin)).left().row();
        content.add(user).width(340f).row();
        content.add(new Label("Contrasena", skin)).left().row();
        content.add(password).width(340f).row();
        content.add(error).left().row();

        TextButton cancelBtn = new TextButton("Cancelar", skin);
        TextButton okBtn = new TextButton("Entrar", skin);

        cancelBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { dialog.hide(); }});
        okBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                String u = user.getText().trim();
                String p = password.getText().trim();

                if (!isAlnum(u) || !isAlnum(p)) {
                    error.setText("Solo letras o numeros");
                    return;
                }

                try {
                    String path = ManejoArchivos.buscarUsuario(u);
                    if (path == null) {
                        error.setText("Usuario no existe");
                        return;
                    }

                    ManejoArchivos.setArchivo(u);

                    long ahora = System.currentTimeMillis();
                    ManejoUsuarios.UsuarioActivo = new com.elkinedwin.LogicaUsuario.Usuario(u, "", "", ahora);

                    LeerArchivo.cargarUsuario();
                    ManejoUsuarios.UsuarioActivo.recalcularTiempoPromedio();

                    String passArchivo = ManejoUsuarios.UsuarioActivo.getContrasena();
                    if (passArchivo == null || !passArchivo.equals(p)) {
                        ManejoUsuarios.UsuarioActivo = null;
                        error.setText("Contrasena incorrecta");
                        return;
                    }

                    Long anterior = ManejoUsuarios.UsuarioActivo.getUltimaSesion();
                    if (anterior == null) anterior = 0L;
                    ManejoUsuarios.UsuarioActivo.sesionAnterior = anterior;
                    ManejoUsuarios.UsuarioActivo.sesionActual = ahora;

                    ArchivoGuardar.guardarFechas();

                    dialog.hide();
                    game.setScreen(new MenuScreen(game));

                } catch (Exception ex) {
                    error.setText("Fallo");
                }
            }
        });

        Table bt = dialog.getButtonTable();
        bt.pad(0,16f,16f,16f);
        bt.defaults().width(140f).height(54f).padLeft(8f).padRight(8f);
        bt.add(cancelBtn);
        bt.add(okBtn);

        dialog.show(stage);
        stage.setKeyboardFocus(user);
    }

    private void createPlayerDialog() {
        final Dialog dialog = new Dialog("Crear jugador", skin);
        Table content = dialog.getContentTable();
        content.pad(16f);
        content.defaults().pad(6f).fillX();

        final TextField user = new TextField("", skin, skin.has("tfSmall", TextField.TextFieldStyle.class) ? "tfSmall" : "default");
        user.setMessageText("Usuario");
        user.setTextFieldFilter(onlyAlnumFilter());

        final TextField name = new TextField("", skin, skin.has("tfSmall", TextField.TextFieldStyle.class) ? "tfSmall" : "default");
        name.setMessageText("Nombre");
        name.setTextFieldFilter(onlyAlnumFilter());

        final TextField password = new TextField("", skin, skin.has("tfSmall", TextField.TextFieldStyle.class) ? "tfSmall" : "default");
        password.setMessageText("Contrasena");
        password.setPasswordMode(true);
        password.setPasswordCharacter('*');
        password.setTextFieldFilter(onlyAlnumFilter());

        final Label error = new Label("", skin, skin.has("error", Label.LabelStyle.class) ? "error" : "default");
        if (!skin.has("error", Label.LabelStyle.class)) error.setColor(Color.SALMON);

        content.add(new Label("Usuario", skin)).left().row();
        content.add(user).width(340f).row();
        content.add(new Label("Nombre", skin)).left().row();
        content.add(name).width(340f).row();
        content.add(new Label("Contrasena", skin)).left().row();
        content.add(password).width(340f).row();
        content.add(error).left().row();

        TextButton cancelBtn = new TextButton("Cancelar", skin);
        TextButton createBtn = new TextButton("Crear", skin);

        cancelBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { dialog.hide(); }});
        createBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                String u = user.getText().trim();
                String n = name.getText().trim();
                String p = password.getText().trim();

                if (!isAlnum(u) || !isAlnum(n) || !isAlnum(p)) {
                    error.setText("Solo letras o numeros");
                    return;
                }

                try {
                    String existe = ManejoArchivos.buscarUsuario(u);
                    if (existe != null) {
                        error.setText("Usuario ya existe");
                        return;
                    }

                    ManejoArchivos.crearUsuario(n, u, p);

                    dialog.hide();
                    new Dialog("Listo", skin).text("Usuario creado").button("OK", true).show(stage);

                } catch (Exception ex) {
                    error.setText("Fallo");
                }
            }
        });

        Table bt = dialog.getButtonTable();
        bt.pad(0,16f,16f,16f);
        bt.defaults().width(140f).height(54f).padLeft(8f).padRight(8f);
        bt.add(cancelBtn);
        bt.add(createBtn);

        dialog.show(stage);
        stage.setKeyboardFocus(user);
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

        TextButton.TextButtonStyle bs = new TextButton.TextButtonStyle();
        bs.font = ui;
        bs.fontColor = ui.getColor();
        skin.add("default", bs);

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

        Window.WindowStyle ws = new Window.WindowStyle();
        ws.titleFont = uiSmall;
        ws.titleFontColor = Color.WHITE;
        ws.background = whiteDraw.tint(new Color(0, 0, 0, 1));
        skin.add("default", ws);

        return skin;
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

        Label.LabelStyle lse = new Label.LabelStyle();
        lse.font = font;
        lse.fontColor = Color.SALMON;
        skin.add("error", lse);

        return skin;
    }

    private TextField.TextFieldFilter onlyAlnumFilter() {
        return (textField, c) -> Character.isLetterOrDigit(c);
    }

    private boolean isAlnum(String s) {
        return s != null && s.matches("[A-Za-z0-9]+");
    }
}
