package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.utility.AssetManagerUtility;

public class HpBar {

	private static final int BAR_HEIGHT = 20;
	private static final float MIN_VALUE = 0f;
	private static final float MAX_VALUE = 100f;
	private static final float STEP_SIZE = 1f;
	private static final float ANIMATION_DURATION = 0.25f;
	public static final int HEALTHBAR_Y_OFFSET = 50;
	public static final float HEIGHT_FACTOR = 5f;
	private ProgressBar healthBar;

	public void init(Entity entity) {
		final Skin statusUISkin = AssetManagerUtility.getSkin();

		ProgressBarStyle progressBarStyle = entity.isPlayerUnit() ? statusUISkin.get("blue-hp-bar", ProgressBarStyle.class) : statusUISkin.get("red-hp-bar", ProgressBarStyle.class);

		healthBar = new ProgressBar(MIN_VALUE, MAX_VALUE, STEP_SIZE, false, progressBarStyle);
		healthBar.setValue(MAX_VALUE);
		healthBar.setAnimateDuration(ANIMATION_DURATION);

		progressBarStyle.background.setMinHeight(BAR_HEIGHT);
		progressBarStyle.knobBefore.setMinHeight(BAR_HEIGHT);
		healthBar.setPosition(entity.getVisualComponent().getEntityactor().getX(), (entity.getVisualComponent().getEntityactor().getY() * HEALTHBAR_Y_OFFSET));
		healthBar.setWidth(entity.getVisualComponent().getEntityactor().getWidth());
		healthBar.getStyle().background.setMinWidth(entity.getVisualComponent().getEntityactor().getWidth());
		// progressBarStyle.knobBefore.setMinWidth(entity.getVisualComponent().getEntityactor().getWidth());
	}

	public ProgressBar getHealthBar() {
		return healthBar;
	}

	public void update(Entity entity) {
		healthBar.setPosition(entity.getVisualComponent().getEntityactor().getX(), (entity.getVisualComponent().getEntityactor().getY() * HEALTHBAR_Y_OFFSET));
		getHealthBar().setValue(((float) entity.getHp() / (float) entity.getEntityData().getMaxHP()) * 100.0f);
		if (getHealthBar().getValue() == 0) {
			getHealthBar().setVisible(false);
		}
	}
}
