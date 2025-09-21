package com.elkinedwin.LogicaUsuario;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class AudioBus {
    private static float master = 0.7f;
    private static final Set<Music> musics = Collections.synchronizedSet(new HashSet<Music>());

    private AudioBus(){}

    public static void setMasterVolume(float v) {
        if (v < 0f) v = 0f;
        if (v > 1f) v = 1f;
        master = v;
        synchronized (musics) {
            for (Music m : musics) {
                if (m != null) m.setVolume(master);
            }
        }
    }

    public static float getMasterVolume() { return master; }

    public static void registerMusic(Music m) {
        if (m == null) return;
        musics.add(m);
        m.setVolume(master);
    }

    public static void unregisterMusic(Music m) {
        if (m == null) return;
        musics.remove(m);
    }

    public static long play(Sound sfx, float volume) {
        if (sfx == null) return -1L;
        return sfx.play(volume * master);
    }
}
