package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import Utility.Utility;

public class ActionUIButton {

	protected ImageButton button;
	protected boolean active;
	protected TextureRegion tr;
	protected TextureRegionDrawable buttonImage;
	protected ImageButtonStyle btnStyle;

	public ActionUIButton(String imageFileName) {
		Utility.loadTextureAsset(imageFileName);
		tr = new TextureRegion(Utility.getTextureAsset(imageFileName));
		buttonImage = new TextureRegionDrawable(tr);
		btnStyle = new ImageButtonStyle();
		btnStyle.up = buttonImage;
		button = new ImageButton(btnStyle);
	}

	public ImageButton getButton() {
		return button;
	}

	public void setButton(ImageButton button) {
		this.button = button;
	}
}

enum Action {
	MOVE, ATTACK, SPELL, SKIP
}
