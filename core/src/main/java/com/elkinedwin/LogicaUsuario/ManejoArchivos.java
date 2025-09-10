/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elkinedwin.LogicaUsuario;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.Calendar;

public class ManejoArchivos {
public static File Usuarios;
public static RandomAccessFile ArchivoActivo; 
public ManejoArchivos(){
Usuarios=new File("Usuarios");
Usuarios.mkdir();
}    
private RandomAccessFile CrearArchivoUsuario(String nombrearchivo) throws FileNotFoundException{
return new RandomAccessFile("Usuarios/"+nombrearchivo+".bin","rw");
}
/*
Formato:                    
                              0  1  2  3  4  5  6
boolean: niveles completados [1][2][3][4][5][6][7]
                              7  11   15 19  23   27  31
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
                              81        85    89        93         97
int:KeyCode controles        [arriba][abajo][derecha][izquierda][Reiniciar]
                              101 105 109 113 117  121   125
int: Tiempototalxnivel       [1] [2] [3] [4] [5] [6]  [7]
                               129
int Idioma                    int                            //Espanol=1,Ingles=2,Portugues=3;
                               133                  141
Long Fechas:                 FechaRegistro     UltimaSesion        
                              149
String Nombre:               Nombre
                             ?        {Esto es para el historial de partidas, cada partida tendra la fecha de realizacion
                                       los intentos de esa sesion de juego o partida,(cada partida sera si se completa
                                       el nivel o presiona salir), tendra tambien los logros que consiguio en esa partida,Y el tiempo de esa partida
String datos               Usuario,Contrasena,PartidasFecha.PartidasIntentos.
                           PartidasLogros.PartidasTiempo,ImagenPath,
                           
*/
public Boolean InicializarUsuario(String nombre,String usuario,String password) throws FileNotFoundException, IOException{
    if(BuscarArchivoUsuario(usuario)==null){
    return false;
    }
    
    RandomAccessFile archivo=CrearArchivoUsuario(nombre);
    //Valores de niveles iniciados en False y 0
    for (int i = 0; i < 7; i++) {
     archivo.writeBoolean(false);
    }
    for (int i = 0; i < 17; i++) {
      archivo.writeInt(0);
    }
  //Volumen 
    archivo.writeInt(80);
 //Asignar KeyCode teclas
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
   String datos="";
   datos += usuario+","+password+","+" "+"."+" "+"."+" "+"."+" "+","+" "+",";
   archivo.writeUTF(datos);
  ManejoUsuarios.UsuarioActivo=new Usuario(usuario,nombre,password,fechas);
  return true;
    
}
public String BuscarArchivoUsuario(String usuario) {
    if (usuario == null) return null;

    String base = stripBinExt(usuario).trim();
    if (base.isEmpty()) return null;

    File dir = new File("Usuarios");
    if (!dir.isDirectory()) return null;

    File f = new File(dir, base + ".bin");
    return f.isFile() ? f.getPath() : null; 
}

private String stripBinExt(String s) {
    int p = s.lastIndexOf('.');
    return (p >= 0) ? s.substring(0, p) : s;
}

public void setArchivoactivo(String patharchivo) throws FileNotFoundException{
ManejoArchivos.ArchivoActivo=new RandomAccessFile("patharchivo","rw");
}

}
