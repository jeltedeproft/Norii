package com.jelte.norii.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.AssetManagerUtility;

public class BottomBar {
	private static final String UNKNOWN_HERO_IMAGE = "nochar";
	private static final String MOVE_BUTTON_SPRITE_NAME = "move";
	private static final String ATTACK_BUTTON_SPRITE_NAME = "attack";
	private static final String SKIP_BUTTON_SPRITE_NAME = "skip";
	private static final String ACTIONS = "Actions";
	private static final String INFO = "Information";
	private static final String STATS = "Stats";

	private static final int NUMBER_OF_STATS_SHOWN = 5;
	private static final int PORTRAIT_WIDTH_PADDING = 5;
	private static final int PORTRAIT_HEIGHT_PADDING = 5;
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
	private ImageButton moveImageButton;
	private ImageButton attackImageButton;
	private ImageButton skipImageButton;
	private SpellImageButton spellImageButton;

	private Label infoLabel;
	private Label statsLabel;
	private Label spellInfo;

	private Entity activeUnit;
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
		// initInfo();
	}

	private void initPortrait() {
		final ImageButtonStyle imageButtonStyle = AssetManagerUtility.getSkin().get("Portrait", ImageButtonStyle.class);
//		imageButtonStyle.imageUp.setMinHeight(tilePixelHeight * NUMBER_OF_STATS_SHOWN);
//		imageButtonStyle.imageUp.setMinWidth(tilePixelWidth * NUMBER_OF_STATS_SHOWN);
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
		actionsLabel = new Label(ACTIONS, AssetManagerUtility.getSkin());
		infoLabel = new Label(INFO, AssetManagerUtility.getSkin());
		statsLabel = new Label(STATS, AssetManagerUtility.getSkin());
		spellInfo = new Label("", AssetManagerUtility.getSkin());
		final ImageButtonStyle moveButtonStyle = new ImageButtonStyle();
		moveImageButton = new ImageButton(moveButtonStyle);
		final TextureRegionDrawable moveImage = new TextureRegionDrawable(new TextureRegion(AssetManagerUtility.getSprite(MOVE_BUTTON_SPRITE_NAME)));
		moveButtonStyle.up = moveImage;
		moveImageButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				hud.sendMessage(MessageToBattleScreen.CLICKED_ON_MOVE, activeUnit.getEntityID(), null);
			}
		});

		final ImageButtonStyle attackButtonStyle = new ImageButtonStyle();
		attackImageButton = new ImageButton(attackButtonStyle);
		final TextureRegionDrawable attackImage = new TextureRegionDrawable(new TextureRegion(AssetManagerUtility.getSprite(ATTACK_BUTTON_SPRITE_NAME)));
		attackButtonStyle.up = attackImage;
		attackImageButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				hud.sendMessage(MessageToBattleScreen.CLICKED_ON_ATTACK, activeUnit.getEntityID(), null);
			}
		});

		final ImageButtonStyle skipButtonStyle = new ImageButtonStyle();
		skipImageButton = new ImageButton(skipButtonStyle);
		final TextureRegionDrawable skipImage = new TextureRegionDrawable(new TextureRegion(AssetManagerUtility.getSprite(SKIP_BUTTON_SPRITE_NAME)));
		skipButtonStyle.up = skipImage;
		skipImageButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				hud.sendMessage(MessageToBattleScreen.CLICKED_ON_SKIP, activeUnit.getEntityID(), null);
			}
		});

		final ImageButtonStyle spellButtonStyle = new ImageButtonStyle();
		spellImageButton = new SpellImageButton(spellButtonStyle);
		final TextureRegionDrawable spellImage = new TextureRegionDrawable(new TextureRegion(AssetManagerUtility.getSprite(SKIP_BUTTON_SPRITE_NAME)));
		spellButtonStyle.up = spellImage;
		spellImageButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				hud.sendMessage(MessageToBattleScreen.CLICKED_ON_ABILITY, activeUnit.getEntityID(), spellImageButton.getAbility());
			}
		});
	}

	private void changeSpellImage(final String spellImageName) {
		final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(spellImageName));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		final ImageButtonStyle oldStyle = spellImageButton.getStyle();
		oldStyle.imageUp = trd;
		spellImageButton.setStyle(oldStyle);
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
		table = new Table();
		table.setBackground(AssetManagerUtility.getSkin().getDrawable("window-noborder"));
		table.setTransform(false);
		table.setPosition(0, 0);
		table.pad(0);
		table.add(heroImageButton).width(tilePixelWidth * 3).height((tilePixelHeight * 3)).pad(0).align(Align.bottomLeft);
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

		subtable.setBackground(AssetManagerUtility.getSkin().getDrawable("windowgray"));
		table.add(subtable).align(Align.topLeft).height(tilePixelHeight * 3).width(tilePixelWidth * 6);

		final Table actionsTable = new Table();
		actionsTable.add(actionsLabel).height(HERO_NAME_LABEL_HEIGHT).align(Align.topLeft).width(HERO_NAME_LABEL_WIDTH).padTop(7).padBottom(PAD_BOTTOM_TITLE);
		actionsTable.row();
		actionsTable.align(Align.topLeft);
		actionsTable.pad(WINDOW_PADDING);
		actionsTable.padLeft(5);
		actionsTable.add(moveImageButton).size(tilePixelWidth, tilePixelHeight).pad(ICON_PADDING);
		actionsTable.add(attackImageButton).size(tilePixelWidth, tilePixelHeight).pad(ICON_PADDING);
		actionsTable.row();
		actionsTable.add(skipImageButton).size(tilePixelWidth, tilePixelHeight).pad(ICON_PADDING);
		actionsTable.add(spellImageButton).size(tilePixelWidth, tilePixelHeight).pad(ICON_PADDING);

		actionsTable.setBackground(AssetManagerUtility.getSkin().getDrawable("windowgray"));
		table.add(actionsTable).align(Align.topLeft).height(tilePixelHeight * 3).width(tilePixelWidth * 6);

		final Table infoTable = new Table();
		infoTable.add(infoLabel).height(HERO_NAME_LABEL_HEIGHT).align(Align.topLeft).width(HERO_NAME_LABEL_WIDTH).padTop(7).padBottom(PAD_BOTTOM_TITLE);
		infoTable.row();
		infoTable.align(Align.topLeft);
		infoTable.pad(WINDOW_PADDING);
		infoTable.padLeft(5);
		infoTable.add(spellInfo).size(tilePixelWidth, tilePixelHeight);

		infoTable.setBackground(AssetManagerUtility.getSkin().getDrawable("windowgray"));
		table.add(infoTable).align(Align.topLeft).height(tilePixelHeight * 3).width(tilePixelWidth * 6);

		table.validate();
		table.invalidateHierarchy();
		table.pack();

	}

	public void setHero(final Entity entity) {
		if (entity != null) {
			if (!entity.getEntityData().getName().equalsIgnoreCase(heroNameLabel.getText().toString())) {
				spellImageButton.setEntity(entity);
				spellImageButton.setAbility((Ability) entity.getAbilities().toArray()[0]);
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
		for (final Ability ability : entity.getAbilities()) {
			changeSpellImage(ability.getSpellData().getIconSpriteName());
			changeSpellInfo(ability.getSpellData().getInfoText());
		}
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

}
