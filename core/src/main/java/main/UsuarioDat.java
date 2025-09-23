package main;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;

public class UsuarioDat {

    // Datos básicos
    private String nombre;
    private String usuario;
    private String contrasena;
    public String avatar;

    private long fechaRegistro;
    private long ultimaSesion;
    public long sesionActual;
    public long sesionAnterior;

    // Progreso y estadísticas
    private ArrayList<Integer> mayorPuntuacion = new ArrayList<>();
    private ArrayList<Boolean> nivelesCompletados = new ArrayList<>();
    private ArrayList<Integer> tiempoPorNivel = new ArrayList<>();
    private ArrayList<Integer> partidasPorNivel = new ArrayList<>();
    private ArrayList<Integer> mejorTiempoPorNivel = new ArrayList<>();
    private int tiempoJugadoTotal = 0;
    private boolean tutoCompleto = false;

    // Configuración
    public int volumen = 80;
    public int moverArriba = 19;
    public int moverAbajo  = 20;
    public int moverDer    = 22;
    public int moverIzq    = 21;
    public int reiniciar   = 46;
    public int idioma      = 1;

    // Historial de partidas
    public ArrayList<Partida> historial = new ArrayList<>();

    private RandomAccessFile archivo;

    public UsuarioDat(String rutaArchivo) throws IOException {
        archivo = new RandomAccessFile(rutaArchivo, "rw");
        if (archivo.length() == 0) {
            inicializarDatos();
            guardarTodo();
        } else {
            cargarTodo();
        }
    }

    private void inicializarDatos() {
        long ahora = Calendar.getInstance().getTimeInMillis();
        fechaRegistro = ahora;
        ultimaSesion = ahora;
        sesionActual = ahora;
        sesionAnterior = 0L;

        for (int i = 0; i < 7; i++) {
            mayorPuntuacion.add(0);
            nivelesCompletados.add(false);
            tiempoPorNivel.add(0);
            partidasPorNivel.add(0);
            mejorTiempoPorNivel.add(0);
        }
    }

    // --------------------------------------
    // Guardar y cargar todo de forma secuencial
    // --------------------------------------
    public void guardarTodo() throws IOException {
        archivo.seek(0);

        archivo.writeUTF(nombre == null ? "" : nombre);
        archivo.writeUTF(usuario == null ? "" : usuario);
        archivo.writeUTF(contrasena == null ? "" : contrasena);
        archivo.writeUTF(avatar == null ? "" : avatar);

        archivo.writeLong(fechaRegistro);
        archivo.writeLong(ultimaSesion);
        archivo.writeLong(sesionActual);
        archivo.writeLong(sesionAnterior);

        archivo.writeBoolean(tutoCompleto);
        for (boolean nivel : nivelesCompletados) archivo.writeBoolean(nivel);
        for (int score : mayorPuntuacion) archivo.writeInt(score);
        archivo.writeInt(tiempoJugadoTotal);
        for (int p : partidasPorNivel) archivo.writeInt(p);
        for (int t : tiempoPorNivel) archivo.writeInt(t);
        for (int t : mejorTiempoPorNivel) archivo.writeInt(t);

        // Configuración
        archivo.writeInt(volumen);
        archivo.writeInt(moverArriba);
        archivo.writeInt(moverAbajo);
        archivo.writeInt(moverDer);
        archivo.writeInt(moverIzq);
        archivo.writeInt(reiniciar);
        archivo.writeInt(idioma);

        // Historial de partidas
        archivo.writeInt(historial.size());
        for (Partida p : historial) {
            archivo.writeUTF(p.getFecha() == null ? "" : p.getFecha());
            archivo.writeInt(p.getIntentos());
            archivo.writeUTF(p.getLogros() == null ? "" : p.getLogros());
            archivo.writeInt(p.getTiempo());
            archivo.writeInt(p.getNivel());
        }

        archivo.getFD().sync();
    }

    public void cargarTodo() throws IOException {
        archivo.seek(0);

        nombre = archivo.readUTF();
        usuario = archivo.readUTF();
        contrasena = archivo.readUTF();
        avatar = archivo.readUTF();

        fechaRegistro = archivo.readLong();
        ultimaSesion  = archivo.readLong();
        sesionActual  = archivo.readLong();
        sesionAnterior= archivo.readLong();

        tutoCompleto = archivo.readBoolean();
        nivelesCompletados.clear();
        for (int i = 0; i < 7; i++) nivelesCompletados.add(archivo.readBoolean());

        mayorPuntuacion.clear();
        for (int i = 0; i < 7; i++) mayorPuntuacion.add(archivo.readInt());

        tiempoJugadoTotal = archivo.readInt();

        partidasPorNivel.clear();
        for (int i = 0; i < 7; i++) partidasPorNivel.add(archivo.readInt());

        tiempoPorNivel.clear();
        for (int i = 0; i < 7; i++) tiempoPorNivel.add(archivo.readInt());

        mejorTiempoPorNivel.clear();
        for (int i = 0; i < 7; i++) mejorTiempoPorNivel.add(archivo.readInt());

        volumen = archivo.readInt();
        moverArriba = archivo.readInt();
        moverAbajo  = archivo.readInt();
        moverDer    = archivo.readInt();
        moverIzq    = archivo.readInt();
        reiniciar   = archivo.readInt();
        idioma      = archivo.readInt();

        historial.clear();
        int cant = archivo.readInt();
        for (int i = 0; i < cant; i++) {
            String fecha = archivo.readUTF();
            int intentos = archivo.readInt();
            String logros = archivo.readUTF();
            int tiempo = archivo.readInt();
            int nivel  = archivo.readInt();
            historial.add(new Partida(fecha,intentos,logros,tiempo,nivel));
        }
    }

    public void cerrar() throws IOException {
        if (archivo != null) archivo.close();
    }

    // --------------------------------------
    // Getters y setters simples
    // --------------------------------------
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public boolean getTutoCompleto() { return tutoCompleto; }
    public void setTutoCompleto(boolean v) { tutoCompleto = v; }
}
