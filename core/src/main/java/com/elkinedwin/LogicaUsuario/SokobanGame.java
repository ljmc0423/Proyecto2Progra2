package com.elkinedwin.LogicaUsuario;

import com.badlogic.gdx.Game;
import Screens.GameScreen;

public class SokobanGame extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
