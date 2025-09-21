package com.elkinedwin.LogicaUsuario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public final class AudioX {
    private AudioX(){}

    public static Music newMusic(String internalPath) {
        Music m = Gdx.audio.newMusic(Gdx.files.internal(internalPath));
        AudioBus.registerMusic(m);
        return m;
    }
    public static Music newMusic(FileHandle fh) {
        Music m = Gdx.audio.newMusic(fh);
        AudioBus.registerMusic(m);
        return m;
    }

    public static Sound newSound(String internalPath) {
        return Gdx.audio.newSound(Gdx.files.internal(internalPath));
    }
    public static Sound newSound(FileHandle fh) {
        return Gdx.audio.newSound(fh);
    }

    public static long play(Sound sfx) { return AudioBus.play(sfx, 1f); }
    public static long play(Sound sfx, float volumeRelativo) { return AudioBus.play(sfx, volumeRelativo); }
}
