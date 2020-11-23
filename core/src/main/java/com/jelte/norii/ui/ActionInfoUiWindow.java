package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.utility.AssetManagerUtility;

public class ActionInfoUiWindow extends Window {
	private static final float WIDTH_TILES = 13;
	private static final float HEIGHT_TILES = 5;
	private static final float ACTION_NAME_LABEL_WIDTH = 50;
	private static final int PADDING = 0;

	private final float tilePixelWidth;
	private final float tilePixelHeight;

	private ActionUIButton linkedActionUIButton;

	public ActionInfoUiWindow(final ActionUIButton actionUIButton, int mapWidth, int mapHeight) {
		super("", AssetManagerUtility.getSkin());

		tilePixelWidth = Hud.UI_VIEWPORT_WIDTH / mapWidth;
		tilePixelHeight = Hud.UI_VIEWPORT_HEIGHT / mapHeight;

		initVariables(actionUIButton);
		configureMainWindow();
		addWidgets();

		this.setSize(tilePixelWidth * WIDTH_TILES, tilePixelHeight * HEIGHT_TILES);
		this.pad(PADDING);
		this.padTop(15);
		this.padLeft(10);
	}

	private void configureMainWindow() {
		setVisible(false);
		setResizable(true);
	}

	private void initVariables(final ActionUIButton actionUIButton) {
		linkedActionUIButton = actionUIButton;
		this.getTitleLabel().setText(linkedActionUIButton.getName());
	}

	private void addWidgets() {
		for (final Label label : linkedActionUIButton.getLabels()) {
			this.add(label).align(Align.left).expandX().width(ACTION_NAME_LABEL_WIDTH).top();
		}
		this.pack();
	}

	public void update() {
		setVisible(linkedActionUIButton.getVisible());
	}
}
