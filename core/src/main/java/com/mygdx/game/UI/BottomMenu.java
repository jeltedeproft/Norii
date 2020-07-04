package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Entities.Entity;

import Utility.Utility;

public class BottomMenu extends Window {
	private static final String UNKNOWN_HERO_IMAGE_LOCATION = "sprites/gui/portraits/unknown.png";
	private static final String PORTRAIT_BORDER_IMAGE_LOCATION = "sprites/gui/portraits/goodhud.png";

	private Label heroNameLabel;

	private int heroHP;
	private int heroAP;
	private int heroINI;

	private Image heroImage;
	private Image heroImageBorder;
	private Entity linkedEntity;

	private float tileWidthPixel;
	private float tileHeightPixel;

	private static final int HUD_HEIGHT = 1;
	private static final int HUD_WIDTH = 3;
	private static final int HUD_BORDER_HEIGHT = 3;
	private static final int HUD_BORDER_WIDTH = 8;
	private static final int TILE_TO_PIXEL_RATIO = 25;
	private static final int HERO_PORTRAIT_X = 0;
	private static final int HERO_PORTRAIT_Y = 0;

	public BottomMenu(final Entity[] entities) {
		super("", Utility.getSkin());
		initVariables();
		linkUnitsToMenu(entities);
		initElementsForUI();
		addElementsToWindow();
	}

	private void initVariables() {
		tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_PIXEL_RATIO;
		tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_PIXEL_RATIO;
	}

	private void linkUnitsToMenu(final Entity[] entities) {
		for (final Entity entity : entities) {
			entity.setbottomMenu(this);
		}
	}

	private void initElementsForUI() {
		initWindow();
		initPortraitBorder();
		changeHeroImage(UNKNOWN_HERO_IMAGE_LOCATION);
		initHeroLabel();
	}

	private void initWindow() {
		this.pad(0);
		this.setPosition(0, Gdx.graphics.getHeight() - this.getHeight());
		setTransform(true);
		final WindowStyle styleTransparent = Utility.getSkin().get("transparent", WindowStyle.class);
		this.setStyle(styleTransparent);
	}

	private void initPortraitBorder() {
		Utility.loadTextureAsset(PORTRAIT_BORDER_IMAGE_LOCATION);
		final TextureRegion tr = new TextureRegion(Utility.getTextureAsset(PORTRAIT_BORDER_IMAGE_LOCATION));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		// trd.setMinHeight(HUD_BORDER_HEIGHT * tileHeightPixel);
		// trd.setMinWidth(HUD_BORDER_WIDTH * tileWidthPixel);
		heroImageBorder = new Image(trd);
		heroImageBorder.setPosition(0, 0);
	}

	private void changeHeroImage(final String heroImageLink) {
		Utility.loadTextureAsset(heroImageLink);
		final TextureRegion tr = new TextureRegion(Utility.getTextureAsset(heroImageLink));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		// trd.setMinHeight(HUD_HEIGHT * tileHeightPixel);
		// trd.setMinWidth(HUD_WIDTH * tileWidthPixel);
		if (heroImage != null) {
			heroImage.setDrawable(trd);
		} else {
			heroImage = new Image(trd);
		}
		heroImage.setPosition(0, 0);
		heroImage.setAlign(Align.left);
	}

	private void initHeroLabel() {
		final Skin statusUISkin = Utility.getSkin();
		heroNameLabel = new Label("", statusUISkin);
	}

	private void addElementsToWindow() {
		populateHeroImage();
	}

	private void populateHeroImage() {
		final Group group = new Group();
		group.addActor(heroImageBorder);
		group.addActor(heroImage);
		heroImageBorder.setPosition(60, 40);
		this.setPosition(-100, Gdx.graphics.getHeight() - this.getHeight() - 30);
		// add(heroImageBorder);
		add(group).expand().bottom().left();
		// addActor(heroImageBorder);
		// addActor(heroImage);
		setClip(false);
		setTransform(true);
		setMovable(true);
		this.padTop(100);
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
		heroINI = linkedEntity.getEntityData().getBaseInitiative();
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

	private void updateSize() {
		tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_PIXEL_RATIO;
		tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_PIXEL_RATIO;
		updateMainTable();
		updateHeroImage();
		updateContainers();
	}

	private void updateMainTable() {
		final float scaledWidth = Gdx.graphics.getWidth();
		final float scaledHeight = HUD_BORDER_HEIGHT * tileHeightPixel;
		setSize(scaledWidth, scaledHeight);
	}

	private void updateHeroImage() {
		heroImage.getDrawable().setMinHeight(HUD_HEIGHT * tileHeightPixel);
		heroImage.getDrawable().setMinWidth(HUD_WIDTH * tileWidthPixel);
		heroImageBorder.getDrawable().setMinHeight(HUD_BORDER_HEIGHT * tileHeightPixel);
		heroImageBorder.getDrawable().setMinWidth(HUD_BORDER_WIDTH * tileWidthPixel);
	}

	private void updateContainers() {
		setSize(HUD_BORDER_WIDTH * tileWidthPixel, HUD_BORDER_HEIGHT * tileHeightPixel);
	}
}
