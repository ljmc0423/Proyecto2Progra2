package pantallas;

import static com.badlogic.gdx.Gdx.files;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.elkinedwin.LogicaUsuario.ManejoUsuarios;
import main.Partida;

import java.util.List;

public class HistorialScreen extends BaseScreen {

    private final Game game;

    private FreeTypeFontGenerator generator;
    private BitmapFont titleFont, h2Font, bodyFont, smallFont;

    private Texture texPanelBg, texDivider;

    public HistorialScreen(Game game) { this.game = game; }

    @Override
    protected void onShow() {
        // Fuentes similares a MiPerfilScreen (mismo archivo)
        generator = new FreeTypeFontGenerator(files.internal("fonts/pokemon_fire_red.ttf"));
        titleFont = genFont(60, "000000");
        h2Font    = genFont(30, "000000");
        bodyFont  = genFont(20, "000000");
        smallFont = genFont(14, "000000");

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        Label.LabelStyle h2Style    = new Label.LabelStyle(h2Font,    h2Font.getColor());
        Label.LabelStyle keyStyle   = new Label.LabelStyle(bodyFont,  Color.BLACK);
        Label.LabelStyle valStyle   = new Label.LabelStyle(bodyFont,  Color.BLACK);

        texPanelBg  = makeColorTex(255, 255, 255, 22);
        texDivider  = makeColorTex(255, 255, 255, 38);

        TextureRegionDrawable panelBg   = new TextureRegionDrawable(new TextureRegion(texPanelBg));
        TextureRegionDrawable dividerBg = new TextureRegionDrawable(new TextureRegion(texDivider));

        // Barra superior con botón volver
        TextButton.TextButtonStyle backStyle = new TextButton.TextButtonStyle();
        backStyle.font = bodyFont;
        backStyle.fontColor = Color.BLACK;
        Texture transparent = makeColorTex(0,0,0,0);
        TextureRegionDrawable trd = new TextureRegionDrawable(new TextureRegion(transparent));
        backStyle.up = trd; backStyle.over = trd; backStyle.down = trd; backStyle.checked = trd; backStyle.disabled = trd;

        TextButton btnBack = new TextButton("Volver", backStyle);
        btnBack.getLabel().setColor(Color.BLACK);
        btnBack.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Table topbar = new Table();
        topbar.add(btnBack).left().pad(12f);
        root.add(topbar).expandX().fillX().row();

        // Contenido scrolleable
        Table content = new Table();
        content.top().pad(24f);
        content.defaults().pad(10f);

        ScrollPane sp = new ScrollPane(content);
        sp.setFadeScrollBars(false);
        root.add(sp).expand().fill().pad(0, 16f, 16f, 16f).row();

        // Título
        content.add(new Label("HISTORIAL", titleStyle)).center().padBottom(10f).row();

        // Tarjeta
        Table card = new Table();
        card.setBackground(panelBg);
        card.pad(14f);
        card.defaults().left().pad(8f);
        content.add(card).expandX().fillX().row();

        List<Partida> historial = (ManejoUsuarios.UsuarioActivo != null)
                ? ManejoUsuarios.UsuarioActivo.historial
                : null;

        if (historial == null || historial.isEmpty()) {
            Label empty = new Label("Sin partidas registradas", new Label.LabelStyle(h2Font, Color.BLACK));
            card.add(empty).left().pad(12f).row();
        } else {
            // Encabezados
            Table header = new Table();
            header.defaults().left().pad(6f);
            header.add(new Label("Fecha", keyStyle)).width(280f);
            header.add(new Label("Nivel", keyStyle)).width(120f);
            header.add(new Label("Tiempo", keyStyle)).width(160f);
            header.add(new Label("Intentos", keyStyle)).width(140f);
            header.add(new Label("Logros", keyStyle)).growX().row();
            card.add(header).expandX().fillX().row();

            Image divTop = new Image(dividerBg);
            card.add(divTop).height(1.2f).expandX().fillX().row();

            for (int i = historial.size() - 1; i >= 0; i--) { // más reciente primero
                Partida p = historial.get(i);

                String fecha = safeText(p.getFecha());
                String nivel = String.valueOf(p.getNivel());
                String tiempo = formatHuman(p.getTiempo());
                String intentos = String.valueOf(p.getIntentos());
                String logros = safeText(p.getLogros());

                Table row = new Table();
                row.defaults().left().pad(6f);
                row.add(new Label(fecha, valStyle)).width(280f);
                row.add(new Label(nivel, valStyle)).width(120f);
                row.add(new Label(tiempo, valStyle)).width(160f);
                row.add(new Label(intentos, valStyle)).width(140f);

                // Logros: multilínea bonito
                Label lLogros = new Label(logros, valStyle);
                lLogros.setWrap(true);
                row.add(lLogros).growX().width(520f).row();

                card.add(row).expandX().fillX().row();

                if (i > 0) {
                    Image div = new Image(dividerBg);
                    card.add(div).height(1.0f).expandX().fillX().row();
                }
            }
        }
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

    private String formatHuman(int sec){
        if (sec <= 0) return "0s";
        int h = sec / 3600;
        int m = (sec % 3600) / 60;
        int s = sec % 60;
        if (h > 0) return String.format("%d:%02d:%02d", h, m, s);
        return String.format("%02d:%02d", m, s);
    }

    private String safeText(String s){
        if (s == null || s.trim().isEmpty()) return "-";
        return s;
    }

    @Override
    public void hide() {
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
        if (texPanelBg != null) texPanelBg.dispose();
        if (texDivider != null) texDivider.dispose();
    }
}
