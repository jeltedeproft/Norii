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
	protected ActionInfoUIWindow popUpWindow;
	protected boolean isHovering;
	protected boolean entered = false;
	protected boolean exited = false;
	protected String infotext;
	protected String actionName = "action";

	private boolean hovering = false;

	public ActionUIButton(String imageFileName) {
		Utility.loadTextureAsset(imageFileName);
		tr = new TextureRegion(Utility.getTextureAsset(imageFileName));
		buttonImage = new TextureRegionDrawable(tr);
		btnStyle = new ImageButtonStyle();
		btnStyle.up = buttonImage;
		button = new ImageButton(btnStyle);
		button.addListener(new ActionUIButtonHoverListener(this));
	}

	protected void initPopUp() {
		popUpWindow = new ActionInfoUIWindow(this);
	}

	public ImageButton getButton() {
		return button;
	}

	public TextureRegionDrawable getButtonImage() {
		return buttonImage;
	}

	public String getInfoText() {
		return infotext;
	}

	public String getName() {
		return actionName;
	}

	public void setButton(ImageButton button) {
		this.button = button;
	}

	public void showPopUp() {
		hovering = true;
	}

	public void hidePopUp() {
		hovering = false;
	}

	public boolean getVisible() {
		return hovering;
	}

	public ActionInfoUIWindow getPopUp() {
		return popUpWindow;
	}

	public void setIsHovering(boolean state) {
		this.isHovering = state;
	}

	public boolean isHovering() {
		return isHovering;
	}

	public void setEntered(boolean entered) {
		this.entered = entered;
	}

	public void setExited(boolean exited) {
		this.exited = exited;
	}
}

enum Action {
	MOVE, ATTACK, SPELL, SKIP
}
