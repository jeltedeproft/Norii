package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Entities.Entity;

import Utility.Utility;

public class PortraitUI {
	private static final String TAG = PortraitUI.class.getSimpleName();

	private Image _heroPortrait;
	private TextureRegionDrawable _heroPortraitScalable;

	private Boolean isActive;
	private Entity linkedEntity;

	public PortraitUI(Entity entity){
		this.linkedEntity = entity;
		Utility.loadTextureAsset(entity.getPortraitPath());
		TextureRegion tr = new TextureRegion(Utility.getTextureAsset(entity.getPortraitPath()));
		_heroPortraitScalable = new TextureRegionDrawable(tr);
		_heroPortrait = new Image(_heroPortraitScalable);

	}

	public int getIni() {
		return linkedEntity.getIni();
	}

	public Image get_heroPortrait() {
		return _heroPortrait;
	}

	public void set_heroPortrait(Image _heroPortrait) {
		this._heroPortrait = _heroPortrait;
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
	
	public TextureRegionDrawable get_heroPortraitScalable() {
		return _heroPortraitScalable;
	}
	
}
