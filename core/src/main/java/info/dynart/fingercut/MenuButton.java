package info.dynart.fingercut;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MenuButton extends TextButton {

    private boolean wasPress;
    private GameScreen screen;

    MenuButton(GameScreen screen, String text, TextButtonStyle style) {
        super(text, style);
        this.screen = screen;
    }

    @Override
    public void act(float delta) {
        if (isPressed()) {
            if (!wasPress) {
                screen.playSound(screen.pressSound);
            }
            wasPress = true;
        }
        else {
            wasPress = false;
        }
    }

}
