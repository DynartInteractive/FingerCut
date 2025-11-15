package info.dynart.fingercut;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

abstract class Blade extends Actor {

    private boolean scoreCounted;
    private Sound sound = null;
    private long soundId = 0;

    Image shadow;
    GameScreen screen;
    Segment[] segments;
    boolean disabled;
    boolean soundDisabled;

    Blade(GameScreen gameScreen, TextureAtlas atlas) {
        screen = gameScreen;
        disabled = true;
    }

    public void init() {
        scoreCounted = false;
        disabled = true;
        sound = null;
        soundId = 0;
        setPosition(0, -1000);
    };

    public void use() {
        disabled = false;
        scoreCounted = false;
        soundDisabled = false;
    }

    void playSound(Sound sound) {
        if (soundDisabled) {
            return;
        }
        this.sound = sound;
        soundId = screen.playSound(sound, getSoundVolume());
    }

    private float getSoundVolume() {
        float y = screen.gameStage.getHeight() / 2;
        y = Math.abs(y - getY());
        if (y > 320)  {
            return 0;
        }
        float reverseDistance = 1f - y/320f;
        return reverseDistance * reverseDistance;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (getY() < -getHeight()/2 - 20) {
            disabled = true;
        }
        if (disabled || !screen.state.equals("play")) {
            return;
        }

        if (getY() < screen.gameStage.fingerPos.y && !scoreCounted) {
            screen.incScore();
            scoreCounted = true;
        }

        if (soundId != 0) {
            sound.setVolume(soundId, getSoundVolume());
        }
    }



    public Image getShadow() {
        return shadow;
    }

}
