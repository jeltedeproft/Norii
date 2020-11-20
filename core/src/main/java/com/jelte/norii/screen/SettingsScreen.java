package com.jelte.norii.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

public class SettingsScreen implements Screen {
	private static final String[] displayModes = { "2048 x 1080" };

	private Label titleLabel;
	private Label settingsLabel;
	private SelectBox<String> aspectRatioSelectBox;
	private TextButton exit;
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

		titleLabel = new Label("SETTINGS", statusUISkin, "bigFont");
		titleLabel.setAlignment(Align.top);
		settingsLabel = new Label("aspect ratio", statusUISkin);

		aspectRatioSelectBox = new SelectBox<>(statusUISkin);
		aspectRatioSelectBox.setItems(displayModes);

		exit = new TextButton("exit", statusUISkin);
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

	private void addButtons() {
		table.add(titleLabel).expandX().colspan(10).spaceBottom(100).height(250).width(1000).row();
		table.add(settingsLabel).height(75).width(200).row();
		table.add(settingsLabel).height(75).width(200).row();
		table.add(settingsLabel).height(75).width(200).row();
		table.add(settingsLabel).height(75).width(200);
		table.add(aspectRatioSelectBox).height(75).width(500).row();
		table.add(exit).spaceTop(100).height(100).width(100).row();

		stage.addActor(table);
	}

	private void addListeners() {
		exit.addListener(new InputListener() {
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
		updatebg(delta);
		stage.act(delta);
		stage.draw();

		parallaxCamera.translate(2, 0, 0);
	}

	public void updatebg(final float delta) {
		backgroundbatch.begin();
		parallaxBackground.draw(parallaxCamera, backgroundbatch);
		backgroundbatch.end();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().setScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		table.setSize(width, height);
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
