package Screens;

import static com.badlogic.gdx.Gdx.files;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
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

    private Texture dlgBgTex, tfBgTex, tfSelTex, tfCurTex, btnClearTex;
    private TextureRegionDrawable dlgBgDr, tfBgDr, tfSelDr, tfCurDr, btnClearDr;
    private Window.WindowStyle winStyleCached;
    private TextField.TextFieldStyle tfStyleCached;
    private TextButton.TextButtonStyle btnStyleCached;

    public MiPerfilScreen(Game game) { this.game = game; }

    @Override
    protected void onShow() {
        generator = new FreeTypeFontGenerator(files.internal("fonts/pokemon_fire_red.ttf"));
        titleFont = genFont(60, "000000");
        h2Font    = genFont(30, "000000");
        bodyFont  = genFont(20, "000000");
        smallFont = genFont(10, "000000");

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        Label.LabelStyle h2Style    = new Label.LabelStyle(h2Font,    h2Font.getColor());
        Label.LabelStyle keyStyle   = new Label.LabelStyle(bodyFont,  Color.BLACK);
        Label.LabelStyle valStyle   = new Label.LabelStyle(bodyFont,  Color.BLACK);
        Label.LabelStyle cellStyle  = new Label.LabelStyle(bodyFont,  Color.BLACK);
        Label.LabelStyle thStyle    = new Label.LabelStyle(bodyFont,  Color.BLACK);
        Label.LabelStyle hintStyle  = new Label.LabelStyle(smallFont, smallFont.getColor());

        texPanelBg  = makeColorTex(255, 255, 255, 22);
        texDivider  = makeColorTex(255, 255, 255, 38);

        String editIconPath = "ui/boton_cambiar.png";
        if (files.internal(editIconPath).exists()) {
            texEditIcon = new Texture(files.internal(editIconPath));
        } else if (files.internal("ui/edit.png").exists()) {
            texEditIcon = new Texture(files.internal("ui/edit.png"));
        } else {
            texEditIcon = makeColorTex(255,255,255,180);
        }
        ImageButton.ImageButtonStyle editStyle = new ImageButton.ImageButtonStyle();
        editStyle.imageUp = new TextureRegionDrawable(new TextureRegion(texEditIcon));

        TextureRegionDrawable panelBg   = new TextureRegionDrawable(new TextureRegion(texPanelBg));
        TextureRegionDrawable dividerBg = new TextureRegionDrawable(new TextureRegion(texDivider));

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        TextButton.TextButtonStyle backStyle = new TextButton.TextButtonStyle();
        backStyle.font = bodyFont;
        backStyle.fontColor = Color.BLACK;
        backStyle.overFontColor = Color.BLACK;
        backStyle.downFontColor = Color.BLACK;
        backStyle.checkedFontColor = Color.BLACK;
        backStyle.disabledFontColor = Color.BLACK;
        Texture transparent = makeColorTex(0,0,0,0);
        TextureRegionDrawable trd = new TextureRegionDrawable(new TextureRegion(transparent));
        backStyle.up = trd; backStyle.over = trd; backStyle.down = trd; backStyle.checked = trd; backStyle.disabled = trd;

        TextButton btnBack = new TextButton("REGRESAR", backStyle);
        btnBack.getLabel().setColor(Color.BLACK);
        btnBack.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { game.setScreen(new MenuScreen(game)); }
        });

        Table topbar = new Table();
        topbar.add(btnBack).left().pad(12f);
        root.add(topbar).expandX().fillX().row();

        Table content = new Table();
        content.top().pad(24f);
        content.defaults().pad(10f);

        ScrollPane sp = new ScrollPane(content);
        sp.setFadeScrollBars(false);
        root.add(sp).expand().fill().pad(0, 16f, 16f, 16f).row();

        Usuario u = ManejoUsuarios.UsuarioActivo;

        avatarImg = new Image();
        avatarImg.setScaling(Scaling.fit);
        reloadAvatar();

        ImageButton btnEditAvatar = new ImageButton(editStyle);
        
        btnEditAvatar.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { onChangeAvatar(); }
        });

        Table avatarRow = new Table();
        avatarRow.add(avatarImg).size(220f, 220f).center().padRight(12f);
        avatarRow.add().width(8f);
        avatarRow.add(btnEditAvatar).size(122f, 28f).top().right();
        content.add(avatarRow).center().row();

        String displayName = (u != null && u.getNombre()!=null && !u.getNombre().isEmpty())
                ? u.getNombre() : (u != null ? u.getUsuario() : "Invitado");
        content.add(new Label(displayName, titleStyle)).padTop(2f).center().row();

        content.add(cardHeader("DATOS DE USUARIO", h2Style)).expandX().fillX().row();

        Table cardInfo = new Table();
        cardInfo.setBackground(panelBg);
        cardInfo.pad(20f);
        cardInfo.defaults().left().pad(10f);

        lblNombreVal  = new Label(textOrDash(u != null ? u.getNombre() : null),  valStyle);
        Table rNombre = kvRowWithValue("Nombre completo", lblNombreVal, keyStyle);
        cardInfo.add(rNombre).expandX().fillX().row();
        addDivider(cardInfo, dividerBg);

        lblUsuarioVal = new Label(textOrDash(u != null ? u.getUsuario() : null), valStyle);
        Table rUsuario = kvRowWithValue("Usuario", lblUsuarioVal, keyStyle);
        ImageButton btnEditUsuario = new ImageButton(editStyle);
        btnEditUsuario.addListener(new ClickListener() { @Override public void clicked(InputEvent e,float x,float y){ onEditUsuario(); }});
        rUsuario.add(btnEditUsuario).size(122f, 28f).padLeft(8f);
        cardInfo.add(rUsuario).expandX().fillX().row();
        addDivider(cardInfo, dividerBg);

        lblPassVal = new Label(mask(u != null ? u.getContrasena() : null), valStyle);
        Table rPass = kvRowWithValue("Contrasena", lblPassVal, keyStyle);
        ImageButton btnEditPass = new ImageButton(editStyle);
        btnEditPass.addListener(new ClickListener() { @Override public void clicked(InputEvent e,float x,float y){ onEditPass(); }});
        rPass.add(btnEditPass).size(122f, 28f).padLeft(8f);
        cardInfo.add(rPass).expandX().fillX().row();
        addDivider(cardInfo, dividerBg);

        int totalSeg = (u != null) ? u.getTiempoJugadoTotal() : 0;
        String totalFmt = formatSecondsHuman(totalSeg) + "   (" + (totalSeg/60) + " min)";
        cardInfo.add(kvRow("Tiempo total de juego", totalFmt, keyStyle, valStyle)).expandX().fillX().row();
        addDivider(cardInfo, dividerBg);

        int partidasJugadas = 0;
        if (u != null && u.historial != null) partidasJugadas = u.historial.size();
        cardInfo.add(kvRow("Total partidas jugadas", "  "+String.valueOf(partidasJugadas), keyStyle, valStyle)).expandX().fillX().row();
        
        content.add(cardInfo).expandX().fillX().row();

        content.add(cardHeader("ESTADISTICAS", h2Style)).expandX().fillX().padTop(12f).row();

        Table cardProg = new Table();
        cardProg.setBackground(panelBg);
        cardProg.pad(14f);
        cardProg.defaults().left().pad(6f);

        Table header = new Table();
        header.defaults().left().pad(6f);
        header.add(new Label("Nivel", thStyle)).width(120f);
        header.add(new Label("Completado", thStyle)).width(200f);
        header.add(new Label("Partidas", thStyle)).width(160f);
        header.add(new Label("Tiempo promedio ", thStyle)).width(260f);
        header.add(new Label("Mejor tiempo", thStyle)).width(240f);
        header.add(new Label("Mejor puntuacion", thStyle)).width(200f).row();
        cardProg.add(header).expandX().fillX().row();

        for (int n = 1; n <= 7; n++) {
            boolean completo = (u != null) && u.getNivelCompletado(n);
            int promedio = 0;
            int mejorTiempo = 0;
            int bestSteps = 0;
            int partidasNivel = 0;
            if (u != null) {
                try { promedio = u.getTiempoPromedioNivel(n); } catch (Throwable ignored) {}
                try { mejorTiempo = u.getMejorTiempoPorNivel(n); } catch (Throwable ignored) {}
                try { bestSteps = u.getMayorPuntuacion(n); } catch (Throwable ignored) {}
                try { partidasNivel = u.getPartidasPorNivel(n); } catch (Throwable ignored) {}
            }

            String partidasTxt      = (partidasNivel > 0) ? String.valueOf(partidasNivel) : "-";
            String promedioTxt      = (promedio > 0) ? formatSecondsHuman(promedio) : "-";
            String mejorTiempoTxt   = (mejorTiempo > 0) ? formatSecondsHuman(mejorTiempo) : "-";
            String bestStepsTxt     = (bestSteps > 0) ? (bestSteps + " pasos") : "-";

            Table row = new Table();
            row.defaults().left().pad(6f);
            row.add(new Label("Nivel " + n, cellStyle)).width(120f);
            row.add(new Label(completo ? "O" : "X", cellStyle)).width(200f);
            row.add(new Label(partidasTxt, cellStyle)).width(160f);
            row.add(new Label(promedioTxt, cellStyle)).width(260f);
            row.add(new Label(mejorTiempoTxt, cellStyle)).width(240f);
            row.add(new Label(bestStepsTxt, cellStyle)).width(200f).row();

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

        dlgBgTex  = makeColorTex(0,0,0,200);
        tfBgTex   = makeColorTex(0,0,0,150);
        tfSelTex  = makeColorTex(255,255,255,80);
        tfCurTex  = makeColorTex(255,255,255,255);
        btnClearTex = makeColorTex(0,0,0,0);

        dlgBgDr  = new TextureRegionDrawable(new TextureRegion(dlgBgTex));
        tfBgDr   = new TextureRegionDrawable(new TextureRegion(tfBgTex));
        tfSelDr  = new TextureRegionDrawable(new TextureRegion(tfSelTex));
        tfCurDr  = new TextureRegionDrawable(new TextureRegion(tfCurTex));
        btnClearDr = new TextureRegionDrawable(new TextureRegion(btnClearTex));

        winStyleCached = new Window.WindowStyle();
        winStyleCached.titleFont = bodyFont;
        winStyleCached.titleFontColor = Color.BLACK;
        winStyleCached.background = dlgBgDr;

        tfStyleCached = new TextField.TextFieldStyle();
        tfStyleCached.font = bodyFont;
        tfStyleCached.fontColor = Color.BLACK;
        tfStyleCached.background = tfBgDr;
        tfStyleCached.selection = tfSelDr;
        tfStyleCached.cursor = tfCurDr;

        btnStyleCached = new TextButton.TextButtonStyle();
        btnStyleCached.font = bodyFont;
        btnStyleCached.fontColor = Color.BLACK;
        btnStyleCached.overFontColor = Color.BLACK;
        btnStyleCached.downFontColor = Color.BLACK;
        btnStyleCached.checkedFontColor = Color.BLACK;
        btnStyleCached.disabledFontColor = Color.BLACK;
        btnStyleCached.up = btnClearDr; btnStyleCached.down = btnClearDr; btnStyleCached.over = btnClearDr;
        btnStyleCached.checked = btnClearDr; btnStyleCached.disabled = btnClearDr;
    }

    private void onChangeAvatar() {
        final String[] selected = new String[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                JFileChooser ch = new JFileChooser();
                ch.setDialogTitle("Seleccionar imagen de avatar");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagenes (png, jpg, jpeg, gif)", "png","jpg","jpeg","gif");
                ch.setFileFilter(filter);
                ch.setAcceptAllFileFilterUsed(false);
                int res = ch.showOpenDialog(null);
                if (res == JFileChooser.APPROVE_OPTION) selected[0] = ch.getSelectedFile().getAbsolutePath();
            });
        } catch (Exception ignored) {}
        if (selected[0] == null || selected[0].trim().isEmpty()) return;
        Usuario u = ManejoUsuarios.UsuarioActivo;
        if (u != null) { u.setAvatar(selected[0]); reloadAvatar(); }
    }

    private void reloadAvatar() {
        if (avatarTex != null) { avatarTex.dispose(); avatarTex = null; }
        String path = "ui/default_avatar.png";
        Usuario u = ManejoUsuarios.UsuarioActivo;
        if (u != null && u.avatar != null && !u.avatar.trim().isEmpty()) {
            if (files.internal(u.avatar).exists()) avatarTex = new Texture(files.internal(u.avatar));
            else if (files.absolute(u.avatar).exists()) avatarTex = new Texture(files.absolute(u.avatar));
        }
        if (avatarTex == null) avatarTex = new Texture(files.internal(path));
        if (avatarImg != null) {
            avatarImg.setDrawable(new TextureRegionDrawable(new TextureRegion(avatarTex)));
            avatarImg.invalidateHierarchy();
        }
    }

    private boolean isAlnum(String s){ return s != null && s.matches("[A-Za-z0-9]+"); }

    private void onEditUsuario() {
        final Dialog dlg = new Dialog("Editar usuario", winStyleCached);
        Table c = dlg.getContentTable(); c.pad(16f); c.defaults().pad(6f).fillX();

        Label l = new Label("Nuevo usuario :", new Label.LabelStyle(bodyFont, Color.BLACK));
        final TextField tf = new TextField("", tfStyleCached);
        final Label err  = new Label("", new Label.LabelStyle(smallFont, Color.GRAY));

        c.add(l).left().row();
        c.add(tf).width(380f).row();
        c.add(err).left().row();

        TextButton cancel = new TextButton("Cancelar", btnStyleCached);
        TextButton ok     = new TextButton("Guardar",  btnStyleCached);

        cancel.addListener(new ClickListener(){ @Override public void clicked(InputEvent e,float x,float y){ dlg.hide(); }});
        ok.addListener(new ClickListener(){
            @Override public void clicked(InputEvent e,float x,float y){
                String v = tf.getText().trim();
                if (!isAlnum(v)) { err.setText("Usuario invalido."); return; }
                Usuario u = ManejoUsuarios.UsuarioActivo;
                if (u != null) { u.setUsuario(v); lblUsuarioVal.setText(v); }
                dlg.hide();
            }
        });

        dlg.getButtonTable().pad(0,16f,16f,16f).defaults().width(140f).pad(6f);
        dlg.getButtonTable().add(cancel);
        dlg.getButtonTable().add(ok);

        showDialog(dlg, tf);
    }

    private void onEditPass() {
        final Dialog dlg = new Dialog("Editar contrasena", winStyleCached);
        Table c = dlg.getContentTable(); c.pad(16f); c.defaults().pad(6f).fillX();

        Label l1 = new Label("Nueva contrasena :", new Label.LabelStyle(bodyFont, Color.BLACK));
        Label l2 = new Label("Confirmar:", new Label.LabelStyle(bodyFont, Color.BLACK));
        final TextField tf1 = new TextField("", tfStyleCached);
        final TextField tf2 = new TextField("", tfStyleCached);
        tf1.setPasswordMode(true); tf1.setPasswordCharacter('.');
        tf2.setPasswordMode(true); tf2.setPasswordCharacter('.');
        final Label err  = new Label("", new Label.LabelStyle(smallFont, Color.GRAY));

        c.add(l1).left().row();
        c.add(tf1).width(380f).row();
        c.add(l2).left().row();
        c.add(tf2).width(380f).row();
        c.add(err).left().row();

        TextButton cancel = new TextButton("Cancelar", btnStyleCached);
        TextButton ok     = new TextButton("Guardar",  btnStyleCached);

        cancel.addListener(new ClickListener(){ @Override public void clicked(InputEvent e,float x,float y){ dlg.hide(); }});
        ok.addListener(new ClickListener(){
            @Override public void clicked(InputEvent e,float x,float y){
                String p1 = tf1.getText().trim();
                String p2 = tf2.getText().trim();
                if (!isAlnum(p1) || !isAlnum(p2)) { err.setText("Solo letras o numeros."); return; }
                if (!p1.equals(p2)) { err.setText("No coincide."); return; }
                Usuario u = ManejoUsuarios.UsuarioActivo;
                if (u != null) { u.setContrasena(p1); lblPassVal.setText(mask(p1)); }
                dlg.hide();
            }
        });

        dlg.getButtonTable().pad(0,16f,16f,16f).defaults().width(140f).pad(6f);
        dlg.getButtonTable().add(cancel);
        dlg.getButtonTable().add(ok);

        showDialog(dlg, tf1);
    }

    private void showDialog(Dialog dlg, Actor focus) {
        dlg.setColor(Color.BLACK);
        dlg.show(stage);
        dlg.toFront();
        dlg.invalidateHierarchy();
        dlg.pack();
        if (focus != null) stage.setKeyboardFocus(focus);
        stage.setScrollFocus(null);
        forceWhiteText(dlg);
    }

    private void forceWhiteText(Actor a) {
        if (a instanceof Label) ((Label)a).setColor(Color.BLACK);
        if (a instanceof TextButton) ((TextButton)a).getLabel().setColor(Color.BLACK);
        if (a instanceof Group) for (Actor ch : ((Group)a).getChildren()) forceWhiteText(ch);
    }

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

    // Formato humano: s / min / h
    private String formatSecondsHuman(int sec){
        if (sec <= 0) return "0 s";
        int h = sec / 3600;
        int m = (sec % 3600) / 60;
        int s = sec % 60;
        if (h > 0) return String.format("%dh %02dmin %02ds", h, m, s);
        if (m > 0) return String.format("%dmin %02ds", m, s);
        return String.format("%ds", s);
    }

    private String mask(String s){
        if (s == null || s.isEmpty()) return "-";
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) b.append('.');
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
        if (dlgBgTex != null) dlgBgTex.dispose();
        if (tfBgTex  != null) tfBgTex.dispose();
        if (tfSelTex != null) tfSelTex.dispose();
        if (tfCurTex != null) tfCurTex.dispose();
        if (btnClearTex != null) btnClearTex.dispose();
    }
}