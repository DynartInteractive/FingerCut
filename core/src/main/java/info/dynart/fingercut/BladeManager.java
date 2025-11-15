package info.dynart.fingercut;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

class BladeManager {

    private Blade[] blades;
    private GameScreen screen;
    private Random rand;
    private int lastCase;
    private Blade lastBlade;

    BladeManager(GameScreen screen, TextureAtlas atlas) {

        this.screen = screen;

        // create blades
        blades = new Blade[30];
        for (int i = 0; i < blades.length; i++) {
            if (i % 2 == 0) {
                blades[i] = new Blade_Slide(screen, atlas);
            }
            else {
                blades[i] = new Blade_Rotate(screen, atlas);
            }

        }

        // add shadows and then blades
        for (int i = 0; i < blades.length; i++) {
            screen.gameStage.addActor(blades[i].shadow);
        }

        for (int i = 0; i < blades.length; i++) {
            screen.gameStage.addActor(blades[i]);
        }

        rand = new Random();
    }

    void init() {
        for (int i = 0; i < blades.length; i++) {
            blades[i].init();
        }
        lastCase = 0;
        createFirst();
    }

    boolean detectCollision(Vector2 fpos, float r) {
        for (int i = 0; i < blades.length; i++) {
            Blade blade = blades[i];
            if (blade.disabled) {
                continue;
            }
            for (int j = 0; j < blade.segments.length; j++) {
                Segment segment = blade.segments[j];
                if (Intersector.intersectSegmentCircle(segment.start, segment.end, fpos, r*r)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean needsNew() {
        float maxY = -1000;
        for (int i = 0; i < blades.length; i++) {
            if (blades[i].disabled) {
                continue;
            }
            if (blades[i].getY() > maxY) {
                maxY = blades[i].getY();
                lastBlade = blades[i];
            }
        }
        return maxY < 856 && maxY > -1000;
    }

    private Blade_Slide getNewSlide() {
        for (int i = 0; i < blades.length; i++) {
            if (blades[i].disabled && blades[i] instanceof Blade_Slide) {
                blades[i].use();
                return (Blade_Slide)blades[i];
            }
        }
        return null;
    }

    private Blade_Rotate getNewRotate() {
        for (int i = 0; i < blades.length; i++) {
            if (blades[i].disabled && blades[i] instanceof Blade_Rotate) {
                blades[i].use();
                return (Blade_Rotate)blades[i];
            }
        }
        return null;
    }

    void act(float delta) {
        if (!needsNew()) {
            return;
        }
        int c;
        do {
            c = rand.nextInt(5);
        }
        while (c == lastCase);
        lastCase = c;
        switch (c) {
            case 0: createSingleSlide(); break;
            case 1: createDoubleSlide(); break;
            case 2: createSingleRotate(); break;
            case 3: createDoubleRotate(); break;
            case 4: createChainSlide(); break;
        }
    }

    // ------------------------
    // --- blade variations ---
    // ------------------------

    private float getRandomX(Blade_Slide blade) {
        return (float)Math.random() * (screen.gameStage.getWidth() - blade.getWidth());
    }

    private float getTopY(Blade blade) {
        return lastBlade.getY() + lastBlade.getHeight()/2 + blade.getHeight()/2 + 150;
    }

    private void createFirst() {
        Blade_Slide blade = getNewSlide();
        blade.setPosition(getRandomX(blade), 800);
        lastBlade = blade;
    }

    private void createSingleSlide() {
        Blade_Slide blade = getNewSlide();
        blade.setPosition(getRandomX(blade), getTopY(blade));
    }

    private void createSingleRotate() {
        Blade_Rotate blade = getNewRotate();
        blade.setPosition(screen.gameStage.getWidth()/2, getTopY(blade) + 70);
        blade.setRotation(rand.nextInt(180));
    }

    private void createDoubleSlide() {
        Blade_Slide blade1 = getNewSlide();
        Blade_Slide blade2 = getNewSlide();
        blade2.soundDisabled = true;
        switch (rand.nextInt(3)) {
            case 0:
                blade1.setPosition(0, getTopY(blade1));
                blade2.setPosition(screen.gameStage.getWidth() - blade2.getWidth(), blade1.getY() + blade2.getHeight() + 40);
                break;
            case 1:
                blade1.setPosition((screen.gameStage.getWidth() - blade2.getWidth()) / 2, getTopY(blade1));
                blade2.setPosition(screen.gameStage.getWidth() - blade2.getWidth(), blade1.getY() + blade2.getHeight() + 40);
                break;
            case 2:
                blade1.setPosition((screen.gameStage.getWidth() - blade2.getWidth()) / 2, getTopY(blade1));
                blade2.setPosition(0, blade1.getY() + blade2.getHeight() + 20);
                break;
        }
        // velocity swap
        if (rand.nextInt(2) == 1) {
            blade2.velX = -blade2.velX;
        }
        // position swap
        if (rand.nextInt(2) == 1) {
            float tmp = blade1.getY();
            blade1.setY(blade2.getY());
            blade2.setY(tmp);
        }
    }

    private void createDoubleRotate() {
        Blade_Rotate blade1 = getNewRotate();
        Blade_Rotate blade2 = getNewRotate();
        int rot = rand.nextInt(180);
        int rotDiff = rand.nextInt(4) * 45;
        // set blades
        blade1.setPosition(40, getTopY(blade1));
        blade1.setRotation(rot);
        blade2.setPosition(screen.gameStage.getWidth() - 40, blade1.getY());
        blade2.setRotation(rot + rotDiff);
        blade2.soundDisabled = true;
        // position swap
        if (rand.nextInt(2) == 1) {
            float tmp = blade1.getX();
            blade1.setX(blade2.getX());
            blade2.setX(tmp);
        }
        // rotation direction swap
        if (rand.nextInt(2) == 1) {
            blade1.rotVel = -blade1.rotVel;
        }
        if (rand.nextInt(2) == 1) {
            blade2.rotVel = -blade2.rotVel;
        }
    }

    private void createChainSlide() {
        Blade_Slide[] slides = new Blade_Slide[rand.nextInt(3) + 3];
        slides[0] = getNewSlide();
        slides[0].setPosition(0, getTopY(slides[0]));
        for (int i = 1; i < slides.length; i++) {
            float x = i % 2 * (screen.gameStage.getWidth() - slides[0].getWidth());
            float y = slides[i-1].getY() + slides[0].getHeight() + 40;
            slides[i] = getNewSlide();
            slides[i].setPosition(x, y);
            if (i % 4 == 1 || i % 4 == 2) {
                slides[i].soundDisabled = true;
            }
        }
    }

}
