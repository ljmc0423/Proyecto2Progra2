package com.elkinedwin.LogicaUsuario;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LeerArchivo {

    public static void cargarUsuario() throws IOException{
        if (ManejoUsuarios.UsuarioActivo == null) throw new IOException("UsuarioActivo null.");

        if (ManejoArchivos.archivoDatos == null ||
            ManejoArchivos.archivoProgreso == null ||
            ManejoArchivos.archivoPartidas == null ||
            ManejoArchivos.archivoConfig == null) {
            throw new IOException("Archivos no listos.");
        }

        leerUsuario();
        leerNombre();
        leerContrasena();
        leerPartidas();
        leerAvatar();

        // PROGRESO.BIN (nuevo layout fijo)
        leerTutorialCompletado();   // [0]
        leerCompletados();          // [1..7]
        leerMayorPuntuacion();      // [8]
        leerTiempoTotal();          // [36]
        leerPuntuacionGeneral();    // [40]
        leerPartidasTotales();      // [44]
        leerPartidasPorNivel();     // [48]
        leerTiempoPorNivel();       // [102]

        // CONFIG / DATOS
        leerConfiguracion();
        leerFechaRegistro();
        leerUltimaSesion();
    }

    //  DATOS.BIN 
    public static void leerUsuario() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoDatos;
        f.seek(16);
        f.readUTF();               
        String datos = f.readUTF();

        StringBuilder u = new StringBuilder();
        for (int i = 0; i < datos.length(); i++) {
            char c = datos.charAt(i);
            if (c == ',') break;
            u.append(c);
        }
        ManejoUsuarios.UsuarioActivo.setUsuario(u.toString());
    }

    public static String leerNombre() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoDatos;
        f.seek(16);
        String nombre = f.readUTF();
        ManejoUsuarios.UsuarioActivo.setNombre(nombre);
        return nombre;
    }

    public static void leerContrasena() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoDatos;
        f.seek(16);
        f.readUTF();               
        String datos = f.readUTF();

        String pass = "";
        int comas = 0;
        boolean leer = false;
        for (int i = 0; i < datos.length(); i++) {
            char c = datos.charAt(i);
            if (c == ',') {
                comas++;
                if (comas == 1) { leer = true; continue; }
                if (comas == 2) { break; }
            } else if (leer) pass += c;
        }
        ManejoUsuarios.UsuarioActivo.setContrasena(pass);
    }

    public static void leerAvatar() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoDatos;
        f.seek(16);
        f.readUTF();                
        String datos = f.readUTF(); 

        String img = "";
        int comas = 0;
        boolean leer = false;
        for (int i = 0; i < datos.length(); i++) {
            char c = datos.charAt(i);
            if (c == ',') {
                comas++;
                if (comas == 2) { leer = true; continue; }
                if (comas == 3) { break; }
            } else if (leer) img += c;
        }
        ManejoUsuarios.UsuarioActivo.setAvatar(img);
    }

    public static void leerFechaRegistro() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoDatos;
        f.seek(0);
        ManejoUsuarios.UsuarioActivo.setFechaRegistro(f.readLong());
    }

    public static void leerUltimaSesion() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoDatos;
        f.seek(8);
        long t = f.readLong();
        ManejoUsuarios.UsuarioActivo.setUltimaSesion(t);
        ManejoUsuarios.UsuarioActivo.sesionAnterior = t;
    }

    // PARTIDAS.BIN 
    public static void leerPartidas() throws IOException {
        RandomAccessFile f = ManejoArchivos.archivoPartidas;
        long len = f.length();
        if (len == 0) return; 

        f.seek(0);
        int count = f.readInt();
        for (int i = 0; i < count; i++) {
            String fecha  = f.readUTF();
            int intentos  = f.readInt();
            String logros = f.readUTF();
            int tiempo    = f.readInt();

            try {
                ManejoUsuarios.UsuarioActivo.historial.add(new Partida(fecha, intentos, logros, tiempo));
            } catch (Exception ignored) {}
        }
    }

    // ===== PROGRESO.BIN =====

    public static void leerTutorialCompletado() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(0);
        boolean v = f.readBoolean();
        ManejoUsuarios.UsuarioActivo.setTutocomplete(v);
    }

    public static void leerCompletados() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(1);
        for (int i = 1; i < 8; i++) {
            ManejoUsuarios.UsuarioActivo.setNivelCompletado(i, f.readBoolean());
        }
    }

    public static void leerMayorPuntuacion() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(8);
        for (int i = 1; i < 8; i++) {
            ManejoUsuarios.UsuarioActivo.setMayorPuntuacion(i, f.readInt());
        }
    }

    public static void leerTiempoTotal() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(36);
        ManejoUsuarios.UsuarioActivo.setTiempoJugadoTotal(f.readInt());
    }

    public static void leerPuntuacionGeneral() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(40);
        ManejoUsuarios.UsuarioActivo.setPuntuacionGeneral(f.readInt());
    }

    public static void leerPartidasTotales() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(44);
        ManejoUsuarios.UsuarioActivo.setPartidasTotales(f.readInt());
    }

    public static void leerPartidasPorNivel() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(48);
        for (int i = 1; i < 8; i++) {
            ManejoUsuarios.UsuarioActivo.setPartidasPorNivel(i, f.readInt());
        }
    }

    public static void leerTiempoPorNivel() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoProgreso;
        f.seek(102);
        for (int i = 1; i < 8; i++) {
            ManejoUsuarios.UsuarioActivo.setTiempoPorNivel(i, f.readInt());
        }
    }

    // CONFIG.BIN 
    public static void leerConfiguracion() throws IOException{
        RandomAccessFile f = ManejoArchivos.archivoConfig;

        f.seek(0);  ManejoUsuarios.UsuarioActivo.setConfiguracion("Volumen", f.readInt());

        f.seek(4);  ManejoUsuarios.UsuarioActivo.setConfiguracion("MoverArriba", f.readInt());
        f.seek(8);  ManejoUsuarios.UsuarioActivo.setConfiguracion("MoverAbajo", f.readInt());
        f.seek(12); ManejoUsuarios.UsuarioActivo.setConfiguracion("MoverDer", f.readInt());
        f.seek(16); ManejoUsuarios.UsuarioActivo.setConfiguracion("MoverIzq", f.readInt());
        f.seek(20); ManejoUsuarios.UsuarioActivo.setConfiguracion("Reiniciar", f.readInt());

        f.seek(24); ManejoUsuarios.UsuarioActivo.setConfiguracion("Idioma", f.readInt());
    }
}
