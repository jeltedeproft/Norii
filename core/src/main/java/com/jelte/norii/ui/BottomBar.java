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

public class BottomBar extends Table {
	private static final int PAD_TOP_HERO_NAME_LABEL = 10;
	private static final int PAD_BOTTOM_STATS = 10;
	private static final int PAD_LEFT_STATS = 5;
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
	private static final int HERO_NAME_LABEL_HEIGHT = 20;
	private static final int HERO_NAME_LABEL_WIDTH = 50;
	private static final int STATS_WIDTH = 20;
	private static final int STATS_HEIGHT = 20;
	private static final int PAD_BOTTOM_TITLE = 10;
	private static final int ICON_PADDING = 10;

	private int heroHP;
	private int heroMagDef;
	private int heroPhyDef;

	private ImageButton heroImageButton;

	private Label heroNameLabel;
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

	private Table subtable;
	private Table actionsTable;
	private Table infoTable;

	private Entity activeUnit;
	private boolean actionsVisible;
	private final Hud hud;

	public BottomBar(Hud hud) {
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
		hpLabel = new Label("hp: ", statusUISkin);
		hp = new Label("", statusUISkin);
		magDefLabel = new Label("mag def: ", statusUISkin);
		magDef = new Label("", statusUISkin);
		phyDefLabel = new Label("phy def: ", statusUISkin);
		phyDef = new Label("", statusUISkin);
	}

