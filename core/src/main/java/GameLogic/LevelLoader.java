package GameLogic;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class LevelLoader {

    private final File level;

    public LevelLoader(String ruta) {
        this.level = new File(ruta);
    }

    public char[][] LevelCharData() {
        if (!level.exists()) return null;

        ArrayList<char[]> filas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(level))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                filas.add(linea.toCharArray());
            }
        } catch (IOException e) {
            return null;
        }

        char data[][] = new char[filas.size()][];
        for (int i = 0; i < filas.size(); i++) {
            data[i] = filas.get(i);
        }
        return data;
    }
}
