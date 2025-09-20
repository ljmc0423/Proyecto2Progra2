package com.elkinedwin.LogicaUsuario;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

public class ManejoArchivos {

    // Carpeta base y referencias actuales
    public static File carpetaUsuarios;
    public static File carpetaUsuarioActual;

    public static RandomAccessFile archivoDatos;
    public static RandomAccessFile archivoProgreso;
    public static RandomAccessFile archivoPartidas;
    public static RandomAccessFile archivoConfig;

    public static void iniciarCpadre() {
        if (carpetaUsuarios == null) carpetaUsuarios = new File("Usuarios");
        if (!carpetaUsuarios.exists()) carpetaUsuarios.mkdirs();
    }

    public static String buscarUsuario(String usuario) {
        if (usuario == null) return null;
        iniciarCpadre();
        String base = usuario.trim();
        if (base.isEmpty()) return null;
        File dir = new File(carpetaUsuarios, base);
        return dir.isDirectory() ? dir.getPath() : null;
    }

    public static String buscarArchivoUsuario(String usuario) {
        return buscarUsuario(usuario);
    }

    public static void setArchivo(String usuario) throws IOException {
        iniciarCpadre();
        if (usuario == null || usuario.trim().isEmpty())
            throw new FileNotFoundException("Usuario vacío.");

        carpetaUsuarioActual = new File(carpetaUsuarios, usuario);
        if (!carpetaUsuarioActual.isDirectory()){
            throw new FileNotFoundException("No existe el usuario: " + usuario);
        }

        archivoDatos    = new RandomAccessFile(new File(carpetaUsuarioActual, "Datos.bin"), "rw");
        archivoProgreso = new RandomAccessFile(new File(carpetaUsuarioActual, "Progreso.bin"), "rw");
        archivoPartidas = new RandomAccessFile(new File(carpetaUsuarioActual, "Partidas.bin"), "rw");
        archivoConfig   = new RandomAccessFile(new File(carpetaUsuarioActual, "Config.bin"), "rw");
    }

    public static void crearUsuario(String nombre, String usuario, String contrasena) throws IOException {
        iniciarCpadre();
        if (usuario == null || usuario.trim().isEmpty())
            throw new IOException("Usuario invalido");

        File dir = new File(carpetaUsuarios, usuario);
        if (dir.exists())
            throw new IOException("El usuario ya existe");

        if (!dir.mkdirs())
            throw new IOException("No se pudo crear la carpeta del usuario.");

        long ahora = Calendar.getInstance().getTimeInMillis();

        // ===== Datos.bin =====
        archivoDatos = new RandomAccessFile(new File(dir, "Datos.bin"), "rw");
        // [0] FechaRegistro, [8] UltimaSesion
        archivoDatos.seek(0);
        archivoDatos.writeLong(ahora);  // fechaRegistro
        archivoDatos.writeLong(0L);     // ultimaSesion (primer login la definirá)
        // [16] Nombre (UTF)
        archivoDatos.seek(16);
        archivoDatos.writeUTF(nombre == null ? "" : nombre);
        //  UTF: "usuario,contrasena,imagen,"
        String imgDefecto = "../Imagenes/LogoU.png";
        StringBuilder datos = new StringBuilder();
        datos.append(usuario == null ? "" : usuario).append(',')
             .append(contrasena == null ? "" : contrasena).append(',')
             .append(imgDefecto).append(',');
        archivoDatos.writeUTF(datos.toString());
        archivoDatos.getFD().sync();

        // ===== Progreso.bin =====
        archivoProgreso = new RandomAccessFile(new File(dir, "Progreso.bin"), "rw");
        // [0..6] boolean[7] niveles completados
        archivoProgreso.seek(0);
        for (int i = 0; i < 7; i++) archivoProgreso.writeBoolean(false);
        // [7] int[7] mayor puntuación
        archivoProgreso.seek(7);
        for (int i = 0; i < 7; i++) archivoProgreso.writeInt(0);
        // [35] int tiempo total
        archivoProgreso.seek(35); archivoProgreso.writeInt(0);
        // [39] int puntuación general
        archivoProgreso.seek(39); archivoProgreso.writeInt(0);
        // [43] int partidas totales
        archivoProgreso.seek(43); archivoProgreso.writeInt(0);
        // [47] int[7] partidas por nivel
        archivoProgreso.seek(47);
        for (int i = 0; i < 7; i++) archivoProgreso.writeInt(0);
        // [101] int[7] tiempo por nivel
        archivoProgreso.seek(101);
        for (int i = 0; i < 7; i++) archivoProgreso.writeInt(0);
        archivoProgreso.getFD().sync();

        // ===== Config.bin =====
        archivoConfig = new RandomAccessFile(new File(dir, "Config.bin"), "rw");
        archivoConfig.seek(0);  archivoConfig.writeInt(80); // Volumen
        archivoConfig.seek(4);  archivoConfig.writeInt(19); // MoverArriba
        archivoConfig.seek(8);  archivoConfig.writeInt(20); // MoverAbajo
        archivoConfig.seek(12); archivoConfig.writeInt(22); // MoverDer
        archivoConfig.seek(16); archivoConfig.writeInt(21); // MoverIzq
        archivoConfig.seek(20); archivoConfig.writeInt(46); // Reiniciar
        archivoConfig.seek(24); archivoConfig.writeInt(1);  // Idioma
        archivoConfig.getFD().sync();

        // ===== Partidas.bin =====
        archivoPartidas = new RandomAccessFile(new File(dir, "Partidas.bin"), "rw");
        archivoPartidas.setLength(0);
        archivoPartidas.getFD().sync();

        // Referencia en memoria
        ManejoUsuarios.UsuarioActivo = new Usuario(usuario, nombre, contrasena, ahora);
       
        ManejoUsuarios.UsuarioActivo.setUltimaSesion(0L);
        ManejoUsuarios.UsuarioActivo.sesionAnterior = 0L;
        ManejoUsuarios.UsuarioActivo.sesionActual = 0L;

        carpetaUsuarioActual = dir;
    }
}