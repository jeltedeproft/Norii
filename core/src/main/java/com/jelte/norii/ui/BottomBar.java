package com.jelte.norii.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.AssetManagerUtility;

public class BottomBar extends Table {
	private static final int PAD_TOP_HERO_NAME_LABEL = 4;
	private static final int PAD_BOTTOM_STATS = 2;
	private static final int PAD_LEFT_STATS = 6;
	private static final String NO_ABILITY_TEXT = "NoAbility";
	private static final String BACKGROUND_SKIN = "windowBackground10Patch";
	private static final String UNKNOWN_HERO_IMAGE = "nochar";
	private static final String ACTIONS = "Actions";
	private static final String INFO = "Ability";
	private static final String STATS = "Stats";

	private static final int WINDOW_PADDING = 6;
	private static final int ROW_HEIGHT = 22;
	private static final int LABEL_WIDTH = 108;
	private static final int VALUE_WIDTH = 56;
	private static final int PAD_BOTTOM_TITLE = 4;
	private static final int ICON_PADDING = 4;

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
	private ImageTextButton moveImageButton;
	private ImageTextButton attackImageButton;
	private ImageTextButton skipImageButton;
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
		spellInfo.setWrap(true);
		final ImageButtonStyle abilityIconStyle = new ImageButtonStyle();
		abilityIcon = new ImageButton(abilityIconStyle);
		moveImageButton = new ImageTextButton("Move", statusUISkin, "action-move");
		moveImageButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				hud.sendMessage(MessageToBattleScreen.CLICKED_ON_MOVE, activeUnit.getEntityID(), null);
			}
		});

		attackImageButton = new ImageTextButton("Attack", statusUISkin, "action-attack");
		attackImageButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				hud.sendMessage(MessageToBattleScreen.CLICKED_ON_ATTACK, activeUnit.getEntityID(), null);
			}
		});

		skipImageButton = new ImageTextButton("Wait", statusUISkin, "action-skip");
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
		final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(spellImageName));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		final ImageButtonStyle oldStyle = abilityIcon.getStyle();
		oldStyle.imageUp = trd;
		oldStyle.imageUp.setMinHeight(64f);
		oldStyle.imageUp.setMinWidth(64f);
		abilityIcon.setStyle(oldStyle);
	}

	private void changeHeroImage(final String heroImageName) {
		final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(heroImageName));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		final ImageButtonStyle oldStyle = heroImageButton.getStyle();
		oldStyle.imageUp = trd;
		oldStyle.imageUp.setMinHeight(72f);
		oldStyle.imageUp.setMinWidth(72f);
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

		add(subtable).align(Align.topLeft).height(140).width(230);
		add(actionsTable).align(Align.topLeft).height(140).width(340);
		add(infoTable).align(Align.topLeft).height(140).width(290);

		validate();
		invalidateHierarchy();
		pack();

	}

	private void configureMainTable() {
		setBackground(AssetManagerUtility.getSkin().getDrawable("window-noborder"));
		setTransform(false);
		pad(0);
		add(heroImageButton).width(96).height(140).pad(0).align(Align.center);
	}

	private Table createStatsTable() {
		final Table statsTable = new Table();
		statsTable.add(statsLabel).height(ROW_HEIGHT).align(Align.topLeft).colspan(2).padTop(4).padBottom(PAD_BOTTOM_TITLE);
		statsTable.row();
		statsTable.align(Align.topLeft);
		statsTable.pad(WINDOW_PADDING);
		statsTable.add(heroNameLabel).height(ROW_HEIGHT).align(Align.left).colspan(2).width(LABEL_WIDTH + VALUE_WIDTH).padTop(PAD_TOP_HERO_NAME_LABEL);
		statsTable.row();

		statsTable.add(hpLabel).align(Align.left).width(LABEL_WIDTH).height(ROW_HEIGHT);
		statsTable.add(hp).height(ROW_HEIGHT).width(VALUE_WIDTH).padBottom(PAD_BOTTOM_STATS).padLeft(PAD_LEFT_STATS);
		statsTable.row();

		statsTable.add(magDefLabel).align(Align.left).width(LABEL_WIDTH).height(ROW_HEIGHT);
		statsTable.add(magDef).width(VALUE_WIDTH).height(ROW_HEIGHT).padBottom(PAD_BOTTOM_STATS).padLeft(PAD_LEFT_STATS);
		statsTable.row();

		statsTable.add(phyDefLabel).align(Align.left).width(LABEL_WIDTH).height(ROW_HEIGHT);
		statsTable.add(phyDef).width(VALUE_WIDTH).height(ROW_HEIGHT).padLeft(PAD_LEFT_STATS);

		statsTable.setBackground(AssetManagerUtility.getSkin().getDrawable(BACKGROUND_SKIN));
		return statsTable;
	}

	private Table createActionsTable() {
		final Table table = new Table();
		table.add(actionsLabel).height(ROW_HEIGHT).align(Align.topLeft).colspan(2).padTop(4).padBottom(PAD_BOTTOM_TITLE);
		table.row();
		table.align(Align.topLeft);
		table.pad(WINDOW_PADDING);
		table.add(moveImageButton).size(150, 36).pad(ICON_PADDING);
		table.add(attackImageButton).size(150, 36).pad(ICON_PADDING);
		table.row();
		table.add(skipImageButton).size(150, 36).pad(ICON_PADDING);
		table.add(spellImageButton).size(150, 36).pad(ICON_PADDING);

		table.setBackground(AssetManagerUtility.getSkin().getDrawable(BACKGROUND_SKIN));
		return table;
	}

	private Table createInfoTable() {
		final Table table = new Table();
		table.add(infoLabel).height(ROW_HEIGHT).align(Align.topLeft).colspan(2).padTop(4).padBottom(PAD_BOTTOM_TITLE);
		table.row();
		table.align(Align.topLeft);
		table.pad(WINDOW_PADDING);
		table.add(abilityIcon).size(64, 64).padRight(8);
		table.add(spellInfo).width(180).height(72);
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
		String displayName = entity.getEntityData().isKing()
				? entity.getEntityData().getName() + " [KING]"
				: entity.getEntityData().getName();
		heroNameLabel.setText(displayName);
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
