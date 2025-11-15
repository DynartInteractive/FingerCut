package info.dynart.fingercut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {

    // resources
    private BitmapFont font1;
    private BitmapFont font2;
    private TextureAtlas atlas;
    private Sound startSound;
    Sound blade1Sound;
    Sound blade2Sound;
    Sound pressSound;
    private Sound releaseSound;
    private Sound bloodSound;
    private Sound laughSound;
    private Sound achiSound;
    private Sound[] painSound = new Sound[2];

    // UI
    GameStage gameStage;
    private Stage mainMenuStage;
    private Stage gameOverStage;
    private Stage settingsStage;
    private Background background[] = new Background[2];
    private Image fingerImage;
    private Image bloodImage;
    private Image achiIcon;
    private Label achiLabel;
    private Table achiTable;
    private Table gameTable;
    private Table gameOverTable;
    private Table mainMenuTable;
    private Label holdLabel;
    private Label scoreLabel;
    private Label gameOverScoreLabel;
    private Table settingsTable;
    private Viewport viewport = new ExtendViewport(480, 480);

    // game mechanics
    private Preferences prefs; // next time this needs to be in the main class
    private FingerCut game;
    private BladeManager bladeMgr;
    private int score;
    float vel;
    private float fingerRadius;
    private float deathTime = 0;
    String state = "mainmenu";
    private String lastState = "";
    private Vector2 step = new Vector2();
    private Vector2 fpos = new Vector2();
    private String username = "";
    private Random rand;

    // achievement related
    private float hideAchiTime;
    private static final float maxHideAchiTime = 1.5f;
    private HashMap<FingerCut.Achi, TextureRegionDrawable> achiDrawables = new HashMap<FingerCut.Achi, TextureRegionDrawable>();
    private ArrayList<FingerCut.Achi> currentAchis = new ArrayList<FingerCut.Achi>();

    private int deathCount;

    GameScreen(FingerCut game) {
        this.game = game;
        rand = new Random();
    }

    private  void setScore(int s) {
        score = s;
        StringBuilder strBuild = new StringBuilder();
        strBuild.append(score);
        scoreLabel.setText(strBuild);
        gameOverScoreLabel.setText(strBuild);

    }

    void incScore() {
        setScore(score+1);
        if (score >= 25) {
            addAchi(FingerCut.Achi.Bronze);
        }
        if (score >= 50) {
            addAchi(FingerCut.Achi.Silver);
        }
        if (score >= 75) {
            addAchi(FingerCut.Achi.Gold);
        }
    }

    private void addAchi(FingerCut.Achi achi) {
        if (!currentAchis.contains(achi)) {
            username = prefs.getString("lastUsername", "");
            prefs.putInteger(username + ".achi." + achi.ordinal(), 1);
            currentAchis.add(achi);
            int achiIndex = achi.ordinal();
            hideAchiTime = maxHideAchiTime;
            achiTable.setColor(1, 1, 1, 0);
            achiLabel.setText(FingerCut.AchiNames[achiIndex]);
            achiIcon.setDrawable(achiDrawables.get(achi));
            playSound(achiSound, 1f);
        }
    }

    private boolean hasAchi(FingerCut.Achi achi) {
        username = prefs.getString("lastUsername", "");
        int ret = prefs.getInteger(username + ".achi." + achi.ordinal(), 0);
        return ret != 0;
    }

    private void loadAchis() {
        currentAchis.clear();
        for (FingerCut.Achi achi : FingerCut.Achi.values()) {
            if (hasAchi(achi)) {
                currentAchis.add(achi);
            }
        }
    }

    @Override
    public void render(float delta) {

        if (state.equals("mainmenu")) {
            if (!state.equals(lastState)) {
                Gdx.input.setInputProcessor(mainMenuStage);
            }
            mainMenuStage.act(delta);
        }

        if (state.equals("settings")) {
            if (!state.equals(lastState)) {
                Gdx.input.setInputProcessor(settingsStage);
            }
            settingsStage.act(delta);
        }

        if (state.equals("ready")) {
            if (!state.equals(lastState)) {
                loadAchis();
                setScore(0);
                Gdx.input.setInputProcessor(gameStage);
                bloodImage.setVisible(false);
                holdLabel.setVisible(true);
                scoreLabel.setVisible(true);
                background[0].setPosition(0, 0);
            }
            gameStage.act(delta);
            if (gameStage.fingerDown) {
                state = "play";
                vel = 150;
                playSound(startSound);
            }
        }

        if (state.equals("play")) {

            if (!state.equals(lastState)) {
                gameStage.lastFingerPos.set(gameStage.fingerPos);
                holdLabel.setVisible(false);
                vel = 170f;
            }

            if (hideAchiTime > 0) {
                hideAchiTime -= delta;
                float m = maxHideAchiTime/4f;
                if (hideAchiTime < maxHideAchiTime - m) {
                    achiTable.setColor(1, 1, 1, 1);
                }
                else {
                    achiTable.setColor(1, 1, 1, (maxHideAchiTime - hideAchiTime)/maxHideAchiTime * 4);
                }
                if (hideAchiTime < 0) {
                    achiTable.setColor(1, 1, 1, 0);
                    hideAchiTime = 0;
                }
            }

            bladeMgr.act(delta);

            vel += delta / 5;

            // calculate finger substep
            float subStep = 5;
            step.set(gameStage.fingerPos);
            step.sub(gameStage.lastFingerPos);
            step.x /= subStep;
            step.y /= subStep;

            // step and check
            fpos.set(gameStage.lastFingerPos);
            for (int i = 0; i < subStep; i++) {
                gameStage.act(delta / subStep);
                if (!gameStage.fingerDown || fpos.x < 10 || fpos.y < 10 || fpos.x > gameStage.getWidth() || fpos.y > gameStage.getHeight()) {
                    state = "laugh";
                    deathTime = 0.9f;
                    break;
                }
                else {
                    if (bladeMgr.detectCollision(fpos, fingerRadius)) {
                        bloodImage.setVisible(true);
                        bloodImage.setX(fpos.x - bloodImage.getWidth()/2);
                        bloodImage.setY(fpos.y - bloodImage.getHeight()/2);
                        fingerImage.setX(fpos.x - fingerImage.getWidth()/2);
                        fingerImage.setY(fpos.y - fingerImage.getHeight()/2);
                        state = "blood";
                        deathTime = 0.7f;
                        break;
                    }
                }
                fpos.add(step);
            }

            gameStage.lastFingerPos.set(gameStage.fingerPos);

            if (!state.equals("blood") && !state.equals("laugh")) {
                fingerImage.setPosition(gameStage.fingerPos.x - fingerImage.getWidth()/2, gameStage.fingerPos.y - fingerImage.getHeight()/2);
            }
        }

        if (state.equals("laugh")) {
            if (!state.equals(lastState)) {
                playSound(laughSound);
            }
        }

        if (state.equals("blood")) {
            if (!state.equals(lastState)) {
                vel = 0;
                playSound(bloodSound);
                playSound(painSound[rand.nextInt(2)]);
                vibrate(250);
            }
        }

        if (deathTime > 0) {
            deathTime -= delta;
            if (deathTime < 0) {
                state = "gameover";
            }
        }

        if (state.equals("gameover")) {
            if (!state.equals(lastState)) {
                deathCount++;
                if (deathCount == 3) {
                    deathCount = 0;
                    game.adHandler.showAd();
                }
                vel = 0;
                scoreLabel.setVisible(false);
                achiTable.setColor(1, 1, 1, 0);
                hideAchiTime = 0;
                Gdx.input.setInputProcessor(gameOverStage);
            }
            gameOverStage.act(delta);
        }

        fingerImage.setVisible(gameStage.fingerDown && (state.equals("play") || state.equals("gameover") || state.equals("blood")));
        background[1].setPosition(background[0].getX(), background[0].getY() + background[0].getHeight());

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameStage.draw();

        if (state.equals("mainmenu")) {
            mainMenuStage.draw();
        }
        else if (state.equals("settings")) {
            settingsStage.draw();
        }
        else if (state.equals("gameover")) {
            gameOverStage.draw();
        }

        lastState = state;
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);

        gameStage.setViewport(viewport);
        mainMenuStage.setViewport(viewport);
        settingsStage.setViewport(viewport);
        gameOverStage.setViewport(viewport);

        mainMenuTable.setBounds(0, 0, gameStage.getWidth(), gameStage.getHeight());
        mainMenuTable.invalidate();

        settingsTable.setBounds(0, 0, gameStage.getWidth(), gameStage.getHeight());
        settingsTable.invalidate();

        gameOverTable.setBounds(0, 0, gameStage.getWidth(), gameStage.getHeight());
        gameOverTable.invalidate();

        float h = (float)height/(float)width * 480f;
        gameTable.setBounds(0, (gameStage.getHeight()-h)/2, 480, h);
        gameTable.invalidate();
    }

    void load() {
        // LOADING ..
        atlas = new TextureAtlas(Gdx.files.internal("data/game.pack"));
        font1 = new BitmapFont(Gdx.files.internal("data/font1.fnt"));
        font2 = new BitmapFont(Gdx.files.internal("data/font2.fnt"));
        startSound = Gdx.audio.newSound(Gdx.files.internal("data/start.mp3"));
        pressSound = Gdx.audio.newSound(Gdx.files.internal("data/press.mp3"));
        releaseSound = Gdx.audio.newSound(Gdx.files.internal("data/release.mp3"));
        bloodSound = Gdx.audio.newSound(Gdx.files.internal("data/blood.mp3"));
        laughSound = Gdx.audio.newSound(Gdx.files.internal("data/laugh.mp3"));
        blade1Sound = Gdx.audio.newSound(Gdx.files.internal("data/blade1.mp3"));
        blade2Sound = Gdx.audio.newSound(Gdx.files.internal("data/blade2.mp3"));
        achiSound = Gdx.audio.newSound(Gdx.files.internal("data/achi.mp3"));
        painSound[0] = Gdx.audio.newSound(Gdx.files.internal("data/pain1.mp3"));
        painSound[1] = Gdx.audio.newSound(Gdx.files.internal("data/pain2.mp3"));
        //
    }


    @Override
    public void show() {
        prefs = Gdx.app.getPreferences("fingercut0.6");

        font1.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        font2.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

        // create stages
        gameStage = new GameStage();
        mainMenuStage = new Stage();
        gameOverStage = new Stage();
        settingsStage = new Stage();

        // create background
        background[0] = new Background(this, atlas.findRegion("bg1"));
        background[1] = new Background(this, atlas.findRegion("bg1"));
        gameStage.addActor(background[0]);
        gameStage.addActor(background[1]);

        // create blades
        bladeMgr = new BladeManager(this, atlas);
        bladeMgr.init();

        // create finger
        fingerImage = new Image(atlas.findRegion("finger"));
        gameStage.addActor(fingerImage);
        fingerRadius = fingerImage.getWidth()/2;

        // create blood
        bloodImage = new Image(atlas.findRegion("blood"));
        bloodImage.setVisible(false);
        gameStage.addActor(bloodImage);

        // create hold label
        LabelStyle ls = new LabelStyle();
        ls.font = font1;

        holdLabel = new Label("Hold down your finger,\nand do not release!\nAvoid the blades!", ls);
        holdLabel.setAlignment(Align.center);
        holdLabel.setVisible(false);

        // create score label
        ls = new LabelStyle();
        ls.font = font2;
        ls.fontColor = new Color(0xffde84ff);

        scoreLabel = new Label("0", ls);
        scoreLabel.setAlignment(Align.center);
        scoreLabel.setVisible(false);

        // create achi table
        achiIcon = new Image(atlas.findRegion("achi_bronze"));

        ls = new LabelStyle();
        ls.font = font1;
        achiLabel = new Label("Bronze Finger", ls);

        achiTable = new Table();
        achiTable.add(achiIcon).align(Align.right);
        achiTable.add().width(10);
        achiTable.add(achiLabel).align(Align.left);
        achiTable.setColor(1, 1, 1, 0);

        achiDrawables.clear();
        for (FingerCut.Achi achi : FingerCut.Achi.values()) {
            achiDrawables.put(achi, new TextureRegionDrawable(atlas.findRegion(FingerCut.AchiRegions[achi.ordinal()])));
        }

        // create game table (for score, achi and hold text)
        gameTable = new Table();
        gameTable.add().height(25).row();
        gameTable.add(scoreLabel).align(Align.top).expand(true, false).row();
        gameTable.add(achiTable).align(Align.top).expand(true, true).row();
        gameTable.add(holdLabel).align(Align.bottom).expand(true, true).row();
        gameTable.add().height(150).row();
        gameStage.addActor(gameTable);

        // create skin for menus
        Skin skin = new Skin(atlas);
        TextButtonStyle bs = new TextButtonStyle();
        bs.up = skin.getDrawable("button");
        bs.unpressedOffsetY = 8;
        bs.pressedOffsetY = 5;
        //bs.fontColor = new Color(0xffde84ff);
        bs.font = font2;

        // create menus
        createMainMenuTable(skin);
        createGameOverTable(skin);
        createSettingsTable(bs);
    }

    private void start() {
        state = "ready";
        gameStage.fingerDown = false;
        gameStage.fingerNumber = -1;
        bladeMgr.init();
    }

    long playSound(Sound sound) {
        return playSound(sound, 1.0f);
    }

    long playSound(Sound sound, float vol) {
        if (prefs.getBoolean("sound", true)) {
            return sound.play(vol);
        }
        return 0;
    }

    private void vibrate(int millis) {
        if (prefs.getBoolean("vibrate", true)) {
            Gdx.input.vibrate(millis);
        }
    }

    private void createMainMenuTable(Skin skin) {

        mainMenuTable = new Table();

        Image logoImage = new Image(atlas.findRegion("logo"));

        TextButtonStyle bs = new TextButtonStyle();
        bs.up = skin.getDrawable("play_up");
        bs.down = skin.getDrawable("play_down");
        bs.font = font2;
        MenuButton playButton = new MenuButton(this, "", bs);
        playButton.addListener(new ClickListener() { @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            playSound(releaseSound);
            start();
        }
        });

        bs = new TextButtonStyle();
        bs.up = skin.getDrawable("settings_up");
        bs.down = skin.getDrawable("settings_down");
        bs.font = font2;
        MenuButton settingsButton = new MenuButton(this, "", bs);
        settingsButton.addListener(new ClickListener() { @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            playSound(releaseSound);
            state = "settings";
        }
        });

        bs = new TextButtonStyle();
        bs.up = skin.getDrawable("privacy_up");
        bs.down = skin.getDrawable("privacy_down");
        bs.font = font2;
        MenuButton scoreButton = new MenuButton(this, "", bs);
        scoreButton.addListener(new ClickListener() { @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            Gdx.net.openURI("https://dynart.net/?action=privacy_policy&app=Finger%20Cut");
        }
        });

