package com.elkinedwin.LogicaUsuario;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ArchivoGuardar {

    // ===== PROGRESO.BIN =====

    public static void guardarNivelesCompletados() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(0);
        for (int i = 1; i < 8; i++) {
            f.writeBoolean(ManejoUsuarios.UsuarioActivo.getNivelCompletado(i));
        }
    }

    public static void guardarMayorPuntuacion() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(7);
        for (int i = 1; i < 8; i++) {
            f.writeInt(ManejoUsuarios.UsuarioActivo.getMayorPuntuacion(i));
        }
    }

    public static void guardarTiempoTotal() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(35);
        f.writeInt(ManejoUsuarios.UsuarioActivo.getTiempoJugadoTotal());
    }

    public static void guardarPuntuacionGeneral() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(39);
        f.writeInt(ManejoUsuarios.UsuarioActivo.getPuntuacionGeneral());
    }

    public static void guardarTotalPartidas() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(43);
        f.writeInt(ManejoUsuarios.UsuarioActivo.getPartidasTotales());
    }

    public static void guardarPartidasPorNivel() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(47);
        for (int i = 1; i < 8; i++) {
            f.writeInt(ManejoUsuarios.UsuarioActivo.getPartidasPorNivel(i));
        }
    }

    public static void guardarTiempoPorNivel() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(101);
        for (int i = 1; i < 8; i++) {
            f.writeInt(ManejoUsuarios.UsuarioActivo.getTiempoPorNivel(i));
        }
    }

    // ===== CONFIG.BIN =====

    public static void guardarConfiguracion() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoConfig;

        int vol       = ManejoUsuarios.UsuarioActivo.getConfiguracion("Volumen");
        int arriba    = ManejoUsuarios.UsuarioActivo.getConfiguracion("MoverArriba");
        int abajo     = ManejoUsuarios.UsuarioActivo.getConfiguracion("MoverAbajo");
        int derecha   = ManejoUsuarios.UsuarioActivo.getConfiguracion("MoverDer");
        int izquierda = ManejoUsuarios.UsuarioActivo.getConfiguracion("MoverIzq");
        int reiniciar = ManejoUsuarios.UsuarioActivo.getConfiguracion("Reiniciar");
        int idioma    = ManejoUsuarios.UsuarioActivo.getConfiguracion("Idioma");

        f.seek(0);  f.writeInt(vol);
        f.seek(4);  f.writeInt(arriba);
        f.seek(8);  f.writeInt(abajo);
        f.seek(12); f.writeInt(derecha);
        f.seek(16); f.writeInt(izquierda);
        f.seek(20); f.writeInt(reiniciar);
        f.seek(24); f.writeInt(idioma);
    }

    // ===== DATOS.BIN =====

    public static void guardarFechas() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoDatos;

        // FechaRegistro
        long reg = ManejoUsuarios.UsuarioActivo.getFechaRegistro() == null ? 0L : ManejoUsuarios.UsuarioActivo.getFechaRegistro();
        f.seek(0);
        f.writeLong(reg);

        // UltimaSesion (guardamos la "anterior")
        Long anterior = ManejoUsuarios.UsuarioActivo.sesionAnterior;
        if (anterior == null) anterior = ManejoUsuarios.UsuarioActivo.getUltimaSesion();
        if (anterior == null) anterior = 0L;
        f.seek(8);
        f.writeLong(anterior);
    }

    public static void guardarDatosUTF() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoDatos;

        // Nombre
        f.seek(16);
        f.writeUTF(ManejoUsuarios.UsuarioActivo.getNombre() == null ? "" : ManejoUsuarios.UsuarioActivo.getNombre());

        // "usuario,contrasena,imagen,"
        StringBuilder datos = new StringBuilder();
        String usuario  = ManejoUsuarios.UsuarioActivo.getUsuario();
        String pass     = ManejoUsuarios.UsuarioActivo.getContrasena();
        String imgPath  = ManejoUsuarios.UsuarioActivo.avatar;

        datos.append(usuario == null ? "" : usuario)
             .append(',')
             .append(pass == null ? "" : pass)
             .append(',')
             .append(imgPath == null ? "" : imgPath)
             .append(',');

        f.writeUTF(datos.toString());
    }

    // ===== PARTIDAS.BIN =====
    public static void guardarPartidas() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoPartidas;

        if (ManejoUsuarios.UsuarioActivo.historial == null || ManejoUsuarios.UsuarioActivo.historial.isEmpty()) {
            
            f.setLength(0);
            return;
        }

        f.seek(0);
        int count = ManejoUsuarios.UsuarioActivo.historial.size();
        f.writeInt(count);

        for (int i = 0; i < count; i++) {
            Partida p = ManejoUsuarios.UsuarioActivo.historial.get(i);

            String fecha    = (p.getFecha()  == null) ? "" : p.getFecha();
            int intentos    = p.getIntentos();
            String logros   = (p.getLogros() == null) ? "" : p.getLogros();
            int tiempo      = p.getTiempo();

            f.writeUTF(fecha);
            f.writeInt(intentos);
            f.writeUTF(logros);
            f.writeInt(tiempo);
        }
        // si se reescribe menos que antes, opcional: truncar a posición actual
        // f.setLength(f.getFilePointer());
    }

    // ===== AGRUPADOR =====

    public static void guardarTodo() throws IOException {
        if (ManejoArchivos.archivoDatos == null ||
            ManejoArchivos.archivoProgreso == null ||
            ManejoArchivos.archivoPartidas == null ||
            ManejoArchivos.archivoConfig == null) {
            throw new IOException("Archivos no listos.");
        }

        guardarNivelesCompletados();
        guardarMayorPuntuacion();
        guardarTiempoTotal();
        guardarPuntuacionGeneral();
        guardarTotalPartidas();
        guardarPartidasPorNivel();
        guardarTiempoPorNivel();

        guardarConfiguracion();

        guardarFechas();
        guardarDatosUTF();

        guardarPartidas();
    }

    public static void guardarTodoCerrarSesion() throws IOException {
        if (ManejoArchivos.archivoDatos == null ||
            ManejoArchivos.archivoProgreso == null ||
            ManejoArchivos.archivoPartidas == null ||
            ManejoArchivos.archivoConfig == null) {
            throw new IOException("Archivos no listos.");
        }

        // Actualizamos la "anterior" a ahora (sesionActual no se guarda en archivo)
        if (ManejoUsuarios.UsuarioActivo != null) {
            Long ahora = System.currentTimeMillis();
            ManejoUsuarios.UsuarioActivo.sesionAnterior = ahora;
        }

        guardarTodo();
    }
}