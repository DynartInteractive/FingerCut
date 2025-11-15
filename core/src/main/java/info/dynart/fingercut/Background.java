package info.dynart.fingercut;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Background extends Image {

    private GameScreen screen;

    Background(GameScreen gameScreen, AtlasRegion region) {
        super(region);
        screen = gameScreen;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float y = getY() - screen.vel * delta;
        if (y < -getImageHeight()) {
            y = 0;
        }
        setY(y);
    }

}
