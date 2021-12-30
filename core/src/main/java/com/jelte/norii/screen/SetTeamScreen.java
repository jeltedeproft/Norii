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
import com.jelte.norii.magic.AbilitiesEnum;
import com.jelte.norii.magic.SpellData;
import com.jelte.norii.magic.SpellFileReader;
import com.jelte.norii.profile.ProfileManager;
import com.jelte.norii.ui.VideoDrawable;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.parallax.ParallaxBackground;
import com.jelte.norii.utility.parallax.ParallaxUtils.WH;
import com.jelte.norii.utility.parallax.TextureRegionParallaxLayer;

public class SetTeamScreen extends GameScreen {
	private static final int PREVIEW_VIDEO_HEIGHT = 450;
	private static final int PREVIEW_VIDEO_WIDTH = 500;
	private static final int ABILITY_INFO_HEIGHT = 450;
	private static final int ABILITY_INFO_WIDTH = 300;
	private static final int MAX_HEROES_PER_ROW = 4;
	private static final int MAIN_TABLE_COLSPAN = 2;
	private static final int SAVE_BUTTON_WIDTH = 150;
	private static final int SAVE_BUTTON_HEIGHT = 50;
	private static final int SAVE_BUTTON_PAD_TOP = 100;
	private static final int EXIT_BUTTON_WIDTH = 100;
	private static final int EXIT_BUTTON_HEIGHT = 50;
	private static final int EXIT_BUTTON_PAD_TOP = 100;
	private static final int ABILITY_TABLE_COLSPAN = 3;
	private static final int SELECTED_HEROES_WIDTH = 500;
	private static final int SELECTED_HEROES_COLSPAN = 1;
	private static final int ALL_HEROES_TABLE_WIDTH = 500;
	private static final int ALL_HEROES_TABLE_COLSPAN = 1;
	private static final int ALL_HEROES_TABLE_SIZE = 50;
	private static final int ALL_HEROES_TABLE_PAD = 0;
	private static final int HEROES_TITLES_TABLE_COLSPAN = 2;
	private static final int AVAILABLE_HEROES_LABEL_WIDTH = 700;
	private static final int TEAM_LABEL_WIDTH = 700;
	private static final int TITLE_PAD_TOP = 30;
	private static final int TITLE_HEIGHT = 50;
	private static final int TITLE_COLSPAN = 2;
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
	private TextButton exitTextButton;
	private TextButton saveTextButton;
	private Stage stage;
	private Table mainTable;
	private Table titleTable;
	private Table heroesTitlesTable;
	private Table allHeroesTable;

	// ability table
	private Table unitInfoTable;
	private Table unitStatsTable;
	private Table abilityStatsTable;
	private ImageButton abilityIcon;
	private TextArea abilityInfo;
	private TextArea unitInfo;
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
	private ObjectMap<Integer, SpellData> spellData;
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
		spellData = SpellFileReader.getSpellData();
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

