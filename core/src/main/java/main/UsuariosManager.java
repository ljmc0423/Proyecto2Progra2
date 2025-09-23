package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UsuariosManager {

    private static final File CARPETA_USUARIOS = new File("Usuarios");
    public static UsuarioDat usuarioActivo;
    public static ArrayList<String> listaUsuarios = new ArrayList<>();

    public static void iniciarCarpeta() {
        if (!CARPETA_USUARIOS.exists()) CARPETA_USUARIOS.mkdirs();
    }

    public static boolean existeUsuario(String nombre) {
        File f = new File(CARPETA_USUARIOS, nombre + ".dat");
        return f.exists();
    }

    public static UsuarioDat crearUsuario(String nombre, String user, String pass) throws IOException {
        iniciarCarpeta();
        File f = new File(CARPETA_USUARIOS, user + ".dat");
        if (f.exists()) throw new IOException("El usuario ya existe");
        usuarioActivo = new UsuarioDat(f.getPath());
        usuarioActivo.setNombre(nombre);
        usuarioActivo.setUsuario(user);
        usuarioActivo.setContrasena(pass);
        usuarioActivo.guardarTodo();
        return usuarioActivo;
    }

    public static UsuarioDat cargarUsuario(String user) throws IOException {
        iniciarCarpeta();
        File f = new File(CARPETA_USUARIOS, user + ".dat");
        if (!f.exists()) throw new IOException("Usuario no encontrado");
        usuarioActivo = new UsuarioDat(f.getPath());
        return usuarioActivo;
    }

    public static void borrarUsuario(String user) {
        File f = new File(CARPETA_USUARIOS, user + ".dat");
        if (f.exists()) f.delete();
    }

    public static String[] listarUsuarios() {
        iniciarCarpeta();
        return CARPETA_USUARIOS.list((dir, nombre) -> nombre.endsWith(".dat"));
    }
}
