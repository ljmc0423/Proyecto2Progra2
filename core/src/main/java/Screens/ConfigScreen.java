package Screens;

import com.badlogic.gdx.Game;
import static com.badlogic.gdx.Gdx.app;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ConfigScreen extends BaseScreen {

    private static final String PREFS_NAME = "sokoban_prefs";
    private static final String KEY_VOLUME = "volume";
    private static final float DEFAULT_VOLUME = 0.6f;

    private final Game game;
    private Skin skin;
    private Preferences prefs;

    public ConfigScreen(Game game) {
        this.game = game;
    }

    public static float getStoredVolume() {
        return app.getPreferences(PREFS_NAME).getFloat(KEY_VOLUME, DEFAULT_VOLUME);
    }

    @Override
    protected void onShow() {
        prefs = app.getPreferences(PREFS_NAME);
        float initialVolume = prefs.getFloat(KEY_VOLUME, DEFAULT_VOLUME);

        skin = buildMinimalSkin();

        Label title = new Label("Configuraciones", skin);
        title.setFontScale(1.2f);

        Label volumeLbl = new Label("Volumen", skin);
        Label valueLbl = new Label(Math.round(initialVolume * 100) + "%", skin);

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
            @Override
            public void clicked(InputEvent event, float x, float y) {
                prefs.flush();
                game.setScreen(new MenuScreen(game));
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.top().padTop(40f);
        root.add(title).padBottom(50f).row();

        Table row = new Table();
        row.defaults().pad(8f);
        row.add(volumeLbl).left().padRight(12f);
        row.add(volumeSlider).width(300f);
        row.add(valueLbl).width(60f).right();

        root.add(row).center().row();
        root.add(backBtn).padTop(28f);
    }

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
        ss.background = s.newDrawable("white", new Color(1, 1, 1, 0.12f));
        ss.knob = s.newDrawable("white", Color.WHITE);
        ss.knobBefore = s.newDrawable("white", new Color(1, 1, 1, 0.35f));
        ss.background.setMinHeight(16f);
        ss.knob.setMinHeight(24f);
        ss.knob.setMinWidth(24f);
        s.add("default-horizontal", ss);

        TextButton.TextButtonStyle bs = new TextButton.TextButtonStyle();
        bs.font = font;
        bs.up = s.newDrawable("white", new Color(1, 1, 1, 0.12f));
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
