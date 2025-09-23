// logica/MapaNivel.java
package logica;

public class MapaNivel {

    private char[][] mapa;

    public MapaNivel(char[][] datos) {
        this.mapa = datos;
    }

    public char obtenerTile(int x, int y) {
        return mapa[y][x];
    }

    public void colocarTile(int x, int y, char tile) {
        mapa[y][x] = tile;
    }

    public boolean esPared(int x, int y) {
        return mapa[y][x] == ConstantesJuego.PARED;
    }

    public boolean esLibre(int x, int y) {
        char t = mapa[y][x];
        return t == ConstantesJuego.SUELO || t == ConstantesJuego.META || t == ConstantesJuego.ASCENSOR;
    }

    public boolean esCaja(int x, int y) {
        char t = mapa[y][x];
        return t == ConstantesJuego.CAJA || t == ConstantesJuego.CAJA_EN_META;
    }

    public boolean dentroDeLimites(int x, int y) {
        return x >= 0 && x < ConstantesJuego.COLUMNAS && y >= 0 && y < ConstantesJuego.FILAS;
    }
}
