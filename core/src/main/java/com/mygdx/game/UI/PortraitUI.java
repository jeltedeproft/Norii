package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Entities.Entity;

import Utility.Utility;

public class PortraitUI {
	private Image heroPortrait;
	private Image heroPortraitBorder;
	private Image heroPortraitBorderColored;
	private TextureRegionDrawable heroPortraitScalable;
	private TextureRegionDrawable heroPortraitScalableBorder;
	private TextureRegionDrawable heroPortraitScalableBorderColored;
	private TextureRegion trBorder;
	private TextureRegion trBorderColored;
	private static final String PORTRAIT_BORDER_FILE_PATH = "sprites/gui/heroActive.png";
	private static final String PORTRAIT_BORDER_FILE_PATH_GREEN = "sprites/gui/heroActiveGreen.png";
	private static final String PORTRAIT_BORDER_FILE_PATH_RED = "sprites/gui/heroActiveRed.png";

	private Boolean isActive;
	private final Entity linkedEntity;

	public PortraitUI(Entity entity) {
		this.linkedEntity = entity;
		loadScalableImage(entity);
	}

	private void loadScalableImage(Entity entity) {
		Utility.loadTextureAsset(entity.getPortraitPath());
		final TextureRegion tr = new TextureRegion(Utility.getTextureAsset(entity.getPortraitPath()));
		heroPortraitScalable = new TextureRegionDrawable(tr);
		heroPortrait = new Image(heroPortraitScalable);

		Utility.loadTextureAsset(PORTRAIT_BORDER_FILE_PATH);
		trBorder = new TextureRegion(Utility.getTextureAsset(PORTRAIT_BORDER_FILE_PATH));
		heroPortraitScalableBorder = new TextureRegionDrawable(trBorder);
		heroPortraitBorder = new Image(heroPortraitScalableBorder);
		heroPortraitBorder.setVisible(false);

		if (entity.isPlayerUnit()) {
			Utility.loadTextureAsset(PORTRAIT_BORDER_FILE_PATH_GREEN);
			trBorderColored = new TextureRegion(Utility.getTextureAsset(PORTRAIT_BORDER_FILE_PATH_GREEN));
		} else {
			Utility.loadTextureAsset(PORTRAIT_BORDER_FILE_PATH_RED);
			trBorderColored = new TextureRegion(Utility.getTextureAsset(PORTRAIT_BORDER_FILE_PATH_RED));
		}

		heroPortraitScalableBorderColored = new TextureRegionDrawable(trBorderColored);
		heroPortraitBorderColored = new Image(heroPortraitScalableBorderColored);
		heroPortraitBorderColored.setVisible(true);
	}

	public int getIni() {
		return linkedEntity.getBaseInitiative();
	}

	public Image getHeroPortrait() {
		return heroPortrait;
	}

	public void setHeroPortrait(Image heroPortrait) {
		this.heroPortrait = heroPortrait;
	}

	public Image getHeroPortraitBorder() {
		return heroPortraitBorder;
	}

	public void setHeroPortraitBorder(Image heroPortraitBorder) {
		this.heroPortraitBorder = heroPortraitBorder;
	}

	public Image getHeroPortraitBorderColored() {
		return heroPortraitBorderColored;
	}

	public void setHeroPortraitBorderColored(Image heroPortraitBorder) {
		this.heroPortraitBorderColored = heroPortraitBorder;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Entity getLinkedEntity() {
		return linkedEntity;
	}

	public TextureRegionDrawable getHeroPortraitScalable() {
		return heroPortraitScalable;
	}

	public TextureRegionDrawable getHeroPortraitScalableBorder() {
		return heroPortraitScalableBorder;
	}

	public TextureRegionDrawable getHeroPortraitScalableBorderColored() {
		return heroPortraitScalableBorderColored;
	}
}