//        bs = new TextButtonStyle();
//        bs.up = skin.getDrawable("achievements_up");
//        bs.down = skin.getDrawable("achievements_down");
//        bs.font = font2;
//        MenuButton achievementsButton = new MenuButton(this, "", bs);
//        achievementsButton.addListener(new ClickListener() { @Override
//        public void clicked(InputEvent event, float x, float y) {
//            super.clicked(event, x, y);
//            playSound(releaseSound);
//        }
//        });

        LabelStyle ls = new LabelStyle();
        ls.font = font1;

        Label copyrightLabel = new Label("Dynart Copyright 2014", ls);
        mainMenuTable.add(logoImage).colspan(2).row();
        mainMenuTable.add().height(10).row();
        mainMenuTable.add(playButton).align(Align.right);
        mainMenuTable.add(settingsButton).align(Align.left).row();
        mainMenuTable.add(scoreButton).align(Align.center).colspan(2).row();
//        mainMenuTable.add(achievementsButton).align(Align.left).row();
        mainMenuTable.add(copyrightLabel).align(Align.center).height(100).colspan(2).row();
        mainMenuStage.addActor(mainMenuTable);
    }


    private void createGameOverTable(Skin skin) {

        gameOverTable = new Table();

        Image gameOverImage = new Image(atlas.findRegion("gameover"));

        TextButtonStyle bs = new TextButtonStyle();
        bs.up = skin.getDrawable("replay_up");
        bs.down = skin.getDrawable("replay_down");
        bs.font = font2;
        MenuButton restartButton = new MenuButton(this, "", bs);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playSound(releaseSound);
                start();
            }
        });

