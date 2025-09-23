// logica/CargadorNivel.java
package logica;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CargadorNivel {

    private final File archivoNivel;

    public CargadorNivel(String ruta) {
        archivoNivel = new File(ruta);
    }

    public DatosNivel cargar() throws IOException {
        char[][] datos = new char[ConstantesJuego.FILAS][ConstantesJuego.COLUMNAS];
        int inicioX = -1, inicioY = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(archivoNivel))) {
            for (int y = 0; y < ConstantesJuego.FILAS; y++) {
                String linea = br.readLine();
                for (int x = 0; x < ConstantesJuego.COLUMNAS; x++) {
                    char ch = linea.charAt(x);

                    if (ch == ConstantesJuego.JUGADOR || ch == ConstantesJuego.JUGADOR_EN_META) {
                        inicioX = x;
                        inicioY = y;
                        ch = (ch == ConstantesJuego.JUGADOR_EN_META) ? ConstantesJuego.META : ConstantesJuego.SUELO;
                    }

                    datos[y][x] = ch;
                }
            }
        }

        return new DatosNivel(new MapaNivel(datos), inicioX, inicioY);
    }
}
