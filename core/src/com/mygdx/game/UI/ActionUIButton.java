package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Screen.BattleScreen;

import Utility.Utility;

public class ActionUIButton {
	
	protected Button button;
	protected boolean active;

	public ActionUIButton(String imageFileName) {
		Utility.loadTextureAsset(imageFileName);
		Drawable buttonImage = new TextureRegionDrawable(new TextureRegion(Utility.getTextureAsset(imageFileName)));
		button = new Button(buttonImage);
		button.getBackground().setMinHeight(Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH);
		button.getBackground().setMinWidth(Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT);
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}
	
	public void setImageSize() {
		button.getBackground().setMinHeight(Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH);
		button.getBackground().setMinWidth(Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT);
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
