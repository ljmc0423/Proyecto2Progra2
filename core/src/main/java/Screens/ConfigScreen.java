package Screens;

import com.badlogic.gdx.Game;
import static com.badlogic.gdx.Gdx.app;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;

public class ConfigScreen extends BaseScreen {

    private static final String PREFS_NAME = "sokoban_prefs";
    private static final String KEY_VOLUME = "volume";
    private static final float DEFAULT_VOL = 0.6f;

    private final Game game;
    private Skin skin;
    private Preferences prefs;

    public ConfigScreen(Game game) { this.game = game; }

    public static float getStoredVolume() {
        return app.getPreferences(PREFS_NAME).getFloat(KEY_VOLUME, DEFAULT_VOL);
    }

    @Override
    protected void onShow() {
        prefs = app.getPreferences(PREFS_NAME);
        float initialVolume = prefs.getFloat(KEY_VOLUME, DEFAULT_VOL);

        // === Teclas: se leen DIRECTO del HashMap del UsuarioActivo ===
        // Asumo que ManejoUsuarios.UsuarioActivo.configuraciones != null y contiene ints (KeyCodes)
        int kUp    = getCfg("MoverArriba", Input.Keys.UP);
        int kDown  = getCfg("MoverAbajo",  Input.Keys.DOWN);
        int kLeft  = getCfg("MoverIzq",    Input.Keys.LEFT);
        int kRight = getCfg("MoverDer",    Input.Keys.RIGHT);

        skin = buildMinimalSkin();

        Label title = new Label("Configuraciones", skin);
        title.setFontScale(1.2f);

        // Volumen (igual que antes)
        Label volumeLbl = new Label("Volumen", skin);
        Label valueLbl  = new Label(Math.round(initialVolume * 100) + "%", skin);

        Slider volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumeSlider.setValue(initialVolume);
        volumeSlider.addListener((event) -> {
            float v = volumeSlider.getValue();
            valueLbl.setText(Math.round(v * 100) + "%");
            prefs.putFloat(KEY_VOLUME, v).flush();
            return false;
        });

        TextButton backBtn = new TextButton("Volver", skin);
        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                prefs.flush();
                game.setScreen(new MenuScreen(game));
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.top().padTop(40f);
        root.add(title).padBottom(32f).row();

        // Fila volumen
        Table rowVolume = new Table();
        rowVolume.defaults().pad(8f);
        rowVolume.add(volumeLbl).left().padRight(12f);
        rowVolume.add(volumeSlider).width(300f);
        rowVolume.add(valueLbl).width(60f).right();
        root.add(rowVolume).center().padBottom(24f).row();

        // Controles
        Label controlsTitle = new Label("Controles", skin);
        root.add(controlsTitle).padBottom(8f).row();

        Table keysTable = new Table();
        keysTable.defaults().pad(6f);
        root.add(keysTable).center().row();

        KeyBinder binderUp    = new KeyBinder("Mover arriba",    kUp,    "MoverArriba");
        KeyBinder binderDown  = new KeyBinder("Mover abajo",     kDown,  "MoverAbajo");
        KeyBinder binderLeft  = new KeyBinder("Mover izquierda", kLeft,  "MoverIzq");
        KeyBinder binderRight = new KeyBinder("Mover derecha",   kRight, "MoverDer");

        keysTable.add(binderUp.name).left().padRight(10f);
        keysTable.add(binderUp.button).width(220f).row();

        keysTable.add(binderDown.name).left().padRight(10f);
        keysTable.add(binderDown.button).width(220f).row();

        keysTable.add(binderLeft.name).left().padRight(10f);
        keysTable.add(binderLeft.button).width(220f).row();

        keysTable.add(binderRight.name).left().padRight(10f);
        keysTable.add(binderRight.button).width(220f).row();

        root.add(backBtn).padTop(28f);

        // Captura global de tecla (si algún KeyBinder está escuchando)
        stage.addListener(new InputListener() {
            @Override public boolean keyDown(InputEvent event, int keycode) {
                boolean handled = false;
                handled |= binderUp.tryCapture(keycode);
                handled |= binderDown.tryCapture(keycode);
                handled |= binderLeft.tryCapture(keycode);
                handled |= binderRight.tryCapture(keycode);
                return handled;
            }
        });
    }

    // Lee una tecla desde el HashMap directo de UsuarioActivo, con default
    private int getCfg(String key, int def) {
        try {
            Integer v = ManejoUsuarios.UsuarioActivo.configuracion.get(key);
            return (v != null) ? v : def;
        } catch (Exception e) {
            return def;
        }
    }

    // Capturador que escribe directo en ManejoUsuarios.UsuarioActivo.configuraciones
    private class KeyBinder {
        final Label name;
        final TextButton button;
        final String cfgKey;
        boolean listening = false;
        int value;

        KeyBinder(String label, int initialValue, String cfgKey) {
            this.name = new Label(label, skin);
            this.button = new TextButton(Input.Keys.toString(initialValue), skin);
            this.cfgKey = cfgKey;
            this.value = initialValue;

            button.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    listening = true;
                    button.setText("Presiona una tecla...");
                }
            });
        }

        boolean tryCapture(int keycode) {
            if (!listening) return false;

            // (Opcional) cancelar con ESC
            if (keycode == Input.Keys.ESCAPE) {
                listening = false;
                button.setText(Input.Keys.toString(value));
                return true;
            }

            value = keycode;
            button.setText(Input.Keys.toString(value));
            try {
                ManejoUsuarios.UsuarioActivo.configuracion.put(cfgKey, value);
            } catch (Exception ignored) { /* si por alguna razón es null, no rompemos la UI */ }
            listening = false;
            return true;
        }
    }

    // === Skin minimal idéntico al tuyo ===
    private Skin buildMinimalSkin() {
        Skin s = new Skin();

        BitmapFont font = new BitmapFont();
        s.add("default-font", font, BitmapFont.class);

        Pixmap px = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        px.setColor(Color.WHITE);
        px.fill();
        Texture white = new Texture(px);
        px.dispose();
        s.add("white", white);

        Label.LabelStyle ls = new Label.LabelStyle();
        ls.font = font;
        ls.fontColor = Color.WHITE;
        s.add("default", ls);

        Slider.SliderStyle ss = new Slider.SliderStyle();
        ss.background  = s.newDrawable("white", new Color(1, 1, 1, 0.12f));
        ss.knob        = s.newDrawable("white", Color.WHITE);
        ss.knobBefore  = s.newDrawable("white", new Color(1, 1, 1, 0.35f));
        ss.background.setMinHeight(16f);
        ss.knob.setMinHeight(24f);
        ss.knob.setMinWidth(24f);
        s.add("default-horizontal", ss);

        TextButton.TextButtonStyle bs = new TextButton.TextButtonStyle();
        bs.font = font;
        bs.up   = s.newDrawable("white", new Color(1, 1, 1, 0.12f));
        bs.down = s.newDrawable("white", new Color(1, 1, 1, 0.20f));
        bs.over = s.newDrawable("white", new Color(1, 1, 1, 0.16f));
        bs.fontColor = Color.WHITE;
        s.add("default", bs);

        return s;
    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
    }
}
