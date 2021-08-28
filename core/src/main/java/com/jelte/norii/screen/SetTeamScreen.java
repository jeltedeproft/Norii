package com.jelte.norii.screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jelte.norii.entities.EntityData;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.profile.ProfileManager;
import com.jelte.norii.ui.VideoDrawable;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.parallax.ParallaxBackground;
import com.jelte.norii.utility.parallax.ParallaxUtils.WH;
import com.jelte.norii.utility.parallax.TextureRegionParallaxLayer;

public class SetTeamScreen extends GameScreen {
	private static final String TITLE_FONT = "bigFont";
	private static final String TITLE = "SET TEAM";
	private static final String YOUR_TEAM = "Your Team";
	private static final String EMPTY_ERROR = "Team can't be empty";
	private static final String AVAILABLE = "Available Heroes";
	private static final String EXIT = "exit";
	private static final String SAVE = "Save and Exit";
	private static final int FADE_IN_DURATION = 2;
	private static final int FADE_OUT_DURATION = 2;

	private Label titleLabel;
	private Label yourTeamLabel;
	private Label availableHeroesLabel;
	private Label notEmptyLabel;
	private TextButton exit;
	private TextButton save;
	private Stage stage;
	private Table mainTable;
	private Table titleTable;
	private Table heroesTitlesTable;
	private Table allHeroesTable;

	// ability table
	private Table abilityTable;
	private ImageButton abilityIcon;
	private TextArea abilityInfo;
	private VideoDrawable videoDrawable;

	private VerticalGroup selectedHeroesTableVerticalGroup;
	private Table saveAndExitTable;

	private OrthographicCamera parallaxCamera;
	private ParallaxBackground parallaxBackground;
	private SpriteBatch backgroundbatch;

	private Array<String> availableHeroesNames;
	private Array<String> teamHeroesNames;
	private Array<ImageButton> availableHeroes;
	private Array<ImageButton> teamHeroes;
	private ObjectMap<Integer, EntityData> entityData;
	private Map<String, String> heroNamesToImagePaths;
	private float maxHeroCount;
	private ImageButton button;

	public SetTeamScreen() {
		initializeVariables();
		fillAvailableHeroes();
		createBackground();
		createButtons();
		createHeroPortraits();
		createAbilityInfoPanel();
		addButtons();
		addListeners();
	}

