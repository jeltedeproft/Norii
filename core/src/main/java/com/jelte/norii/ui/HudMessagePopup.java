package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.jelte.norii.utility.AssetManagerUtility;

public class HudMessagePopup {
	private Label popupMessage;

	public HudMessagePopup(String text, HudMessages hudMessages) {
		popupMessage = new Label(text, AssetManagerUtility.getSkin());
		popupMessage.setPosition((hudMessages.getMapWidth() / HudMessages.POPUP_WIDTH_FACTOR) * hudMessages.getTilePixelWidth(), (hudMessages.getMapHeight() / HudMessages.POPUP_HEIGHT_FACTOR) * hudMessages.getTilePixelHeight());
		popupMessage.setVisible(false);
	}

	public Label getPopupMessage() {
		return popupMessage;
	}

	public void setPopupMessage(Label popupMessage) {
		this.popupMessage = popupMessage;
	}

}
