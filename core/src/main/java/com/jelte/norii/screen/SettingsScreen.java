package com.jelte.norii.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.parallax.ParallaxBackground;
import com.jelte.norii.utility.parallax.ParallaxUtils.WH;
import com.jelte.norii.utility.parallax.TextureRegionParallaxLayer;

public class SettingsScreen extends GameScreen {
	private static final int SETTINGS_LABEL_WIDTH = 200;
	private static final int SETTINGS_LABEL_HEIGHT = 75;
	private static final int TITLE_WIDTH = 1000;
	private static final int TITLE_HEIGHT = 250;
	private static final int TITLE_SPACE_BOTTOM = 100;
	private static final int TITLE_COLSPAN = 10;
	private static final float SPEED_FOREGROUND_TREES = .6f;
	private static final float SPEED_BACKGROUND_MIDDLE_TREES = .75f;
	private static final float SPEED_BACKGROUND_LIGHT = .6f;
	private static final float SPEED_BACKGROUND_TREES = .3f;
	private static final String FOREGROUND = "foreground";
	private static final String BACKGROUND_MIDDLE_TREES = "background-middle-trees";
	private static final String BACKGROUND_LIGHT = "background-light";
	private static final String BACKGROUND_BACK_TREES = "background-back-trees";
	private static final String[] displayModes = { "2048 x 1080" };
	private static final String TITLE_FONT = "bigFont";
	private static final String TITLE = "SETTINGS";
	private static final String EXIT = "exit";
	private static final String ASPECT_RATIO = "aspect ratio";

	private Label titleLabel;
	private Label settingsLabel;
	private SelectBox<String> aspectRatioSelectBox;
	private TextButton exitTextButton;
	private Stage stage;
	private Table table;
	private OrthographicCamera parallaxCamera;
	private ParallaxBackground parallaxBackground;
	private SpriteBatch backgroundbatch;

	public SettingsScreen() {
		initializeVariables();
		createBackground();
		createButtons();
		addButtons();
		addListeners();
	}

	private void initializeVariables() {
		backgroundbatch = new SpriteBatch();
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), backgroundbatch);
		parallaxCamera = new OrthographicCamera();
		parallaxCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		parallaxCamera.update();
		table = new Table();
		table.setFillParent(true);
	}

	private void createButtons() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();

		titleLabel = new Label(TITLE, statusUISkin, TITLE_FONT);
		titleLabel.setAlignment(Align.top);
		settingsLabel = new Label(ASPECT_RATIO, statusUISkin);

		aspectRatioSelectBox = new SelectBox<>(statusUISkin);
		aspectRatioSelectBox.setItems(displayModes);

		exitTextButton = new TextButton(EXIT, statusUISkin);
	}

	private void createBackground() {
		final int worldHeight = Gdx.graphics.getHeight();

		final TextureAtlas atlas = AssetManagerUtility.getTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);

		final TextureRegion backTrees = atlas.findRegion(BACKGROUND_BACK_TREES);
		final TextureRegionParallaxLayer backTreesLayer = new TextureRegionParallaxLayer(backTrees, worldHeight, new Vector2(SPEED_BACKGROUND_TREES, SPEED_BACKGROUND_TREES), WH.HEIGHT);

		final TextureRegion lights = atlas.findRegion(BACKGROUND_LIGHT);
		final TextureRegionParallaxLayer lightsLayer = new TextureRegionParallaxLayer(lights, worldHeight, new Vector2(SPEED_BACKGROUND_LIGHT, SPEED_BACKGROUND_LIGHT), WH.HEIGHT);

		final TextureRegion middleTrees = atlas.findRegion(BACKGROUND_MIDDLE_TREES);
		final TextureRegionParallaxLayer middleTreesLayer = new TextureRegionParallaxLayer(middleTrees, worldHeight, new Vector2(SPEED_BACKGROUND_MIDDLE_TREES, SPEED_BACKGROUND_MIDDLE_TREES), WH.HEIGHT);

		final TextureRegion frontTrees = atlas.findRegion(FOREGROUND);
		final TextureRegionParallaxLayer frontTreesLayer = new TextureRegionParallaxLayer(frontTrees, worldHeight, new Vector2(SPEED_FOREGROUND_TREES, SPEED_FOREGROUND_TREES), WH.HEIGHT);

		parallaxBackground = new ParallaxBackground();
		parallaxBackground.addLayers(backTreesLayer, lightsLayer, middleTreesLayer, frontTreesLayer);
	}

	private void addButtons() {
		table.add(titleLabel).expandX().colspan(TITLE_COLSPAN).spaceBottom(TITLE_SPACE_BOTTOM).height(TITLE_HEIGHT).width(TITLE_WIDTH).row();
		table.add(settingsLabel).height(SETTINGS_LABEL_HEIGHT).width(SETTINGS_LABEL_WIDTH).row();
		table.add(settingsLabel).height(SETTINGS_LABEL_HEIGHT).width(SETTINGS_LABEL_WIDTH).row();
		table.add(settingsLabel).height(SETTINGS_LABEL_HEIGHT).width(SETTINGS_LABEL_WIDTH).row();
		table.add(settingsLabel).height(SETTINGS_LABEL_HEIGHT).width(SETTINGS_LABEL_WIDTH);
		table.add(aspectRatioSelectBox).height(75).width(500).row();
		table.add(exitTextButton).spaceTop(100).height(100).width(100).row();

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

		aspectRatioSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				@SuppressWarnings("unchecked")
				final SelectBox<String> selectBox = (SelectBox<String>) actor;
				final String displayMode = selectBox.getSelected();
				final String[] splitDisplays = displayMode.split("x");
				resize(Integer.parseInt(splitDisplays[0].trim()), Integer.parseInt(splitDisplays[1].trim()));
				Gdx.graphics.setWindowedMode(Integer.parseInt(splitDisplays[0].trim()), Integer.parseInt(splitDisplays[1].trim()));
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

		parallaxCamera.translate(2, 0, 0);
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
