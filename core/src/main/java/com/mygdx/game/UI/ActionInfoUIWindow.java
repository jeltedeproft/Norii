package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import Utility.Utility;

public class ActionInfoUIWindow extends UIWindow {
	private LabelStyle labelStyle;
	private Label actionInfoLabel;
	private Label actionnameLabel;
	private String actionName;
	private String actionInfo;
	private TextureRegionDrawable actionIconDrawable;
	private Image actionIcon;

	private ActionUIButton linkedActionUIButton;

	private static final float WIDTH_TILES = 6;
	private static final float HEIGHT_TILES = 3;
	private static final float ICON_WIDTH = 80;
	private static final float ICON_HEIGHT = 80;
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
		actionIconDrawable = linkedActionUIButton.getButtonImage();
		actionInfo = linkedActionUIButton.getInfoText();
		actionName = linkedActionUIButton.getName();
	}

	@Override
	protected void createWidgets() {
		createFont();
		createLabels();
	}

	private void createFont() {
		final BitmapFont font = Utility.getFreeTypeFontAsset("fonts/sporty.ttf");
		labelStyle = new LabelStyle();
		labelStyle.font = font;
	}

	private void createLabels() {
		final LabelStyle labelStyle = Utility.createLabelStyle("fonts/sporty.ttf", 15, 1, Color.LIGHT_GRAY, 1, 1);
		actionInfoLabel = new Label(actionInfo, labelStyle);
		actionInfoLabel.setWrap(true);
		actionnameLabel = new Label(actionName, labelStyle);
		actionIcon = new Image(actionIconDrawable);
	}

	@Override
	protected void addWidgets() {
		this.add(actionIcon).align(Align.left).width(ICON_WIDTH).height(ICON_HEIGHT);
		this.add(actionnameLabel).align(Align.left).expandX().width(ACTION_NAME_LABEL_WIDTH);
		row();

		this.add(actionInfoLabel).align(Align.left).expandX().colspan(5);
		this.pack();
	}

	@Override
	public void update() {
		super.update();
		updateSizeElements();

		if (Boolean.TRUE.equals(linkedActionUIButton.getVisible())) {
			setVisible(true);
		} else {
			setVisible(false);
		}
	}

	private void updateSizeElements() {
		setSize(WIDTH_TILES * tileWidthPixel, HEIGHT_TILES * tileHeightPixel);
		invalidate();
	}

	@Override
	protected void updatePos() {

	}
}
