package info.dynart.fingercut;

import com.badlogic.gdx.Game;

public class FingerCut extends Game {

	enum Achi { Gold, Silver, Bronze }
	static final String AchiNames[] = {"Gold Finger", "Silver Finger", "Bronze Finger"};
	static final String AchiRegions[] = {"achi_gold", "achi_silver", "achi_bronze"};

	AdHandler adHandler;

	public FingerCut(AdHandler adHandler) {
		this.adHandler = adHandler;
	}

	@Override
	public void create() {
		setScreen(new SplashScreen(this, new GameScreen(this)));
	}

}
