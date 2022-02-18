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
import com.badlogic.gdx.utils.viewport.FitViewport;
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
	private static final int TITLE_COLSPAN = 10;
	private static final int SEARCH_TEXT_BUTTON_WIDTH_FACTOR = 10;
	private static final int SEARCH_TEXT_BUTTON_HEIGHT_FACTOR = 5;
	private static final int TITLE_WIDTH_FACTOR = 25;
	private static final int TITLE_HEIGHT_FACTOR = 10;
	private static final int TITLE_SPACE_BOTTOM_FACTOR = 5;
	private static final String TITLE_FONT = "bigFont";
	private static final String TITLE = "MULTIPLAYER";
	private static final String EXIT = "exit";
	private static final String SEARCH = "search";
	private static final String PROPERTIES = "properties";
	private static final int MULTIPLAYER_LABEL_HEIGHT_FACTOR = 5;
	private static final int MULTIPLAYER_LABEL_WIDTH_FACTOR = 15;
	private static final int EXIT_WIDTH_FACTOR = 15;
	private static final int EXIT_HEIGHT_FACTOR = 5;
	private static final int EXIT_SPACE_TOP_FACTOR = 5;

	private Label titleLabel;
	private Label multiplayerLabel;
	private TextButton searchTextButton;
	private TextButton exitTextButton;
	private Stage stage;
	private Table table;
	private OrthographicCamera parallaxCamera;
	private ParallaxBackground parallaxBackground;
	private SpriteBatch backgroundbatch;

	private int screenWidth;
	private int screenHeight;
	private int widthPercent;
	private int heightPercent;

	public MultiplayerScreen() {
		initializeVariables();
		createBackground();
		createButtons();
		addButtons();
		addListeners();
	}

	private void initializeVariables() {
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		widthPercent = screenWidth / 100;
		heightPercent = screenHeight / 100;
		backgroundbatch = new SpriteBatch();
		stage = new Stage(new FitViewport(screenWidth, screenHeight), backgroundbatch);
		parallaxCamera = new OrthographicCamera();
		parallaxCamera.setToOrtho(false, screenWidth, screenHeight);
		parallaxCamera.update();
		table = new Table();
		table.setFillParent(true);
	}

	private void createBackground() {
		final TextureAtlas atlas = AssetManagerUtility.getTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);

		final TextureRegion backTrees = atlas.findRegion("background-back-trees");
		final TextureRegionParallaxLayer backTreesLayer = new TextureRegionParallaxLayer(backTrees, screenHeight, new Vector2(.3f, .3f), WH.HEIGHT);

		final TextureRegion lights = atlas.findRegion("background-light");
		final TextureRegionParallaxLayer lightsLayer = new TextureRegionParallaxLayer(lights, screenHeight, new Vector2(.6f, .6f), WH.HEIGHT);

		final TextureRegion middleTrees = atlas.findRegion("background-middle-trees");
		final TextureRegionParallaxLayer middleTreesLayer = new TextureRegionParallaxLayer(middleTrees, screenHeight, new Vector2(.75f, .75f), WH.HEIGHT);

		final TextureRegion frontTrees = atlas.findRegion("foreground");
		final TextureRegionParallaxLayer frontTreesLayer = new TextureRegionParallaxLayer(frontTrees, screenHeight, new Vector2(.6f, .6f), WH.HEIGHT);

		parallaxBackground = new ParallaxBackground();
		parallaxBackground.addLayers(backTreesLayer, lightsLayer, middleTreesLayer, frontTreesLayer);
	}

	private void createButtons() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();

		titleLabel = new Label(TITLE, statusUISkin, TITLE_FONT);
		titleLabel.setAlignment(Align.top);

		searchTextButton = new TextButton(SEARCH, statusUISkin);
		exitTextButton = new TextButton(EXIT, statusUISkin);
	}

	private void addButtons() {
		table.add(titleLabel).expandX().colspan(TITLE_COLSPAN).spaceBottom(TITLE_SPACE_BOTTOM_FACTOR * heightPercent).height(TITLE_HEIGHT_FACTOR * heightPercent).width(TITLE_WIDTH_FACTOR * widthPercent).row();
		table.add(searchTextButton).height(SEARCH_TEXT_BUTTON_HEIGHT_FACTOR * heightPercent).width(SEARCH_TEXT_BUTTON_WIDTH_FACTOR * widthPercent).row();
		table.add(multiplayerLabel).height(MULTIPLAYER_LABEL_HEIGHT_FACTOR * heightPercent).width(MULTIPLAYER_LABEL_WIDTH_FACTOR * widthPercent).row();
		table.add(multiplayerLabel).height(MULTIPLAYER_LABEL_HEIGHT_FACTOR * heightPercent).width(MULTIPLAYER_LABEL_WIDTH_FACTOR * widthPercent).row();
		table.add(multiplayerLabel).height(MULTIPLAYER_LABEL_HEIGHT_FACTOR * heightPercent).width(MULTIPLAYER_LABEL_WIDTH_FACTOR * widthPercent);
		table.add(exitTextButton).spaceTop(EXIT_SPACE_TOP_FACTOR * heightPercent).height(EXIT_HEIGHT_FACTOR * heightPercent).width(EXIT_WIDTH_FACTOR * widthPercent).row();

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
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		updatebg();
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
		stage.getViewport().update(width, height, false);
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
