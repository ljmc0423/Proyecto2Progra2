package main;

public class Partida {
    private String fecha;   // Fecha de la partida
    private int intentos;   // Número de intentos realizados
    private String logros;  // Logros obtenidos
    private int tiempo;     // Duración en segundos
    private int nivel;      // Nivel jugado

    public Partida(String fecha, int intentos, String logros, int tiempo, int nivel) {
        this.fecha = fecha;
        this.intentos = intentos;
        this.logros = logros;
        this.tiempo = tiempo;
        this.nivel = nivel;
    }

    // ----------------------------
    // Getters y setters
    // ----------------------------
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public int getIntentos() { return intentos; }
    public void setIntentos(int intentos) { this.intentos = intentos; }

    public String getLogros() { return logros; }
    public void setLogros(String logros) { this.logros = logros; }

    public int getTiempo() { return tiempo; }
    public void setTiempo(int tiempo) { this.tiempo = tiempo; }

    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }
}
