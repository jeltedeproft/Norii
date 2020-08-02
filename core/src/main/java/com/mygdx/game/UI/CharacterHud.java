package com.mygdx.game.UI;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Entities.Entity;

import Utility.Utility;

public class CharacterHud extends Window {
	private static final String UNKNOWN_HERO_IMAGE_LOCATION = "sprites/gui/portraits/nochar.png";

	private int heroHP;
	private int heroAP;

	private ImageButton heroImageButton;
	private HorizontalGroup horizontalGroup;
	private Window statsGroup;

	private Entity linkedEntity;

	private float tileWidthPixel;
	private float tileHeightPixel;

	private Label heroNameLabel;
	private Label hpLabel;
	private Label hp;
	private Label apLabel;
	private Label ap;
	private LabelStyle labelStyle;

	private static final int STATS_MENU_ELEMENT_PADDING = 20;
	private static final int HP_LABEL_WIDTH = 50;
	private static final int HUD_BORDER_HEIGHT = 3;
	private static final int HUD_BORDER_WIDTH = 8;
	private static final int TILE_TO_PIXEL_RATIO = 25;

	public CharacterHud(final List<Entity> allUnits) {
		super("", Utility.getSkin());
		initVariables();
		linkUnitsToMenu(allUnits);
		initElementsForUI();
		populateHeroImage();
	}

	private void initVariables() {
		tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_PIXEL_RATIO;
		tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_PIXEL_RATIO;
	}

	private void linkUnitsToMenu(final List<Entity> allUnits) {
		allUnits.forEach((entity) -> entity.setbottomMenu(this));
	}

	private void initElementsForUI() {
		initWindow();
		createFont();
		initPortrait();
		initStatsMenu();
	}

	private void initWindow() {
		final WindowStyle styleTransparent = Utility.getSkin().get("transparent", WindowStyle.class);
		setStyle(styleTransparent);
		pad(0);
		setPosition(0, Gdx.graphics.getHeight());
		setTransform(true);
		setClip(false);
		setTransform(true);
		setMovable(true);
		padTop(30);
		padLeft(10);
	}

	private void initPortrait() {
		heroImageButton = new ImageButton(Utility.getSkin().get("Portrait", ImageButtonStyle.class));
		heroImageButton.setPosition(0, 0);
	}

	private void createFont() {
		final BitmapFont font = Utility.getFreeTypeFontAsset("fonts/sporty.ttf");
		labelStyle = new LabelStyle();
		labelStyle.font = font;
	}

	private void initStatsMenu() {
		final Skin statusUISkin = Utility.getSkin();
		statsGroup = new Window("", statusUISkin.get("default", WindowStyle.class));
		statsGroup.setResizable(true);
		statsGroup.top();

		heroNameLabel = new Label("", labelStyle);
		hpLabel = new Label(" hp:", labelStyle);
		hp = new Label("", labelStyle);
		apLabel = new Label(" ap:", labelStyle);
		ap = new Label("", labelStyle);
		addLabelsToStatsGroup();
	}

	private void addLabelsToStatsGroup() {
		statsGroup.add(heroNameLabel).align(Align.topLeft).colspan(3);
		statsGroup.row();

		statsGroup.add(hpLabel).align(Align.topLeft).expandX().width(HP_LABEL_WIDTH);
		statsGroup.add(hp).align(Align.topLeft).padRight(STATS_MENU_ELEMENT_PADDING).expandX();
		statsGroup.row();

		statsGroup.add(apLabel).align(Align.left).expandX();
		statsGroup.add(ap).align(Align.left).padRight(STATS_MENU_ELEMENT_PADDING).expandX();
	}

	private void changeHeroImage(final String heroImageLink) {
		Utility.loadTextureAsset(heroImageLink);
		final TextureRegion tr = new TextureRegion(Utility.getTextureAsset(heroImageLink));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		final ImageButtonStyle oldStyle = heroImageButton.getStyle();
		oldStyle.imageUp = trd;
		heroImageButton.setStyle(oldStyle);
	}

	private void populateHeroImage() {
		horizontalGroup = new HorizontalGroup();
		horizontalGroup.addActor(heroImageButton);
		horizontalGroup.padTop(20);
		horizontalGroup.fill();

		horizontalGroup.addActor(statsGroup);
		add(horizontalGroup).expand().left();
	}

	public void setHero(final Entity entity) {
		if (entity != null) {
			if (!entity.getEntityData().getName().equalsIgnoreCase(heroNameLabel.getText().toString())) {
				linkedEntity = entity;
				initiateHeroStats();
				populateElementsForUI(entity);
			}
		} else {
			resetStats();
		}
	}

	private void initiateHeroStats() {
		heroHP = linkedEntity.getHp();
		heroAP = linkedEntity.getAp();
	}

	private void populateElementsForUI(final Entity entity) {
		heroNameLabel.setText(entity.getEntityData().getName());
		changeHeroImage(entity.getEntityData().getPortraitSpritePath());
	}

	private void resetStats() {
		heroNameLabel.setText("");
		changeHeroImage(UNKNOWN_HERO_IMAGE_LOCATION);
	}

	public void update() {
		updateStats();
		updateLabels();
		updateSize();
	}

	private void updateStats() {
		if (linkedEntity != null) {
			heroHP = linkedEntity.getHp();
			heroAP = linkedEntity.getAp();

			if (Boolean.TRUE.equals(linkedEntity.getEntityactor().getIsHovering())) {
				setVisible(true);
			}
		}
	}

	private void updateLabels() {
		hp.setText(String.valueOf(heroHP));
		ap.setText(String.valueOf(heroAP));

		if (heroAP == 0) {
			ap.setColor(Color.RED);
		} else {
			ap.setColor(Color.WHITE);
		}
	}

	private void updateSize() {
		tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_PIXEL_RATIO;
		tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_PIXEL_RATIO;
		updateMainTable();
		updateContainers();
	}

	private void updateMainTable() {
		final float scaledWidth = Gdx.graphics.getWidth();
		final float scaledHeight = HUD_BORDER_HEIGHT * tileHeightPixel;
		setSize(scaledWidth, scaledHeight);
	}

	private void updateContainers() {
		setSize(HUD_BORDER_WIDTH * tileWidthPixel, HUD_BORDER_HEIGHT * tileHeightPixel);
	}
}
