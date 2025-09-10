
package com.elkinedwin.LogicaUsuario;

import java.io.IOException;
import java.io.RandomAccessFile;


public class ArchivoGuardar {

private static RandomAccessFile archivo;

public static void setArchivo(RandomAccessFile archivoactivo){
archivo=archivoactivo;
}
public static void setNivelesCompletados() throws IOException{
    archivo.seek(0);
    for (int i = 1; i < 8; i++) {
     archivo.writeBoolean(ManejoUsuarios.UsuarioActivo.getNivelesCompletados(i));
    }
}
public static void setMayorPuntuacion() throws IOException{
    archivo.seek(7);
    for (int i = 1; i < 8; i++) {
     ManejoUsuarios.UsuarioActivo.getMayorPuntuacion(i);
    }
}
public static void setTiempototal() throws IOException{
archivo.seek(35);
archivo.writeInt(ManejoUsuarios.UsuarioActivo.getTiempototal());
}

public static void setPuntuacionGeneral() throws IOException{
archivo.seek(39);
archivo.writeInt(ManejoUsuarios.UsuarioActivo.getPuntuaciongeneral());
}
public static void setTotalPartidas() throws IOException{
archivo.seek(43);
archivo.writeInt(ManejoUsuarios.UsuarioActivo.getPartidastotales());
}
public static void setPartidasnivel() throws IOException{
archivo.seek(47);
    for (int i = 1; i < 8; i++) {
       archivo.writeInt(ManejoUsuarios.UsuarioActivo.getPartidasnivel(i));
    }
}
public static void setConfi() throws IOException{
archivo.seek(77);
archivo.writeInt(ManejoUsuarios.UsuarioActivo.getConfi("Volumen"));
archivo.writeInt(ManejoUsuarios.UsuarioActivo.getConfi("MoverArriba"));
archivo.writeInt(ManejoUsuarios.UsuarioActivo.getConfi("MoverAbajo"));
archivo.writeInt(ManejoUsuarios.UsuarioActivo.getConfi("MoverIzq"));
archivo.writeInt(ManejoUsuarios.UsuarioActivo.getConfi("MoverDer"));
archivo.writeInt(ManejoUsuarios.UsuarioActivo.getConfi("Reinciar"));
archivo.writeInt(ManejoUsuarios.UsuarioActivo.getConfi("Volumen"));
archivo.seek(129);
archivo.writeInt(ManejoUsuarios.UsuarioActivo.getConfi("Idioma"));

}
public static void Tiempototalnivel() throws IOException{
archivo.seek(101);
    for (int i = 1; i < 8; i++) {
     archivo.writeInt(ManejoUsuarios.UsuarioActivo.getTiempoTnivel(i));
    }
}
public static void setFechas() throws IOException{
archivo.seek(133);
archivo.writeLong(ManejoUsuarios.UsuarioActivo.getFecharegistro());
archivo.writeLong(ManejoUsuarios.UsuarioActivo.sesionActual);
}
public static void setDatosUTF() throws IOException {
    archivo.seek(149);

    
    archivo.writeUTF(ManejoUsuarios.UsuarioActivo.getNombre());

   
    StringBuilder datos = new StringBuilder();

    
    String usuario   = ManejoUsuarios.UsuarioActivo.getUsuario();
    String password  = ManejoUsuarios.UsuarioActivo.getPassword();
    datos.append(usuario == null ? "" : usuario)
         .append(',')
         .append(password == null ? "" : password)
         .append(',');

    
    if (ManejoUsuarios.UsuarioActivo.historialpartidas == null ||
        ManejoUsuarios.UsuarioActivo.historialpartidas.isEmpty()) {

        datos.append("..."); // "", "", "", ""  -> 4 vacíos (tres puntos)
    } else {
        for (int i = 0; i < ManejoUsuarios.UsuarioActivo.historialpartidas.size(); i++) {
            Partida p = ManejoUsuarios.UsuarioActivo.historialpartidas.get(i);

            String fecha   = (p.getFecha()  == null) ? "" : p.getFecha();
            String intentos= String.valueOf(p.getIntentos());
            String logros  = (p.getLogros() == null) ? "" : p.getLogros();
            String tiempo  = String.valueOf(p.getTiempo());

            datos.append(fecha).append('.')
                 .append(intentos).append('.')
                 .append(logros).append('.')
                 .append(tiempo);

            if (i < ManejoUsuarios.UsuarioActivo.historialpartidas.size() - 1) {
                datos.append('>');
            }
        }
    }

    
    datos.append(',');
    String imgPath = ManejoUsuarios.UsuarioActivo.avatar; 
    datos.append(imgPath == null ? "" : imgPath)
         .append(',');

    
    archivo.writeUTF(datos.toString());
}

}
