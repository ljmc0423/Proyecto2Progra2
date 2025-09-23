// logica/ConstantesJuego.java
package logica;

public final class ConstantesJuego {

    public static final int TAM_TILE = 16;

    public static final int COLUMNAS = 32;
    public static final int FILAS = 18;

    public static final int PIXELES_ANCHO = TAM_TILE * COLUMNAS;
    public static final int PIXELES_ALTO = TAM_TILE * FILAS;

    // Representación de tiles
    public static final char PARED = '#';
    public static final char SUELO = '.';
    public static final char META = '+';
    public static final char CAJA = '$';
    public static final char JUGADOR = '@';
    public static final char CAJA_EN_META = '*';
    public static final char JUGADOR_EN_META = '&';
    public static final char OBSTACULO = '!';
    public static final char ASCENSOR = '?';
    public static final char BOTON_ASCENSOR = '(';

    private ConstantesJuego() {}
}