		exitTextButton = new TextButton(EXIT, statusUISkin);
		exitTextButton.align(Align.bottom);
		saveTextButton = new TextButton(SAVE, statusUISkin);
		saveTextButton.align(Align.bottom);
	}

	private void createHeroPortraits() {
		final ImageButtonStyle btnStyle = button.getStyle();
		for (final EntityData entity : entityData.values()) {
			final SpellData spellForEntity = findSpellForUnit(entity);
			final String heroImageName = entity.getPortraitSpritePath();
			final String abilityIconName = spellForEntity.getIconSpriteName();

			final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(heroImageName));
			final TextureRegion trAbility = new TextureRegion(AssetManagerUtility.getSprite(abilityIconName));

			final TextureRegionDrawable buttonImage = new TextureRegionDrawable(tr);
			final TextureRegionDrawable abilityImage = new TextureRegionDrawable(trAbility);

			final ImageButtonStyle heroButtonStyle = new ImageButtonStyle();
			heroButtonStyle.imageUp = buttonImage;
			heroButtonStyle.up = btnStyle.up;
			heroButtonStyle.down = btnStyle.down;
			final ImageButton heroImageButton = new ImageButton(heroButtonStyle);
			availableHeroes.add(heroImageButton);

			heroImageButton.addListener(new InputListener() {
				@Override
				public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
					tryAddHeroImageButtonToTeamPanel(heroImageName);
					teamHeroesNames.add(entity.getName());
					return true;
				}
			});

			heroImageButton.addListener(new InputListener() {
				@Override
				public void enter(InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
					// ability
					final ImageButtonStyle abilityButtonStyle = new ImageButtonStyle();
					abilityButtonStyle.imageUp = abilityImage;
					abilityButtonStyle.up = btnStyle.up;
					abilityButtonStyle.down = btnStyle.down;
					abilityIcon.setStyle(abilityButtonStyle);
					abilityInfo.setText("ability: " + spellForEntity.getName() + "\n__________\n damage: " + spellForEntity.getDamage());

					// unit
					unitInfo.setText(entity.getName() + "\n__________\n" + swapVariables(entity.getSpellExplanation(), spellForEntity));
				}
			});

			heroNamesToImagePaths.put(entity.getName(), heroImageName);
		}
	}

	private SpellData findSpellForUnit(EntityData entity) {
		for (final SpellData spell : spellData.values()) {
			if (AbilitiesEnum.valueOf(entity.getAbility()).ordinal() == spell.getId()) {
				return spell;
			}
		}
		return null;
	}

	protected String swapVariables(String spellExplanation, SpellData spellData) {
		spellExplanation = spellExplanation.replaceFirst("%DAMAGE%", Integer.toString(spellData.getDamage()));
		spellExplanation = spellExplanation.replaceFirst("%APCOST%", Integer.toString(spellData.getApCost()));
		spellExplanation = spellExplanation.replaceFirst("%RANGE%", Integer.toString(spellData.getRange()));
		return spellExplanation.replaceFirst("%TURNS%", Integer.toString(spellData.getDurationInTurns()));
	}

	private void tryAddHeroImageButtonToTeamPanel(String heroImageName) {
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
		unitInfoTable = new Table();
		unitStatsTable = new Table();
		abilityStatsTable = new Table();

		unitInfo = new TextArea("UnitInfo", AssetManagerUtility.getSkin());
		unitInfo.setText("");

		abilityInfo = new TextArea("AbilityInfo", AssetManagerUtility.getSkin());
		abilityInfo.setText("");

		final ImageButtonStyle imageButtonStyle = AssetManagerUtility.getSkin().get("Portrait", ImageButtonStyle.class);
		abilityIcon = new ImageButton(imageButtonStyle);

		unitInfoTable.center();
		unitInfoTable.add(unitInfo).width(ABILITY_INFO_WIDTH).height(ABILITY_INFO_HEIGHT);
		unitInfoTable.add(abilityInfo).width(ABILITY_INFO_WIDTH).height(ABILITY_INFO_HEIGHT);
		// unitInfoTable.add(abilityIcon);

		videoDrawable = new VideoDrawable(Gdx.files.internal("video/test.webm"));
		final Image image = new Image(videoDrawable);
		unitInfoTable.add(image).width(PREVIEW_VIDEO_WIDTH).height(PREVIEW_VIDEO_HEIGHT);
	}

	private void createBackground() {
		final int worldHeight = Gdx.graphics.getHeight();

		final TextureAtlas atlas = AssetManagerUtility.getTextureAtlas(AssetManagerUtility.SPRITES_ATLAS_PATH);

		final TextureRegion backTrees = atlas.findRegion("background-back-trees");
		final TextureRegionParallaxLayer backTreesLayer = new TextureRegionParallaxLayer(backTrees, worldHeight, new Vector2(.3f, .3f), WH.HEIGHT);

		final TextureRegion lights = atlas.findRegion("background-light");
		final TextureRegionParallaxLayer lightsLayer = new TextureRegionParallaxLayer(lights, worldHeight, new Vector2(.6f, .6f), WH.HEIGHT);

		final TextureRegion middleTrees = atlas.findRegion("background-middle-trees");
		final TextureRegionParallaxLayer middleTreesLayer = new TextureRegionParallaxLayer(middleTrees, worldHeight, new Vector2(.75f, .75f), WH.HEIGHT);

		final TextureRegion frontTrees = atlas.findRegion("foreground");
		final TextureRegionParallaxLayer frontTreesLayer = new TextureRegionParallaxLayer(frontTrees, worldHeight, new Vector2(.6f, .6f), WH.HEIGHT);

		parallaxBackground = new ParallaxBackground();
		parallaxBackground.addLayers(backTreesLayer, lightsLayer, middleTreesLayer, frontTreesLayer);
	}

	private void addButtons() {
		initTables();

		addStuffToTables();

		mainTable.pack();
		stage.addActor(mainTable);

		notEmptyLabel.setPosition(Gdx.app.getGraphics().getWidth() / 2.0f, Gdx.app.getGraphics().getHeight() / 2.0f);
		notEmptyLabel.setVisible(false);
		stage.addActor(notEmptyLabel);
	}

	private void addStuffToTables() {
		titleTable.add(titleLabel);
		mainTable.add(titleTable).align(Align.top).colspan(TITLE_COLSPAN).height(TITLE_HEIGHT).padTop(TITLE_PAD_TOP).expandX().fillX().row();
		heroesTitlesTable.add(yourTeamLabel).align(Align.top).width(TEAM_LABEL_WIDTH).expandX().fillX();
		heroesTitlesTable.add(availableHeroesLabel).align(Align.right).width(AVAILABLE_HEROES_LABEL_WIDTH).expandX().fillX().row();
		mainTable.add(heroesTitlesTable).colspan(HEROES_TITLES_TABLE_COLSPAN).expandX().fillX().row();

		int count = 0;
		for (final ImageButton heroButton : availableHeroes) {
			if (count >= MAX_HEROES_PER_ROW) {
				allHeroesTable.add(heroButton).pad(ALL_HEROES_TABLE_PAD).size(ALL_HEROES_TABLE_SIZE);
				allHeroesTable.row();
				count = 0;
			} else {
				allHeroesTable.add(heroButton).pad(ALL_HEROES_TABLE_PAD).size(ALL_HEROES_TABLE_SIZE);
				count++;
			}
		}

		mainTable.add(allHeroesTable).align(Align.center).align(Align.center).colspan(ALL_HEROES_TABLE_COLSPAN).width(ALL_HEROES_TABLE_WIDTH).expand();

		for (final String name : teamHeroesNames) {
			tryAddHeroImageButtonToTeamPanel(heroNamesToImagePaths.get(name));
		}
		mainTable.add(selectedHeroesTableVerticalGroup).align(Align.center).colspan(SELECTED_HEROES_COLSPAN).width(SELECTED_HEROES_WIDTH).expand().row();

		mainTable.add(unitInfoTable).colspan(ABILITY_TABLE_COLSPAN).expandX().row();

		saveAndExitTable.add(exitTextButton).padTop(EXIT_BUTTON_PAD_TOP).height(EXIT_BUTTON_HEIGHT).width(EXIT_BUTTON_WIDTH);
		saveAndExitTable.add(saveTextButton).padTop(SAVE_BUTTON_PAD_TOP).height(SAVE_BUTTON_HEIGHT).width(SAVE_BUTTON_WIDTH);

		mainTable.add(saveAndExitTable).colspan(MAIN_TABLE_COLSPAN);
	}

	private void initTables() {
		titleTable = new Table();
		allHeroesTable = new Table();
		selectedHeroesTableVerticalGroup = new VerticalGroup();
		saveAndExitTable = new Table();
		mainTable.setFillParent(true);
	}

	private void addListeners() {
		exitTextButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
				ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);
				return true;
			}
		});

		saveTextButton.addListener(new InputListener() {
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
