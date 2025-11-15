package info.dynart.fingercut.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;

import barsoosayque.libgdxoboe.OboeAudio;
import info.dynart.fingercut.FingerCut;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.

        initialize(new FingerCut(new AndroidAdHandler(this)), configuration);
    }

    @Override
    public AndroidAudio createAudio(android.content.Context context, AndroidApplicationConfiguration config) {
        // Use Oboe for better audio latency on Android
        return new OboeAudio(context.getAssets());
    }
}
