// logica/DatosNivel.java
package logica;

public class DatosNivel {

    private final MapaNivel mapa;
    private final int inicioX;
    private final int inicioY;

    public DatosNivel(MapaNivel mapa, int inicioX, int inicioY) {
        this.mapa = mapa;
        this.inicioX = inicioX;
        this.inicioY = inicioY;
    }

    public MapaNivel obtenerMapa() {
        return mapa;
    }

    public int obtenerInicioX() {
        return inicioX;
    }

    public int obtenerInicioY() {
        return inicioY;
    }
}
