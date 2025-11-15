package info.dynart.fingercut;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SplashScreen implements Screen {

    private Stage stage;
    private Image logo;
    private FingerCut app;
    private GameScreen gameScreen;

    private int count = 0;

    SplashScreen(FingerCut a, GameScreen s) {
        app = a;
        gameScreen = s;
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = new ExtendViewport(480, 480);
        viewport.update(width, height, true);
        stage.setViewport(viewport);

        logo.setX((480 - logo.getWidth()) / 2);
        logo.setY((856 - logo.getHeight()) / 2);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.102f, 0.102f, 0.102f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
        if (count == 0) {
            //app.advertMgr.loadBanner();
        }
        else if (count == 1)  {
            gameScreen.load();
            app.setScreen(gameScreen);
        }
        count++;
    }

    @Override
    public void show() {
        stage = new Stage();
        logo = new Image(new Texture(Gdx.files.internal("data/dynart.png")));
        stage.addActor(logo);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

}
