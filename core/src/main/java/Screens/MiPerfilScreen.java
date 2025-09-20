package Screens;

import static com.badlogic.gdx.Gdx.files;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import com.elkinedwin.LogicaUsuario.ManejoUsuarios;
import com.elkinedwin.LogicaUsuario.Usuario;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MiPerfilScreen extends BaseScreen {

    private final Game game;

    private FreeTypeFontGenerator generator;
    private BitmapFont titleFont, h2Font, bodyFont, smallFont;

    private Texture avatarTex;
    private Image   avatarImg;

    private Texture texPanelBg, texDivider, texEditIcon;

    private Label lblNombreVal;
    private Label lblUsuarioVal;
    private Label lblPassVal;

    public MiPerfilScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        // ===== Fuentes (sin tildes/ñ por el font) =====
        generator = new FreeTypeFontGenerator(files.internal("fonts/pokemon_fire_red.ttf"));
        titleFont = genFont(88, "E6DFC9");
        h2Font    = genFont(48, "E6DFC9");
        bodyFont  = genFont(34, "E6DFC9");
        smallFont = genFont(26, "BFC4D0");

        // ===== Styles base =====
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        Label.LabelStyle h2Style    = new Label.LabelStyle(h2Font,    h2Font.getColor());
        Label.LabelStyle keyStyle   = new Label.LabelStyle(bodyFont,  new Color(1,1,1,0.85f));
        Label.LabelStyle valStyle   = new Label.LabelStyle(bodyFont,  Color.WHITE);
        Label.LabelStyle cellStyle  = new Label.LabelStyle(bodyFont,  Color.WHITE);
        Label.LabelStyle thStyle    = new Label.LabelStyle(bodyFont,  new Color(1,1,1,0.95f));
        Label.LabelStyle hintStyle  = new Label.LabelStyle(smallFont, smallFont.getColor());

        // ===== RECURSOS UI =====
        texPanelBg  = makeColorTex(255, 255, 255, 22);
        texDivider  = makeColorTex(255, 255, 255, 38);

        String editIconPath = "../Imagenes/Editar.png";
        if (files.internal(editIconPath).exists()) {
            texEditIcon = new Texture(files.internal(editIconPath));
        } else if (files.internal("ui/edit.png").exists()) {
            texEditIcon = new Texture(files.internal("ui/edit.png"));
        } else {
            texEditIcon = makeColorTex(255,255,255,180); // fallback
        }
        ImageButton.ImageButtonStyle editStyle = new ImageButton.ImageButtonStyle();
        editStyle.imageUp = new TextureRegionDrawable(new TextureRegion(texEditIcon));

        TextureRegionDrawable panelBg   = new TextureRegionDrawable(new TextureRegion(texPanelBg));
        TextureRegionDrawable dividerBg = new TextureRegionDrawable(new TextureRegion(texDivider));

        // ===== Root =====
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // ===== Botón VOLVER (fix: estilo propio con blanco forzado en todos los estados) =====
        TextButton.TextButtonStyle backStyle = new TextButton.TextButtonStyle();
        backStyle.font = bodyFont;
        backStyle.fontColor = Color.WHITE;
        backStyle.overFontColor = Color.WHITE;
        backStyle.downFontColor = Color.WHITE;
        backStyle.checkedFontColor = Color.WHITE;
        backStyle.disabledFontColor = Color.WHITE;
        Texture transparent = makeColorTex(0,0,0,0);
        TextureRegionDrawable trd = new TextureRegionDrawable(new TextureRegion(transparent));
        backStyle.up = trd;
        backStyle.over = trd;
        backStyle.down = trd;
        backStyle.checked = trd;
        backStyle.disabled = trd;

        TextButton btnBack = new TextButton("Volver", backStyle);
        // Asegura que la label del botón quede blanca
        btnBack.getLabel().setColor(Color.WHITE);

        btnBack.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table topbar = new Table();
        topbar.add(btnBack).left().pad(12f);
        root.add(topbar).expandX().fillX().row();

        // ===== Scroller =====
        Table content = new Table();
        content.top().pad(24f);
        content.defaults().pad(10f);

        ScrollPane sp = new ScrollPane(content);
        sp.setFadeScrollBars(false);
        root.add(sp).expand().fill().pad(0, 16f, 16f, 16f).row();

        // ===== Cabecera (avatar + editar) =====
        Usuario u = ManejoUsuarios.UsuarioActivo;

        avatarImg = new Image();
        avatarImg.setScaling(Scaling.fit);
        reloadAvatar();

        ImageButton btnEditAvatar = new ImageButton(editStyle);
        btnEditAvatar.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                onChangeAvatar();
            }
        });

        Table avatarRow = new Table();
        avatarRow.add(avatarImg).size(220f, 220f).center().padRight(12f);
        avatarRow.add().width(8f);
        avatarRow.add(btnEditAvatar).size(48f, 48f).top().right();
        content.add(avatarRow).center().row();

        String displayName = (u != null && u.getNombre()!=null && !u.getNombre().isEmpty())
                ? u.getNombre() : (u != null ? u.getUsuario() : "Invitado");
        content.add(new Label(displayName, titleStyle)).padTop(2f).center().row();

        // ===== Card: Datos de la cuenta =====
        content.add(cardHeader("Datos de la cuenta", h2Style)).expandX().fillX().row();

        Table cardInfo = new Table();
        cardInfo.setBackground(panelBg);
        cardInfo.pad(20f);
        cardInfo.defaults().left().pad(10f);

        // Nombre (solo lectura)
        lblNombreVal  = new Label(textOrDash(u != null ? u.getNombre() : null),  valStyle);
        Table rNombre = kvRowWithValue("Nombre completo", lblNombreVal, keyStyle);
        cardInfo.add(rNombre).expandX().fillX().row();
        addDivider(cardInfo, dividerBg);

        // Usuario + editar
        lblUsuarioVal = new Label(textOrDash(u != null ? u.getUsuario() : null), valStyle);
        Table rUsuario = kvRowWithValue("Usuario", lblUsuarioVal, keyStyle);
        ImageButton btnEditUsuario = new ImageButton(editStyle);
        btnEditUsuario.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { onEditUsuario(); }
        });
        rUsuario.add(btnEditUsuario).size(32f, 32f).padLeft(8f);
        cardInfo.add(rUsuario).expandX().fillX().row();
        addDivider(cardInfo, dividerBg);

        // Contrasena + editar
        lblPassVal = new Label(mask(u != null ? u.getContrasena() : null), valStyle);
        Table rPass = kvRowWithValue("Contrasena", lblPassVal, keyStyle);
        ImageButton btnEditPass = new ImageButton(editStyle);
        btnEditPass.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { onEditPass(); }
        });
        rPass.add(btnEditPass).size(32f, 32f).padLeft(8f);
        cardInfo.add(rPass).expandX().fillX().row();
        addDivider(cardInfo, dividerBg);

        // Fechas y tiempo (solo lectura)
        cardInfo.add(kvRow("Fecha de registro",
                formatMillis(u != null ? u.getFechaRegistro() : null), keyStyle, valStyle))
                .expandX().fillX().row();
        addDivider(cardInfo, dividerBg);

        cardInfo.add(kvRow("Ultima sesion",
                (u != null ? u.getUltimaSesionTexto() : "-"), keyStyle, valStyle))
                .expandX().fillX().row();
        addDivider(cardInfo, dividerBg);

        int totalSeg = (u != null) ? u.getTiempoJugadoTotal() : 0;
        String totalFmt = formatSeconds(totalSeg) + "   (" + (totalSeg/60) + " min)";
        cardInfo.add(kvRow("Tiempo total de juego", totalFmt, keyStyle, valStyle))
                .expandX().fillX().row();

        content.add(cardInfo).expandX().fillX().row();

        // ===== Card: Progreso =====
        content.add(cardHeader("Progreso", h2Style)).expandX().fillX().padTop(12f).row();

        Table cardProg = new Table();
        cardProg.setBackground(panelBg);
        cardProg.pad(14f);
        cardProg.defaults().left().pad(6f);

        Table header = new Table();
        header.defaults().left().pad(6f);
        header.add(new Label("Nivel", thStyle)).width(140f);
        header.add(new Label("Estado", thStyle)).width(260f);
        header.add(new Label("Tiempo promedio", thStyle)).width(260f);
        header.add(new Label("Mayor puntuacion", thStyle)).width(240f).row();
        cardProg.add(header).expandX().fillX().row();

        for (int n = 1; n <= 7; n++) {
            boolean completo = (u != null) && u.getNivelCompletado(n);
            int promedio = 0;
            if (u != null) {
                try { promedio = u.getTiempoPromedioNivel(n - 1); } catch (Throwable ignored) {}
            }
            int best = (u != null) ? u.getMayorPuntuacion(n) : 0;

            Table row = new Table();
            row.defaults().left().pad(6f);
            row.add(new Label("Nivel " + n, cellStyle)).width(140f);
            row.add(new Label(completo ? "Completado" : "Sin completar", cellStyle)).width(260f);
            row.add(new Label(formatSeconds(promedio), cellStyle)).width(260f);
            row.add(new Label(String.valueOf(best), cellStyle)).width(240f).row();

            cardProg.add(row).expandX().fillX().row();

            if (n < 7) {
                Image div = new Image(dividerBg);
                cardProg.add(div).height(1f).expandX().fillX().padTop(2f).padBottom(2f).row();
            }
        }

        Label hint = new Label("", hintStyle);
        hint.setColor(new Color(1,1,1,0.55f));
        cardProg.add(hint).left().padTop(8f).row();

        content.add(cardProg).expandX().fillX().row();
    }

    // ===== Dialog style =====
    private Window.WindowStyle dialogStyle() {
        Window.WindowStyle ws = new Window.WindowStyle();
        ws.titleFont = bodyFont;
        ws.titleFontColor = Color.WHITE;
        ws.background = new TextureRegionDrawable(new TextureRegion(makeColorTex(0,0,0,200)));
        return ws;
    }

    // ===== Avatar =====
    private void onChangeAvatar() {
        final String[] selected = new String[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                JFileChooser ch = new JFileChooser();
                ch.setDialogTitle("Seleccionar imagen de avatar");
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Imagenes (png, jpg, jpeg, gif)", "png","jpg","jpeg","gif");
                ch.setFileFilter(filter);
                ch.setAcceptAllFileFilterUsed(false);
                int res = ch.showOpenDialog(null);
                if (res == JFileChooser.APPROVE_OPTION) {
                    selected[0] = ch.getSelectedFile().getAbsolutePath();
                }
            });
        } catch (Exception ignored) {}

        if (selected[0] == null || selected[0].trim().isEmpty()) return;

        Usuario u = ManejoUsuarios.UsuarioActivo;
        if (u != null) {
            u.setAvatar(selected[0]);  
            reloadAvatar();           
        }
    }

  
    private void reloadAvatar() {
        if (avatarTex != null) { avatarTex.dispose(); avatarTex = null; }

        String path = "ui/default_avatar.png";
        Usuario u = ManejoUsuarios.UsuarioActivo;
        if (u != null && u.avatar != null && !u.avatar.trim().isEmpty()) {
            if (files.internal(u.avatar).exists()) {
                avatarTex = new Texture(files.internal(u.avatar));
            } else if (files.absolute(u.avatar).exists()) {
                avatarTex = new Texture(files.absolute(u.avatar));
            }
        }
        if (avatarTex == null) avatarTex = new Texture(files.internal(path));

        if (avatarImg != null) {
            avatarImg.setDrawable(new TextureRegionDrawable(new TextureRegion(avatarTex)));
            avatarImg.invalidateHierarchy();
        }
    }

   
    private boolean isAlnum(String s){ return s != null && s.matches("[A-Za-z0-9]+"); }

    private void onEditUsuario() {
        final Dialog dlg = new Dialog("Editar usuario", dialogStyle());
        Table c = dlg.getContentTable(); c.pad(16f); c.defaults().pad(6f).fillX();

        Label l = new Label("Nuevo usuario :",
                new Label.LabelStyle(bodyFont, bodyFont.getColor()));
        final TextField tf = themedTextField();
        final Label hint = new Label("",
                new Label.LabelStyle(smallFont, Color.LIGHT_GRAY));
        final Label err  = new Label("", new Label.LabelStyle(smallFont, Color.SALMON));

        c.add(l).left().row();
        c.add(tf).width(380f).row();
        c.add(hint).left().row();
        c.add(err).left().row();

        TextButton cancel = new TextButton("Cancelar", btnStyle());
        TextButton ok     = new TextButton("Guardar",  btnStyle());

        cancel.addListener(new ClickListener(){ @Override public void clicked(InputEvent e,float x,float y){ dlg.hide(); }});
        ok.addListener(new ClickListener(){
            @Override public void clicked(InputEvent e,float x,float y){
                String v = tf.getText().trim();
                if (!isAlnum(v)) { err.setText("Usuario invalido."); return; }
                Usuario u = ManejoUsuarios.UsuarioActivo;
                if (u != null) {
                    u.setUsuario(v);          
                    lblUsuarioVal.setText(v); 
                }
                dlg.hide();
            }
        });

        dlg.getButtonTable().pad(0,16f,16f,16f).defaults().width(140f).pad(6f);
        dlg.button(cancel); dlg.button(ok);
        dlg.show(stage);
        stage.setKeyboardFocus(tf);
    }

    private void onEditPass() {
        final Dialog dlg = new Dialog("Editar contrasena", dialogStyle());
        Table c = dlg.getContentTable(); c.pad(16f); c.defaults().pad(6f).fillX();

        Label l1 = new Label("Nueva contrasena :",
                new Label.LabelStyle(bodyFont, bodyFont.getColor()));
        Label l2 = new Label("Confirmar:",
                new Label.LabelStyle(bodyFont, bodyFont.getColor()));
        final TextField tf1 = themedTextField();
        final TextField tf2 = themedTextField();
        final Label err  = new Label("", new Label.LabelStyle(smallFont, Color.SALMON));

        c.add(l1).left().row();
        c.add(tf1).width(380f).row();
        c.add(l2).left().row();
        c.add(tf2).width(380f).row();
        c.add(err).left().row();

        TextButton cancel = new TextButton("Cancelar", btnStyle());
        TextButton ok     = new TextButton("Guardar",  btnStyle());

        cancel.addListener(new ClickListener(){ @Override public void clicked(InputEvent e,float x,float y){ dlg.hide(); }});
        ok.addListener(new ClickListener(){
            @Override public void clicked(InputEvent e,float x,float y){
                String p1 = tf1.getText().trim();
                String p2 = tf2.getText().trim();
                if (!isAlnum(p1) || !isAlnum(p2)) { err.setText(""); return; }
                if (!p1.equals(p2)) { err.setText("No coincide."); return; }
                Usuario u = ManejoUsuarios.UsuarioActivo;
                if (u != null) {
                    u.setContrasena(p1);               
                    lblPassVal.setText(mask(p1));      
                }
                dlg.hide();
            }
        });

        dlg.getButtonTable().pad(0,16f,16f,16f).defaults().width(140f).pad(6f);
        dlg.button(cancel); dlg.button(ok);
        dlg.show(stage);
        stage.setKeyboardFocus(tf1);
    }

    // ===== estilos para botones / campos =====
    private TextButton.TextButtonStyle btnStyle() {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font = bodyFont;
        s.fontColor = bodyFont.getColor();
        return s;
    }

    private TextField themedTextField() {
        TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
        tfs.font = bodyFont;
        tfs.fontColor = Color.WHITE;
        tfs.cursor = new TextureRegionDrawable(new TextureRegion(makeColorTex(255,255,255,255)));
        tfs.selection = new TextureRegionDrawable(new TextureRegion(makeColorTex(255,255,255,80)));
        tfs.background = new TextureRegionDrawable(new TextureRegion(makeColorTex(0,0,0,150)));
        return new TextField("", tfs);
    }

    // ===== Helpers UI =====
    private BitmapFont genFont(int size, String hex) {
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = size; p.color = Color.valueOf(hex);
        return generator.generateFont(p);
    }

    private Texture makeColorTex(int r, int g, int b, int a) {
        Pixmap pm = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pm.setColor(r/255f, g/255f, b/255f, a/255f);
        pm.fill();
        Texture t = new Texture(pm); pm.dispose();
        return t;
    }

    private Table cardHeader(String text, Label.LabelStyle style) {
        Table t = new Table();
        t.defaults().left();
        t.add(new Label(text, style)).left();
        return t;
    }

    private void addDivider(Table table, TextureRegionDrawable dividerBg) {
        Image div = new Image(dividerBg);
        table.add(div).height(1f).expandX().fillX().padTop(2f).padBottom(2f).row();
    }

    private Table kvRow(String key, String value, Label.LabelStyle keyStyle, Label.LabelStyle valStyle) {
        Table row = new Table();
        row.defaults().left().pad(6f);
        row.add(new Label(key + ":", keyStyle)).width(300f).left().padRight(24f);
        Label v = new Label(value == null || value.isEmpty() ? "-" : value, valStyle);
        row.add(v).left().expandX().fillX();
        return row;
    }

    private Table kvRowWithValue(String key, Label valueLabel, Label.LabelStyle keyStyle) {
        Table row = new Table();
        row.defaults().left().pad(6f);
        row.add(new Label(key + ":", keyStyle)).width(300f).left().padRight(24f);
        row.add(valueLabel).left().expandX().fillX();
        return row;
    }

    private String textOrDash(String s){ return (s == null || s.isEmpty()) ? "-" : s; }

    private String formatMillis(Long m){
        if (m == null || m <= 0) return "-";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(m));
    }

    private String formatSeconds(int sec){
        int h = sec / 3600, m = (sec % 3600) / 60, s = sec % 60;
        if (h > 0) return String.format("%dh %02dm %02ds", h, m, s);
        if (m > 0) return String.format("%dm %02ds", m, s);
        return String.format("%ds", s);
    }

    private String mask(String s){
        if (s == null || s.isEmpty()) return "-";
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) b.append('*');
        return b.toString();
    }

    @Override
    public void hide() {
        if (avatarTex != null) { avatarTex.dispose(); avatarTex = null; }
        if (texPanelBg != null) { texPanelBg.dispose(); texPanelBg = null; }
        if (texDivider != null) { texDivider.dispose(); texDivider = null; }
        if (texEditIcon != null) { texEditIcon.dispose(); texEditIcon = null; }
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (generator != null) generator.dispose();
        if (titleFont != null) titleFont.dispose();
        if (h2Font != null)    h2Font.dispose();
        if (bodyFont != null)  bodyFont.dispose();
        if (smallFont != null) smallFont.dispose();
        if (avatarTex != null) { avatarTex.dispose(); avatarTex = null; }
        if (texPanelBg != null) { texPanelBg.dispose(); texPanelBg = null; }
        if (texDivider != null) { texDivider.dispose(); texDivider = null; }
        if (texEditIcon != null) { texEditIcon.dispose(); texEditIcon = null; }
    }
}