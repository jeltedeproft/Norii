package com.mygdx.game.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

import utility.AssetManagerUtility;

public class ActionInfoUIWindow extends UIWindow {
	private Label actionInfoLabel;
	private Label actionnameLabel;
	private Label underline;
	private String actionName;
	private String actionInfo;

	private ActionUIButton linkedActionUIButton;

	private static final float WIDTH_TILES = 6;
	private static final float HEIGHT_TILES = 3;
	private static final float ACTION_NAME_LABEL_WIDTH = 150;

	public ActionInfoUIWindow(final ActionUIButton actionUIButton) {
		super("", WIDTH_TILES, HEIGHT_TILES);
		initVariables(actionUIButton);
		configureMainWindow();
		createWidgets();
		addWidgets();
	}

	@Override
	protected void configureMainWindow() {
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

	@Override
	protected void createWidgets() {
		final BitmapFont bitmapFont = AssetManagerUtility.getFreeTypeFontAsset("15_fonts/sporty.ttf");
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = bitmapFont;
		actionInfoLabel = new Label(actionInfo, labelStyle);
		actionInfoLabel.setWrap(true);
		actionnameLabel = new Label(actionName, labelStyle);
		underline = new Label("_____________", labelStyle);
	}

	@Override
	protected void addWidgets() {
		this.add(actionnameLabel).align(Align.left).expandX().width(ACTION_NAME_LABEL_WIDTH).height(30);
		row();

		this.add(underline).align(Align.left).expandX().width(ACTION_NAME_LABEL_WIDTH).height(25).top();
		row();

		this.add(actionInfoLabel).align(Align.left).expandX().colspan(5).height(75);
		this.pack();
	}

	@Override
	public void update() {
		super.update();
		updateSizeElements();
		setVisible(linkedActionUIButton.getVisible());
	}

	private void updateSizeElements() {
		setSize(WIDTH_TILES * tileWidthPixel, HEIGHT_TILES * tileHeightPixel);
		invalidate();
	}

	@Override
	protected void updatePos() {
		// no-op
	}
}
