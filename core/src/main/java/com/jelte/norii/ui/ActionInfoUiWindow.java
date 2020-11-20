package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.utility.AssetManagerUtility;

public class ActionInfoUiWindow extends Window {
	private static final String FONT_FILENAME = "15_fonts/sporty.ttf";
	private static final float WIDTH_TILES = 13;
	private static final float HEIGHT_TILES = 5;
	private static final float ACTION_NAME_LABEL_WIDTH = 50;
	private static final int PADDING = 0;

	private Label actionInfoLabel;
	private Label actionnameLabel;
	private Label underline;
	private String actionName;
	private String actionInfo;

	private final float tilePixelWidth;
	private final float tilePixelHeight;

	private ActionUIButton linkedActionUIButton;

	public ActionInfoUiWindow(final ActionUIButton actionUIButton, int mapWidth, int mapHeight) {
		super("", AssetManagerUtility.getSkin());

		tilePixelWidth = Hud.UI_VIEWPORT_WIDTH / mapWidth;
		tilePixelHeight = Hud.UI_VIEWPORT_HEIGHT / mapHeight;

		initVariables(actionUIButton);
		configureMainWindow();
		createWidgets();
		addWidgets();

		this.setSize(tilePixelWidth * WIDTH_TILES, tilePixelHeight * HEIGHT_TILES);
		this.pad(PADDING);
	}

	private void configureMainWindow() {
		setVisible(false);
		setResizable(true);
	}

	private void initVariables(final ActionUIButton actionUIButton) {
		linkedActionUIButton = actionUIButton;
		getInfoFromButton();
	}

	private void getInfoFromButton() {
		actionInfo = linkedActionUIButton.getInfoText();
		actionName = linkedActionUIButton.getName();
	}

	private void createWidgets() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();
		actionInfoLabel = new Label(actionInfo, statusUISkin);
		actionInfoLabel.setWrap(true);
		actionnameLabel = new Label(actionName, statusUISkin);
		underline = new Label("_____________", statusUISkin);
	}

	private void addWidgets() {
		this.add(actionnameLabel).align(Align.left).expandX().width(ACTION_NAME_LABEL_WIDTH).height(tilePixelHeight);
		row();

		this.add(underline).align(Align.left).expandX().width(ACTION_NAME_LABEL_WIDTH).height(tilePixelHeight).top();
		row();

		this.add(actionInfoLabel).align(Align.left).expandX().colspan(5).height(tilePixelHeight);
		this.pack();
	}

	public void update() {
		setVisible(linkedActionUIButton.getVisible());
	}
}
