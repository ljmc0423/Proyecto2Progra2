// Screens/PantallaNivel.java
package pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import logica.ConstantesJuego;
import logica.JuegoSokoban;
import logica.MapaNivel;

public class PantallaNivel implements Screen {

    private final Game app;
    private final int idNivel;
    private final JuegoSokoban juego;
    private SpriteBatch lote;
    private ShapeRenderer forma;
    private BitmapFont fuente;

    private Texture jugadorTex, cajaTex, cajaMetaTex, sueloTex, paredTex, metaTex;
    private float parpadeoTiempo = 0f;

    public PantallaNivel(Game app, int idNivel) {
        this.app = app;
        this.idNivel = idNivel;
        this.juego = new JuegoSokoban();
    }

    @Override
    public void show() {
        lote = new SpriteBatch();
        forma = new ShapeRenderer();
        fuente = new BitmapFont();

        jugadorTex = new Texture("texturas/jugador.png");
        cajaTex = new Texture("texturas/caja.png");
        cajaMetaTex = new Texture("texturas/caja_meta.png");
        sueloTex = new Texture("texturas/suelo.png");
        paredTex = new Texture("texturas/pared.png");
        metaTex = new Texture("texturas/meta.png");

        juego.iniciarNivel(idNivel);
    }

    @Override
    public void render(float delta) {
        handleInput();
        juego.procesarMovimientos();

        parpadeoTiempo += delta;

        ScreenUtils.clear(Color.DARK_GRAY);
        lote.begin();
        dibujarMapa();
        dibujarHUD();
        lote.end();
    }

    private void dibujarMapa() {
        MapaNivel mapa = juego.obtenerMapa();

        for (int y = 0; y < ConstantesJuego.FILAS; y++) {
            for (int x = 0; x < ConstantesJuego.COLUMNAS; x++) {
                int px = x * ConstantesJuego.TAM_TILE;
                int py = (ConstantesJuego.FILAS - 1 - y) * ConstantesJuego.TAM_TILE; // invertir eje y

                char tile = mapa.obtenerTile(x, y);
                switch (tile) {
                    case ConstantesJuego.SUELO:
                        lote.draw(sueloTex, px, py, ConstantesJuego.TAM_TILE, ConstantesJuego.TAM_TILE);
                        break;
                    case ConstantesJuego.PARED:
                        lote.draw(paredTex, px, py, ConstantesJuego.TAM_TILE, ConstantesJuego.TAM_TILE);
                        break;
                    case ConstantesJuego.META:
                        lote.draw(metaTex, px, py, ConstantesJuego.TAM_TILE, ConstantesJuego.TAM_TILE);
                        break;
                    case ConstantesJuego.CAJA:
                        lote.draw(cajaTex, px, py, ConstantesJuego.TAM_TILE, ConstantesJuego.TAM_TILE);
                        break;
                    case ConstantesJuego.CAJA_EN_META:
                        lote.draw(cajaMetaTex, px, py, ConstantesJuego.TAM_TILE, ConstantesJuego.TAM_TILE);
                        break;
                    case ConstantesJuego.JUGADOR:
                    case ConstantesJuego.JUGADOR_EN_META:
                        // dibuja suelo debajo del jugador
                        lote.draw(sueloTex, px, py, ConstantesJuego.TAM_TILE, ConstantesJuego.TAM_TILE);
                        // parpadeo si está en meta
                        if (tile == ConstantesJuego.JUGADOR_EN_META && (int)(parpadeoTiempo*2)%2 == 0) {
                            lote.draw(jugadorTex, px, py, ConstantesJuego.TAM_TILE, ConstantesJuego.TAM_TILE);
                        } else if(tile == ConstantesJuego.JUGADOR) {
                            lote.draw(jugadorTex, px, py, ConstantesJuego.TAM_TILE, ConstantesJuego.TAM_TILE);
                        }
                        break;
                    default:
                        lote.draw(sueloTex, px, py, ConstantesJuego.TAM_TILE, ConstantesJuego.TAM_TILE);
                        break;
                }
            }
        }

        // dibujar jugador real
        int jugadorX = juego.obtenerXJugador();
        int jugadorY = juego.obtenerYJugador();
        int px = jugadorX * ConstantesJuego.TAM_TILE;
        int py = (ConstantesJuego.FILAS - 1 - jugadorY) * ConstantesJuego.TAM_TILE;
        lote.draw(jugadorTex, px, py, ConstantesJuego.TAM_TILE, ConstantesJuego.TAM_TILE);
    }

    private void dibujarHUD() {
        String info = "Nivel: " + idNivel +
                "   Pasos: " + juego.obtenerPasosJugador() +
                "   Empujes: " + juego.obtenerEmpujesJugador();
        fuente.draw(lote, info, 10, ConstantesJuego.PIXELES_ALTO - 10);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Keys.R)) {
            juego.iniciarNivel(idNivel);
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            juego.agregarMovimiento(0, -1);
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            juego.agregarMovimiento(0, 1);
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            juego.agregarMovimiento(-1, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            juego.agregarMovimiento(1, 0);
        }

        if (juego.hayVictoria()) {
            app.setScreen(new PantallaVictoria(app, idNivel, juego.obtenerPasosJugador(),
                    juego.obtenerEmpujesJugador(), 0)); // tiempo fijo o cronometrado
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
        jugadorTex.dispose();
        cajaTex.dispose();
        cajaMetaTex.dispose();
        sueloTex.dispose();
        paredTex.dispose();
        metaTex.dispose();
    }
}
