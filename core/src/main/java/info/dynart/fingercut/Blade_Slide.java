package info.dynart.fingercut;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Blade_Slide extends Blade {

    private Image blade;
    private Image lath;
    private int soundDir;

    float velX;

    Blade_Slide(GameScreen gameScreen, TextureAtlas atlas) {
        super(gameScreen, atlas);

        shadow = new Image(atlas.findRegion("blade1_shadow"));
        blade = new Image(atlas.findRegion("blade1"));
        lath = new Image(atlas.findRegion("lath1"));

        segments = new Segment[8];
        for (int i = 0; i < segments.length; i++) {
            segments[i] = new Segment();
        }
        setWidth(blade.getWidth());
        setHeight(blade.getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (disabled || !screen.state.equals("play")) {
            return;
        }

        velX = Math.signum(velX) * screen.vel*2;

        float y = getY() - screen.vel * delta;
        float x = getX() + velX * delta;

        if (x < 0) {
            velX = -velX;
            x = 0;
        }

        if (x > screen.gameStage.getWidth() - blade.getWidth()) {
            velX = -velX;
            x = screen.gameStage.getWidth() - blade.getWidth();
        }

        if ((velX > 0 && x + blade.getWidth()/2 > 120 && soundDir != Math.signum(velX)) ||
                (velX < 0 && x + blade.getWidth()/2 < 360 && soundDir != Math.signum(velX))) {
            soundDir = (int)Math.signum(velX);
            playSound(screen.blade1Sound);
        }

        setPosition(x, y);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        lath.draw(batch, parentAlpha);
        blade.draw(batch, parentAlpha);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);

        blade.setPosition(x, y - blade.getHeight()/2);
        lath.setPosition(0, y - lath.getHeight()/2 - 12);
        shadow.setPosition(x-15, y - blade.getHeight()/2 - 45);

        float x1 = x + 5;
        float y1 = y - blade.getHeight()/2 + 5;
        float x2 = x + blade.getWidth() - 10;
        float y2 = y - blade.getHeight()/2 + blade.getHeight() - 10;

        float s = 15;

        segments[0].set(x1 + s, y1, x2 - s, y1);
        segments[1].set(x1 + s, y1, x1 + s, y2);
        segments[2].set(x1 + s, y2, x2 - s, y2);
        segments[3].set(x2 - s, y1, x2 - s, y2);
        segments[4].set(x1, y1 + s, x2, y1 + s);
        segments[5].set(x1, y1 + s, x1, y2 - s);
        segments[6].set(x1, y2 - s, x2, y2 - s);
        segments[7].set(x2, y2 - s, x2, y1 + s);
    }

    @Override
    public void use() {
        super.use();
        velX = 1;
    }





}
