package main;

import com.badlogic.gdx.Game;
import pantallas.LoginScreen;

public class SokobanMain extends Game {

    @Override
    public void create() {
        setScreen(new LoginScreen(this));
    }
}
