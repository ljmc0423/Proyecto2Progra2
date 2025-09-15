package GameLogic;

import com.elkinedwin.LogicaUsuario.ManejoUsuarios;


public enum Directions {
    UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0), QUIT(0, 0);

    public final int dx, dy;

    Directions(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

   
    public static Directions desdeTecla(int keycode) {
       
        int arr = 38, aba = 40, izq = 37, der = 39;

        if (ManejoUsuarios.UsuarioActivo != null && ManejoUsuarios.UsuarioActivo.configuracion != null) {
            Integer vArr = ManejoUsuarios.UsuarioActivo.configuracion.get("MoverArriba");
            Integer vAba = ManejoUsuarios.UsuarioActivo.configuracion.get("MoverAbajo");
            Integer vIzq = ManejoUsuarios.UsuarioActivo.configuracion.get("MoverIzq");
            Integer vDer = ManejoUsuarios.UsuarioActivo.configuracion.get("MoverDer");

            if (vArr != null && vArr != 0) arr = vArr;
            if (vAba != null && vAba != 0) aba = vAba;
            if (vIzq != null && vIzq != 0) izq = vIzq;
            if (vDer != null && vDer != 0) der = vDer;
        }

        if (keycode == arr) return Directions.UP;
        if (keycode == aba) return Directions.DOWN;
        if (keycode == izq) return Directions.LEFT;
        if (keycode == der) return Directions.RIGHT;
        return null;
    }

    
     
    public static boolean esTeclaReiniciar(int keycode) {
        int rei = 82;
        if (ManejoUsuarios.UsuarioActivo != null && ManejoUsuarios.UsuarioActivo.configuracion != null) {
            Integer v = ManejoUsuarios.UsuarioActivo.configuracion.get("Reiniciar");
            if (v != null && v != 0) rei = v;
        }
        return keycode == rei;
    }
}
