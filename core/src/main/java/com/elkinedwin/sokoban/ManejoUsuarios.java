
package com.elkinedwin.sokoban;

import java.util.ArrayList;


public class ManejoUsuarios {
public static ArrayList<Usuario> ListaUsuarios=new ArrayList<>();

public static Usuario buscarusuario(int indice,String usuario){
if(indice>ListaUsuarios.size()){
return null;
}
if(ListaUsuarios.get(indice).getUsuario().equals(usuario)){
return ListaUsuarios.get(indice);
}
return buscarusuario(indice-1,usuario);
}   
public static boolean BorrarUsuario(String usuario){
Usuario borrar=buscarusuario(0,usuario);
if(borrar!=null){
ListaUsuarios.remove(borrar);
return true;
}
return false;
}
public static boolean Usuariounico(String usuario){
if(buscarusuario(0,usuario)!=null){
return true;
}
return false;
}
    
}
