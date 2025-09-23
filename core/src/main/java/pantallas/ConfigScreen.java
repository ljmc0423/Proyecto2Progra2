package pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.badlogic.gdx.utils.Scaling;
import com.elkinedwin.LogicaUsuario.AudioBus;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;

public class ConfigScreen extends BaseScreen {

    private final Game game;
    private Skin skin;

    private KeyBinder kbUp, kbDown, kbLeft, kbRight, kbReiniciar;
    private Slider sliderVolumen;
    private Label lblVolumenValor;
    private SelectBox<String> sbIdioma;

    private TextButton btnGuardar, btnVolver;

    private int volOriginal;
    private int idiomaOriginal;

    private Texture gradientTex;

    // Layout: delgado y arriba
    private static final float CONTENT_MAX_WIDTH = 900f;
    private static final float TOP_OFFSET        = 16f;

    public ConfigScreen(Game game) { this.game = game; }

    @Override
    protected void onShow() {
        skin = buildSkin();

        BitmapFont f = skin.getFont("default-font");
        f.getData().setScale(1.15f);

        int kUp    = getCfg("MoverArriba",  Input.Keys.W);
        int kDown  = getCfg("MoverAbajo",   Input.Keys.S);
        int kLeft  = getCfg("MoverIzq",     Input.Keys.A);
        int kRight = getCfg("MoverDer",     Input.Keys.D);
        int kRei   = getCfg("Reiniciar",    Input.Keys.R);

        Integer vVol = cfgAny("volumen", "Volumen");
        volOriginal = vVol != null ? vVol : 70;


        gradientTex = makeVerticalGradient(16, 400,
                new Color(0.08f, 0.10f, 0.13f, 1f),
                new Color(0.12f, 0.14f, 0.18f, 1f));
        Image bg = new Image(new TextureRegionDrawable(new TextureRegion(gradientTex)));
        bg.setFillParent(true);
        bg.setScaling(Scaling.fill);
        stage.addActor(bg);

        kbUp        = new KeyBinder("Arriba",     "MoverArriba", kUp);
        kbDown      = new KeyBinder("Abajo",      "MoverAbajo",  kDown);
        kbLeft      = new KeyBinder("Izquierda",  "MoverIzq",    kLeft);
        kbRight     = new KeyBinder("Derecha",    "MoverDer",    kRight);
        kbReiniciar = new KeyBinder("Reiniciar",  "Reiniciar",   kRei);

        Label lblVol = new Label("Volumen", skin, "section");
        lblVol.setFontScale(1.1f);
        sliderVolumen = new Slider(0, 100, 1, false, skin, "thick");
        sliderVolumen.setValue(volOriginal);
        lblVolumenValor = new Label(volOriginal + "%", skin, "mono");
        lblVolumenValor.setFontScale(1.05f);

        sliderVolumen.addListener(new InputListener() {
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { onVolChanged(); return true; }
            @Override public void touchDragged(InputEvent event, float x, float y, int pointer) { onVolChanged(); }
            @Override public void touchUp(InputEvent event, float x, float y, int pointer, int b) { onVolChanged(); }
        });

        Label tituloIdioma = new Label("Idioma", skin, "section");
        tituloIdioma.setFontScale(1.1f);
        sbIdioma = new SelectBox<>(skin);
        sbIdioma.setItems("Espanol", "Ingles");
        sbIdioma.setSelected(idiomaOriginal == 2 ? "Ingles" : "Espanol");
        sbIdioma.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { actualizarEstadoGuardar(); }
        });

        btnGuardar = new TextButton("Guardar cambios", skin, "cta");
        btnGuardar.getLabel().setFontScale(1.05f);
        btnGuardar.setDisabled(true);
        btnGuardar.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (btnGuardar.isDisabled()) return;

                putCfg("MoverArriba", kbUp.getActual());
                putCfg("MoverAbajo",  kbDown.getActual());
                putCfg("MoverIzq",    kbLeft.getActual());
                putCfg("MoverDer",    kbRight.getActual());
                putCfg("Reiniciar",   kbReiniciar.getActual());

                int nuevoVol = Math.round(sliderVolumen.getValue());
                putCfgAny(nuevoVol, "volumen", "Volumen");

                int nuevoIdioma = "Ingles".equals(sbIdioma.getSelected()) ? 2 : 1;
                putCfgAny(nuevoIdioma, "idioma", "Idioma");

                kbUp.confirmar(); kbDown.confirmar(); kbLeft.confirmar(); kbRight.confirmar(); kbReiniciar.confirmar();
                volOriginal = nuevoVol;
                idiomaOriginal = nuevoIdioma;

                actualizarEstadoGuardar();

                Dialog d = new Dialog("", skin);
                d.setModal(true);
                d.getContentTable().pad(12f);
                d.getContentTable().add(new Label("✅  Cambios guardados", skin, "dialog")).left();
                d.button("OK", true);
                d.show(stage);
            }
        });

        btnVolver = new TextButton("Regresar", skin, "ghost");
        btnVolver.getLabel().setFontScale(1.05f);
        btnVolver.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { game.setScreen(new MenuScreen(game)); }
        });

        // Contenido (sin encabezado)
        Table content = new Table();
        content.pad(20f, 24f, 20f, 24f);

        // Tarjeta: CONTROLES
        Table cardControles = makeCard();
        Label secControles = new Label("Controles", skin, "section");
        secControles.setFontScale(1.12f);
        cardControles.add(secControles).left().padBottom(8f).row();
        Label hint = new Label("Clic en un botón y presiona una tecla  ·  ESC cancela", skin, "hint");
        cardControles.add(hint).left().padBottom(6f).row();

        Table t = new Table();
        t.defaults().pad(8f);
        t.add(kbUp.lbl).left().padRight(12f).width(150f);        t.add(kbUp.btn).width(300f).height(40f).row();
        t.add(kbDown.lbl).left().padRight(12f).width(150f);      t.add(kbDown.btn).width(300f).height(40f).row();
        t.add(kbLeft.lbl).left().padRight(12f).width(150f);      t.add(kbLeft.btn).width(300f).height(40f).row();
        t.add(kbRight.lbl).left().padRight(12f).width(150f);     t.add(kbRight.btn).width(300f).height(40f).row();
        t.add(kbReiniciar.lbl).left().padRight(12f).width(150f); t.add(kbReiniciar.btn).width(300f).height(40f).row();
        cardControles.add(t).growX();
        content.add(cardControles).growX().padBottom(14f).row();

        // Tarjeta: AUDIO
        Table cardAudio = makeCard();
        Label secAudio = new Label("Audio", skin, "section");
        secAudio.setFontScale(1.12f);
        cardAudio.add(secAudio).left().padBottom(8f).row();
        Table ta = new Table();
        ta.defaults().pad(8f);
        ta.add(lblVol).left().padRight(12f).width(150f);
        ta.add(sliderVolumen).growX().padRight(12f).minWidth(420f).height(26f);
        ta.add(lblVolumenValor).width(80f).left();
        cardAudio.add(ta).growX();
        content.add(cardAudio).growX().padBottom(14f).row();

        

        // Footer botones
        Table botones = new Table();
        botones.defaults().pad(10f).width(240f).height(52f);
        botones.add(btnVolver);
        botones.add(btnGuardar);
        content.add(botones).right();

        // Scroll (con estilo default registrado)
        ScrollPane scroller = new ScrollPane(content, skin);
        scroller.setFadeScrollBars(false);
        scroller.setScrollingDisabled(true, false);

        // Marco que centra y limita ancho; y coloca arriba
        Table frame = new Table();
        frame.setFillParent(true);
        frame.top().padTop(TOP_OFFSET).padBottom(16f);
        frame.add(scroller).width(CONTENT_MAX_WIDTH).growY().top();
        stage.addActor(frame);

        // Captura de teclas global
        stage.addListener(new InputListener() {
            @Override public boolean keyDown(InputEvent event, int keycode) {
                boolean handled = false;
                handled |= kbUp.tryCapture(keycode);
                handled |= kbDown.tryCapture(keycode);
                handled |= kbLeft.tryCapture(keycode);
                handled |= kbRight.tryCapture(keycode);
                handled |= kbReiniciar.tryCapture(keycode);
                if (handled) actualizarEstadoGuardar();
                return handled;
            }
        });
    }

    private void onVolChanged() {
        int v = Math.round(sliderVolumen.getValue());
        lblVolumenValor.setText(v + "%");
        AudioBus.setMasterVolume(v / 100f);
        actualizarEstadoGuardar();
    }

    private void actualizarEstadoGuardar() {
        int idiomaNow = "Ingles".equals(sbIdioma.getSelected()) ? 2 : 1;
        boolean cambioValido =
                kbUp.cambioValido() ||
                kbDown.cambioValido() ||
                kbLeft.cambioValido() ||
                kbRight.cambioValido() ||
                kbReiniciar.cambioValido() ||
                Math.round(sliderVolumen.getValue()) != volOriginal ||
                idiomaNow != idiomaOriginal;
        btnGuardar.setDisabled(!cambioValido);
    }

    private int getCfg(String key, int def) {
        try { Integer v = ManejoUsuarios.UsuarioActivo.configuracion.get(key); return (v != null) ? v : def; }
        catch (Exception e) { return def; }
    }

    private Integer cfgAny(String... keys) {
        try {
            if (ManejoUsuarios.UsuarioActivo == null || ManejoUsuarios.UsuarioActivo.configuracion == null) return null;
            for (String k : keys) {
                Integer v = ManejoUsuarios.UsuarioActivo.configuracion.get(k);
                if (v != null) return v;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void putCfg(String key, int value) { try { ManejoUsuarios.UsuarioActivo.configuracion.put(key, value); } catch (Exception ignored) {} }
    private void putCfgAny(int value, String... keys) {
        try {
            if (ManejoUsuarios.UsuarioActivo == null || ManejoUsuarios.UsuarioActivo.configuracion == null) return;
            for (String k : keys) {
                if (ManejoUsuarios.UsuarioActivo.configuracion.containsKey(k)) {
                    ManejoUsuarios.UsuarioActivo.configuracion.put(k, value);
                    return;
                }
            }
            ManejoUsuarios.UsuarioActivo.configuracion.put(keys[0], value);
        } catch (Exception ignored) {}
    }

    /** Enlace de tecla: fondo como estaba, texto SIEMPRE BLANCO. */
    private class KeyBinder {
        final Label lbl;
        final TextButton btn;
        int original;
        int actual;
        boolean escuchando = false;

        KeyBinder(String nombre, String cfgKey, int valor) {
            this.lbl = new Label(nombre, skin, "label-dim");
            this.lbl.setFontScale(1.02f);
            this.btn = new TextButton(Input.Keys.toString(valor), skin, "key");
            this.btn.getLabel().setFontScale(1.02f);
            // **Texto siempre blanco**
            this.btn.getLabel().setColor(Color.WHITE);

            this.original = valor;
            this.actual = valor;

            btn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    escuchando = true;
                    btn.setText("Presiona una tecla…");
                    // mantener texto en blanco mientras escucha
                    btn.getLabel().setColor(Color.WHITE);
                }
            });
        }

        boolean tryCapture(int keycode) {
            if (!escuchando) return false;
            if (keycode == Input.Keys.ESCAPE) {
                escuchando = false;
                btn.setText(Input.Keys.toString(actual));
                // asegurar blanco al salir
                btn.getLabel().setColor(Color.WHITE);
                return true;
            }
            actual = keycode;
            btn.setText(Input.Keys.toString(actual));
            escuchando = false;
            // asegurar blanco después de capturar
            btn.getLabel().setColor(Color.WHITE);
            return true;
        }

        boolean cambioValido() { return actual != original; }
        void confirmar() { original = actual; }
        int getActual() { return actual; }
    }

    private Skin buildSkin() {
        Skin s = new Skin();

        // FUENTE
        BitmapFont font = new BitmapFont();
        s.add("default-font", font, BitmapFont.class);

        // BASE BLANCA
        Pixmap px = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        px.setColor(Color.WHITE);
        px.fill();
        Texture white = new Texture(px);
        px.dispose();
        s.add("white", white);

        // LABELS
        s.add("default", new Label.LabelStyle(font, new Color(1,1,1,0.94f)));
        s.add("label-dim", new Label.LabelStyle(font, new Color(1,1,1,0.86f)));
        s.add("hint", new Label.LabelStyle(font, new Color(1,1,1,0.52f)));
        s.add("section", new Label.LabelStyle(font, new Color(1,1,1,0.92f)));
        s.add("dialog", new Label.LabelStyle(font, new Color(1,1,1,0.96f)));
        s.add("mono", new Label.LabelStyle(font, new Color(1,1,1,0.92f)));

        // BOTONES BASE
        TextButton.TextButtonStyle bs = new TextButton.TextButtonStyle();
        bs.font = font;
        bs.up   = s.newDrawable("white", new Color(1, 1, 1, 0.12f));
        bs.over = s.newDrawable("white", new Color(1, 1, 1, 0.18f));
        bs.down = s.newDrawable("white", new Color(1, 1, 1, 0.25f));
        bs.disabled = s.newDrawable("white", new Color(1,1,1,0.06f));
        bs.fontColor = Color.WHITE;
        s.add("default", bs);

        // CTA y Ghost
        TextButton.TextButtonStyle cta = new TextButton.TextButtonStyle(bs);
        cta.up   = s.newDrawable("white", new Color(0.30f, 0.62f, 1f, 0.90f));
        cta.over = s.newDrawable("white", new Color(0.35f, 0.67f, 1f, 0.98f));
        cta.down = s.newDrawable("white", new Color(0.27f, 0.58f, 0.95f, 1f));
        cta.disabled = s.newDrawable("white", new Color(0.30f, 0.62f, 1f, 0.40f));
        cta.fontColor = Color.WHITE;
        s.add("cta", cta);

        TextButton.TextButtonStyle ghost = new TextButton.TextButtonStyle(bs);
        ghost.up = s.newDrawable("white", new Color(1,1,1,0.10f));
        ghost.over = s.newDrawable("white", new Color(1,1,1,0.14f));
        ghost.down = s.newDrawable("white", new Color(1,1,1,0.20f));
        s.add("ghost", ghost);

        // **Key: fondo como estaba (gris), texto blanco**
        TextButton.TextButtonStyle key = new TextButton.TextButtonStyle(bs);
        key.up   = s.newDrawable("white", new Color(1,1,1,0.18f));
        key.over = s.newDrawable("white", new Color(1,1,1,0.24f));
        key.down = s.newDrawable("white", new Color(1,1,1,0.30f));
        key.fontColor = Color.WHITE;
        s.add("key", key);

        // WINDOW / DIALOG
        Window.WindowStyle ws = new Window.WindowStyle();
        ws.titleFont = font;
        ws.titleFontColor = Color.WHITE;
        ws.background = s.newDrawable("white", new Color(0f, 0f, 0f, 0.86f));
        s.add("default", ws);

        // SLIDER
        Slider.SliderStyle ssl = new Slider.SliderStyle();
        ssl.background = s.newDrawable("white", new Color(1, 1, 1, 0.16f));
        ssl.knob = s.newDrawable("white", Color.WHITE);
        ssl.knobBefore = s.newDrawable("white", new Color(1, 1, 1, 0.60f));
        ssl.knobAfter  = s.newDrawable("white", new Color(1, 1, 1, 0.12f));
        s.add("default", ssl);

        Slider.SliderStyle thick = new Slider.SliderStyle(ssl);
        thick.background.setMinHeight(12f);
        thick.knob.setMinHeight(26f);
        thick.knob.setMinWidth(26f);
        thick.knobBefore.setMinHeight(12f);
        thick.knobAfter.setMinHeight(12f);
        s.add("thick", thick);

        // SELECTBOX + lista
        SelectBox.SelectBoxStyle sbs = new SelectBox.SelectBoxStyle();
        sbs.font = font;
        sbs.fontColor = Color.WHITE;
        sbs.background = s.newDrawable("white", new Color(1,1,1,0.14f));
        sbs.backgroundOver = s.newDrawable("white", new Color(1,1,1,0.20f));
        sbs.backgroundOpen = s.newDrawable("white", new Color(1,1,1,0.20f));
        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = font;
        listStyle.fontColorSelected = Color.BLACK;
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.selection = s.newDrawable("white", new Color(1,1,1,0.94f));
        listStyle.background = s.newDrawable("white", new Color(0,0,0,0.68f));
        sbs.listStyle = listStyle;
        sbs.scrollStyle = new ScrollPane.ScrollPaneStyle(); // usa scroll por defecto
        s.add("default", sbs);

        // Estilo por defecto para ScrollPane
        ScrollPane.ScrollPaneStyle sps = new ScrollPane.ScrollPaneStyle();
        sps.background   = s.newDrawable("white", new Color(1,1,1,0.04f));
        sps.vScroll      = s.newDrawable("white", new Color(1,1,1,0.10f));
        sps.vScrollKnob  = s.newDrawable("white", new Color(1,1,1,0.35f));
        sps.hScroll      = s.newDrawable("white", new Color(1,1,1,0.10f));
        sps.hScrollKnob  = s.newDrawable("white", new Color(1,1,1,0.35f));
        s.add("default", sps);

        return s;
    }

    private Table makeCard() {
        Table card = new Table();
        card.defaults().pad(6f);
        card.pad(14f);
        card.background(skin.newDrawable("white", new Color(1,1,1,0.08f)));
        Image sepTop = new Image(skin.newDrawable("white", new Color(1,1,1,0.10f)));
        sepTop.setHeight(1.2f);
        sepTop.setScaling(Scaling.stretchX);
        card.add(sepTop).growX().height(1.2f).padBottom(12f).row();
        return card;
    }

    private Texture makeVerticalGradient(int w, int h, Color top, Color bottom) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        for (int y = 0; y < h; y++) {
            float t = 1f - (y / (float)(h - 1));
            float r = top.r * t + bottom.r * (1f - t);
            float g = top.g * t + bottom.g * (1f - t);
            float b = top.b * t + bottom.b * (1f - t);
            float a = top.a * t + bottom.a * (1f - t);
            p.setColor(r, g, b, a);
            p.drawLine(0, y, w, y);
        }
        Texture tx = new Texture(p);
        p.dispose();
        return tx;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (skin != null) skin.dispose();
        if (gradientTex != null) gradientTex.dispose();
    }
}
