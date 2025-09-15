package com.elkinedwin.LogicaUsuario;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LeerArchivo {

    private static RandomAccessFile archivo;

    public static void usarArchivo(RandomAccessFile ref){
        archivo = ref;
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

  

    public static void leerUsuario() throws IOException {
        archivo.seek(149);
        archivo.readUTF();                 
        String datos = archivo.readUTF();  
        StringBuilder u = new StringBuilder();
        for (int i = 0; i < datos.length(); i++) {
            char c = datos.charAt(i);
            if (c == ',') break;
            u.append(c);
        }
        ManejoUsuarios.UsuarioActivo.setUsuario(u.toString());
    }

    public static String leerNombre() throws IOException {
        archivo.seek(149);
        String nombre = archivo.readUTF(); 
        ManejoUsuarios.UsuarioActivo.setNombre(nombre);
        return nombre;
    }

    public static void leerContrasena() throws IOException {
        archivo.seek(149);
        archivo.readUTF();                
        String datos = archivo.readUTF();

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

    public static void leerPartidas() throws IOException {
        archivo.seek(149);
        archivo.readUTF();                 
        String datos = archivo.readUTF();  

        String blob = "";
        int comas = 0;
        boolean leer = false;
        for (int i = 0; i < datos.length(); i++) {
            char c = datos.charAt(i);
            if (c == ',') {
                comas++;
                if (comas == 2) { 
                    leer = true;  
                    continue; }
                if (comas == 3) { break; }
            } else if (leer) blob += c;
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
        archivo.seek(149);
        archivo.readUTF();                
        String datos = archivo.readUTF();  

        String img = "";
        int comas = 0;
        boolean leer = false;
        for (int i = 0; i < datos.length(); i++) {
            char c = datos.charAt(i);
            if (c == ',') {
                comas++;
                if (comas == 3) { leer = true; continue; }
                if (comas == 4) { break; }
            } else if (leer) img += c;
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
        ManejoUsuarios.UsuarioActivo.setConfiguracion("MoverDer", archivo.readInt());
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
