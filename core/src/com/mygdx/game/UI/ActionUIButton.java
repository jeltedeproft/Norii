package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import Utility.Utility;

public class ActionUIButton {
	
	protected ImageButton button;
	protected boolean active;

	public ActionUIButton(String imageFileName, Action action) {
		Utility.loadTextureAsset(imageFileName);
		Drawable buttonImage = new TextureRegionDrawable(new TextureRegion(Utility.getTextureAsset(imageFileName)));
		button = new ImageButton(buttonImage);
		button.getImageCell().size(32, 32);
		//put border around it?
	}

	public ImageButton getButton() {
		return button;
	}

	public void setButton(ImageButton button) {
		this.button = button;
	}

}

enum Action {
	   MOVE,
	   ATTACK,
	   SPELL,
	   SKIP,
	   UPGRADE,
	   SPELLBOOK
}