	private void initializeVariables() {
		backgroundbatch = new SpriteBatch();
		button = new ImageButton(AssetManagerUtility.getSkin());
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), backgroundbatch);
		parallaxCamera = new OrthographicCamera();
		parallaxCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		parallaxCamera.update();
		mainTable = new Table();
		titleTable = new Table();
		heroesTitlesTable = new Table();
		allHeroesTable = new Table();
		saveAndExitTable = new Table();
		availableHeroes = new Array<>();
		teamHeroes = new Array<>();
		heroNamesToImagePaths = new HashMap<>();
	}

	private void fillAvailableHeroes() {
		availableHeroesNames = ProfileManager.getInstance().getAvailableHeroes();
		teamHeroesNames = ProfileManager.getInstance().getTeamHeroes();
		maxHeroCount = ProfileManager.getInstance().getMaxHeroCount();
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

		notEmptyLabel = new Label(EMPTY_ERROR, statusUISkin, TITLE_FONT);

		exit = new TextButton(EXIT, statusUISkin);
		exit.align(Align.bottom);
		save = new TextButton(SAVE, statusUISkin);
		save.align(Align.bottom);
	}

	private void createHeroPortraits() {
		final ImageButtonStyle btnStyle = button.getStyle();
		for (final EntityData data : entityData.values()) {
			final String heroImageName = data.getPortraitSpritePath();
			final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(heroImageName));
			final TextureRegionDrawable buttonImage = new TextureRegionDrawable(tr);
			final ImageButtonStyle heroButtonStyle = new ImageButtonStyle();
			heroButtonStyle.imageUp = buttonImage;
			heroButtonStyle.up = btnStyle.up;
			heroButtonStyle.down = btnStyle.down;
			final ImageButton heroImageButton = new ImageButton(heroButtonStyle);
			availableHeroes.add(heroImageButton);

			heroImageButton.addListener(new InputListener() {
				@Override
				public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
					tryToAddHeroToTeam(heroImageName);
					teamHeroesNames.add(data.getName());
					return true;
				}
			});

			heroImageButton.addListener(new InputListener() {
				@Override
				public void enter(InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
					final ImageButtonStyle heroButtonStyle = new ImageButtonStyle();
					heroButtonStyle.imageUp = buttonImage;
					heroButtonStyle.up = btnStyle.up;
					heroButtonStyle.down = btnStyle.down;
					abilityIcon.setStyle(heroButtonStyle);
					abilityInfo.setText(data.getSpellExplanation());
				}
			});

			heroNamesToImagePaths.put(data.getName(), heroImageName);
		}
	}

	private void tryToAddHeroToTeam(String heroImageName) {
		final int currentHeroes = teamHeroes.size;
		if (currentHeroes < maxHeroCount) {
			final ImageButtonStyle btnStyle = button.getStyle();
			final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(heroImageName));
			final TextureRegionDrawable buttonImage = new TextureRegionDrawable(tr);
			final ImageButtonStyle heroButtonStyle = new ImageButtonStyle();
			heroButtonStyle.imageUp = buttonImage;
			heroButtonStyle.up = btnStyle.up;
			heroButtonStyle.down = btnStyle.down;
			final ImageButton heroImageButton = new ImageButton(heroButtonStyle);
			teamHeroes.add(heroImageButton);
			selectedHeroesTableVerticalGroup.addActor(heroImageButton);

			heroImageButton.addListener(new InputListener() {
				@Override
				public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
					teamHeroes.removeValue(heroImageButton, true);
					for (final Entry<String, String> nameAndPath : heroNamesToImagePaths.entrySet()) {
						if (nameAndPath.getValue().equals(heroImageName)) {
							teamHeroesNames.removeValue(nameAndPath.getKey(), false);
						}
					}
					heroImageButton.remove();
					selectedHeroesTableVerticalGroup.removeActor(heroImageButton);
					return true;
				}
			});
		}
	}

	private void createAbilityInfoPanel() {
		abilityTable = new Table();

		final ImageButtonStyle imageButtonStyle = AssetManagerUtility.getSkin().get("Portrait", ImageButtonStyle.class);
		abilityIcon = new ImageButton(imageButtonStyle);

		abilityInfo = new TextArea("AbilityInfo", AssetManagerUtility.getSkin());
		abilityInfo.setText("this unit can launche a fireball that damages a single target for 5 fire damage");

		abilityTable.center();
		abilityTable.add(abilityIcon);
		abilityTable.add(abilityInfo).width(500).height(150);

		videoDrawable = new VideoDrawable(Gdx.files.internal("video/test.webm"));
		final Image image = new Image(videoDrawable);
		abilityTable.add(image).width(500).height(150);
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
		titleTable = new Table();
		allHeroesTable = new Table();
		selectedHeroesTableVerticalGroup = new VerticalGroup();
		saveAndExitTable = new Table();
		titleTable.add(titleLabel);
		mainTable.setFillParent(true);
		mainTable.add(titleTable).align(Align.top).colspan(2).height(50).padTop(30).expandX().fillX().row();

		heroesTitlesTable.add(yourTeamLabel).align(Align.top).width(700).expandX().fillX();
		heroesTitlesTable.add(availableHeroesLabel).align(Align.right).width(700).expandX().fillX().row();
		mainTable.add(heroesTitlesTable).colspan(2).expandX().fillX().row();

		int count = 0;
		for (final ImageButton heroButton : availableHeroes) {
			if (count >= 4) {
				allHeroesTable.add(heroButton).pad(0).size(50);
				allHeroesTable.row();
				count = 0;
			} else {
				allHeroesTable.add(heroButton).pad(0).size(50);
				count++;
			}
		}

		mainTable.add(allHeroesTable).align(Align.center).align(Align.center).colspan(1).width(500).expand();

		for (final String name : teamHeroesNames) {
			tryToAddHeroToTeam(heroNamesToImagePaths.get(name));
		}
		mainTable.add(selectedHeroesTableVerticalGroup).align(Align.center).colspan(1).width(500).expand().row();

		mainTable.add(abilityTable).colspan(3).expandX().row();

		saveAndExitTable.add(exit).padTop(100).height(50).width(100);
		saveAndExitTable.add(save).padTop(100).height(50).width(150);

		mainTable.add(saveAndExitTable).colspan(2);

		mainTable.pack();
		stage.addActor(mainTable);

		notEmptyLabel.setPosition(Gdx.app.getGraphics().getWidth() / 2, Gdx.app.getGraphics().getHeight() / 2);
		notEmptyLabel.setVisible(false);
		stage.addActor(notEmptyLabel);
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
				if (teamHeroesNames.isEmpty()) {
					notEmptyLabel.setVisible(true);
					notEmptyLabel.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(FADE_IN_DURATION), Actions.fadeOut(FADE_OUT_DURATION)));
				} else {
					ProfileManager.getInstance().setTeamHeroes(teamHeroesNames);
					ProfileManager.getInstance().saveSettings();
					ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);
				}
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
		stage.getViewport().update(width, height);
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
		videoDrawable.dispose();
	}
}
