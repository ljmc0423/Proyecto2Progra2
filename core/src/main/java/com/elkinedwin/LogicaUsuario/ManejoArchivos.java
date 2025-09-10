package com.elkinedwin.LogicaUsuario;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

public class ManejoArchivos {

    public static File Usuarios;
    public static RandomAccessFile ArchivoActivo;

    
   public static void initStorage() {
    if (Usuarios == null) Usuarios = new File("Usuarios");
    if (!Usuarios.exists()) Usuarios.mkdirs();
    System.out.println("Usuarios dir = " + Usuarios.getAbsolutePath());
    
}

    
    private static void ensureStorage() {
        if (Usuarios == null || !Usuarios.exists()) {
            initStorage();
        }
    }

  
    private static RandomAccessFile CrearArchivoUsuario(String nombrearchivo) throws FileNotFoundException {
        ensureStorage();
        return new RandomAccessFile(new File(Usuarios, nombrearchivo + ".bin"), "rw");
    }

    /*
    Formato:
                                  0  1  2  3  4  5  6
    boolean: niveles completados [1][2][3][4][5][6][7]
                                  7   11  15  19  23  27  31
    int:MayorPuntuacion c/nivel  [1] [2] [3] [4] [5] [6] [7]
                                  35
    int: TiempoTotalJugado        int
                                  39
    int: puntuacionGeneral        int
                                   43
    int: totalpartidas            int
                                   47 51 55 59 64 69 73
    int: partidasxnivel           [1][2][3][4][5][6][7]
                                   77
    int: volumen                  int
                                  81   85     89       93         97
    int:KeyCode controles        [arr][abajo][derecha][izquierda][Reiniciar]
                                  101 105 109 113 117 121 125
    int: Tiempototalxnivel       [1] [2] [3] [4] [5] [6] [7]
                                   129
    int Idioma                    int          // 1=Español, 2=Inglés, 3=Portugués
                                   133         141
    long Fechas:                 FechaRegistro  UltimaSesion
                                   149
    String Nombre:               Nombre
    String datos (UTF):          Usuario,Contrasena,Partidas(Fecha.Intentos.Logros.Tiempo[>...]),ImagenPath,
    */

    public static void InicializarUsuario(String nombre, String usuario, String password) throws IOException {
        ensureStorage();

        RandomAccessFile archivo = CrearArchivoUsuario(nombre);

       
        for (int i = 0; i < 7; i++) {
            archivo.writeBoolean(false);
        }

        
        for (int i = 0; i < 17; i++) {
            archivo.writeInt(0);
        }

        archivo.writeInt(80);

       
        archivo.writeInt(38);
        archivo.writeInt(40);
        archivo.writeInt(39);
        archivo.writeInt(37);
        archivo.writeInt(82);

        
        for (int i = 0; i < 7; i++) {
            archivo.writeInt(0);
        }

        
        archivo.writeInt(1);

       
        long fechas = Calendar.getInstance().getTimeInMillis();
        archivo.writeLong(fechas); 
        archivo.writeLong(fechas); 

        
        archivo.writeUTF(nombre);

        
        StringBuilder datos = new StringBuilder();
        datos.append(usuario == null ? "" : usuario).append(',')
             .append(password == null ? "" : password).append(',')
             .append("...")  
             .append(',')
             .append("")     
             .append(',');

        archivo.writeUTF(datos.toString());

        
        ManejoUsuarios.UsuarioActivo = new Usuario(usuario, nombre, password, fechas);
    }

    
    public static String BuscarArchivoUsuario(String usuario) {
        if (usuario == null) return null;

        ensureStorage();

        String base = stripBinExt(usuario).trim();
        if (base.isEmpty()) return null;

        File f = new File(Usuarios, base + ".bin");
        return f.isFile() ? f.getPath() : null;
    }

    private static String stripBinExt(String s) {
        int p = s.lastIndexOf('.');
        return (p >= 0) ? s.substring(0, p) : s;
    }

    
    public static void setArchivoactivo(String patharchivo) throws FileNotFoundException {
        
        ArchivoActivo = new RandomAccessFile(patharchivo, "rw");
    }

    
    public static void setArchivoActivoPorUsuario(String usuario) throws IOException {
        ensureStorage();
        String path = BuscarArchivoUsuario(usuario);
        if (path == null) throw new FileNotFoundException("No existe el usuario: " + usuario);
        ArchivoActivo = new RandomAccessFile(path, "rw");
    }
}
