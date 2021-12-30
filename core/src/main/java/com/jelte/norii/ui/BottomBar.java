package com.jelte.norii.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.AssetManagerUtility;

public class BottomBar {
	private static final String NO_ABILITY_TEXT = "NoAbility";
	private static final String BACKGROUND_SKIN = "windowgray";
	private static final String UNKNOWN_HERO_IMAGE = "nochar";
	private static final String MOVE_BUTTON_SPRITE_NAME = "move";
	private static final String ATTACK_BUTTON_SPRITE_NAME = "attack";
	private static final String SKIP_BUTTON_SPRITE_NAME = "skip";
	private static final String DEFAULT_SKIN = "default";
	private static final String ACTIONS = "Actions";
	private static final String INFO = "Ability";
	private static final String STATS = "Stats";

	private static final int WINDOW_PADDING = 0;
	private static final int HERO_NAME_LABEL_HEIGHT = 15;
	private static final int HERO_NAME_LABEL_WIDTH = 55;
	private static final int STATS_WIDTH = 80;
	private static final int STATS_HEIGHT = 20;
	private static final int PAD_BOTTOM_TITLE = 10;
	private static final float ICON_PADDING = 2f;

	private int heroHP;
	private int heroMagDef;
	private int heroPhyDef;
	private final float tilePixelWidth;
	private final float tilePixelHeight;

	private ImageButton heroImageButton;
	private Table table;

	private Label heroNameLabel;
	private Label underscore;
	private Label hpLabel;
	private Label hp;
	private Label magDefLabel;
	private Label magDef;
	private Label phyDefLabel;
	private Label phyDef;

	private Label actionsLabel;
	private TextButton moveImageButton;
	private TextButton attackImageButton;
	private TextButton skipImageButton;
	private SpellImageButton spellImageButton;

	private Label infoLabel;
	private Label statsLabel;
	private Label spellInfo;
	private ImageButton abilityIcon;

	private Entity activeUnit;
	private boolean actionsVisible;
	private final Hud hud;

	public BottomBar(int mapWidth, int mapHeight, Hud hud) {
		tilePixelWidth = Hud.UI_VIEWPORT_WIDTH / mapWidth;
		tilePixelHeight = Hud.UI_VIEWPORT_HEIGHT / mapHeight;
		this.hud = hud;

		initElementsForUI();
		populateHeroImageAndStats();
	}

	private void initElementsForUI() {
		initPortrait();
		initStatsMenu();
		initActions();
	}

	private void initPortrait() {
		final ImageButtonStyle imageButtonStyle = AssetManagerUtility.getSkin().get("Portrait", ImageButtonStyle.class);
		heroImageButton = new ImageButton(imageButtonStyle);
	}

