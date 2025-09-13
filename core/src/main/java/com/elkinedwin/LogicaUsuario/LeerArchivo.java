package com.elkinedwin.LogicaUsuario;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LeerArchivo {

    private static RandomAccessFile archivo;

    private static boolean cacheLista = false;
    private static String nombreCache = null;
    private static String datosCache  = null;

    public static void usarArchivo(RandomAccessFile ref){
        archivo = ref;
        cacheLista = false;
        nombreCache = null;
        datosCache  = null;
    }

    public static void cargarUsuario() throws IOException{
        if (archivo == null) throw new IOException("Archivo no listo.");
        if (ManejoUsuarios.UsuarioActivo == null) throw new IOException("UsuarioActivo null.");

        leerUsuario();
        leerNombre();
        leerContrasena();
        leerPartidas();
        leerAvatar();
        leerCompletados();
        leerMayorPuntuacion();
        leerTiempoTotal();
        leerPuntuacionGeneral();
        leerPartidasTotales();
        leerPartidasPorNivel();
        leerConfiguracion();
        leerTiempoPorNivel();
        leerFechaRegistro();
        leerUltimaSesion();
    }

    private static void cargarNombreYDatos() throws IOException {
        if (cacheLista) return;
        IOException fallo = null;
        try {
            archivo.seek(149);
            String n = archivo.readUTF();
            String d = archivo.readUTF();
            nombreCache = n;
            datosCache  = d;
            cacheLista = true;
            return;
        } catch (IOException e) {
            fallo = e;
        }
        try {
            archivo.seek(147);
            String n = archivo.readUTF();
            String d = archivo.readUTF();
            nombreCache = n;
            datosCache  = d;
            cacheLista = true;
        } catch (IOException e2) {
            throw new IOException("No se pudieron leer Nombre/Datos.", fallo);
        }
    }

    public static void leerUsuario() throws IOException {
        cargarNombreYDatos();
        String datos = datosCache == null ? "" : datosCache;
        StringBuilder u = new StringBuilder();
        for (int i = 0; i < datos.length(); i++) {
            char c = datos.charAt(i);
            if (c == ',') break;
            u.append(c);
        }
        ManejoUsuarios.UsuarioActivo.setUsuario(u.toString());
    }

    public static String leerNombre() throws IOException {
        cargarNombreYDatos();
        String nombre = nombreCache == null ? "" : nombreCache;
        ManejoUsuarios.UsuarioActivo.setNombre(nombre);
        return nombre;
    }

    public static void leerContrasena() throws IOException {
        cargarNombreYDatos();
        String datos = datosCache == null ? "" : datosCache;

        String pass = "";
        int comas = 0;
        boolean leer = false;

        for (int i = 0; i < datos.length(); i++) {
            char c = datos.charAt(i);
            if (c == ',') {
                comas++;
                if (comas == 1) { leer = true; continue; }
                if (comas == 2) { leer = false; break; }
            }
            if (leer) pass += c;
        }
        ManejoUsuarios.UsuarioActivo.setContrasena(pass);
    }

    public static void leerPartidas() throws IOException {
        cargarNombreYDatos();
        String datos = datosCache == null ? "" : datosCache;

        String blob = "";
        int comas = 0;
        boolean leer = false;
        for (int i = 0; i < datos.length(); i++) {
            char c = datos.charAt(i);
            if (c == ',') {
                comas++;
                if (comas == 2) { leer = true;  continue; }
                if (comas == 3) { leer = false; break; }
            }
            if (leer) blob += c;
        }
        if (blob.isEmpty()) return;

        String[] partes = blob.split(">");
        for (String p : partes) {
            if (p == null) continue;
            p = p.trim();
            if (p.isEmpty()) continue;

            String[] a = p.split("\\.");
            if (a.length < 4) continue;

            String fecha  = a[0];
            int intentos  = 0;
            int tiempo    = 0;
            try { intentos = Integer.parseInt(a[1].trim()); } catch (Exception ignored) {}
            String logros = a[2];
            try { tiempo   = Integer.parseInt(a[3].trim()); } catch (Exception ignored) {}

            try {
                ManejoUsuarios.UsuarioActivo.historial.add(new Partida(fecha, intentos, logros, tiempo));
            } catch (Exception ignored) {}
        }
    }

    public static void leerAvatar() throws IOException {
        cargarNombreYDatos();
        String datos = datosCache == null ? "" : datosCache;

        String img = "";
        int comas = 0;
        boolean leer = false;

        for (int i = 0; i < datos.length(); i++) {
            char c = datos.charAt(i);
            if (c == ',') {
                comas++;
                if (comas == 3) { leer = true; continue; }
                if (comas == 4) { break; }
            }
            if (leer) img += c;
        }
        ManejoUsuarios.UsuarioActivo.setAvatar(img);
    }

    public static void leerCompletados() throws IOException{
        archivo.seek(0);
        for (int i = 1; i < 8; i++) {
            ManejoUsuarios.UsuarioActivo.setNivelCompletado(i, archivo.readBoolean());
        }
    }

    public static void leerMayorPuntuacion() throws IOException{
        archivo.seek(7);
        for (int i = 1; i < 8; i++) {
            ManejoUsuarios.UsuarioActivo.setMayorPuntuacion(i, archivo.readInt());
        }
    }

    public static void leerTiempoTotal() throws IOException{
        archivo.seek(35);
        ManejoUsuarios.UsuarioActivo.setTiempoJugadoTotal(archivo.readInt());
    }

    public static void leerPuntuacionGeneral() throws IOException{
        archivo.seek(39);
        ManejoUsuarios.UsuarioActivo.setPuntuacionGeneral(archivo.readInt());
    }

    public static void leerPartidasTotales() throws IOException{
        archivo.seek(43);
        ManejoUsuarios.UsuarioActivo.setPartidasTotales(archivo.readInt());
    }

    public static void leerPartidasPorNivel() throws IOException{
        archivo.seek(47);
        for (int i = 1; i < 8; i++) {
            ManejoUsuarios.UsuarioActivo.setPartidasPorNivel(i, archivo.readInt());
        }
    }

    public static void leerConfiguracion() throws IOException{
        archivo.seek(77);
        ManejoUsuarios.UsuarioActivo.setConfiguracion("Volumen", archivo.readInt());
        ManejoUsuarios.UsuarioActivo.setConfiguracion("MoverArriba", archivo.readInt());
        ManejoUsuarios.UsuarioActivo.setConfiguracion("MoverAbajo", archivo.readInt());
        ManejoUsuarios.UsuarioActivo.setConfiguracion("MoverDere", archivo.readInt());
        ManejoUsuarios.UsuarioActivo.setConfiguracion("MoverIzq", archivo.readInt());
        ManejoUsuarios.UsuarioActivo.setConfiguracion("Reiniciar", archivo.readInt());
        archivo.seek(129);
        ManejoUsuarios.UsuarioActivo.setConfiguracion("Idioma", archivo.readInt());
    }

    public static void leerTiempoPorNivel() throws IOException{
        archivo.seek(101);
        for (int i = 1; i < 8; i++) {
            ManejoUsuarios.UsuarioActivo.setTiempoPorNivel(i, archivo.readInt());
        }
    }

    public static void leerFechaRegistro() throws IOException{
        archivo.seek(133);
        ManejoUsuarios.UsuarioActivo.setFechaRegistro(archivo.readLong());
    }

    public static void leerUltimaSesion() throws IOException{
        archivo.seek(141);
        long t = archivo.readLong();
        ManejoUsuarios.UsuarioActivo.setUltimaSesion(t);
        ManejoUsuarios.UsuarioActivo.sesionAnterior = t;
    }
}
