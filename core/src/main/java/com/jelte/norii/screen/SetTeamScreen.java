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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jelte.norii.entities.EntityData;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.profile.ProfileManager;
import com.jelte.norii.profile.ProfileObserver;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.parallax.ParallaxBackground;
import com.jelte.norii.utility.parallax.ParallaxUtils.WH;
import com.jelte.norii.utility.parallax.TextureRegionParallaxLayer;

public class SetTeamScreen extends GameScreen implements ProfileObserver {
	private static final String TITLE_FONT = "bigFont";
	private static final String TITLE = "SET TEAM";
	private static final String YOUR_TEAM = "Your Team";
	private static final String AVAILABLE = "Available Heroes";
	private static final String EXIT = "exit";
	private static final String SAVE = "Save and Exit";

	private Label titleLabel;
	private Label yourTeamLabel;
	private Label availableHeroesLabel;
	private TextButton exit;
	private TextButton save;
	private Stage stage;
	private Table table;
	private OrthographicCamera parallaxCamera;
	private ParallaxBackground parallaxBackground;
	private SpriteBatch backgroundbatch;

	private Array<String> availableHeroesNames;
	private Array<String> teamHeroesNames;
	private Array<ImageButton> availableHeroes;
	private Array<ImageButton> teamHeroes;
	private ObjectMap<Integer, EntityData> entityData;

	public SetTeamScreen() {
		initializeVariables();
		fillAvailableHeroes();
		createBackground();
		createButtons();
		createHeroPortraits();
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
		availableHeroes = new Array<>();
		teamHeroes = new Array<>();
	}

	private void fillAvailableHeroes() {
		availableHeroesNames = ProfileManager.getInstance().getProperty("availableHeroes");
		teamHeroesNames = ProfileManager.getInstance().getProperty("teamHeroes");
		entityData = EntityFileReader.getUnitData();
	}

	private void createButtons() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();

		titleLabel = new Label(TITLE, statusUISkin, TITLE_FONT);
		titleLabel.setAlignment(Align.top);

		yourTeamLabel = new Label(AVAILABLE, statusUISkin);
		yourTeamLabel.setAlignment(Align.center);

		availableHeroesLabel = new Label(YOUR_TEAM, statusUISkin);
		availableHeroesLabel.setAlignment(Align.center);

		exit = new TextButton(EXIT, statusUISkin);
		exit.align(Align.bottom);
		save = new TextButton(SAVE, statusUISkin);
		save.align(Align.bottom);
	}

	private void createHeroPortraits() {
		ImageButton button = new ImageButton(AssetManagerUtility.getSkin());
		ImageButtonStyle btnStyle = button.getStyle();
		for (EntityData data : entityData.values()) {
			String heroImageName = data.getPortraitSpritePath();
			TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(heroImageName));
			TextureRegionDrawable buttonImage = new TextureRegionDrawable(tr);
			ImageButtonStyle heroButtonStyle = new ImageButtonStyle();
			heroButtonStyle.imageUp = buttonImage;
			heroButtonStyle.up = btnStyle.up;
			heroButtonStyle.down = btnStyle.down;
			ImageButton heroImageButton = new ImageButton(heroButtonStyle);
			availableHeroes.add(heroImageButton);
		}
	}

	private void createBackground() {
		final int worldWidth = Gdx.graphics.getWidth();
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

	private void addButtons() {
		table.add(titleLabel).colspan(20).spaceBottom(100).height(100).width(100).row();
		table.add(yourTeamLabel).height(75).width(50);
		table.add(availableHeroesLabel).colspan(10).height(75).width(50).row();

		int count = 0;
		for (ImageButton heroButton : availableHeroes) {
			if (count >= 4) {
				table.add(heroButton).pad(0).size(50).uniform();
				table.add(new Label("test", AssetManagerUtility.getSkin())).size(50);
				table.add(new Label("test", AssetManagerUtility.getSkin())).size(50);
				table.add(new Label("test", AssetManagerUtility.getSkin())).size(50);
				table.add(new Label("test", AssetManagerUtility.getSkin())).size(50);
				table.add(new Label("test", AssetManagerUtility.getSkin())).size(50);
				table.add(new Label("test", AssetManagerUtility.getSkin())).size(50);
				table.row();
				count = 0;
			} else {
				table.add(heroButton).pad(0).size(50).uniform();
				count++;
			}
		}

		table.add(exit).padTop(100).height(50).width(100);
		table.add(save).padTop(100).height(50).width(150);

		table.setFillParent(true);
		table.pack();
		table.debug();
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

		save.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				// ProfileManager.getInstance().setProperty("teamHeroes", object);
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

	@Override
	public void onNotify(ProfileManager profileManager, ProfileEvent event) {
		switch (event) {
		case SAVING_PROFILE:
			// no-op
			break;
		default:
			break;
		}

	}

}
