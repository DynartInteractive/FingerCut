package info.dynart.fingercut;

import com.badlogic.gdx.math.Vector2;

public class Segment {

    Vector2 start = new Vector2();
    Vector2 end = new Vector2();

    public Segment() {
    }

    public void set(float sx, float sy, float ex, float ey) {
        start.x = sx;
        start.y = sy;
        end.x = ex;
        end.y = ey;
    }

    public void rotate(float angle) {
        start.rotate(angle);
        end.rotate(angle);
    }

    public void translate(float x, float y) {
        start.add(x, y);
        end.add(x, y);
    }
}