	private void initActions() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();
		actionsLabel = new Label(ACTIONS, statusUISkin);
		infoLabel = new Label(INFO, statusUISkin);
		statsLabel = new Label(STATS, statusUISkin);
		spellInfo = new Label("", statusUISkin);
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
		oldStyle.imageUp.setMinHeight(Hud.UI_VIEWPORT_HEIGHT * 0.2f);
		oldStyle.imageUp.setMinWidth(Hud.UI_VIEWPORT_WIDTH * 0.2f);
		abilityIcon.setStyle(oldStyle);
	}

	private void changeHeroImage(final String heroImageName) {
		final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(heroImageName));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		final ImageButtonStyle oldStyle = heroImageButton.getStyle();
		oldStyle.imageUp = trd;
		oldStyle.imageUp.setMinHeight(Hud.UI_VIEWPORT_HEIGHT * 0.2f);
		oldStyle.imageUp.setMinWidth(Hud.UI_VIEWPORT_WIDTH * 0.2f);
		heroImageButton.setStyle(oldStyle);
	}

	private void changeHeroImage() {
		changeHeroImage(UNKNOWN_HERO_IMAGE);
	}

	private void populateHeroImageAndStats() {
		configureMainTable();

		subtable = createStatsTable();
		actionsTable = createActionsTable();
		infoTable = createInfoTable();

		add(subtable).align(Align.topLeft).height(Hud.UI_VIEWPORT_HEIGHT * 0.15f).width(Hud.UI_VIEWPORT_WIDTH * 0.33f);
		add(actionsTable).align(Align.topLeft).height(Hud.UI_VIEWPORT_HEIGHT * 0.15f).width(Hud.UI_VIEWPORT_WIDTH * 0.33f);
		add(infoTable).align(Align.topLeft).height(Hud.UI_VIEWPORT_HEIGHT * 0.15f).width(Hud.UI_VIEWPORT_WIDTH * 0.70f);

		validate();
		invalidateHierarchy();
		pack();

	}

	private void configureMainTable() {
		setBackground(AssetManagerUtility.getSkin().getDrawable("window-noborder"));
		setTransform(false);
		pad(0);
		add(heroImageButton).width(Hud.UI_VIEWPORT_WIDTH * 0.1f).height((Hud.UI_VIEWPORT_HEIGHT * 0.15f)).pad(0).align(Align.center);
	}

	private Table createStatsTable() {
		final Table statsTable = new Table();
		statsTable.add(statsLabel).height(HERO_NAME_LABEL_HEIGHT).align(Align.topLeft).width(HERO_NAME_LABEL_WIDTH).padTop(7).padBottom(PAD_BOTTOM_TITLE);
		statsTable.row();
		statsTable.align(Align.topLeft);
		statsTable.pad(WINDOW_PADDING);
		statsTable.padLeft(5);
		statsTable.add(heroNameLabel).height(HERO_NAME_LABEL_HEIGHT).align(Align.topLeft).width(HERO_NAME_LABEL_WIDTH).padTop(PAD_TOP_HERO_NAME_LABEL).padBottom(0);
		statsTable.row();

		statsTable.add(hpLabel).align(Align.bottomLeft).width(STATS_WIDTH).height(STATS_HEIGHT);
		statsTable.add(hp).height(STATS_HEIGHT).width(STATS_WIDTH).padBottom(PAD_BOTTOM_STATS).padLeft(PAD_LEFT_STATS);
		statsTable.row();

		statsTable.add(magDefLabel).align(Align.bottomLeft).width(STATS_WIDTH).height(STATS_HEIGHT).colspan(1);
		statsTable.add(magDef).width(STATS_WIDTH).height(STATS_HEIGHT).padBottom(PAD_BOTTOM_STATS).padLeft(PAD_LEFT_STATS);
		statsTable.add().padLeft(PAD_LEFT_STATS).width(STATS_WIDTH);
		statsTable.add().padLeft(PAD_LEFT_STATS).width(STATS_WIDTH);
		statsTable.add().padLeft(PAD_LEFT_STATS).width(STATS_WIDTH);
		statsTable.row();

		statsTable.add(phyDefLabel).align(Align.bottomLeft).width(STATS_WIDTH).height(STATS_HEIGHT).colspan(1);
		statsTable.add(phyDef).width(STATS_WIDTH).height(STATS_HEIGHT).padLeft(PAD_LEFT_STATS);
		statsTable.add().padLeft(PAD_LEFT_STATS).width(STATS_WIDTH);
		statsTable.add().padLeft(PAD_LEFT_STATS).width(STATS_WIDTH);
		statsTable.add().padLeft(PAD_LEFT_STATS).width(STATS_WIDTH);

		statsTable.setBackground(AssetManagerUtility.getSkin().getDrawable(BACKGROUND_SKIN));
		return statsTable;
	}

	private Table createActionsTable() {
		final Table table = new Table();
		table.add(actionsLabel).height(HERO_NAME_LABEL_HEIGHT).align(Align.topLeft).width(HERO_NAME_LABEL_WIDTH).padTop(7).padBottom(PAD_BOTTOM_TITLE);
		table.row();
		table.align(Align.topLeft);
		table.pad(WINDOW_PADDING);
		table.padLeft(5);
		table.add(moveImageButton).size(Hud.UI_VIEWPORT_WIDTH * 0.1f, Hud.UI_VIEWPORT_HEIGHT * 0.04f).pad(ICON_PADDING);
		table.add(attackImageButton).size(Hud.UI_VIEWPORT_WIDTH * 0.1f, Hud.UI_VIEWPORT_HEIGHT * 0.04f).pad(ICON_PADDING);
		table.row();
		table.add(skipImageButton).size(Hud.UI_VIEWPORT_WIDTH * 0.1f, Hud.UI_VIEWPORT_HEIGHT * 0.04f).pad(ICON_PADDING);
		table.add(spellImageButton).size(Hud.UI_VIEWPORT_WIDTH * 0.1f, Hud.UI_VIEWPORT_HEIGHT * 0.04f).pad(ICON_PADDING);

		table.setBackground(AssetManagerUtility.getSkin().getDrawable(BACKGROUND_SKIN));
		return table;
	}

	private Table createInfoTable() {
		final Table table = new Table();
		table.add(infoLabel).height(HERO_NAME_LABEL_HEIGHT).align(Align.topLeft).width(HERO_NAME_LABEL_WIDTH).padTop(7).padBottom(PAD_BOTTOM_TITLE);
		table.row();
		table.align(Align.topLeft);
		table.pad(WINDOW_PADDING);
		table.padLeft(5);
		table.add(abilityIcon).size(Hud.UI_VIEWPORT_WIDTH * 0.03f, Hud.UI_VIEWPORT_HEIGHT * 0.03f);
		table.add(spellInfo).size(Hud.UI_VIEWPORT_WIDTH * 0.03f, Hud.UI_VIEWPORT_HEIGHT * 0.03f);
		table.setBackground(AssetManagerUtility.getSkin().getDrawable(BACKGROUND_SKIN));
		return table;
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
				setVisible(true);
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
		setVisible(false);
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
