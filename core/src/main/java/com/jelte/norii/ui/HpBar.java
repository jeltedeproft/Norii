package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.utility.AssetManagerUtility;

public class HpBar {

	private static final int OFFSET_BAR_Y = 12;
	private static final float MIN_VALUE = 0f;
	private static final float STEP_SIZE = 1f;
	private static final float ANIMATION_DURATION = 0.25f;
	public static final int HEALTHBAR_Y_OFFSET = 50;
	public static final float HEIGHT_FACTOR = 5f;
	private final ProgressBar healthBar;

	private final float tilePixelWidth;
	private final float tilePixelHeight;

	public HpBar(Entity entity, int mapWidth, int mapHeight) {
		tilePixelWidth = Hud.UI_VIEWPORT_WIDTH / mapWidth;
		tilePixelHeight = Hud.UI_VIEWPORT_HEIGHT / mapHeight;

		final Skin statusUISkin = AssetManagerUtility.getSkin();

		ProgressBarStyle progressBarStyle;
		if (entity.isPlayerUnit()) {
			progressBarStyle = statusUISkin.get("blue-hp-bar", ProgressBarStyle.class);
		} else {
			progressBarStyle = statusUISkin.get("red-hp-bar", ProgressBarStyle.class);
		}

		healthBar = new ProgressBar(MIN_VALUE, entity.getEntityData().getMaxHP(), STEP_SIZE, false, progressBarStyle);
		healthBar.setValue(entity.getEntityData().getMaxHP());
		healthBar.setAnimateDuration(ANIMATION_DURATION);
		healthBar.setPosition(entity.getVisualComponent().getEntityactor().getX() * tilePixelWidth, ((entity.getVisualComponent().getEntityactor().getY() * tilePixelHeight) + OFFSET_BAR_Y));
		healthBar.setWidth(tilePixelWidth);

		progressBarStyle.background.setMinHeight(tilePixelHeight / HEIGHT_FACTOR);
		progressBarStyle.knobBefore.setMinHeight(tilePixelHeight / HEIGHT_FACTOR);

		progressBarStyle.background.setMinWidth(tilePixelWidth);
		progressBarStyle.knobBefore.setMinWidth(tilePixelWidth);
	}

	public ProgressBar getHealthBar() {
		return healthBar;
	}

	public void update(Entity unit) {
		getHealthBar().setPosition(unit.getVisualComponent().getEntityactor().getX() * tilePixelWidth, ((unit.getVisualComponent().getEntityactor().getY() * tilePixelHeight) + HEALTHBAR_Y_OFFSET));
		getHealthBar().setValue(unit.getHp());
		if (getHealthBar().getValue() == 0) {
			getHealthBar().setVisible(false);
		}
	}

}
