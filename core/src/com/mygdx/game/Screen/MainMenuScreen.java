package com.mygdx.game.Screen;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityFileReader;

import Utility.Utility;

import com.mygdx.game.Entities.Owner;
import com.mygdx.game.Entities.Player;

public class MainMenuScreen extends GameScreen {
	private static final String TAG = MainMenuScreen.class.getSimpleName();
	
	public static final int FRAME_WIDTH = 200;
	public static final int FRAME_HEIGHT = 200;
	private static String defaultBackgroundPath = "sprites/gui/stars_bg/space2x3.png";
	private static final int FRAME_COLS = 2;
	private static final int FRAME_ROWS = 3;

	private Stage stage;
	private Table mainMenuTableOfButtons;
	private TextButton newGameButton;
	private TextButton exitButton;
	private Label title;
	private ArrayList<Owner> fighters;
	private ArrayList<Entity> monsters;
	private Animation<TextureRegion> bganimation;
	private SpriteBatch backgroundbatch;

	
	protected float frameTime = 0f;
	protected Sprite frameSprite = null;
	protected TextureRegion currentFrame = null;
	private TextureRegion[] bgFrames;

	public MainMenuScreen(Object... params){
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
	}
	
	private void initializeClassVariables() {
		stage = new Stage();
		mainMenuTableOfButtons = new Table();
		mainMenuTableOfButtons.setFillParent(true);
		EntityFileReader.loadUnitStatsInMemory();
	}
	
	private void createBackground(){
		backgroundbatch = new SpriteBatch();
		Utility.loadTextureAsset(defaultBackgroundPath);
		loadBackgroundSpritesIntoArray();
		initializeBgAnimation();
	}
	
	private void loadBackgroundSpritesIntoArray(){
		Texture texture = Utility.getTextureAsset(defaultBackgroundPath);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		
		
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
    	Skin statusUISkin = Utility.getStatusUISkin();
    	
    	FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/BLKCHCRY.ttf"));
    	FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    	parameter.size = 105;
    	BitmapFont font = generator.generateFont(parameter);

    	LabelStyle labelStyle = new LabelStyle();
    	labelStyle.font = font;

    	title = new Label("Norii:", labelStyle);
		newGameButton = new TextButton("New Game", statusUISkin);
		exitButton = new TextButton("Exit",statusUISkin);
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
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				addUnitsToPlayer();
				return true;
			}
								  }
		);

		exitButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.exit();
				return true;
			}
		});
	}
	
	private void addUnitsToPlayer() {
		fighters = new ArrayList<Owner>();
		monsters = new ArrayList<Entity>();
		monsters.add(new Entity(1));
		monsters.add(new Entity(2));
		monsters.add(new Entity(3));
		monsters.add(new Entity(4));
		Player.getInstance().setTeam(monsters);
		fighters.add(Player.getInstance());
		ScreenManager.getInstance().showScreen( ScreenEnum.BATTLE,fighters); 
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updatebg(delta);
		 
		stage.act(delta);
		stage.draw();
	}
	
	public void updatebg(float delta){
		frameTime = (frameTime + delta)%70; //Want to avoid overflow

		currentFrame = bganimation.getKeyFrame(frameTime, true);
		backgroundbatch.begin();
		backgroundbatch.draw(currentFrame, 0, 0,stage.getViewport().getWorldWidth(),stage.getViewport().getWorldHeight());
		backgroundbatch.end();
	}

	@Override
	public void resize(int width, int height) {
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




