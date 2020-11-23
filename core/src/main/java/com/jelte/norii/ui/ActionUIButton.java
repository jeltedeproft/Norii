package com.jelte.norii.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.jelte.norii.utility.AssetManagerUtility;

public class ActionUIButton {

	protected ImageButton button;
	protected boolean active;
	protected TextureRegion tr;
	protected TextureRegionDrawable buttonImage;
	protected ImageButtonStyle btnStyle;
	protected ActionInfoUiWindow popUpWindow;
	protected boolean hovering;
	protected boolean entered = false;
	protected boolean exited = false;
	protected String actionName = "action";
	protected Array<Label> labels;

	private boolean visible = false;

	public ActionUIButton(String imageName) {
		tr = new TextureRegion(AssetManagerUtility.getSprite(imageName));
		buttonImage = new TextureRegionDrawable(tr);
		btnStyle = new ImageButtonStyle();
		btnStyle.up = buttonImage;
		button = new ImageButton(btnStyle);
		button.addListener(new ActionUIButtonHoverListener(this));
		labels = new Array<>();
	}

	protected void initPopUp(int mapWidth, int mapHeight) {
		popUpWindow = new ActionInfoUiWindow(this, mapWidth, mapHeight);
	}

	public ImageButton getButton() {
		return button;
	}

	public TextureRegionDrawable getButtonImage() {
		return buttonImage;
	}

	public String getName() {
		return actionName;
	}

	public void setButton(ImageButton button) {
		this.button = button;
	}

	public void showPopUp() {
		visible = true;
	}

	public void hidePopUp() {
		visible = false;
	}

	public boolean getVisible() {
		return visible;
	}

	public ActionInfoUiWindow getPopUp() {
		return popUpWindow;
	}

	public void setIsHovering(boolean state) {
		this.hovering = state;
	}

	public boolean isHovering() {
		return hovering;
	}

	public void setEntered(boolean entered) {
		this.entered = entered;
	}

	public void setExited(boolean exited) {
		this.exited = exited;
	}

	public Array<Label> getLabels() {
		return labels;
	}
}

enum Action {
	MOVE, ATTACK, SPELL, SKIP
}
