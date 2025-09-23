package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
        
        Texture bgTex = new Texture("ui/titlescreen.png"); //fondo
        Image bgImg = new Image(bgTex);
        bgImg.setFillParent(true);

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

        ClickListener loginListener = new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { loginDialog(); } };
        ClickListener createListener = new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { createPlayerDialog(); } };
        ClickListener exitListener = new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { app.exit(); } };

        if (loginImg != null) loginImg.addListener(loginListener);
        if (createPlayerImg != null) createPlayerImg.addListener(createListener);
        if (exitImg != null) exitImg.addListener(exitListener);

        btnLogin.addListener(loginListener);
        btnCrear.addListener(createListener);
        btnSalir.addListener(exitListener);

        stage.addActor(bgImg); // atrás de todo
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        //mover tabla a izq
        root.top().left().padTop(80f).padLeft(30f);//mover aqui

        //titulo
        if (titleImg != null) 
            root.add(titleImg).left().padBottom(70f).row();
        else 
            root.add(lblTitle).left().padBottom(70f).row();

        //botones
        root.defaults().padTop(14f).padBottom(14f); //espaciado entre filas

        if (loginImg != null) root.add(loginImg).left().row(); 
        else root.add(btnLogin).left().row();

        if (createPlayerImg != null) root.add(createPlayerImg).left().padRight(5f).row(); 
        else root.add(btnCrear).left().row();

        if (exitImg != null) root.add(exitImg).left().padTop(28f).row(); 
        else root.add(btnSalir).left().padTop(28f).row();

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
        final Dialog dialog = new Dialog("", skin);

        Table content = dialog.getContentTable();
        content.padTop(24f);
        content.padLeft(24f);
        content.padRight(24f);
        content.padBottom(24f);
        content.defaults().pad(6f).fillX();

        // --- TITLE ---
        Label.LabelStyle titleStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        BitmapFont clonedTitleFont = new BitmapFont(titleStyle.font.getData(), titleStyle.font.getRegion(), false);
        clonedTitleFont.getData().setScale(0.6f); // match previous smaller title
        titleStyle.font = clonedTitleFont;
        Label titleLabel = new Label("INICIAR SESION", titleStyle);
        content.add(titleLabel).center().padBottom(20f).row();

        // --- FIELD LABELS ---
        Label.LabelStyle fieldLabelStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        BitmapFont clonedFieldFont = new BitmapFont(fieldLabelStyle.font.getData(), fieldLabelStyle.font.getRegion(), false);
        clonedFieldFont.getData().setScale(0.6f);
        fieldLabelStyle.font = clonedFieldFont;

        // --- TEXT FIELDS ---
        TextField.TextFieldStyle fieldTextStyle = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
        BitmapFont clonedTF = new BitmapFont(fieldTextStyle.font.getData(), fieldTextStyle.font.getRegion(), false);
        clonedTF.getData().setScale(0.5f);
        fieldTextStyle.font = clonedTF;

        final TextField user = new TextField("", fieldTextStyle);
        user.setMessageText("Usuario");
        user.setTextFieldFilter(onlyAlnumFilter());

        final TextField password = new TextField("", fieldTextStyle);
        password.setMessageText("Contrasena");
        password.setPasswordMode(true);
        password.setPasswordCharacter('.');
        password.setTextFieldFilter(onlyAlnumFilter());

        final Label error = new Label("", fieldLabelStyle);
        error.setColor(Color.GRAY);

        content.add(new Label("Usuario", fieldLabelStyle)).left().padLeft(10f).row();
        content.add(user).width(380f).height(32f).padLeft(10f).row();
        content.add(new Label("Contrasena", fieldLabelStyle)).left().padLeft(10f).row();
        content.add(password).width(380f).height(32f).padLeft(10f).row();
        content.add(error).left().padLeft(10f).row();

        // --- BUTTONS ---
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        BitmapFont clonedBtnFont = new BitmapFont(buttonStyle.font.getData(), buttonStyle.font.getRegion(), false);
        clonedBtnFont.getData().setScale(0.35f);
        buttonStyle.font = clonedBtnFont;

        TextButton cancelBtn = new TextButton("Cancelar", buttonStyle);
        TextButton okBtn = new TextButton("Entrar", buttonStyle);

        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        okBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
                    if (anterior == null || anterior == 0L) {
                        ManejoUsuarios.UsuarioActivo.sesionAnterior = ahora;
                        ManejoUsuarios.UsuarioActivo.setUltimaSesion(ahora);
                    } else {
                        ManejoUsuarios.UsuarioActivo.sesionAnterior = anterior;
                    }
                    ManejoUsuarios.UsuarioActivo.sesionActual = ahora;

                    ArchivoGuardar.guardarFechas();
                    dialog.hide();
                    game.setScreen(new MenuScreen(game));

                } catch (Exception ex) {
                    error.setText("Fallo");
                }
            }
        });

        dialog.show(stage);
        dialog.setSize(450f, dialog.getHeight() + 40f); //más ancho

        Table bt = dialog.getButtonTable();
        bt.padTop(16f).padBottom(20f).padLeft(16f).padRight(16f);
        bt.defaults().width(140f).height(40f).space(24f); //botones encogidos
        bt.add(cancelBtn);
        bt.add(okBtn);

        stage.setKeyboardFocus(user);
    }

    private void createPlayerDialog() {
        //caja sin titulo, se sale de los bordes
        final Dialog dialog = new Dialog("", skin);

        Table content = dialog.getContentTable();
        content.padTop(24f); //bajando contenido de tabla
        content.padLeft(24f);
        content.padRight(24f);
        content.padBottom(24f);
        content.defaults().pad(6f).fillX();

        Label.LabelStyle titleStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        BitmapFont clonedTitleFont = new BitmapFont(titleStyle.font.getData(), titleStyle.font.getRegion(), false);
        clonedTitleFont.getData().setScale(0.6f);
        titleStyle.font = clonedTitleFont;
        Label titleLabel = new Label("CREAR JUGADOR", titleStyle);
        content.add(titleLabel).center().padBottom(20f).row();

        Label.LabelStyle fieldLabelStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        BitmapFont clonedFieldFont = new BitmapFont(fieldLabelStyle.font.getData(), fieldLabelStyle.font.getRegion(), false);
        clonedFieldFont.getData().setScale(0.6f);
        fieldLabelStyle.font = clonedFieldFont;

        TextField.TextFieldStyle fieldTextStyle = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
        BitmapFont clonedTF = new BitmapFont(fieldTextStyle.font.getData(), fieldTextStyle.font.getRegion(), false);
        clonedTF.getData().setScale(0.5f);
        fieldTextStyle.font = clonedTF;

        final TextField user = new TextField("", fieldTextStyle);
        user.setMessageText("Usuario");
        user.setTextFieldFilter(onlyAlnumFilter());

        final TextField name = new TextField("", fieldTextStyle);
        name.setMessageText("Nombre");
        name.setTextFieldFilter(onlyAlnumFilter());

        final TextField password = new TextField("", fieldTextStyle);
        password.setMessageText("Contrasena");
        password.setPasswordMode(true);
        password.setPasswordCharacter('.');
        password.setTextFieldFilter(onlyAlnumFilter());

        final Label error = new Label("", fieldLabelStyle);
        error.setColor(Color.GRAY);

        content.add(new Label("Usuario", fieldLabelStyle)).left().padLeft(10f).row();
        content.add(user).width(380f).height(32f).padLeft(10f).row();
        content.add(new Label("Nombre", fieldLabelStyle)).left().padLeft(10f).row();
        content.add(name).width(380f).height(32f).padLeft(10f).row();
        content.add(new Label("Contrasena", fieldLabelStyle)).left().padLeft(10f).row();
        content.add(password).width(380f).height(32f).padLeft(10f).row();
        content.add(error).left().padLeft(10f).row();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        BitmapFont clonedBtnFont = new BitmapFont(buttonStyle.font.getData(), buttonStyle.font.getRegion(), false);
        clonedBtnFont.getData().setScale(0.35f);
        buttonStyle.font = clonedBtnFont;

        TextButton cancelBtn = new TextButton("Cancelar", buttonStyle);
        TextButton createBtn = new TextButton("Crear", buttonStyle);

        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        createBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
                    new Dialog("", skin).text("Usuario creado").button("OK", true).show(stage);

                } catch (Exception ex) {
                    error.setText("Fallo");
                }
            }
        });

        //caja de dialogo más ancha
        dialog.show(stage);
        dialog.setSize(450f, dialog.getHeight() + 40f);

        //botones menos anchos
        Table bt = dialog.getButtonTable();
        bt.padTop(16f).padBottom(20f).padLeft(16f).padRight(16f);
        bt.defaults().width(140f).height(40f).space(24f);
        bt.add(cancelBtn);
        bt.add(createBtn);

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
