package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Screen.BattleScreen;

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

	private float infoUIOffsetX;
	private float infoUIOffsetY;

	private static final float WIDTH_TILES = 12;
	private static final float HEIGHT_TILES = 4;
	private static final float LABEL_WIDTH = 100;
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
		infoUIOffsetX = Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH;
		infoUIOffsetY = Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT;
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
		final BitmapFont font = Utility.getFreeTypeFontAsset("fonts/twilight.ttf");
		labelStyle = new LabelStyle();
		labelStyle.font = font;
	}

	private void createLabels() {
		actionInfoLabel = new Label(actionInfo, labelStyle);
		actionnameLabel = new Label(actionName, labelStyle);
		actionIcon = new Image(actionIconDrawable);
	}

	@Override
	protected void addWidgets() {
		this.add(actionIcon).align(Align.left).width(LABEL_WIDTH);
		this.add(actionnameLabel).align(Align.left).expandX().width(ACTION_NAME_LABEL_WIDTH);
		row();

		this.add(actionInfoLabel).align(Align.left).expandX().colspan(5);
		this.pack();
	}

	@Override
	public void update() {
		super.update();
		infoUIOffsetX = Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH;
		infoUIOffsetY = Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT;

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
