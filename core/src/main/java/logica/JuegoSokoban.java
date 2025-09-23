// logica/JuegoSokoban.java
package logica;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JuegoSokoban {

    private MapaNivel mapa;
    private int jugadorX;
    private int jugadorY;
    private int pasosJugador = 0;
    private int empujesJugador = 0;

    private int nivelActual = 0;
    private boolean victoria;

    private BlockingQueue<int[]> colaMovimientos = new LinkedBlockingQueue<>();

    // ----------------------------
    // Métodos públicos
    // ----------------------------
    public void iniciarNivel(int id) {
        this.nivelActual = id;
        String ruta = "niveles/nivel0" + id + ".txt";
        CargadorNivel cargador = new CargadorNivel(ruta);
        try {
            DatosNivel datos = cargador.cargar();
            this.mapa = datos.obtenerMapa();
            this.jugadorX = datos.obtenerInicioX();
            this.jugadorY = datos.obtenerInicioY();
            this.pasosJugador = 0;
            this.empujesJugador = 0;
            this.victoria = verificarVictoria();
        } catch (IOException e) {
            throw new RuntimeException("Error cargando nivel: " + ruta, e);
        }
    }

    public void agregarMovimiento(int dx, int dy) {
        colaMovimientos.offer(new int[]{dx, dy});
    }

    public void procesarMovimientos() {
        new Thread(() -> {
            while (true) {
                try {
                    int[] mov = colaMovimientos.take();
                    aplicarMovimiento(mov[0], mov[1]);
                    Thread.sleep(50); // pausa para simular animación
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    public int obtenerXJugador() { return jugadorX; }
    public int obtenerYJugador() { return jugadorY; }
    public int obtenerPasosJugador() { return pasosJugador; }
    public int obtenerEmpujesJugador() { return empujesJugador; }
    public boolean hayVictoria() { return victoria; }
    public MapaNivel obtenerMapa() { return mapa; }
    public int nivelActual() { return nivelActual; }

    // ----------------------------
    // Lógica interna
    // ----------------------------
    private void aplicarMovimiento(int dx, int dy) {
        int nx = jugadorX + dx;
        int ny = jugadorY + dy;

        if (!mapa.dentroDeLimites(nx, ny) || mapa.esPared(nx, ny)) return;

        if (mapa.esCaja(nx, ny)) {
            int bx = nx + dx;
            int by = ny + dy;
            if (!mapa.dentroDeLimites(bx, by) || !mapa.esLibre(bx, by)) return;

            // empuja caja
            char cajaDestino = mapa.obtenerTile(bx, by) == ConstantesJuego.META ? ConstantesJuego.CAJA_EN_META : ConstantesJuego.CAJA;
            mapa.colocarTile(bx, by, cajaDestino);
            mapa.colocarTile(nx, ny, ConstantesJuego.SUELO);

            // mueve jugador
            jugadorX = nx;
            jugadorY = ny;
            pasosJugador++;
            empujesJugador++;
        } else if (mapa.esLibre(nx, ny)) {
            jugadorX = nx;
            jugadorY = ny;
            pasosJugador++;
        }

        victoria = verificarVictoria();
    }

    private boolean verificarVictoria() {
        for (int y = 0; y < ConstantesJuego.FILAS; y++) {
            for (int x = 0; x < ConstantesJuego.COLUMNAS; x++) {
                char t = mapa.obtenerTile(x, y);
                if (t == ConstantesJuego.CAJA || t == ConstantesJuego.BOTON_ASCENSOR) {
                    return false;
                }
            }
        }
        return true;
    }
}
