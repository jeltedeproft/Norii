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
import com.jelte.norii.multiplayer.NetworkMessage;
import com.jelte.norii.multiplayer.NetworkMessage.MessageType;
import com.jelte.norii.multiplayer.ServerCommunicator;
import com.jelte.norii.ui.LoginWidget;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.parallax.ParallaxBackground;
import com.jelte.norii.utility.parallax.ParallaxUtils.WH;
import com.jelte.norii.utility.parallax.TextureRegionParallaxLayer;

public class LoginScreen extends GameScreen {
	private static final String TITLE_FONT = "bigFont";
	private static final String TITLE = "LOGIN";
	private static final String EXIT = "quit";

	private Label titleLabel;
	private Label multiplayerLabel;
	private TextButton exitTextButton;
	private Stage stage;
	private Table table;
	private OrthographicCamera parallaxCamera;
	private ParallaxBackground parallaxBackground;
	private SpriteBatch backgroundbatch;
	private LoginWidget loginWidget;
	private ServerCommunicator serverCom = ServerCommunicator.getInstance();

	public LoginScreen() {
		loadAssets();
		initializeVariables();
		createBackground();
		createButtons();
		addButtons();
		addListeners();
	}

	private void loadAssets() {
		AssetManagerUtility.loadTextureAtlas(AssetManagerUtility.SKIN_TEXTURE_ATLAS_PATH);
		AssetManagerUtility.loadTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);
	}

	private void initializeVariables() {
		backgroundbatch = new SpriteBatch();
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), backgroundbatch);
		parallaxCamera = new OrthographicCamera();
		parallaxCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		parallaxCamera.update();
		table = new Table();
		table.setFillParent(true);
		loginWidget = new LoginWidget();
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

		titleLabel = new Label(TITLE, statusUISkin, TITLE_FONT);
		titleLabel.setAlignment(Align.top);

		exitTextButton = new TextButton(EXIT, statusUISkin);
	}

	private void addButtons() {
		table.add(titleLabel).expandX().colspan(10).spaceBottom(100).height(250).width(1000).row();
		table.add(loginWidget.getWindow()).height(300).width(300).row();
		table.add(exitTextButton).spaceTop(100).height(100).width(100).row();

		stage.addActor(table);
	}

	private void addListeners() {
		exitTextButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				Gdx.app.exit();
				return true;
			}
		});

		loginWidget.getLoginTextButton().addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				String username = loginWidget.getUsername();
				String password = loginWidget.getPassword();
				NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.TRY_LOGIN);
				message.makeLoginMessage(username, password);
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
		updatebg(delta);
		stage.act(delta);
		stage.draw();

		parallaxCamera.translate(2, 0, 0);
		checkLogin();
	}

	public void updatebg(final float delta) {
		backgroundbatch.begin();
		parallaxBackground.draw(parallaxCamera, backgroundbatch);
		backgroundbatch.end();
	}

	private void checkLogin() {
		if (serverCom.isNewMessage()) {
			NetworkMessage oldestMessage = serverCom.getOldestMessageFromServer();
			if (oldestMessage.getType() == MessageType.LOGIN_VALIDATION) {
				if (oldestMessage.getLoginWorked().equalsIgnoreCase("true")) {
					ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);
				}
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().setScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
