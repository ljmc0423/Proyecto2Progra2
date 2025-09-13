package com.elkinedwin.LogicaUsuario;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

public class ManejoArchivos {

    public static File carpetaUsuarios;
    public static RandomAccessFile archivoAbierto;

    public static void iniciarAlmacenamiento() {
        if (carpetaUsuarios == null) carpetaUsuarios = new File("Usuarios");
        if (!carpetaUsuarios.exists()) carpetaUsuarios.mkdirs();
    }

    private static void asegurarCarpeta() {
        if (carpetaUsuarios == null || !carpetaUsuarios.exists()) {
            iniciarAlmacenamiento();
        }
    }

    private static RandomAccessFile crearArchivoUsuario(String base) throws FileNotFoundException {
        asegurarCarpeta();
        return new RandomAccessFile(new File(carpetaUsuarios, base + ".bin"), "rw");
    }

    /*
    Formato (offsets):
    0..6  : boolean[7] niveles completados
    7     : int[7]  MayorPuntuacion
    35    : int     TiempoTotalJugado
    39    : int     PuntuacionGeneral
    43    : int     TotalPartidas
    47    : int[7]  PartidasPorNivel
    77    : int     Volumen
    81    : int[5]  KeyCodes (Arriba, Abajo, Derecha, Izquierda, Reiniciar)
    101   : int[7]  TiempoPorNivel
    129   : int     Idioma
    133   : long    FechaRegistro
    141   : long    UltimaSesion (siempre la ANTERIOR)
    149   : UTF     Nombre
           UTF     Datos: "Usuario,Password,Partidas(Fecha.Intentos.Logros.Tiempo[>...]),ImagenPath,"
    */
    public static void crearUsuario(String nombre, String usuario, String contrasena) throws IOException {
        asegurarCarpeta();

        RandomAccessFile f = crearArchivoUsuario(usuario);

        f.seek(0);
        for (int i = 0; i < 7; i++) f.writeBoolean(false);

        f.seek(7);
        for (int i = 0; i < 7; i++) f.writeInt(0);

        f.seek(35); f.writeInt(0);
        f.seek(39); f.writeInt(0);
        f.seek(43); f.writeInt(0);

        f.seek(47);
        for (int i = 0; i < 7; i++) f.writeInt(0);

        f.seek(77); f.writeInt(80);

        f.seek(81);
        f.writeInt(38);
        f.writeInt(40);
        f.writeInt(39);
        f.writeInt(37);
        f.writeInt(82);

        f.seek(101);
        for (int i = 0; i < 7; i++) f.writeInt(0);

        f.seek(129); f.writeInt(1);

        long ahora = Calendar.getInstance().getTimeInMillis();
        f.seek(133); f.writeLong(ahora);
        f.seek(141); f.writeLong(ahora);

        f.seek(149);
        f.writeUTF(nombre == null ? "" : nombre);

        StringBuilder datos = new StringBuilder();
        datos.append(usuario == null ? "" : usuario).append(',')
             .append(contrasena == null ? "" : contrasena).append(',')
             .append("...").append(',')
             .append("").append(',');
        f.writeUTF(datos.toString());

        ManejoUsuarios.UsuarioActivo = new Usuario(usuario, nombre, contrasena, ahora);
    }

    public static String buscarArchivoUsuario(String usuario) {
        if (usuario == null) return null;
        asegurarCarpeta();
        String base = quitarExtBin(usuario).trim();
        if (base.isEmpty()) return null;
        File f = new File(carpetaUsuarios, base + ".bin");
        return f.isFile() ? f.getPath() : null;
    }

    private static String quitarExtBin(String s) {
        int p = s.lastIndexOf('.');
        return (p >= 0) ? s.substring(0, p) : s;
    }

    public static void abrirArchivo(String ruta) throws FileNotFoundException {
        archivoAbierto = new RandomAccessFile(ruta, "rw");
    }

    public static void abrirArchivoDeUsuario(String usuario) throws IOException {
        asegurarCarpeta();
        String path = buscarArchivoUsuario(usuario);
        if (path == null) throw new FileNotFoundException("No existe el usuario: " + usuario);
        archivoAbierto = new RandomAccessFile(path, "rw");
    }
}
