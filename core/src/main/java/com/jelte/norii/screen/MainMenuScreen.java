package com.jelte.norii.screen;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.jelte.norii.ai.AITeamFileReader;
import com.jelte.norii.ai.AITeams;
import com.jelte.norii.audio.AudioObserver;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.Player;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.magic.SpellFileReader;
import com.jelte.norii.utility.AssetManagerUtility;

public class MainMenuScreen extends GameScreen {
	private static final float FRAME_DURATION = 0.2f;

	private static String MAIN_MENU_GIF = "mainMenuGif";

	private Stage stage;
	private Table mainMenuTableOfButtons;
	private TextButton newGameButton;
	private TextButton settingsButton;
	private TextButton exitButton;
	private Label title;
	private ArrayList<PlayerEntity> playerMonsters;
	private Animation<TextureRegion> bganimation;
	private SpriteBatch backgroundbatch;
	private AITeams selectedLevel;

	protected float frameTime = 0f;
	protected Sprite frameSprite = null;
	protected TextureRegion currentFrame = null;

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
		AssetManagerUtility.loadFreeTypeFontAsset("24_fonts/sporty.ttf", 24, 1, Color.LIGHT_GRAY, 1, 1);
		AssetManagerUtility.loadFreeTypeFontAsset("15_fonts/sporty.ttf", 15, 1, Color.LIGHT_GRAY, 1, 1);
		AssetManagerUtility.loadFreeTypeFontAsset("95_fonts/sporty.ttf", 95, 1, Color.LIGHT_GRAY, 1, 1);
		AssetManagerUtility.loadTextureAtlas(AssetManagerUtility.SKIN_TEXTURE_ATLAS_PATH);
		AssetManagerUtility.loadTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);
		EntityFileReader.loadUnitStatsInMemory();
		SpellFileReader.loadSpellsInMemory();
		AITeamFileReader.loadLevelsInMemory();
	}

	private void initializeClassVariables() {
		playerMonsters = new ArrayList<>();
		stage = new Stage();
		mainMenuTableOfButtons = new Table();
		mainMenuTableOfButtons.setFillParent(true);
		selectedLevel = AITeams.DESERT_TEAM;
		ScreenManager.setMainMenu(this);
	}

	private void createBackground() {
		backgroundbatch = new SpriteBatch();
		initializeBgAnimation();
	}

	private void initializeBgAnimation() {
		bganimation = AssetManagerUtility.getAnimation(MAIN_MENU_GIF, 0.3f, Animation.PlayMode.LOOP);
		bganimation.setFrameDuration(FRAME_DURATION);
	}

	private void createButtons() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();
		final BitmapFont bitmapFont = AssetManagerUtility.getFreeTypeFontAsset("95_fonts/sporty.ttf");
		final LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = bitmapFont;

		title = new Label("Norii:", labelStyle);
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
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				addUnitsToPlayer();
				ScreenManager.getInstance().showScreenSafe(ScreenEnum.BATTLE, selectedLevel);
				return true;
			}
		});

		settingsButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				ScreenManager.getInstance().showScreenSafe(ScreenEnum.SETTINGS, selectedLevel);
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
	}

	public void updatebg(final float delta) {
		frameTime = (frameTime + delta) % 70; // Want to avoid overflow

		currentFrame = bganimation.getKeyFrame(frameTime, true);
		backgroundbatch.begin();
		backgroundbatch.draw(currentFrame, 0, 0, stage.getViewport().getWorldWidth(),
				stage.getViewport().getWorldHeight());
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
	}
}
