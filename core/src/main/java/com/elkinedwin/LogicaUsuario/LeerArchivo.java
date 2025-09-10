package com.elkinedwin.LogicaUsuario;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LeerArchivo {

    private static RandomAccessFile archivinho;

   public static void setArchivoLeer(RandomAccessFile archivoactivo){
    
    archivinho=archivoactivo;  
   }
    
    public static String LeerUsuario() throws IOException {
        archivinho.seek(149);
        archivinho.readUTF();
        String datos = archivinho.readUTF();

        String usuario = "";
        boolean seguir = true;

        for (int i = 0; i < datos.length(); i++) {
            char c = datos.charAt(i);
            if (c == ',') {
                seguir = false;
            }
            if (seguir) {
                usuario += c;
            } else {
                break;
            }
        }
        return usuario;
    }

    public static String LeerNombre() throws IOException {
        archivinho.seek(149);
        return archivinho.readUTF();

    }

    public static String LeerPassword() throws IOException {

        archivinho.seek(149);
        archivinho.readUTF();
        String datos = archivinho.readUTF();

        String pass = "";
        int comas = 0;
        boolean leer = false;

        for (int i = 0; i < datos.length(); i++) {
            char c = datos.charAt(i);

            if (c == ',') {
                comas++;
                if (comas == 1) {
                    leer = true;
                    continue;
                }
                if (comas == 2) {
                    leer = false;
                    break;
                }
            }

            if (leer) {
                pass += c;
            }
        }
        return pass;

    }
public static void getPartidasArchivo() {
    try {
        archivinho.seek(149);
        archivinho.readUTF();               // pasar el "por default"
        String datos = archivinho.readUTF(); // todo el string pegado

        // extraer lo que está entre la 2da y 3ra coma (las partidas)
        String blob = "";
        int comas = 0;
        boolean leer = false;
        for (int i = 0; i < datos.length(); i++) {
            char c = datos.charAt(i);
            if (c == ',') {
                comas++;
                if (comas == 2) { leer = true;  continue; } // empieza después de la 2da
                if (comas == 3) { leer = false; break; }    // termina en la 3ra
            }
            if (leer) blob += c;
        }
        if (blob.isEmpty()) return;

        // cada partida: Fecha.intentos.logros.tiempo  separadas por '>'
        String[] partes = blob.split(">");
        for (String p : partes) {
            if (p == null) continue;
            p = p.trim();
            if (p.isEmpty()) continue;

            String[] a = p.split("\\."); // {Fecha, intentos, logros, tiempo}
            if (a.length < 4) continue;

            String fecha  = a[0];
            int intentos  = 0;
            int tiempo    = 0;
            try { intentos = Integer.parseInt(a[1].trim()); } catch (Exception ignored) {}
            String logros = a[2];
            try { tiempo   = Integer.parseInt(a[3].trim()); } catch (Exception ignored) {}

            try {
                ManejoUsuarios.UsuarioActivo.historialpartidas.add(new Partida(fecha, intentos, logros, tiempo));
            } catch (Exception ignored) {}
        }
    } catch (Exception e) {
       
    }
}
public static String getImagenArchivo() {
    try {
        archivinho.seek(149);
        archivinho.readUTF();            
        String datos = archivinho.readUTF(); 

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
        return img;
    } catch (Exception e) {
        return "";
    }
}
public static void LeerCompletados() throws IOException{
    archivinho.seek(0);
    for (int i = 1; i < 8; i++) {
    ManejoUsuarios.UsuarioActivo.setNivelCompletados(i,archivinho.readBoolean());
    }
   
}
public static void LeerMayorPuntuacion() throws IOException{
archivinho.seek(7);
    for (int i = 0; i < 10; i++) {
        
    }
}
public static void LeerTiempototal() throws IOException{
archivinho.seek(35);
ManejoUsuarios.UsuarioActivo.setTiempoTJugado(archivinho.readInt());
}
public static void LeerPuntuaciongeneral() throws IOException{
archivinho.seek(39);
ManejoUsuarios.UsuarioActivo.setPuntuaciongeneral(archivinho.readInt());
}
public static void LeerPartidastotales() throws IOException{
archivinho.seek(43);
ManejoUsuarios.UsuarioActivo.setPartidastotales(archivinho.readInt());
}
public static void LeerPartidaspornivel() throws IOException{
archivinho.seek(47);
    for (int i = 1; i < 8; i++) {
     ManejoUsuarios.UsuarioActivo.setPartidasnivel(1, archivinho.readInt());
    }
}
public static void LeerConfiguraciones() throws IOException{
archivinho.seek(77);
 ManejoUsuarios.UsuarioActivo.setConfiguracion("Volumen", archivinho.readInt());
 ManejoUsuarios.UsuarioActivo.setConfiguracion("MoverArriba", archivinho.readInt());
 ManejoUsuarios.UsuarioActivo.setConfiguracion("MoverAbajo", archivinho.readInt());
 ManejoUsuarios.UsuarioActivo.setConfiguracion("MoverDere", archivinho.readInt());
 ManejoUsuarios.UsuarioActivo.setConfiguracion("MoverIzq", archivinho.readInt());
 ManejoUsuarios.UsuarioActivo.setConfiguracion("Reiniciar", archivinho.readInt());
 archivinho.seek(129);
 ManejoUsuarios.UsuarioActivo.setConfiguracion("Idioma", archivinho.readInt());

}
public static void LeerTiempoxnivel() throws IOException{
archivinho.seek(101);
    for (int i = 1; i < 8; i++) {
       ManejoUsuarios.UsuarioActivo.setTiempoxnivel(1, archivinho.readInt());
    }
}
public static void LeerFechaRegistro() throws IOException{
archivinho.seek(133);
ManejoUsuarios.UsuarioActivo.setFecharegistro(archivinho.readLong());
}
public static void LeerUltimaSesion() throws IOException{
archivinho.seek(141);
ManejoUsuarios.UsuarioActivo.setUltimaSesion(archivinho.readLong());
}

}
