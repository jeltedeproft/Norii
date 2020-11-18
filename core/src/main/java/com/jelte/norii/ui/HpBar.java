package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.utility.AssetManagerUtility;

public class HpBar {

	private static final int OFFSET_BAR_Y = 12;
	private static final float MIN_VALUE = 0f;
	private static final float MAX_VALUE = 10f;
	private static final float STEP_SIZE = 1f;
	private static final float ANIMATION_DURATION = 0.25f;
	private final Entity entity;
	private final ProgressBar healthBar;

	private final float tilePixelWidth;
	private final float tilePixelHeight;

	public HpBar(Entity entity, int mapWidth, int mapHeight) {
		this.entity = entity;
		tilePixelWidth = Hud.UI_VIEWPORT_WIDTH / mapWidth;
		tilePixelHeight = Hud.UI_VIEWPORT_HEIGHT / mapHeight;

		final Skin statusUISkin = AssetManagerUtility.getSkin();
		final ProgressBarStyle progressBarStyle = statusUISkin.get("blue-hp-bar", ProgressBarStyle.class);

		healthBar = new ProgressBar(MIN_VALUE, MAX_VALUE, STEP_SIZE, false, progressBarStyle);
		healthBar.setValue(MAX_VALUE);
		healthBar.setAnimateDuration(ANIMATION_DURATION);
		healthBar.setPosition(entity.getCurrentPosition().getTileX() * tilePixelWidth, ((entity.getCurrentPosition().getTileY() * tilePixelHeight) + OFFSET_BAR_Y));
		healthBar.setWidth(tilePixelWidth);

		progressBarStyle.background.setMinHeight(tilePixelHeight / 4f);
		progressBarStyle.knobBefore.setMinHeight(tilePixelHeight / 4f);
	}

	public Entity getEntity() {
		return entity;
	}

	public ProgressBar getHealthBar() {
		return healthBar;
	}

}
