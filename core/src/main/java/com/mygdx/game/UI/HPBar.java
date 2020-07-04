package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Screen.BattleScreen;

import Utility.Utility;

public class HPBar {
	private ProgressBar healthBar;

	private final Entity linkedEntity;

	private float tileWidthPixel;
	private float tileHeightPixel;
	private float offsetY;
	private ProgressBarStyle progressBarStyle;

	private static final float BAR_WIDTH = 1.0f;
	private static final float BAR_HEIGHT = 0.3f;
	private static final int TILE_TO_SCREEN_RATIO = 40;

	public HPBar(final Entity entity) {
		linkedEntity = entity;
		initVariables();
	}

	private void initVariables() {
		tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_SCREEN_RATIO;
		tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_SCREEN_RATIO;
		createDynamicHpBar();
	}

	private void createDynamicHpBar() {
		final Skin statusUISkin = Utility.getSkin();
		progressBarStyle = statusUISkin.get("default-horizontal", ProgressBarStyle.class);
		healthBar = new ProgressBar(0.0f, 10.0f, 1.0f, false, progressBarStyle);
		healthBar.setValue(10.0f);
		healthBar.setAnimateDuration(0.25f);
		healthBar.setBounds(10, 10, 100, 20);
	}

	public void update() {
		tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_SCREEN_RATIO;
		tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_SCREEN_RATIO;
		offsetY = Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT;
		updateSizeHPBar();
		updatePos();
	}

	private void updateSizeHPBar() {
		healthBar.setVisible(linkedEntity.getHp() != 0);
		healthBar.setWidth(((float) linkedEntity.getHp() / (float) linkedEntity.getEntityData().getMaxHP()) * BAR_WIDTH * tileWidthPixel);
		healthBar.setHeight(BAR_HEIGHT * tileHeightPixel);
	}

	private void updatePos() {
		healthBar.setPosition((linkedEntity.getCurrentPosition().getCameraX()), (linkedEntity.getCurrentPosition().getCameraY()) + offsetY);
	}

	public ProgressBar getHpBarWidget() {
		return healthBar;
	}

}
