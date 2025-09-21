package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class TutorialScreen extends BaseScreen {

    private final Game game;
    private Texture diapositivas[];

    private final com.badlogic.gdx.Screen nextScreenAfter;

    public TutorialScreen(Game game) {
        this(game, new GameScreen(game, 0));
    }

    public TutorialScreen(Game game, com.badlogic.gdx.Screen nextScreenAfter) {
        this.game = game;
        this.nextScreenAfter = (nextScreenAfter != null) ? nextScreenAfter : new GameScreen(game, 0);
    }

    @Override
    protected void onShow() {
        diapositivas = new Texture[]{
            new Texture("ui/instruccion_1.png"),
            new Texture("ui/instruccion_2.png"),
            new Texture("ui/instruccion_3.png")
        };

        float fadeTime = 2f;
        float showTime = 4f;
        float delay = 0;

        for (int i = 0; i < diapositivas.length; i++) {
            int index = i;
            Image img = new Image(diapositivas[i]);
            img.getColor().a = 0;
            img.setFillParent(true);
            stage.addActor(img);

            img.addAction(
                sequence(
                    delay(delay),
                    fadeIn(fadeTime),
                    delay(showTime),
                    fadeOut(fadeTime),
                    run(() -> {
                        if (index == diapositivas.length - 1) {
                            game.setScreen(nextScreenAfter);
                        }
                    })
                )
            );

            delay += fadeTime + showTime + fadeTime;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (diapositivas != null) {
            for (Texture t : diapositivas) t.dispose();
        }
    }
}
