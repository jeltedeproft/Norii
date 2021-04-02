package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.jelte.norii.utility.AssetManagerUtility;

public class HudMessagePopup {
	private Label popupMessage;

	public HudMessagePopup(String text, float tilePixelWidth, float tilePixelHeight, int mapWidth, int mapHeight) {
		popupMessage = new Label(text, AssetManagerUtility.getSkin());
		popupMessage.setPosition((mapWidth / HudMessages.POPUP_WIDTH_FACTOR) * tilePixelWidth, (mapHeight / HudMessages.POPUP_HEIGHT_FACTOR) * tilePixelHeight);
		popupMessage.setVisible(false);
	}

	public Label getPopupMessage() {
		return popupMessage;
	}

	public void setPopupMessage(Label popupMessage) {
		this.popupMessage = popupMessage;
	}

}
