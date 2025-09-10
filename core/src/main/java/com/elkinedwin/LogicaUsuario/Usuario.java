package com.elkinedwin.LogicaUsuario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class Usuario {

    private String nombre;
    private String Usuario;
    private String password;
    private Long FechaRegistro;
    private Long UltimaSesion;
    private ArrayList<Integer> Mayorpuntuacion = new ArrayList<>();
    private ArrayList<Boolean> nivelescompletados=new ArrayList<>();
    private int TiempoTJugado = 0;
    private ArrayList<Integer> Tiemponivel= new ArrayList<>();
    private Map<String, Integer> configuraciones = new HashMap();
    private ImageIcon avatar;
    private int Puntuaciongeneral = 0;
    private ArrayList<Usuario> Amigos = new ArrayList<>();
    private int tiempopromedio[] = new int[6];
    public ArrayList<Partida> historialpartidas = new ArrayList<>();
    private ArrayList<Integer> partidasxnivel = new ArrayList<>();
    private int partidastotales=0;
    
    public Usuario(String usuario, String nombre, String password,Long Fechas) {
        this.FechaRegistro=Fechas;
        this.UltimaSesion=Fechas;
        this.Usuario = usuario;
        this.nombre = nombre;
        this.password = password;
        for (int i = 0; i < 7; i++) {
          Mayorpuntuacion.add(0);
        }
        for (int i = 0; i < 7; i++) {
         nivelescompletados.add(false);
        }
        for (int i = 0; i < 7; i++) {
          Tiemponivel.add(0);
        }
        configuraciones.put("Volumen",80);
        configuraciones.put("MoverArriba",38);
        configuraciones.put("MoverAbajo",40);
        configuraciones.put("MoverDere",39);
        configuraciones.put("MoverIzq",37);
        configuraciones.put("Reiniciar",82);
        configuraciones.put("Idioma", 1);
        avatar=new ImageIcon("../Imagenes/Ulogo.png");
        for (int i = 0; i < tiempopromedio.length; i++) {
         tiempopromedio[i]=0;   
        }
        for (int i = 0; i < 7; i++) {
         partidasxnivel.add(0);
        }
   
    }

    public int getPartidastotales() {
        return partidastotales;
    }

    public void setPartidastotales(int partidastotales) {
        this.partidastotales = partidastotales;
    }

    public int getPuntuaciongeneral() {
        return Puntuaciongeneral;
    }

    public void setPuntuaciongeneral(int Puntuaciongeneral) {
        this.Puntuaciongeneral = Puntuaciongeneral;
    }

    public int getTiempoTJugado() {
        return TiempoTJugado;
    }

    public void setTiempoTJugado(int TiempoTJugado) {
        this.TiempoTJugado = TiempoTJugado;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        this.Usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFecharegistro() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        return sdf.format(FechaRegistro);
    }

    public String getUltimasesion() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(UltimaSesion);
    }

    

    public void setFecharegistro(Long Fecha) {
    
    this.FechaRegistro =Fecha;
  }
    public void setUltimaSesion(Long Fecha){
   
    this.UltimaSesion = Fecha;
    }
   

    public int getPuntuacion() {
        return Puntuaciongeneral;
    }

    public void setPuntuacion(int puntuacion) {
        this.Puntuaciongeneral += puntuacion;
    }

    public int getTiempoPromedio(int nivel) {
        return tiempopromedio[nivel];
    }

    public void setTiempoPromedio(int nivel, int puntaje) {
        this.tiempopromedio[nivel] = (tiempopromedio[nivel] + puntaje);
    }
   public void setNivelCompletados(int nivel,Boolean estado){
   nivelescompletados.set(nivel-1, estado);
   }
   public void setPartidasnivel(int nivel,int partidas){
   int partidasactual =partidasxnivel.get(nivel-1);
   partidasactual +=partidas;
       partidasxnivel.set(nivel-1,partidasactual);
   }
  public void setConfiguracion(String configuracion,int ajuste){
  configuraciones.put(configuracion, ajuste);
  }
  public void setTiempoxnivel(int nivel,int tiempo){
   int tiempoactual=Tiemponivel.get(nivel-1);
   tiempoactual += tiempo;
      Tiemponivel.set(nivel-1, tiempoactual);
  }
}
