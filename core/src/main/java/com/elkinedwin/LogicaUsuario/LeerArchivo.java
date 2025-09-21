package com.elkinedwin.LogicaUsuario;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LeerArchivo {

    public static void cargarUsuario() throws IOException {
        if (ManejoUsuarios.UsuarioActivo == null) return;
        leerDatos();
        leerProgreso();
        leerConfig();
    }

    private static void leerDatos() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoDatos;
        if (f == null) return;

        f.seek(0);
        long fechaRegistro = f.readLong();
        long ultimaSesion  = f.readLong();

        f.seek(16);
        String nombre = safeReadUTF(f);

        String packed = safeReadUTF(f);
        String usuario = "";
        String pass = "";
        String img = "";
        if (packed != null) {
            String[] parts = packed.split(",", -1);
            if (parts.length > 0) usuario = parts[0];
            if (parts.length > 1) pass    = parts[1];
            if (parts.length > 2) img     = parts[2];
        }

        Usuario u = ManejoUsuarios.UsuarioActivo;
        u.setFechaRegistro(fechaRegistro);
        u.setUltimaSesion(ultimaSesion);
        u.setNombre(nombre == null ? "" : nombre);
        u.setUsuario(usuario == null ? "" : usuario);
        u.setContrasena(pass == null ? "" : pass);
        u.avatar = (img == null ? "" : img);
    }

    private static void leerProgreso() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        if (f == null) return;

        Usuario u = ManejoUsuarios.UsuarioActivo;

        f.seek(0);
        u.setTutocomplete(f.readBoolean());

        f.seek(1);
        for (int i = 1; i <= 7; i++) {
            u.setNivelCompletado(i, f.readBoolean());
        }

        f.seek(8);
        for (int i = 1; i <= 7; i++) {
            u.setMayorPuntuacion(i, f.readInt());
        }

        f.seek(36);
        u.setTiempoJugadoTotal(f.readInt());

        f.seek(40);
        u.setPuntuacionGeneral(f.readInt());

        f.seek(44);
        u.setPartidasTotales(f.readInt());

        f.seek(48);
        for (int i = 1; i <= 7; i++) {
            u.setPartidasPorNivel(i, f.readInt());
        }

        f.seek(102);
        for (int i = 1; i <= 7; i++) {
            u.setTiempoPorNivel(i, f.readInt());
        }

        // nuevo bloque
        f.seek(130);
        for (int i = 1; i <= 7; i++) {
            u.setMejorTiempoPorNivel(i, f.readInt());
        }
    }

    private static void leerConfig() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoConfig;
        if (f == null) return;

        f.seek(0);
        int vol       = f.readInt();
        int arriba    = f.readInt();
        int abajo     = f.readInt();
        int der       = f.readInt();
        int izq       = f.readInt();
        int reiniciar = f.readInt();
        int idioma    = f.readInt();

        Usuario u = ManejoUsuarios.UsuarioActivo;
        u.setConfiguracion("Volumen", vol);
        u.setConfiguracion("MoverArriba", arriba);
        u.setConfiguracion("MoverAbajo", abajo);
        u.setConfiguracion("MoverDer", der);
        u.setConfiguracion("MoverIzq", izq);
        u.setConfiguracion("Reiniciar", reiniciar);
        u.setConfiguracion("Idioma", idioma);
    }

    private static String safeReadUTF(RandomAccessFile f) {
        try { return f.readUTF(); } catch (Exception e) { return ""; }
    }
}