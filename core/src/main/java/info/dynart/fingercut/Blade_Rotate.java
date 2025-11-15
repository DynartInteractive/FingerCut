package info.dynart.fingercut;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Blade_Rotate extends Blade {

    private Image bladeImage;
    private Image screwImage;

    private boolean topSound;
    float rotVel;

    Blade_Rotate(GameScreen gameScreen, TextureAtlas atlas) {
        super(gameScreen, atlas);

        shadow = new Image(atlas.findRegion("blade2_shadow"));
        shadow.setOrigin(shadow.getWidth()/2, shadow.getHeight()/2);
        bladeImage = new Image(atlas.findRegion("blade2"));
        bladeImage.setOrigin(bladeImage.getWidth()/2, bladeImage.getHeight()/2);
        screwImage = new Image(atlas.findRegion("screw"));

        segments = new Segment[8];
        for (int i = 0; i < segments.length; i++) {
            segments[i] = new Segment();
        }

        setWidth(bladeImage.getWidth());
        setHeight(bladeImage.getWidth()); // because it is rotating

        rotVel = 1;
    }

    @Override
    public void setRotation(float angle) {
        super.setRotation(angle);
        bladeImage.setRotation(angle);
        shadow.setRotation(angle);
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        if (disabled || !screen.state.equals("play")) {
            return;
        }

        setRotation(screen.vel * delta * rotVel + getRotation());

        int r = (int)bladeImage.getRotation() % 360;
        if (r > 90 && r < 180 && !topSound) {
            playSound(screen.blade2Sound);
            topSound = true;
        }
        else if (r > 270 && topSound) {
            playSound(screen.blade2Sound);
            topSound = false;
        }

        float y = getY() - screen.vel * delta;

        setPosition(getX(), y);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        bladeImage.draw(batch, parentAlpha);
        screwImage.draw(batch, parentAlpha);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);

        screwImage.setPosition(x - screwImage.getWidth()/2, y - screwImage.getHeight()/2);
        bladeImage.setPosition(x - bladeImage.getWidth()/2, y - bladeImage.getHeight()/2);
        shadow.setPosition(bladeImage.getX() - 15, bladeImage.getY() - 45);

        float x1 = -bladeImage.getWidth()/2 + 5;
        float y1 = -bladeImage.getHeight()/2 + 5;
        float x2 = bladeImage.getWidth()/2 - 5;
        float y2 = bladeImage.getHeight()/2 - 5;

        float s = 15;

        segments[0].set(x1 + s, y1, x2 - s, y1);
        segments[1].set(x1 + s, y1, x1 + s, y2);
        segments[2].set(x1 + s, y2, x2 - s, y2);
        segments[3].set(x2 - s, y1, x2 - s, y2);
        segments[4].set(x1, y1 + s, x2, y1 + s);
        segments[5].set(x1, y1 + s, x1, y2 - s);
        segments[6].set(x1, y2 - s, x2, y2 - s);
        segments[7].set(x2, y2 - s, x2, y1 + s);

        for (int i = 0; i < segments.length; i++) {
            segments[i].rotate(bladeImage.getRotation());
            segments[i].translate(x, y);
        }

    }

    @Override
    public void init() {
        super.init();
        bladeImage.setRotation(0);
        shadow.setRotation(0);
    }

    @Override
    public void use() {
        super.use();
        rotVel = 1;
    }

}
