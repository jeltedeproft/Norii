package com.jelte.norii.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.ui.PixelViewport;
import com.jelte.norii.entities.Player;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.map.MapFactory.MapType;
import com.jelte.norii.multiplayer.NetworkMessage;
import com.jelte.norii.multiplayer.NetworkMessage.MessageType;
import com.jelte.norii.multiplayer.OnlineEnemy;
import com.jelte.norii.multiplayer.ServerCommunicator;
import com.jelte.norii.profile.PropertiesEnum;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.parallax.ParallaxBackground;
import com.jelte.norii.utility.parallax.ParallaxUtils.WH;
import com.jelte.norii.utility.parallax.TextureRegionParallaxLayer;

public class MultiplayerScreen extends GameScreen {
	private static final String TITLE_FONT = "bigFont";
	private static final String TITLE = "Multiplayer";
	private static final String EXIT = "Exit";
	private static final String SEARCH = "Search";
	private static final String PROPERTIES = "properties";

	private Label titleLabel;
	private TextButton searchTextButton;
	private TextButton exitTextButton;
	private Stage stage;
	private Table table;
	private OrthographicCamera parallaxCamera;
	private ParallaxBackground parallaxBackground;
	private SpriteBatch backgroundbatch;

	public MultiplayerScreen() {
		initializeVariables();
		createBackground();
		createButtons();
		addButtons();
		addListeners();
	}

	private void initializeVariables() {
		int windowWidth = Gdx.graphics.getWidth();
		int windowHeight = Gdx.graphics.getHeight();
		backgroundbatch = new SpriteBatch();
		stage = new Stage(new PixelViewport(), backgroundbatch);
		parallaxCamera = new OrthographicCamera();
		parallaxCamera.setToOrtho(false, windowWidth, windowHeight);
		parallaxCamera.update();
		table = new Table();
		table.setFillParent(true);
	}

	private void createBackground() {
		final TextureAtlas atlas = AssetManagerUtility.getTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);

		final TextureRegion backTrees = atlas.findRegion("background-back-trees");
		int windowHeight = Gdx.graphics.getHeight();
		final TextureRegionParallaxLayer backTreesLayer = new TextureRegionParallaxLayer(backTrees, windowHeight, new Vector2(.3f, .3f), WH.HEIGHT);

		final TextureRegion lights = atlas.findRegion("background-light");
		final TextureRegionParallaxLayer lightsLayer = new TextureRegionParallaxLayer(lights, windowHeight, new Vector2(.6f, .6f), WH.HEIGHT);

		final TextureRegion middleTrees = atlas.findRegion("background-middle-trees");
		final TextureRegionParallaxLayer middleTreesLayer = new TextureRegionParallaxLayer(middleTrees, windowHeight, new Vector2(.75f, .75f), WH.HEIGHT);

		final TextureRegion frontTrees = atlas.findRegion("foreground");
		final TextureRegionParallaxLayer frontTreesLayer = new TextureRegionParallaxLayer(frontTrees, windowHeight, new Vector2(.6f, .6f), WH.HEIGHT);

		parallaxBackground = new ParallaxBackground();
		parallaxBackground.addLayers(backTreesLayer, lightsLayer, middleTreesLayer, frontTreesLayer);
	}

	private void createButtons() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();

		titleLabel = new Label(TITLE, statusUISkin, TITLE_FONT);
		titleLabel.setAlignment(Align.center);

		searchTextButton = new TextButton(SEARCH, statusUISkin);
		exitTextButton = new TextButton(EXIT, statusUISkin);
	}

	private void addButtons() {
		table.add(titleLabel).padBottom(16).row();
		table.add(searchTextButton).width(200).height(32).padBottom(10).row();
		table.add(exitTextButton).width(160).height(32).row();

		stage.addActor(table);
	}

	private void addListeners() {
		exitTextButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);
				return true;
			}
		});
		searchTextButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.SEARCH_OPPONENT);
				String team = Gdx.app.getPreferences(PROPERTIES).getString(PropertiesEnum.TEAM_HEROES.getPropertyName());
				message.makeSearchMessage(ServerCommunicator.getInstance().getClientID(), team);
				ServerCommunicator.getInstance().sendMessage(message);
				return true;
			}
		});
	}

	@Override
	public void show() {
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.04f, 0.02f, 0.08f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		PixelViewport.beginFullScreen();
		updatebg();
		stage.getViewport().apply();
		stage.act(delta);
		stage.draw();
		checkForStartGame();

		parallaxCamera.translate(2, 0, 0);
	}

	private void checkForStartGame() {
		if (ServerCommunicator.getInstance().isNextMessageOfType(MessageType.BATTLE)) {
			NetworkMessage message = ServerCommunicator.getInstance().getOldestMessageFromServer();
			UnitOwner enemyTeamLeader = "true".equals(message.getPlayerStart()) ? new OnlineEnemy(message.getFighter2(), message.getTeam2(), false, message.getGameID())
																				: new OnlineEnemy(message.getFighter1(), message.getTeam1(), true, message.getGameID());
			Player.getInstance().setGameID(message.getGameID());
			MapType mapType = MapType.valueOf(message.getMap());
			AssetManagerUtility.loadMapAsset(mapType.toString());
			ScreenManager.getInstance().showScreen(ScreenEnum.LOAD, enemyTeamLeader, mapType);// give team and map
		}
	}

	public void updatebg() {
		backgroundbatch.begin();
		parallaxBackground.draw(parallaxCamera, backgroundbatch);
		backgroundbatch.end();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		parallaxCamera.setToOrtho(false, width, height);
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
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void dispose() {
		backgroundbatch.dispose();
		stage.dispose();
		parallaxBackground = null;
	}

}
