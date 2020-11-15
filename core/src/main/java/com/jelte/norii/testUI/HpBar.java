package com.jelte.norii.testUI;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.utility.AssetManagerUtility;

public class HpBar {
	private final Entity entity;
	private final ProgressBar healthBar;

	public HpBar(Entity entity, int mapWidth, int mapHeight) {
		this.entity = entity;
		final Skin statusUISkin = AssetManagerUtility.getSkin();
		final ProgressBarStyle progressBarStyle = statusUISkin.get("blue-hp-bar", ProgressBarStyle.class);
		healthBar = new ProgressBar(0.0f, 10.0f, 1.0f, false, progressBarStyle);
		healthBar.setValue(10.0f);
		healthBar.setAnimateDuration(0.25f);
		healthBar.setPosition(entity.getCurrentPosition().getTileX() * (NewHud.UI_VIEWPORT_WIDTH / mapWidth), ((entity.getCurrentPosition().getTileY() * (NewHud.UI_VIEWPORT_HEIGHT / mapHeight)) + 1));
		healthBar.setWidth(NewHud.UI_VIEWPORT_WIDTH / mapWidth);
		progressBarStyle.background.setMinHeight(1);
		progressBarStyle.knobBefore.setMinHeight(1);
	}

	public Entity getEntity() {
		return entity;
	}

	public ProgressBar getHealthBar() {
		return healthBar;
	}

}
