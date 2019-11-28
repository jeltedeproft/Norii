package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import Utility.Utility;

public class ActionUIButton {
	
	protected Button button;
	protected boolean active;

	public ActionUIButton(String imageFileName) {
		Utility.loadTextureAsset(imageFileName);
		Drawable buttonImage = new TextureRegionDrawable(new TextureRegion(Utility.getTextureAsset(imageFileName)));
		button = new Button(buttonImage);
		button.getBackground().setMinHeight(32);
		button.getBackground().setMinWidth(32);
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
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
