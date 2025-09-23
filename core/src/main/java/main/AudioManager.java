package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class AudioManager {

    private AudioManager() {}

    private static float volumenGlobal = 0.7f;
    private static final Set<Music> listaMusica = Collections.synchronizedSet(new HashSet<>());

    // ----------------------------
    // Música
    // ----------------------------
    public static Music cargarMusica(String rutaInterna, boolean reproducirEnBucle) {
        Music musica = Gdx.audio.newMusic(Gdx.files.internal(rutaInterna));
        musica.setVolume(volumenGlobal);
        musica.setLooping(reproducirEnBucle);
        listaMusica.add(musica);
        return musica;
    }

    public static void detenerMusica(Music musica) {
        if (musica != null) {
            musica.stop();
            listaMusica.remove(musica);
        }
    }

    public static void pausarMusica(Music musica) {
        if (musica != null) musica.pause();
    }

    public static void reanudarMusica(Music musica) {
        if (musica != null) musica.play();
    }

    // ----------------------------
    // Sonidos
    // ----------------------------
    public static Sound cargarSonido(String rutaInterna) {
        return Gdx.audio.newSound(Gdx.files.internal(rutaInterna));
    }

    public static long reproducirSonido(Sound sfx, float volumenRelativo) {
        if (sfx == null) return -1;
        return sfx.play(volumenRelativo * volumenGlobal);
    }

    public static long reproducirSonido(Sound sfx) {
        return reproducirSonido(sfx, 1f);
    }

    // ----------------------------
    // Volumen global
    // ----------------------------
    public static void setVolumenGlobal(float v) {
        volumenGlobal = Math.max(0f, Math.min(1f, v));
        synchronized (listaMusica) {
            for (Music m : listaMusica) {
                if (m != null) m.setVolume(volumenGlobal);
            }
        }
    }

    public static float getVolumenGlobal() {
        return volumenGlobal;
    }

    // ----------------------------
    // Utilidades
    // ----------------------------
    public static void detenerTodasMusicas() {
        synchronized (listaMusica) {
            for (Music m : listaMusica) {
                if (m != null) m.stop();
            }
            listaMusica.clear();
        }
    }
}
