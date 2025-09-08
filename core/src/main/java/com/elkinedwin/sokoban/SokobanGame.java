package com.elkinedwin.sokoban;

import com.badlogic.gdx.Game;
import Screens.MenuScreen;

public class SokobanGame extends Game {

    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }
}