	private void initStatsMenu() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();
		heroNameLabel = new Label("", statusUISkin);
		underscore = new Label("______", statusUISkin);
		hpLabel = new Label("hp: ", statusUISkin);
		hp = new Label("", statusUISkin);
		magDefLabel = new Label("mag def: ", statusUISkin);
		magDef = new Label("", statusUISkin);
		phyDefLabel = new Label("phy def: ", statusUISkin);
		phyDef = new Label("", statusUISkin);
	}

	private void initActions() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();
		actionsLabel = new Label(ACTIONS, AssetManagerUtility.getSkin());
		infoLabel = new Label(INFO, AssetManagerUtility.getSkin());
		statsLabel = new Label(STATS, AssetManagerUtility.getSkin());
		spellInfo = new Label("", AssetManagerUtility.getSkin());
		final ImageButtonStyle abilityIconStyle = new ImageButtonStyle();
		abilityIcon = new ImageButton(abilityIconStyle);
		moveImageButton = new TextButton(MOVE_BUTTON_SPRITE_NAME, statusUISkin, DEFAULT_SKIN);
		moveImageButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				hud.sendMessage(MessageToBattleScreen.CLICKED_ON_MOVE, activeUnit.getEntityID(), null);
			}
		});

		attackImageButton = new TextButton(ATTACK_BUTTON_SPRITE_NAME, statusUISkin, DEFAULT_SKIN);
		attackImageButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				hud.sendMessage(MessageToBattleScreen.CLICKED_ON_ATTACK, activeUnit.getEntityID(), null);
			}
		});

		skipImageButton = new TextButton(SKIP_BUTTON_SPRITE_NAME, statusUISkin, DEFAULT_SKIN);
		skipImageButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				hud.sendMessage(MessageToBattleScreen.CLICKED_ON_SKIP, activeUnit.getEntityID(), null);
			}
		});

		spellImageButton = new SpellImageButton();
		spellImageButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				hud.sendMessage(MessageToBattleScreen.CLICKED_ON_ABILITY, activeUnit.getEntityID(), spellImageButton.getAbility());
			}
		});
		setActionsVisible(false);
	}

	private void changeSpellImage(final String spellImageName) {
		spellImageButton.setText(spellImageName);
		final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(spellImageName));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		final ImageButtonStyle oldStyle = abilityIcon.getStyle();
		oldStyle.imageUp = trd;
		oldStyle.imageUp.setMinHeight(tilePixelHeight * 3);
		oldStyle.imageUp.setMinWidth(tilePixelWidth * 3);
		abilityIcon.setStyle(oldStyle);
	}

	private void changeHeroImage(final String heroImageName) {
		final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(heroImageName));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		final ImageButtonStyle oldStyle = heroImageButton.getStyle();
		oldStyle.imageUp = trd;
		oldStyle.imageUp.setMinHeight(tilePixelHeight * 3);
		oldStyle.imageUp.setMinWidth(tilePixelWidth * 3);
		heroImageButton.setStyle(oldStyle);
	}

	private void changeHeroImage() {
		changeHeroImage(UNKNOWN_HERO_IMAGE);
	}

	private void populateHeroImageAndStats() {
		createMainTable();

		final Table subtable = createSubTable();
		final Table actionsTable = createActionsTable();
		final Table infoTable = createInfoTable();

		table.add(subtable).align(Align.topLeft).height(tilePixelHeight * 3).width(tilePixelWidth * 6);
		table.add(actionsTable).align(Align.topLeft).height(tilePixelHeight * 3).width(tilePixelWidth * 6);
		table.add(infoTable).align(Align.topLeft).height(tilePixelHeight * 3).width(tilePixelWidth * 6);

		table.validate();
		table.invalidateHierarchy();
		table.pack();

	}

	private void createMainTable() {
		table = new Table();
		table.setBackground(AssetManagerUtility.getSkin().getDrawable("window-noborder"));
		table.setTransform(false);
		table.setPosition(0, 0);
		table.pad(0);
		table.add(heroImageButton).width(tilePixelWidth * 3).height((tilePixelHeight * 3)).pad(0).align(Align.bottomLeft);
	}

	private Table createSubTable() {
		final Table subtable = new Table();
		subtable.add(statsLabel).height(HERO_NAME_LABEL_HEIGHT).align(Align.topLeft).width(HERO_NAME_LABEL_WIDTH).padTop(7).padBottom(PAD_BOTTOM_TITLE);
		subtable.row();
		subtable.align(Align.topLeft);
		subtable.pad(WINDOW_PADDING);
		subtable.padLeft(5);
		subtable.align(Align.topLeft);
		subtable.add(heroNameLabel).height(HERO_NAME_LABEL_HEIGHT).align(Align.topLeft).width(HERO_NAME_LABEL_WIDTH).padTop(10).padBottom(0);
		subtable.row();
		subtable.add(underscore).height(HERO_NAME_LABEL_HEIGHT).align(Align.topLeft).width(HERO_NAME_LABEL_WIDTH).padTop(0).padBottom(5);
		subtable.row();

		subtable.add(hpLabel).align(Align.bottomLeft).width(STATS_WIDTH).height(STATS_HEIGHT);
		subtable.add(hp).height(STATS_HEIGHT).width(STATS_WIDTH).padBottom(5);
		subtable.row();

		subtable.add(magDefLabel).align(Align.bottomLeft).width(STATS_WIDTH).height(STATS_HEIGHT).colspan(1);
		subtable.add(magDef).height(STATS_HEIGHT).padBottom(5);
		subtable.add().padLeft(25).width(STATS_WIDTH);
		subtable.add().padLeft(25).width(STATS_WIDTH);
		subtable.add().padLeft(25).width(STATS_WIDTH);
		subtable.row();

		subtable.add(phyDefLabel).align(Align.bottomLeft).width(STATS_WIDTH).height(STATS_HEIGHT).colspan(1);
		subtable.add(phyDef).height(STATS_HEIGHT);
		subtable.add().padLeft(25).width(STATS_WIDTH);
		subtable.add().padLeft(25).width(STATS_WIDTH);
		subtable.add().padLeft(25).width(STATS_WIDTH);

		subtable.setBackground(AssetManagerUtility.getSkin().getDrawable(BACKGROUND_SKIN));
		return subtable;
	}

	private Table createActionsTable() {
		final Table actionsTable = new Table();
		actionsTable.add(actionsLabel).height(HERO_NAME_LABEL_HEIGHT).align(Align.topLeft).width(HERO_NAME_LABEL_WIDTH).padTop(7).padBottom(PAD_BOTTOM_TITLE);
		actionsTable.row();
		actionsTable.align(Align.topLeft);
		actionsTable.pad(WINDOW_PADDING);
		actionsTable.padLeft(5);
		actionsTable.add(moveImageButton).size(tilePixelWidth * 2, tilePixelHeight * 1).pad(ICON_PADDING);
		actionsTable.add(attackImageButton).size(tilePixelWidth * 2, tilePixelHeight * 1).pad(ICON_PADDING);
		actionsTable.row();
		actionsTable.add(skipImageButton).size(tilePixelWidth * 2, tilePixelHeight * 1).pad(ICON_PADDING);
		actionsTable.add(spellImageButton).size(tilePixelWidth * 2, tilePixelHeight * 1).pad(ICON_PADDING);

		actionsTable.setBackground(AssetManagerUtility.getSkin().getDrawable(BACKGROUND_SKIN));
		return actionsTable;
	}

	private Table createInfoTable() {
		final Table infoTable = new Table();
		infoTable.add(infoLabel).height(HERO_NAME_LABEL_HEIGHT).align(Align.topLeft).width(HERO_NAME_LABEL_WIDTH).padTop(7).padBottom(PAD_BOTTOM_TITLE);
		infoTable.row();
		infoTable.align(Align.topLeft);
		infoTable.pad(WINDOW_PADDING);
		infoTable.padLeft(5);
		infoTable.add(abilityIcon).size(tilePixelWidth, tilePixelHeight);
		infoTable.add(spellInfo).size(tilePixelWidth, tilePixelHeight);
		infoTable.setBackground(AssetManagerUtility.getSkin().getDrawable(BACKGROUND_SKIN));
		return infoTable;
	}

	public void setHero(final Entity entity) {
		if (entity != null) {
			if (!entity.getEntityData().getName().equalsIgnoreCase(heroNameLabel.getText().toString())) {
				spellImageButton.setEntity(entity);
				if (entity.getAbility() != null) {
					spellImageButton.setAbility(entity.getAbility());
				} else {
					spellImageButton.clearAbility();
					changeSpellImage(NO_ABILITY_TEXT);
					spellInfo.setText("");
				}
				activeUnit = entity;
				this.getTable().setVisible(true);
				initiateHeroStats(entity);
				populateElementsForUI(entity);
			}
		} else {
			resetStats();
		}
	}

	private void initiateHeroStats(Entity entity) {
		heroHP = entity.getHp();
		heroMagDef = entity.getEntityData().getMagicalDefense();
		heroPhyDef = entity.getEntityData().getPhysicalDefense();
	}

	private void populateElementsForUI(final Entity entity) {
		heroNameLabel.setText(entity.getEntityData().getName());
		changeHeroImage(entity.getEntityData().getPortraitSpritePath());
		Ability ability = entity.getAbility();
		changeSpellImage(ability.getSpellData().getIconSpriteName());
		changeSpellInfo(ability.getSpellData().getInfoText());

	}

	private void changeSpellInfo(String infoText) {
		spellInfo.setText(infoText);
	}

	private void resetStats() {
		heroNameLabel.setText("");
		changeHeroImage();
		this.getTable().setVisible(false);
	}

	public void update(Entity entity) {
		updateStats(entity);
		updateLabels();
	}

	private void updateStats(Entity entity) {
		if (entity != null) {
			heroHP = entity.getHp();
			heroMagDef = entity.getEntityData().getMagicalDefense();
			heroPhyDef = entity.getEntityData().getPhysicalDefense();
		}
	}

	private void updateLabels() {
		hp.setText(String.valueOf(heroHP));
		magDef.setText(String.valueOf(heroMagDef) + "%");
		phyDef.setText(String.valueOf(heroPhyDef) + "%");
	}

	public Table getTable() {
		return table;
	}

	public boolean isActionsVisible() {
		return actionsVisible;
	}

	public void setActionsVisible(boolean actionsVisible) {
		this.actionsVisible = actionsVisible;
		moveImageButton.setVisible(actionsVisible);
		skipImageButton.setVisible(actionsVisible);
		attackImageButton.setVisible(actionsVisible);
		spellImageButton.setVisible(actionsVisible);
	}

}
