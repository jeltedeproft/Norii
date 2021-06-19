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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jelte.norii.ai.AITeamFileReader;
import com.jelte.norii.ai.AITeams;
import com.jelte.norii.audio.AudioCommand;
import com.jelte.norii.audio.AudioManager;
import com.jelte.norii.audio.AudioTypeEvent;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.Player;
import com.jelte.norii.magic.SpellFileReader;
import com.jelte.norii.map.MapFactory.MapType;
import com.jelte.norii.profile.ProfileManager;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.parallax.ParallaxBackground;
import com.jelte.norii.utility.parallax.ParallaxUtils.WH;
import com.jelte.norii.utility.parallax.TextureRegionParallaxLayer;

public class MainMenuScreen extends GameScreen {
	private static final float BUTTON_WIDTH_FACTOR = 1 / 5f;
	private static final float BUTTON_HEIGHT_FACTOR = 1 / 18f;
	private static final float BUTTON_PAD_BOTTOM = 40;
	private static final float BUTTON_PAD_TOP = 40;

	private Stage stage;
	private SpriteBatch backgroundbatch;
	private OrthographicCamera parallaxcamera;
	private ParallaxBackground parallaxBackground;

	private Table mainMenuTableOfButtons;
	private TextButton playButton;
	private TextButton playTutorialButton;
	private TextButton setTeamButton;
	private TextButton settingsButton;
	private TextButton exitButton;
	private Label title;

	private ArrayList<Entity> playerMonsters;
	private AITeams selectedLevel;

	protected float frameTime = 0f;
	protected Sprite frameSprite = null;

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
		AssetManagerUtility.loadTextureAtlas(AssetManagerUtility.SKIN_TEXTURE_ATLAS_PATH);
		AssetManagerUtility.loadTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);
		EntityFileReader.loadUnitStatsInMemory();
		ApFileReader.loadApInMemory();
		SpellFileReader.loadSpellsInMemory();
		AITeamFileReader.loadLevelsInMemory();
		ProfileManager.getInstance().initialise();
	}

	private void loadLevelAssets() {
		AssetManagerUtility.loadMapAsset(MapType.BATTLE_MAP_THE_DARK_SWAMP.toString());
	}

	private void initializeClassVariables() {
		backgroundbatch = new SpriteBatch();
		playerMonsters = new ArrayList<>();
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), backgroundbatch);
		parallaxcamera = new OrthographicCamera();
		parallaxcamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		parallaxcamera.update();
		mainMenuTableOfButtons = new Table();
		mainMenuTableOfButtons.setFillParent(true);
		selectedLevel = AITeams.DESERT_TEAM;
	}

	private void createBackground() {
		final int worldHeight = Gdx.graphics.getHeight();

		final TextureAtlas atlas = AssetManagerUtility.getTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);

		final TextureRegion backTrees = atlas.findRegion("background-back-trees");
		final TextureRegionParallaxLayer backTreesLayer = new TextureRegionParallaxLayer(backTrees, worldHeight, new Vector2(.3f, .3f), WH.height);

		final TextureRegion lights = atlas.findRegion("background-light");
		final TextureRegionParallaxLayer lightsLayer = new TextureRegionParallaxLayer(lights, worldHeight, new Vector2(.6f, .6f), WH.height);

		final TextureRegion middleTrees = atlas.findRegion("background-middle-trees");
		final TextureRegionParallaxLayer middleTreesLayer = new TextureRegionParallaxLayer(middleTrees, worldHeight, new Vector2(.75f, .75f), WH.height);

		final TextureRegion frontTrees = atlas.findRegion("foreground");
		final TextureRegionParallaxLayer frontTreesLayer = new TextureRegionParallaxLayer(frontTrees, worldHeight, new Vector2(.6f, .6f), WH.height);

		parallaxBackground = new ParallaxBackground();
		parallaxBackground.addLayers(backTreesLayer, lightsLayer, middleTreesLayer, frontTreesLayer);
	}

	private void createButtons() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();

		title = new Label(" Norii\n_______", statusUISkin, "bigFont");
		playButton = new TextButton("Play", statusUISkin, "lumos30");
		playTutorialButton = new TextButton("Play Tutorial", statusUISkin, "lumos30");
		setTeamButton = new TextButton("Manage Team", statusUISkin, "lumos30");
		settingsButton = new TextButton("Settings", statusUISkin, "lumos30");
		exitButton = new TextButton("Exit", statusUISkin, "lumos30");
	}

	private void createLayout() {
		final int width = Gdx.graphics.getWidth();
		final int height = Gdx.graphics.getHeight();
		final float buttonWidth = width * BUTTON_WIDTH_FACTOR;
		final float buttonHeight = height * BUTTON_HEIGHT_FACTOR;

		mainMenuTableOfButtons.add(title).spaceBottom(BUTTON_PAD_BOTTOM).padTop(BUTTON_PAD_TOP).row();
		mainMenuTableOfButtons.add(playButton).height(buttonHeight).width(buttonWidth).spaceBottom(BUTTON_PAD_BOTTOM).padTop(BUTTON_PAD_TOP).row();
		mainMenuTableOfButtons.add(playTutorialButton).height(buttonHeight).width(buttonWidth).spaceBottom(BUTTON_PAD_BOTTOM).padTop(BUTTON_PAD_TOP).row();
		mainMenuTableOfButtons.add(setTeamButton).height(buttonHeight).width(buttonWidth).spaceBottom(BUTTON_PAD_BOTTOM).padTop(BUTTON_PAD_TOP).row();
		mainMenuTableOfButtons.add(settingsButton).height(buttonHeight).width(buttonWidth).spaceBottom(BUTTON_PAD_BOTTOM).padTop(BUTTON_PAD_TOP).row();
		mainMenuTableOfButtons.add(exitButton).height(buttonHeight).width(buttonWidth).spaceBottom(BUTTON_PAD_BOTTOM).padTop(BUTTON_PAD_TOP).row();

		stage.addActor(mainMenuTableOfButtons);
	}

	private void addListeners() {
		playButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				loadLevelAssets();
				ScreenManager.getInstance().showScreen(ScreenEnum.BATTLE, selectedLevel);
				return true;
			}
		});

		playTutorialButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				addTutorialUnitsToPlayer();
				selectedLevel = AITeams.TUTORIAL;
				loadLevelAssets();
				ScreenManager.getInstance().showScreen(ScreenEnum.BATTLE, selectedLevel);
				return true;
			}
		});

		setTeamButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				ScreenManager.getInstance().showScreen(ScreenEnum.TEAM);
				return true;
			}
		});

		settingsButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				ScreenManager.getInstance().showScreen(ScreenEnum.SETTINGS);
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

	private void addTutorialUnitsToPlayer() {
		playerMonsters.add(new Entity(EntityTypes.BLACK_SLIME_RED_EYES, Player.getInstance()));

		Player.getInstance().setTeam(playerMonsters);
	}

	@Override
	public void render(final float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		updatebg(delta);

		stage.getViewport().apply();
		stage.act(delta);
		stage.draw();

		parallaxcamera.translate(2, 0, 0);
	}

	public void updatebg(final float delta) {
		backgroundbatch.begin();
		parallaxBackground.draw(parallaxcamera, backgroundbatch);
		backgroundbatch.end();
	}

	@Override
	public void resize(final int width, final int height) {
		stage.getViewport().update(width, height);
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
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
		backgroundbatch.dispose();
		parallaxBackground = null;
	}
}
