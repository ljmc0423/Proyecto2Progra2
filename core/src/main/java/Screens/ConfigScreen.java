
package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;

public class ConfigScreen extends BaseScreen {

    private final Game game;
    private Skin skin;

    private KeyBinder kbUp, kbDown, kbLeft, kbRight, kbReiniciar;
    private TextButton btnGuardar, btnVolver;

    public ConfigScreen(Game game) { this.game = game; }

    @Override
    protected void onShow() {
        skin = buildMinimalSkin();

        int kUp    = getCfg("MoverArriba",  Input.Keys.UP);
        int kDown  = getCfg("MoverAbajo",   Input.Keys.DOWN);
        int kLeft  = getCfg("MoverIzq",     Input.Keys.LEFT);
        int kRight = getCfg("MoverDer",     Input.Keys.RIGHT);
        int kRei   = getCfg("Reiniciar",    Input.Keys.R);

        Label titulo = new Label("Controles", skin);
        titulo.setFontScale(1.2f);

        kbUp        = new KeyBinder("Arriba",     "MoverArriba", kUp);
        kbDown      = new KeyBinder("Abajo",      "MoverAbajo",  kDown);
        kbLeft      = new KeyBinder("Izquierda",  "MoverIzq",    kLeft);
        kbRight     = new KeyBinder("Derecha",    "MoverDer",    kRight);
        kbReiniciar = new KeyBinder("Reiniciar",  "Reiniciar",   kRei);

        btnGuardar = new TextButton("Guardar cambios", skin);
        btnGuardar.setDisabled(true);
        btnGuardar.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (btnGuardar.isDisabled()) return;

                putCfg("MoverArriba", kbUp.getActual());
                putCfg("MoverAbajo",  kbDown.getActual());
                putCfg("MoverIzq",    kbLeft.getActual());
                putCfg("MoverDer",    kbRight.getActual());
                putCfg("Reiniciar",   kbReiniciar.getActual());

                kbUp.confirmar();
                kbDown.confirmar();
                kbLeft.confirmar();
                kbRight.confirmar();
                kbReiniciar.confirmar();

                actualizarEstadoGuardar();

                
                new Dialog("Listo", skin)
                        .text("Cambios guardados.")
                        .button("OK", true)
                        .show(stage);
            }
        });

        btnVolver = new TextButton("Regresar", skin);
        btnVolver.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.top().padTop(40f);
        root.add(titulo).padBottom(22f).row();

        Table t = new Table();
        t.defaults().pad(6f);
        t.add(kbUp.lbl).left().padRight(10f);        t.add(kbUp.btn).width(240f).row();
        t.add(kbDown.lbl).left().padRight(10f);      t.add(kbDown.btn).width(240f).row();
        t.add(kbLeft.lbl).left().padRight(10f);      t.add(kbLeft.btn).width(240f).row();
        t.add(kbRight.lbl).left().padRight(10f);     t.add(kbRight.btn).width(240f).row();
        t.add(kbReiniciar.lbl).left().padRight(10f); t.add(kbReiniciar.btn).width(240f).row();

        root.add(t).center().padBottom(16f).row();

        Table botones = new Table();
        botones.defaults().pad(8f).width(180f);
        botones.add(btnVolver);
        botones.add(btnGuardar);
        root.add(botones).center();

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

    private void actualizarEstadoGuardar() {
        boolean cambioValido =
                kbUp.cambioValido() ||
                kbDown.cambioValido() ||
                kbLeft.cambioValido() ||
                kbRight.cambioValido() ||
                kbReiniciar.cambioValido();
        btnGuardar.setDisabled(!cambioValido);
    }

    private int getCfg(String key, int def) {
        try {
            Integer v = ManejoUsuarios.UsuarioActivo.configuracion.get(key);
            return (v != null) ? v : def;
        } catch (Exception e) {
            return def;
        }
    }

    private void putCfg(String key, int value) {
        try {
            ManejoUsuarios.UsuarioActivo.configuracion.put(key, value);
        } catch (Exception ignored) {}
    }

    private class KeyBinder {
        final Label lbl;
        final TextButton btn;
        final String cfgKey;
        int original;
        int actual;
        boolean escuchando = false;

        KeyBinder(String nombre, String cfgKey, int valor) {
            this.lbl = new Label(nombre, skin);
            this.btn = new TextButton(Input.Keys.toString(valor), skin);
            this.cfgKey = cfgKey;
            this.original = valor;
            this.actual = valor;

            btn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    escuchando = true;
                    btn.setText("Presiona una tecla...");
                }
            });
        }

        boolean tryCapture(int keycode) {
            if (!escuchando) return false;

            if (keycode == Input.Keys.ESCAPE) {
                escuchando = false;
                btn.setText(Input.Keys.toString(actual));
                return true;
            }

            actual = keycode;
            btn.setText(Input.Keys.toString(actual));
            escuchando = false;
            return true;
        }

        boolean cambioValido() { return actual != original; }
        void confirmar() { original = actual; }
        int getActual() { return actual; }
    }

    private Skin buildMinimalSkin() {
        Skin s = new Skin();

        BitmapFont font = new BitmapFont();
        s.add("default-font", font, BitmapFont.class);

        Pixmap px = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        px.setColor(Color.WHITE);
        px.fill();
        Texture white = new Texture(px);
        px.dispose();
        s.add("white", white);

        Label.LabelStyle ls = new Label.LabelStyle();
        ls.font = font;
        ls.fontColor = Color.WHITE;
        s.add("default", ls);

        TextButton.TextButtonStyle bs = new TextButton.TextButtonStyle();
        bs.font = font;
        bs.up   = s.newDrawable("white", new Color(1, 1, 1, 0.15f));
        bs.down = s.newDrawable("white", new Color(1, 1, 1, 0.25f));
        bs.over = s.newDrawable("white", new Color(1, 1, 1, 0.20f));
        bs.fontColor = Color.WHITE;
        s.add("default", bs);

        return s;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (skin != null) skin.dispose();
    }
}
