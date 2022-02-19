package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.jelte.norii.utility.AssetManagerUtility;

public class HudMessagePopup {
	private Label popupMessage;

	public HudMessagePopup(String text) {
		popupMessage = new Label(text, AssetManagerUtility.getSkin());
		popupMessage.setPosition((Hud.UI_VIEWPORT_WIDTH / HudMessages.POPUP_WIDTH_FACTOR), (Hud.UI_VIEWPORT_HEIGHT / HudMessages.POPUP_HEIGHT_FACTOR));
		popupMessage.setVisible(false);
	}

	public Label getPopupMessage() {
		return popupMessage;
	}

	public void setPopupMessage(Label popupMessage) {
		this.popupMessage = popupMessage;
	}

}
