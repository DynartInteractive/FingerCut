package info.dynart.fingercut;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameStage extends Stage {

    boolean fingerDown;
    int fingerNumber = -1;
    Vector2 fingerPos = new Vector2();
    Vector2 lastFingerPos = new Vector2();

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean ret = super.touchDown(screenX, screenY, pointer, button);
        if (fingerNumber == -1) {
            fingerNumber = pointer;
            fingerPos.set(screenX, screenY);
            lastFingerPos.set(fingerPos);
            screenToStageCoordinates(fingerPos);
            fingerDown = true;
        }
        return ret;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean ret = super.touchDown(screenX, screenY, pointer, button);
        if (pointer == fingerNumber) {
            fingerDown = false;
        }
        return ret;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        boolean ret = super.touchDragged(screenX, screenY, pointer);
        if (fingerDown && pointer == fingerNumber) {
            fingerPos.set(screenX, screenY);
            screenToStageCoordinates(fingerPos);
        }
        return ret;
    }

}
