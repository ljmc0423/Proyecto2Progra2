package main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Usuario {

    private String nombre;
    private String usuario;
    private String contrasena;

    private Long fechaRegistro;
    private Long ultimaSesion;
    public  Long sesionActual;
    public  Long sesionAnterior;

    // Por nivel (1..7) indexado con nivel-1
    private ArrayList<Integer> mayorPuntuacion = new ArrayList<>();     // menor cantidad de pasos
    private ArrayList<Boolean> nivelesCompletados = new ArrayList<>();
    private ArrayList<Integer> tiempoPorNivel = new ArrayList<>();      // suma de tiempos (s) en partidas ganadas
    private ArrayList<Integer> partidasPorNivel = new ArrayList<>();    // # partidas ganadas por nivel
    private ArrayList<Integer> mejorTiempoPorNivel = new ArrayList<>(); // menor tiempo (s) por nivel

    private int tiempoJugadoTotal = 0;
    private Boolean Tutocomplete;

    public Map<String, Integer> configuracion = new HashMap<>();
    public String avatar;

    private int puntuacionGeneral = 0;

    private ArrayList<Usuario> amigos = new ArrayList<>();
    private int[] tiempoPromedio = new int[7]; // promedio por nivel en segundos

    public ArrayList<Partida> historial = new ArrayList<>();

    private int partidasTotales = 0;

    public Usuario(String usuario, String nombre, String contrasena, Long fechas) {
        this.fechaRegistro = fechas;
        this.ultimaSesion  = fechas;
        this.sesionAnterior = fechas;
        this.sesionActual  = Calendar.getInstance().getTimeInMillis();

        this.usuario = usuario;
        this.nombre  = nombre;
        this.contrasena = contrasena;

        for (int i = 0; i < 7; i++) mayorPuntuacion.add(0);
        for (int i = 0; i < 7; i++) nivelesCompletados.add(false);
        for (int i = 0; i < 7; i++) tiempoPorNivel.add(0);
        for (int i = 0; i < 7; i++) partidasPorNivel.add(0);
        for (int i = 0; i < 7; i++) mejorTiempoPorNivel.add(0);

        configuracion.put("Volumen", 80);
        configuracion.put("MoverArriba", 19);
        configuracion.put("MoverAbajo", 20);
        configuracion.put("MoverDer", 22);
        configuracion.put("MoverIzq", 21);
        configuracion.put("Reiniciar", 46);
        configuracion.put("Idioma", 1);

        for (int i = 0; i < tiempoPromedio.length; i++) tiempoPromedio[i] = 0;
    }

    public void recalcularTiempoPromedio() {
        for (int i = 0; i < 7; i++) {
            int partidas = partidasPorNivel.get(i);
            int tiempo = tiempoPorNivel.get(i);
            tiempoPromedio[i] = (partidas > 0) ? (tiempo / partidas) : 0;
        }
    }

    public Boolean getTutocomplete() { return Tutocomplete; }
    public void setTutocomplete(Boolean tutocomplete) { this.Tutocomplete = tutocomplete; }

    public void recalcularTiempoPromedioNivel(int nivel) {
        int idx = nivel - 1;
        int partidas = partidasPorNivel.get(idx);
        int tiempo = tiempoPorNivel.get(idx);
        tiempoPromedio[idx] = (partidas > 0) ? (tiempo / partidas) : 0;
    }

    public int getTiempoPromedioNivel(int nivel) {
        return tiempoPromedio[nivel - 1];
    }

    public int[] getTiemposPromedio() { return tiempoPromedio; }

    // Sumar tiempo de una partida GANADA de "nivel" y actualizar promedio
    public void acumularPartida(int nivel, int tiempoSegundos) {
        int idx = nivel - 1;
        tiempoPorNivel.set(idx, tiempoPorNivel.get(idx) + tiempoSegundos);
        partidasPorNivel.set(idx, partidasPorNivel.get(idx) + 1);
        partidasTotales++;
        tiempoJugadoTotal += tiempoSegundos;
        recalcularTiempoPromedioNivel(nivel);
    }

    // Mejor tiempo por nivel (min en segundos). 0 => no existe aún
    public int getMejorTiempoPorNivel(int nivel) { return mejorTiempoPorNivel.get(nivel - 1); }
    public void setMejorTiempoPorNivel(int nivel, int v) { mejorTiempoPorNivel.set(nivel - 1, v); }

    public int getPartidasTotales() { return partidasTotales; }
    public void setPartidasTotales(int v) { this.partidasTotales = v; }

    public int getPuntuacionGeneral() { return puntuacionGeneral; }
    public void setPuntuacionGeneral(int v) { this.puntuacionGeneral = v; }

    public int getTiempoJugadoTotal() { return tiempoJugadoTotal; }
    public void setTiempoJugadoTotal(int v) { this.tiempoJugadoTotal = v; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String v) { this.usuario = v; }

    public String getNombre() { return nombre; }
    public void setNombre(String v) { this.nombre = v; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String v) { this.contrasena = v; }

    public Long getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Long v) { this.fechaRegistro = v; }

    public Long getUltimaSesion() { return ultimaSesion; }
    public String getUltimaSesionTexto() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(ultimaSesion);
    }
    public void setUltimaSesion(Long v){
        this.ultimaSesion = v;
        this.sesionAnterior = v;
    }

    public void setNivelCompletado(int nivel, Boolean estado){ nivelesCompletados.set(nivel - 1, estado); }
    public boolean getNivelCompletado(int nivel){ return nivelesCompletados.get(nivel - 1); }

    public void setMayorPuntuacion(int nivel, int puntuacion){ mayorPuntuacion.set(nivel - 1, puntuacion); }
    public int getMayorPuntuacion(int nivel){ return mayorPuntuacion.get(nivel - 1); }

    public void setPartidasPorNivel(int nivel, int partidas){ partidasPorNivel.set(nivel - 1, partidas); }
    public int getPartidasPorNivel(int nivel){ return partidasPorNivel.get(nivel - 1); }

    public void setTiempoPorNivel(int nivel, int tiempo){ tiempoPorNivel.set(nivel - 1, tiempo); }
    public int getTiempoPorNivel(int nivel){ return tiempoPorNivel.get(nivel - 1); }

    public void setConfiguracion(String clave, int valor){ configuracion.put(clave, valor); }
    public int getConfiguracion(String clave){
        Integer v = configuracion.get(clave);
        return (v == null) ? 0 : v;
    }

    public void setAvatar(String path){ this.avatar = path; }
}