package com.elkinedwin.LogicaUsuario;

import com.badlogic.gdx.Game;
import Screens.GameScreen;

public class SokobanMain extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
