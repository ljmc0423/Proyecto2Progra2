
package com.elkinedwin.LogicaUsuario;




public class Partida {

private String Fecha;
private int intentos=0;
private String logros;
private int tiempo;

public Partida(String Fecha,int intentos,String logros,int tiempo){
this.Fecha=Fecha;
this.intentos=intentos;
this.logros=logros;
this.tiempo=tiempo;

    
}

    public int getIntentos() {
        return intentos;
    }

    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }

    public String getLogros() {
        return logros;
    }

    public void setLogros(String logros) {
        this.logros += logros;
    }
   public String getFecha(){
   return Fecha;
   }
   public int getTiempo(){
   return tiempo;
   }
    public void setTiempo(int tiempo){
    this.tiempo = tiempo;
    }
}
