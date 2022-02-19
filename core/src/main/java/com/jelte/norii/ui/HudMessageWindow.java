package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jelte.norii.utility.AssetManagerUtility;

public class HudMessageWindow {
	public static final String TITLE = "Info";
	public static final String CLOSE_BUTTON_SKIN_NAME = "closebutton";

	private Window textWindow;
	private final ImageButtonStyle imageButtonStyle;
	private final ImageButton closeButton;
	private Label textLabel;

	public HudMessageWindow(String text) {
		textWindow = new Window(TITLE, AssetManagerUtility.getSkin());
		imageButtonStyle = AssetManagerUtility.getSkin().get(CLOSE_BUTTON_SKIN_NAME, ImageButtonStyle.class);
		closeButton = new ImageButton(imageButtonStyle);
		textLabel = new Label(text, AssetManagerUtility.getSkin());
		textWindow.add(textLabel);
		textWindow.getTitleTable().add(closeButton).size(20).padBottom(5);
		textWindow.pack();
		textWindow.setMovable(true);
		textWindow.setVisible(false);

		closeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				textWindow.setVisible(false);
			}
		});

		textWindow.setPosition((Hud.UI_VIEWPORT_WIDTH / HudMessages.POPUP_WIDTH_FACTOR), (Hud.UI_VIEWPORT_HEIGHT / HudMessages.POPUP_HEIGHT_FACTOR));
	}

	public Window getTextWindow() {
		return textWindow;
	}

	public void setTextWindow(Window textWindow) {
		this.textWindow = textWindow;
	}

	public Label getTextLabel() {
		return textLabel;
	}

	public void setTextLabel(Label textLabel) {
		this.textLabel = textLabel;
	}

	public void setPosition(float x, float y) {
		textWindow.setPosition(x, y);
	}

	public void setVisible(boolean visible) {
		textWindow.setVisible(visible);
	}

}