//        bs = new TextButtonStyle();
//        bs.up = skin.getDrawable("submit_up");
//        bs.down = skin.getDrawable("submit_down");
//        bs.font = font2;
//        MenuButton submitButton = new MenuButton(this, "", bs);
//        submitButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                super.clicked(event, x, y);
//                playSound(releaseSound);
//            }
//        });

        bs = new TextButtonStyle();
        bs.up = skin.getDrawable("back_up");
        bs.down = skin.getDrawable("back_down");
        bs.font = font2;
        MenuButton backButton = new MenuButton(this, "", bs);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playSound(releaseSound);
                state = "mainmenu";
            }
        });

        Table buttonTable = new Table();
        buttonTable.add(restartButton);
//        buttonTable.add(submitButton).row();
        buttonTable.add(backButton); //.colspan(2).row();

        Table scoreTable = new Table();
        scoreTable.setBackground(skin.getDrawable("black"));

        LabelStyle ls = new LabelStyle();
        ls.font = font1;

        Label label = new Label("Your score:", ls);
        scoreTable.add(label).align(Align.center).padBottom(8);

        ls = new LabelStyle();
        ls.font = font2;
        ls.fontColor = new Color(0xffde84ff);

        gameOverScoreLabel = new Label("0", ls);

        scoreTable.add().width(10);
        scoreTable.add(gameOverScoreLabel).align(Align.center).padBottom(15);


        gameOverTable.add(gameOverImage).row();
        gameOverTable.add().height(5).row();
        gameOverTable.add(scoreTable).width(480).expand(true, false).row();
        gameOverTable.add().height(5).row();
        gameOverTable.add(buttonTable).row();

        gameOverStage.addActor(gameOverTable);
    }

    private void createSettingsTable(TextButtonStyle bs) {
        settingsTable = new Table();

        Skin skin = new Skin(atlas);

        CheckBoxStyle cs = new CheckBoxStyle();
        cs.checkboxOff = skin.getDrawable("checkbox_off");
        cs.checkboxOn = skin.getDrawable("checkbox_on");
        cs.font = bs.font;
        cs.pressedOffsetY = -5;

        final CheckBox soundCheckBox = new CheckBox(" Sound", cs);
        soundCheckBox.setChecked(prefs.getBoolean("sound", true));
        soundCheckBox.getLabelCell().width(210);
        soundCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.putBoolean("sound", soundCheckBox.isChecked());
                prefs.flush();
                playSound(pressSound);
            }
        });

        final CheckBox vibrateCheckBox = new CheckBox(" Vibrate", cs);
        vibrateCheckBox.setChecked(prefs.getBoolean("vibrate", true));
        vibrateCheckBox.getLabelCell().width(210);
        vibrateCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.putBoolean("vibrate", vibrateCheckBox.isChecked());
                prefs.flush();
                playSound(pressSound);
                vibrate(250);
            }
        });

        Image settingsImage = new Image(atlas.findRegion("settings"));
        MenuButton backButton = new MenuButton(this, "Back", bs);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playSound(releaseSound);
                state = "mainmenu";
            }
        });

        settingsTable.add(settingsImage).row();
        settingsTable.add().height(30).row();
        settingsTable.add(soundCheckBox).row();
        settingsTable.add(vibrateCheckBox).row();
        settingsTable.add().height(20).row();
        settingsTable.add(backButton).row();

        settingsStage.addActor(settingsTable);
    }

    @Override
    public void hide() {
        mainMenuStage.dispose();
        gameOverStage.dispose();
        gameStage.dispose();
        atlas.dispose();
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
