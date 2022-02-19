package com.jelte.norii.screen;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jelte.norii.ai.AITeamFileReader;
import com.jelte.norii.ai.AITeamLeader;
import com.jelte.norii.ai.AITeamLeader.LevelEnum;
import com.jelte.norii.audio.AudioCommand;
import com.jelte.norii.audio.AudioManager;
import com.jelte.norii.audio.AudioTypeEvent;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.Player;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.SpellFileReader;
import com.jelte.norii.map.MapFactory.MapType;
import com.jelte.norii.profile.ProfileManager;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.parallax.ParallaxBackground;
import com.jelte.norii.utility.parallax.ParallaxUtils.WH;
import com.jelte.norii.utility.parallax.TextureRegionParallaxLayer;

public class MainMenuScreen extends GameScreen {
	private static final float HEIGHT_REDUCTION_FACTOR = 0.01f;
	private static final float BUTTON_WIDTH_FACTOR = 0.3f;
	private static final float BUTTON_HEIGHT_FACTOR = 0.055f;
	private static final float TITLE_PAD_BOTTOM_PERCENT = 8;
	private static final float BUTTON_PAD_BOTTOM_PERCENT = 2;
	private static final float BUTTON_PAD_TOP_PERCENT = 2;
	private static final String TITLE_FONT_NAME = "bigFont";
	private static final String GENERAL_FONT_NAME = "lumos30";

	private Stage stage;
	private SpriteBatch backgroundbatch;
	private OrthographicCamera parallaxcamera;
	private ParallaxBackground parallaxBackground;

	private Table mainMenuTableOfButtons;
	private TextButton playButton;
	private TextButton multiplayerButton;
	private TextButton playTutorialButton;
	private TextButton setTeamButton;
	private TextButton settingsButton;
	private TextButton exitButton;
	private Label title;

	private ArrayList<Entity> playerMonsters;
	private LevelEnum selectedLevel;

	protected float frameTime = 0f;
	protected Sprite frameSprite = null;

	private int initialWidth;
	private int initialHeight;

	public MainMenuScreen() {
		loadAssets();
		initializeClassVariables();

		createBackground();
		createButtons();
		createLayout();

		addListeners();

		AudioManager.getInstance().onNotify(AudioCommand.MUSIC_LOAD, AudioTypeEvent.MUSIC_TITLE2);
	}

	private void loadAssets() {
		EntityFileReader.loadUnitStatsInMemory();
		ApFileReader.loadApInMemory();
		SpellFileReader.loadSpellsInMemory();
		AITeamFileReader.loadLevelsInMemory();
		ProfileManager.getInstance().initialise();
	}

	private void initializeClassVariables() {
		backgroundbatch = new SpriteBatch();
		playerMonsters = new ArrayList<>();
		initialWidth = Gdx.graphics.getWidth();
		initialHeight = Gdx.graphics.getHeight();
		stage = new Stage(new ExtendViewport(initialWidth, initialHeight), backgroundbatch);
		parallaxcamera = new OrthographicCamera();
		parallaxcamera.setToOrtho(false, initialWidth, initialHeight);
		parallaxcamera.update();
		mainMenuTableOfButtons = new Table();
		mainMenuTableOfButtons.setFillParent(true);
		selectedLevel = LevelEnum.DESERT_TEAM;
	}

	private void createBackground() {
		final TextureAtlas atlas = AssetManagerUtility.getTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);

		final TextureRegion backTrees = atlas.findRegion("background-back-trees");
		final TextureRegionParallaxLayer backTreesLayer = new TextureRegionParallaxLayer(backTrees, initialHeight,
				new Vector2(.3f, .3f), WH.HEIGHT);

		final TextureRegion lights = atlas.findRegion("background-light");
		final TextureRegionParallaxLayer lightsLayer = new TextureRegionParallaxLayer(lights, initialHeight,
				new Vector2(.6f, .6f), WH.HEIGHT);

		final TextureRegion middleTrees = atlas.findRegion("background-middle-trees");
		final TextureRegionParallaxLayer middleTreesLayer = new TextureRegionParallaxLayer(middleTrees, initialHeight,
				new Vector2(.75f, .75f), WH.HEIGHT);

		final TextureRegion frontTrees = atlas.findRegion("foreground");
		final TextureRegionParallaxLayer frontTreesLayer = new TextureRegionParallaxLayer(frontTrees, initialHeight,
				new Vector2(.6f, .6f), WH.HEIGHT);

