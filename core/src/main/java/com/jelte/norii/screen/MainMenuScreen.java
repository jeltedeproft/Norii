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
import com.jelte.norii.audio.AudioObserver;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.Player;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.magic.SpellFileReader;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.parallax.ParallaxBackground;
import com.jelte.norii.utility.parallax.ParallaxUtils.WH;
import com.jelte.norii.utility.parallax.TextureRegionParallaxLayer;

public class MainMenuScreen extends GameScreen {
	private Stage stage;
	private SpriteBatch backgroundbatch;
	private OrthographicCamera parallaxcamera;
	private ParallaxBackground parallaxBackground;

	private Table mainMenuTableOfButtons;
	private TextButton newGameButton;
	private TextButton settingsButton;
	private TextButton exitButton;
	private Label title;

	private ArrayList<PlayerEntity> playerMonsters;
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

		notifyAudio(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_TITLE2);
	}

	private void loadAssets() {
		AssetManagerUtility.loadTextureAtlas(AssetManagerUtility.SKIN_TEXTURE_ATLAS_PATH);
		AssetManagerUtility.loadTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);
		EntityFileReader.loadUnitStatsInMemory();
		SpellFileReader.loadSpellsInMemory();
		AITeamFileReader.loadLevelsInMemory();
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
		final int worldWidth = Gdx.graphics.getWidth();
		final int worldHeight = Gdx.graphics.getHeight();
		final TextureAtlas atlas = AssetManagerUtility.getTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);
		final TextureRegion mountainsRegionA = atlas.findRegion("bgtooga4");
		final TextureRegionParallaxLayer mountainsLayerA = new TextureRegionParallaxLayer(mountainsRegionA, worldWidth, new Vector2(.3f, .3f), WH.width);

		final TextureRegion mountainsRegionB = atlas.findRegion("bgtooga3");
		final TextureRegionParallaxLayer mountainsLayerB = new TextureRegionParallaxLayer(mountainsRegionB, worldWidth * .7275f, new Vector2(.6f, .6f), WH.width);
		mountainsLayerB.setPadLeft(.2725f * worldWidth);

		final TextureRegion cloudsRegion = atlas.findRegion("bgtooga5");
		final TextureRegionParallaxLayer cloudsLayer = new TextureRegionParallaxLayer(cloudsRegion, worldWidth, new Vector2(.6f, .6f), WH.width);
		cloudsLayer.setPadBottom(worldHeight * .467f);

		final TextureRegion buildingsRegionA = atlas.findRegion("bgtooga2");
		final TextureRegionParallaxLayer buildingsLayerA = new TextureRegionParallaxLayer(buildingsRegionA, worldWidth, new Vector2(.75f, .75f), WH.width);

		final TextureRegion buildingsRegionB = atlas.findRegion("bgtooga1");
		final TextureRegionParallaxLayer buildingsLayerB = new TextureRegionParallaxLayer(buildingsRegionB, worldWidth * .8575f, new Vector2(1, 1), WH.width);
		buildingsLayerB.setPadLeft(.07125f * worldWidth);
		buildingsLayerB.setPadRight(buildingsLayerB.getPadLeft());

		parallaxBackground = new ParallaxBackground();
		parallaxBackground.addLayers(mountainsLayerA, mountainsLayerB, cloudsLayer, buildingsLayerA, buildingsLayerB);
	}

	private void createButtons() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();

		title = new Label("Norii:", statusUISkin, "bigFont");
		newGameButton = new TextButton("New Game", statusUISkin);
		settingsButton = new TextButton("Settings", statusUISkin);
		exitButton = new TextButton("Exit", statusUISkin);
	}

	private void createLayout() {
		mainMenuTableOfButtons.add(title).row();
		mainMenuTableOfButtons.add(newGameButton).height(75).width(200).spaceBottom(20).padTop(30).row();
		mainMenuTableOfButtons.add(settingsButton).height(75).width(200).spaceBottom(20).row();
		mainMenuTableOfButtons.add(exitButton).height(75).width(200).spaceBottom(20).row();

		stage.addActor(mainMenuTableOfButtons);
	}

	private void addListeners() {
		newGameButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				addUnitsToPlayer();
				ScreenManager.getInstance().showScreen(ScreenEnum.BATTLE, selectedLevel);
				return true;
			}
		});

		settingsButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				ScreenManager.getInstance().showScreen(ScreenEnum.SETTINGS, selectedLevel);
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
		playerMonsters.add(new PlayerEntity(EntityTypes.BEAST_OGRE));
		playerMonsters.add(new PlayerEntity(EntityTypes.BLACK_CAT));
		playerMonsters.add(new PlayerEntity(EntityTypes.BLACK_DOG));
		playerMonsters.add(new PlayerEntity(EntityTypes.BLACK_GHOST_BALL));
		playerMonsters.add(new PlayerEntity(EntityTypes.BLACK_SLIME));
		playerMonsters.add(new PlayerEntity(EntityTypes.BLACK_SKELETON));
		playerMonsters.add(new PlayerEntity(EntityTypes.BLACK_SLIME_BLUE_EYES));
		playerMonsters.add(new PlayerEntity(EntityTypes.BLACK_SLIME_DARK_BLUE_EYES));
		playerMonsters.add(new PlayerEntity(EntityTypes.BLUE_GHOSTY));
		playerMonsters.add(new PlayerEntity(EntityTypes.FEMALE_EIGHT));
		playerMonsters.add(new PlayerEntity(EntityTypes.FEMALE_ONE));

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
		stage.getViewport().setScreenSize(width, height);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		notifyAudio(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_TITLE2);
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
