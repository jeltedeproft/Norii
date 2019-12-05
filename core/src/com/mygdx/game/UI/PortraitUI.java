package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Entities.Entity;

import Utility.Utility;

public class PortraitUI {
	private static final String TAG = PortraitUI.class.getSimpleName();

	private Image heroPortrait;
	private Image heroPortraitBorder;
	private TextureRegionDrawable heroPortraitScalable;
	private TextureRegionDrawable heroPortraitScalableBorder;
	private static final String PORTRAIT_BORDER_FILE_PATH = "sprites/gui/heroActive.png";

	private Boolean isActive;
	private Entity linkedEntity;

	public PortraitUI(Entity entity){
		this.linkedEntity = entity;
		loadScalableImage(entity);
	}

	private void loadScalableImage(Entity entity) {
		Utility.loadTextureAsset(entity.getPortraitPath());
		TextureRegion tr = new TextureRegion(Utility.getTextureAsset(entity.getPortraitPath()));
		heroPortraitScalable = new TextureRegionDrawable(tr);
		heroPortrait = new Image(heroPortraitScalable);
		
		Utility.loadTextureAsset(PORTRAIT_BORDER_FILE_PATH);
		TextureRegion trBorder = new TextureRegion(Utility.getTextureAsset(PORTRAIT_BORDER_FILE_PATH));
		heroPortraitScalableBorder = new TextureRegionDrawable(trBorder);
		heroPortraitBorder = new Image(heroPortraitScalableBorder);
		heroPortraitBorder.setVisible(false);
	}

	public int getIni() {
		return linkedEntity.getIni();
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
}
