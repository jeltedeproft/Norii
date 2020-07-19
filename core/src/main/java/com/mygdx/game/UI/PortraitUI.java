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
	private static final String PORTRAIT_BORDER_FILE_PATH = "sprites/gui/heroActive.png";

	private Boolean isActive;
	private final Entity linkedEntity;

	public PortraitUI(Entity entity) {
		this.linkedEntity = entity;
		loadScalableImage(entity);
	}

	private void loadScalableImage(Entity entity) {
		createPortraitTexture(entity);
		createPortraitBorderTexture();
	}

	private void createPortraitTexture(Entity entity) {
		Utility.loadTextureAsset(entity.getEntityData().getPortraitSpritePath());
		final TextureRegion tr = new TextureRegion(Utility.getTextureAsset(entity.getEntityData().getPortraitSpritePath()));
		heroPortraitScalable = new TextureRegionDrawable(tr);
		heroPortrait = new Image(heroPortraitScalable);
	}

	private void createPortraitBorderTexture() {
		Utility.loadTextureAsset(PORTRAIT_BORDER_FILE_PATH);
		final TextureRegion trBorder = new TextureRegion(Utility.getTextureAsset(PORTRAIT_BORDER_FILE_PATH));
		heroPortraitScalableBorder = new TextureRegionDrawable(trBorder);
		heroPortraitBorder = new Image(heroPortraitScalableBorder);
		heroPortraitBorder.setVisible(false);
	}

	public int getIni() {
		return linkedEntity.getEntityData().getBaseInitiative();
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