		parallaxBackground = new ParallaxBackground();
		parallaxBackground.addLayers(backTreesLayer, lightsLayer, middleTreesLayer, frontTreesLayer);
	}

	private void createButtons() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();

		title = new Label(" Norii", statusUISkin, TITLE_FONT_NAME);
		playButton = new TextButton("Play", statusUISkin, GENERAL_FONT_NAME);
		multiplayerButton = new TextButton("Multiplayer", statusUISkin, GENERAL_FONT_NAME);
		playTutorialButton = new TextButton("Play Tutorial", statusUISkin, GENERAL_FONT_NAME);
		setTeamButton = new TextButton("Manage Team", statusUISkin, GENERAL_FONT_NAME);
		settingsButton = new TextButton("Settings", statusUISkin, GENERAL_FONT_NAME);
		exitButton = new TextButton("Exit", statusUISkin, GENERAL_FONT_NAME);
	}

	private void createLayout() {
		final float buttonWidth = initialWidth * BUTTON_WIDTH_FACTOR;
		final float buttonHeight = initialHeight * BUTTON_HEIGHT_FACTOR;
		final float titlePadBottom = (initialHeight * HEIGHT_REDUCTION_FACTOR) * TITLE_PAD_BOTTOM_PERCENT;
		final float buttonPadTop = (initialHeight * HEIGHT_REDUCTION_FACTOR) * BUTTON_PAD_TOP_PERCENT;

		mainMenuTableOfButtons.add(title).spaceBottom(titlePadBottom).padTop(buttonPadTop).row();
		mainMenuTableOfButtons.add(playButton).height(buttonHeight).width(buttonWidth)
				.spaceBottom(BUTTON_PAD_BOTTOM_PERCENT).padTop(BUTTON_PAD_TOP_PERCENT).row();
		mainMenuTableOfButtons.add(multiplayerButton).height(buttonHeight).width(buttonWidth)
				.spaceBottom(BUTTON_PAD_BOTTOM_PERCENT).padTop(BUTTON_PAD_TOP_PERCENT).row();
		mainMenuTableOfButtons.add(playTutorialButton).height(buttonHeight).width(buttonWidth)
				.spaceBottom(BUTTON_PAD_BOTTOM_PERCENT).padTop(BUTTON_PAD_TOP_PERCENT).row();
		mainMenuTableOfButtons.add(setTeamButton).height(buttonHeight).width(buttonWidth)
				.spaceBottom(BUTTON_PAD_BOTTOM_PERCENT).padTop(BUTTON_PAD_TOP_PERCENT).row();
		mainMenuTableOfButtons.add(settingsButton).height(buttonHeight).width(buttonWidth)
				.spaceBottom(BUTTON_PAD_BOTTOM_PERCENT).padTop(BUTTON_PAD_TOP_PERCENT).row();
		mainMenuTableOfButtons.add(exitButton).height(buttonHeight).width(buttonWidth)
				.spaceBottom(BUTTON_PAD_BOTTOM_PERCENT).padTop(BUTTON_PAD_TOP_PERCENT).row();

		stage.addActor(mainMenuTableOfButtons);
	}

	private void addListeners() {
		playButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				loadLevelAssets();
				UnitOwner enemyTeamLeader = new AITeamLeader(selectedLevel);
				ScreenManager.getInstance().showScreen(ScreenEnum.LOAD, enemyTeamLeader,
						MapType.BATTLE_MAP_THE_DARK_SWAMP);
				return true;
			}
		});

		multiplayerButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				loadLevelAssets();
				ScreenManager.getInstance().showScreen(ScreenEnum.MULTIPLAYER, selectedLevel);
				return true;
			}
		});

		playTutorialButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				addTutorialUnitsToPlayer();
				selectedLevel = LevelEnum.TUTORIAL;
				loadLevelAssets();
				UnitOwner enemyTeamLeader = new AITeamLeader(selectedLevel);
				ScreenManager.getInstance().showScreen(ScreenEnum.LOAD, enemyTeamLeader,
						MapType.BATTLE_MAP_THE_DARK_SWAMP);
				return true;
			}
		});

		setTeamButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				ScreenManager.getInstance().showScreen(ScreenEnum.TEAM);
				return true;
			}
		});

		settingsButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				ScreenManager.getInstance().showScreen(ScreenEnum.SETTINGS);
				return true;
			}
		});

		exitButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				Gdx.app.exit();
				return true;
			}
		});
	}

	private void loadLevelAssets() {
		AssetManagerUtility.loadMapAsset(MapType.BATTLE_MAP_THE_DARK_SWAMP.toString());
	}

	private void addTutorialUnitsToPlayer() {
		playerMonsters.add(new Entity(EntityTypes.BLACK_SLIME_RED_EYES, Player.getInstance(), true));
		Player.getInstance().setTeam(playerMonsters);
	}

	@Override
	public void render(final float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		updatebg();

		stage.getViewport().apply();
		stage.act(delta);
		stage.draw();

		parallaxcamera.translate(2, 0, 0);
	}

	public void updatebg() {
		backgroundbatch.begin();
		parallaxBackground.draw(parallaxcamera, backgroundbatch);
		backgroundbatch.end();
	}

	@Override
	public void resize(final int width, final int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		AudioManager.getInstance().onNotify(AudioCommand.MUSIC_PLAY_LOOP, AudioTypeEvent.MUSIC_TITLE2);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
		// no-op
	}

	@Override
	public void resume() {
		// no-op
	}

	@Override
	public void dispose() {
		stage.dispose();
		backgroundbatch.dispose();
		parallaxBackground = null;
	}
}
