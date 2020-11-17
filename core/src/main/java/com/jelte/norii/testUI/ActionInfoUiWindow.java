package com.jelte.norii.testUI;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.utility.AssetManagerUtility;

public class ActionInfoUiWindow extends Window {
	private Label actionInfoLabel;
	private Label actionnameLabel;
	private Label underline;
	private String actionName;
	private String actionInfo;

	private final int mapWidth;
	private final int mapHeight;

	private ActionUIButton linkedActionUIButton;

	private static final float WIDTH_TILES = 13;
	private static final float HEIGHT_TILES = 5;
	private static final float ACTION_NAME_LABEL_WIDTH = 50;

	public ActionInfoUiWindow(final ActionUIButton actionUIButton, int mapWidth, int mapHeight) {
		super("", AssetManagerUtility.getSkin());
		this.mapHeight = mapHeight;
		this.mapWidth = mapWidth;
		initVariables(actionUIButton);
		configureMainWindow();
		createWidgets();
		addWidgets();
		this.setSize((NewHud.UI_VIEWPORT_WIDTH / mapWidth) * WIDTH_TILES, (NewHud.UI_VIEWPORT_HEIGHT / mapHeight) * HEIGHT_TILES);
		this.pad(0);
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
		final BitmapFont bitmapFont = AssetManagerUtility.getFreeTypeFontAsset("15_fonts/sporty.ttf");
		final LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = bitmapFont;
		actionInfoLabel = new Label(actionInfo, labelStyle);
		actionInfoLabel.setWrap(true);
		actionnameLabel = new Label(actionName, labelStyle);
		underline = new Label("_____________", labelStyle);
	}

	private void addWidgets() {
		this.add(actionnameLabel).align(Align.left).expandX().width(ACTION_NAME_LABEL_WIDTH).height(16);
		row();

		this.add(underline).align(Align.left).expandX().width(ACTION_NAME_LABEL_WIDTH).height(16).top();
		row();

		this.add(actionInfoLabel).align(Align.left).expandX().colspan(5).height(16);
		this.pack();
	}

	public void update() {
		setVisible(linkedActionUIButton.getVisible());
	}
}
