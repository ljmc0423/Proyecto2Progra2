package com.elkinedwin.LogicaUsuario;

import java.io.File;
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
    // Reglas:
    // - fechaRegistro: no cambia.
    // - ultimaSesion:
    //   * Primer login: si ultimaSesion guardada es 0 y no hay sesionAnterior, usar sesionActual.
    //   * Logins siguientes: ultimaSesion se actualiza al cerrar sesion (con el inicio de la sesion actual).
    //   * En cualquier otro caso se conserva lo guardado.

    public static void guardarFechas() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoDatos;

       
        long reg = ManejoUsuarios.UsuarioActivo.getFechaRegistro() == null ? 0L: ManejoUsuarios.UsuarioActivo.getFechaRegistro();
        f.seek(0);
        f.writeLong(reg);

       
        Long anterior = ManejoUsuarios.UsuarioActivo.sesionAnterior; 
        Long actual   = ManejoUsuarios.UsuarioActivo.sesionActual; 
        long ultimaEnMem = ManejoUsuarios.UsuarioActivo.getUltimaSesion() == null
                            ? 0L : ManejoUsuarios.UsuarioActivo.getUltimaSesion();

        
        f.seek(8);
        long ultimaEnArchivo = f.readLong();

        long valueUltima;
        if ((ultimaEnArchivo == 0L || ultimaEnMem == 0L) &&
            (anterior == null || anterior == 0L) &&
            (actual != null && actual > 0L)) {
            
            valueUltima = actual;
        } else if (anterior != null && anterior > 0L) {
            
            valueUltima = anterior;
        } else {
            
            valueUltima = (ultimaEnMem != 0L) ? ultimaEnMem : ultimaEnArchivo;
        }

        f.seek(8);
        f.writeLong(valueUltima);
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
        // f.setLength(f.getFilePointer()); // opcional
    }

    // ===== RENOMBRAR CARPETA USUARIO =====

    public static void renombrarCarpetaUsuarioSiCambio() throws IOException {
        if (ManejoUsuarios.UsuarioActivo == null) return;

        String newUser = ManejoUsuarios.UsuarioActivo.getUsuario();
        if (newUser == null || newUser.trim().isEmpty()) return;

        File currentDir = ManejoArchivos.carpetaUsuarioActual;
        if (currentDir == null) return;

        if (currentDir.getName().equals(newUser)) return; // nada que hacer

        File parent = ManejoArchivos.carpetaUsuarios;
        if (parent == null) parent = new File("Usuarios");
        if (!parent.exists()) parent.mkdirs();

        File targetDir = new File(parent, newUser);
        if (targetDir.exists()) {
            throw new IOException("Ya existe una carpeta con el usuario: " + newUser);
        }

        // cerrar RAFs
        closeQuietly(ManejoArchivos.archivoDatos);
        closeQuietly(ManejoArchivos.archivoProgreso);
        closeQuietly(ManejoArchivos.archivoPartidas);
        closeQuietly(ManejoArchivos.archivoConfig);
        ManejoArchivos.archivoDatos = null;
        ManejoArchivos.archivoProgreso = null;
        ManejoArchivos.archivoPartidas = null;
        ManejoArchivos.archivoConfig = null;

        // renombrar
        boolean ok = currentDir.renameTo(targetDir);
        if (!ok) {
            throw new IOException("No se pudo renombrar la carpeta: " +
                    currentDir.getAbsolutePath() + " -> " + targetDir.getAbsolutePath());
        }

        // reabrir
        ManejoArchivos.carpetaUsuarioActual = targetDir;
        ManejoArchivos.setArchivo(newUser);
    }

    private static void closeQuietly(RandomAccessFile raf) {
        if (raf != null) { try { raf.close(); } catch (Exception ignored) {} }
    }

    // ===== AGRUPADORES =====

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

        if (ManejoUsuarios.UsuarioActivo != null) {
          
            if (ManejoUsuarios.UsuarioActivo.sesionActual != null &&
                ManejoUsuarios.UsuarioActivo.sesionActual > 0L) {
                ManejoUsuarios.UsuarioActivo.sesionAnterior = ManejoUsuarios.UsuarioActivo.sesionActual;
            }
        }

        
        renombrarCarpetaUsuarioSiCambio();

        guardarTodo();
    }
}