package com.elkinedwin.sokoban;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.ImageIcon;

public class Usuario {

    private String Usuario;
    private String nombre;
    private String password;
    private Calendar FechaRegistro;
    private Calendar UltimaSesion;
    private Boolean niveles[] = new Boolean[6];
    private int Mejorpuntaje[] = new int[6];
    private int TiempoJugado = 0;
    private int Volumen;
    private IDIOMA idioma = IDIOMA.ESPANOL;
    private String controles;
    private ImageIcon avatar;
    private int Puntuaciongeneral = 0;
    private ArrayList<Usuario> Amigos = new ArrayList<>();
    private int tiempopromedio[] = new int[6];
    private int cantidadpartidasxnivel[]=new int[6];

    public Usuario(String usuario, String nombre, String password) {
        this.Usuario = usuario;
        this.nombre = nombre;
        this.password = password;
        this.FechaRegistro = Calendar.getInstance();
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

    public void setUltimaSesion() {
        this.UltimaSesion = Calendar.getInstance();
    }

    public int getTiempoJugado() {
        return TiempoJugado;
    }

    public void setTiempoJugado(int tiempo) {
        this.TiempoJugado += tiempo;
    }

    public int getVolumen() {
        return Volumen;
    }

    public void setVolumen(int Volumen) {
        this.Volumen = Volumen;
    }

    public IDIOMA getIdioma() {
        return idioma;
    }

    public void setIdioma(IDIOMA idioma) {
        this.idioma = idioma;
    }

    public ImageIcon getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageIcon avatar) {
        this.avatar = avatar;
    }
    public int getPuntuacion(){
    return Puntuaciongeneral;
    }
    public void setPuntuacion(int puntuacion){
    this.Puntuaciongeneral += puntuacion;
    }
    public void agregarAmigo(Usuario usuario){
    Amigos.add(usuario);
    }
    public void eliminarAmigo(Usuario usuario){
    Amigos.remove(usuario);
    }
    public int getTiempoPromedio(int nivel){
    return tiempopromedio[nivel];
    }
    public void setTiempoPromedio(int nivel,int puntaje){
    this.tiempopromedio[nivel]= (tiempopromedio[nivel]+puntaje)/cantidadpartidasxnivel[nivel];
    }
    public void SumarCantidadnivel(int nivel){
    this.cantidadpartidasxnivel[nivel]++;
    }
}
