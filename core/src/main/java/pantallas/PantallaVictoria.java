// Screens/PantallaVictoria.java
package pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Input.Keys;

public class PantallaVictoria implements Screen {

    private final Game app;
    private final int nivel, pasos, empujes, segundos;
    private final BitmapFont fuente;

    private SpriteBatch lote;
    private ShapeRenderer forma;

    // animación confeti
    private static class Particula {
        float x, y, vx, vy, tamaño;
        Color color;
    }

    private final Array<Particula> confeti = new Array<>();

    private float tiempoAnimacion = 0f;
    private float factorZoom = 0f;
    private float barrasAnimadas = 0f;

    public PantallaVictoria(Game app, int nivel, int pasos, int empujes, int segundos) {
        this.app = app;
        this.nivel = nivel;
        this.pasos = pasos;
        this.empujes = empujes;
        this.segundos = segundos;
        this.fuente = new BitmapFont();
    }

    @Override
    public void show() {
        lote = new SpriteBatch();
        forma = new ShapeRenderer();

        // inicializar confeti
        for (int i = 0; i < 150; i++) {
            Particula p = new Particula();
            p.x = MathUtils.random(0, 512);
            p.y = MathUtils.random(0, 288);
            p.vx = MathUtils.random(-50f, 50f);
            p.vy = MathUtils.random(50f, 150f);
            p.tamaño = MathUtils.random(4f, 8f);
            p.color = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f);
            confeti.add(p);
        }
    }

    @Override
    public void render(float delta) {
        tiempoAnimacion += delta;

        actualizarConfeti(delta);
        if (factorZoom < 1f) factorZoom += delta * 2f;
        if (barrasAnimadas < 1f) barrasAnimadas += delta * 1.5f;

        // fondo oscuro
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        // confeti
        forma.begin(ShapeRenderer.ShapeType.Filled);
        for (Particula p : confeti) {
            forma.setColor(p.color);
            forma.circle(p.x, p.y, p.tamaño);
        }
        forma.end();

        // panel de stats y texto
        lote.begin();
        // título épico con zoom
        fuente.getData().setScale(2f * factorZoom);
        fuente.setColor(Color.GOLD);
        fuente.draw(lote, "¡VICTORIA ÉPICA!", 100, 250);

        // stats
        fuente.getData().setScale(1f);
        fuente.setColor(Color.WHITE);

        float barraMax = 200f;

        fuente.draw(lote, "Nivel: " + nivel, 50, 200);

        // barra pasos
        float barraPasos = barraMax * barrasAnimadas * Math.min(1f, pasos / 50f);
        forma.begin(ShapeRenderer.ShapeType.Filled);
        forma.setColor(Color.RED);
        forma.rect(50, 180, barraPasos, 10);
        forma.setColor(Color.WHITE);
        forma.rect(50, 180, barraMax, 10);
        forma.end();
        fuente.draw(lote, "Pasos: " + pasos, 50, 175);

        // barra empujes
        float barraEmpujes = barraMax * barrasAnimadas * Math.min(1f, empujes / 50f);
        forma.begin(ShapeRenderer.ShapeType.Filled);
        forma.setColor(Color.BLUE);
        forma.rect(50, 150, barraEmpujes, 10);
        forma.setColor(Color.WHITE);
        forma.rect(50, 150, barraMax, 10);
        forma.end();
        fuente.draw(lote, "Empujes: " + empujes, 50, 145);

        // tiempo
        int min = segundos / 60;
        int seg = segundos % 60;
        fuente.draw(lote, String.format("Tiempo: %02d:%02d", min, seg), 50, 120);

        // instrucciones
        fuente.setColor(Color.YELLOW);
        fuente.draw(lote, "[ENTER] Siguiente nivel  [ESC] Volver al menú", 50, 50);

        lote.end();

        // entrada
        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            app.setScreen(new PantallaNivel(app, nivel + 1));
        }
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            // volver al menú principal
        }
    }

    private void actualizarConfeti(float delta) {
        for (Particula p : confeti) {
            p.x += p.vx * delta;
            p.y += p.vy * delta;

            if (p.y > 288) {
                p.y = 0;
                p.x = MathUtils.random(0, 512);
            }
            if (p.x < 0) p.x = 512;
            if (p.x > 512) p.x = 0;
        }
    }

    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        lote.dispose();
        forma.dispose();
        fuente.dispose();
    }
}
