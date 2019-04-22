package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.game.Entities.Entity;

import Utility.Utility;

public class PortraitUI {
	private static final String TAG = PortraitUI.class.getSimpleName();
	private static final String _portraitSpritePath = "sprites/gui/portraits/knight.png";

	private Image _heroPortrait;
	private Boolean isActive;
	private Entity linkedEntity;

	public PortraitUI(Entity entity){
		this.linkedEntity = entity;
		Utility.loadTextureAsset(_portraitSpritePath);
		_heroPortrait = new Image(Utility.getTextureAsset(_portraitSpritePath));
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
	
}
