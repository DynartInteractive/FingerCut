package info.dynart.fingercut.android;
import com.badlogic.gdx.backends.android.AndroidApplication;

import info.dynart.fingercut.AdHandler;


public class AndroidAdHandler implements AdHandler {

    private AndroidApplication app;


    AndroidAdHandler(AndroidApplication app) {
        this.app = app;

    }


    @Override
    public void showAd() {

    }
}
