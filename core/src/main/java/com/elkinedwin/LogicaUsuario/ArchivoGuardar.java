package com.elkinedwin.LogicaUsuario;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ArchivoGuardar {

    private static RandomAccessFile archivo;

    public static void usarArchivo(RandomAccessFile ref){
        archivo = ref;
    }

    public static void guardarNivelesCompletados() throws IOException{
        archivo.seek(0);
        for (int i = 1; i < 8; i++) {
            archivo.writeBoolean(ManejoUsuarios.UsuarioActivo.getNivelCompletado(i));
        }
    }

    public static void guardarMayorPuntuacion() throws IOException{
        archivo.seek(7);
        for (int i = 1; i < 8; i++) {
            archivo.writeInt(ManejoUsuarios.UsuarioActivo.getMayorPuntuacion(i));
        }
    }

    public static void guardarTiempoTotal() throws IOException{
        archivo.seek(35);
        archivo.writeInt(ManejoUsuarios.UsuarioActivo.getTiempoJugadoTotal());
    }

    public static void guardarPuntuacionGeneral() throws IOException{
        archivo.seek(39);
        archivo.writeInt(ManejoUsuarios.UsuarioActivo.getPuntuacionGeneral());
    }

    public static void guardarTotalPartidas() throws IOException{
        archivo.seek(43);
        archivo.writeInt(ManejoUsuarios.UsuarioActivo.getPartidasTotales());
    }

    public static void guardarPartidasPorNivel() throws IOException{
        archivo.seek(47);
        for (int i = 1; i < 8; i++) {
            archivo.writeInt(ManejoUsuarios.UsuarioActivo.getPartidasPorNivel(i));
        }
    }

    public static void guardarConfiguracion() throws IOException{
        archivo.seek(77);

        int vol       = ManejoUsuarios.UsuarioActivo.getConfiguracion("Volumen");
        int arriba    = ManejoUsuarios.UsuarioActivo.getConfiguracion("MoverArriba");
        int abajo     = ManejoUsuarios.UsuarioActivo.getConfiguracion("MoverAbajo");
        int derecha   = ManejoUsuarios.UsuarioActivo.getConfiguracion("MoverDere");
        if (derecha == 0) derecha = ManejoUsuarios.UsuarioActivo.getConfiguracion("MoverDer");
        int izquierda = ManejoUsuarios.UsuarioActivo.getConfiguracion("MoverIzq");
        int reiniciar = ManejoUsuarios.UsuarioActivo.getConfiguracion("Reiniciar");

        archivo.writeInt(vol);
        archivo.writeInt(arriba);
        archivo.writeInt(abajo);
        archivo.writeInt(derecha);
        archivo.writeInt(izquierda);
        archivo.writeInt(reiniciar);

        archivo.seek(129);
        archivo.writeInt(ManejoUsuarios.UsuarioActivo.getConfiguracion("Idioma"));
    }

    public static void guardarTiempoPorNivel() throws IOException{
        archivo.seek(101);
        for (int i = 1; i < 8; i++) {
            archivo.writeInt(ManejoUsuarios.UsuarioActivo.getTiempoPorNivel(i));
        }
    }

    public static void guardarFechas() throws IOException{
        archivo.seek(133);
        long reg = ManejoUsuarios.UsuarioActivo.getFechaRegistro() == null ? 0L : ManejoUsuarios.UsuarioActivo.getFechaRegistro();
        archivo.writeLong(reg);
        archivo.seek(141);
        Long anterior = ManejoUsuarios.UsuarioActivo.sesionAnterior;
        if (anterior == null) anterior = ManejoUsuarios.UsuarioActivo.getUltimaSesion();
        if (anterior == null) anterior = 0L;
        archivo.writeLong(anterior);
    }

    public static void guardarDatosUTF() throws IOException {
        archivo.seek(149);

        archivo.writeUTF(ManejoUsuarios.UsuarioActivo.getNombre());

        StringBuilder datos = new StringBuilder();

        String usuario  = ManejoUsuarios.UsuarioActivo.getUsuario();
        String pass     = ManejoUsuarios.UsuarioActivo.getContrasena();

        datos.append(usuario == null ? "" : usuario)
             .append(',')
             .append(pass == null ? "" : pass)
             .append(',');

        if (ManejoUsuarios.UsuarioActivo.historial == null ||
            ManejoUsuarios.UsuarioActivo.historial.isEmpty()) {
            datos.append("...");
        } else {
            for (int i = 0; i < ManejoUsuarios.UsuarioActivo.historial.size(); i++) {
                Partida p = ManejoUsuarios.UsuarioActivo.historial.get(i);

                String fecha    = (p.getFecha()  == null) ? "" : p.getFecha();
                String intentos = String.valueOf(p.getIntentos());
                String logros   = (p.getLogros() == null) ? "" : p.getLogros();
                String tiempo   = String.valueOf(p.getTiempo());

                datos.append(fecha).append('.')
                     .append(intentos).append('.')
                     .append(logros).append('.')
                     .append(tiempo);

                if (i < ManejoUsuarios.UsuarioActivo.historial.size() - 1) {
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
