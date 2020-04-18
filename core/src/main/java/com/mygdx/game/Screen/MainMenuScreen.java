package com.mygdx.game.Screen;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.game.AI.Level;
import com.mygdx.game.AI.LevelFileReader;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityFileReader;
import com.mygdx.game.Entities.EntityTypes;
import com.mygdx.game.Entities.Owner;
import com.mygdx.game.Entities.Player;
import com.mygdx.game.Magic.SpellFileReader;

import Utility.Utility;

public class MainMenuScreen extends GameScreen {
	public static final int FRAME_WIDTH = 200;
	public static final int FRAME_HEIGHT = 200;
	private static final int FRAME_COLS = 2;
	private static final int FRAME_ROWS = 3;

	private static String defaultBackgroundPath = "sprites/gui/stars_bg/space2x3.png";

	private Stage stage;
	private Table mainMenuTableOfButtons;
	private TextButton newGameButton;
	private TextButton exitButton;
	private Label title;
	private ArrayList<Owner> fighters;
	private ArrayList<Entity> playerMonsters;
	private Animation<TextureRegion> bganimation;
	private SpriteBatch backgroundbatch;
	private Level selectedLevel;

	protected float frameTime = 0f;
	protected Sprite frameSprite = null;
	protected TextureRegion currentFrame = null;
	private TextureRegion[] bgFrames;

	public MainMenuScreen(final Object... params) {
		loadAssets();
		initializeClassVariables();

		createBackground();
		createButtons();
		createLayout();

		addListeners();

		notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_TITLE);
	}

	private void loadAssets() {
		Utility.loadFreeTypeFontAsset("fonts/BLKCHCRY.ttf", 24);
		EntityFileReader.loadUnitStatsInMemory();
		SpellFileReader.loadSpellsInMemory();
		LevelFileReader.loadLevelsInMemory();
	}

	private void initializeClassVariables() {
		fighters = new ArrayList<Owner>();
		playerMonsters = new ArrayList<Entity>();
		stage = new Stage();
		mainMenuTableOfButtons = new Table();
		mainMenuTableOfButtons.setFillParent(true);
	}

	private void createBackground() {
		backgroundbatch = new SpriteBatch();
		Utility.loadTextureAsset(defaultBackgroundPath);
		loadBackgroundSpritesIntoArray();
		initializeBgAnimation();
	}

	private void loadBackgroundSpritesIntoArray() {
		final Texture texture = Utility.getTextureAsset(defaultBackgroundPath);
		final TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);

		bgFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				bgFrames[index++] = textureFrames[i][j];
			}
		}
	}

	private void initializeBgAnimation() {
		bganimation = new Animation<TextureRegion>(0.050f, bgFrames);
	}

	private void createButtons() {
		final Skin statusUISkin = Utility.getStatusUISkin();
		final LabelStyle labelStyle = createTitleStyle();

		title = new Label("Norii:", labelStyle);
		newGameButton = new TextButton("New Game", statusUISkin);
		exitButton = new TextButton("Exit", statusUISkin);
	}

	private LabelStyle createTitleStyle() {
		return Utility.createLabelStyle("fonts/BLKCHCRY.ttf", 105, 1, Color.LIGHT_GRAY, 1, 1);
	}

	private void createLayout() {
		mainMenuTableOfButtons.add(title).row();
		mainMenuTableOfButtons.add(newGameButton).spaceBottom(10).padTop(50).row();
		mainMenuTableOfButtons.add(exitButton).spaceBottom(10).row();

		stage.addActor(mainMenuTableOfButtons);
	}

	private void addListeners() {
		newGameButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				addUnitsToPlayer();
				ScreenManager.getInstance().showScreen(ScreenEnum.BATTLE, fighters, selectedLevel);
				return true;
			}
		});

		exitButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				Gdx.app.exit();
				return true;
			}
		});
	}

	private void addUnitsToPlayer() {
		final Player player = Player.getInstance();

		playerMonsters.add(new Entity(EntityTypes.COMMANDER));
		playerMonsters.add(new Entity(EntityTypes.ICARUS));
		playerMonsters.add(new Entity(EntityTypes.DEMON));
		playerMonsters.add(new Entity(EntityTypes.SHAMAN));

		player.setTeam(playerMonsters);
		fighters.add(player);
	}

	@Override
	public void render(final float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updatebg(delta);

		stage.act(delta);
		stage.draw();
	}

	public void updatebg(final float delta) {
		frameTime = (frameTime + delta) % 70; //Want to avoid overflow

		currentFrame = bganimation.getKeyFrame(frameTime, true);
		backgroundbatch.begin();
		backgroundbatch.draw(currentFrame, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
		backgroundbatch.end();
	}

	@Override
	public void resize(final int width, final int height) {
		stage.getViewport().setScreenSize(width, height);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_TITLE);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_TITLE);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
		Utility.unloadAsset(defaultBackgroundPath);
		backgroundbatch.dispose();
	}
}
