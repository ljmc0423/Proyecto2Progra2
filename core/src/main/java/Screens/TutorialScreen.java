package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class TutorialScreen extends BaseScreen {

    private final Game game;
    private Texture diapositivas[];

    public TutorialScreen(Game game) {
        this.game = game;
    }

    @Override
    protected void onShow() {
        diapositivas = new Texture[]{
            new Texture("ui/instruccion_1.png"),
            new Texture("ui/instruccion_2.png"),
            new Texture("ui/instruccion_3.png")
        };

        float fadeTime = 1f;
        float showTime = 2f;

        float delay = 0;
        for (int i = 0; i < diapositivas.length; i++) {
            int index = i;
            Image img = new Image(diapositivas[i]);
            img.getColor().a = 0;
            img.setFillParent(true);
            stage.addActor(img);

            img.addAction(
                    Actions.sequence(
                            Actions.delay(delay),
                            Actions.fadeIn(fadeTime),
                            Actions.delay(showTime),
                            Actions.fadeOut(fadeTime),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    if (index == diapositivas.length - 1) {
                                        game.setScreen(new GameScreen(game, 0));
                                    }
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
            for (Texture t : diapositivas) {
                t.dispose();
            }
        }
    }
}
