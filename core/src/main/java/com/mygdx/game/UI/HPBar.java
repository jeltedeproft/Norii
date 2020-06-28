package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Screen.BattleScreen;

import Utility.Utility;

public class HPBar {
	private Image hpBarBackgroundImage;
	private Image hpBarImage;
	private WidgetGroup group;

	private final Entity linkedEntity;

	private float tileWidthPixel;
	private float tileHeightPixel;
	private float offsetY;

	private static final float BAR_WIDTH = 1.0f;
	private static final float BAR_HEIGHT = 0.3f;
	private static final int TILE_TO_SCREEN_RATIO = 40
			;

	public HPBar(final Entity entity) {
		linkedEntity = entity;
		initVariables();
	}

	private void initVariables() {
		group = new WidgetGroup();
		tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_SCREEN_RATIO;
		tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_SCREEN_RATIO;
		createDynamicHpBar();
		addImagesToGroup();
	}

	private void createDynamicHpBar() {
		final TextureAtlas skinAtlas = Utility.getUITextureAtlas();
		final NinePatch hpBarBackgroundPatch = new NinePatch(skinAtlas.findRegion("default-round"), 5, 5, 4, 4);
		final NinePatch hpBarPatch = new NinePatch(skinAtlas.findRegion("default-round-down"), 5, 5, 4, 4);
		hpBarImage = new Image(hpBarPatch);
		hpBarBackgroundImage = new Image(hpBarBackgroundPatch);
	}

	private void addImagesToGroup() {
		group.addActor(hpBarBackgroundImage);
		group.addActor(hpBarImage);
		group.debug();
	}

	public void update() {
		tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_SCREEN_RATIO;
		tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_SCREEN_RATIO;
		offsetY = Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT;
		updateSizeHPBar();
		updatePos();
	}

	private void updateSizeHPBar() {
		hpBarImage.setVisible(linkedEntity.getHp() != 0);
		hpBarImage.setWidth(((float) linkedEntity.getHp() / (float) linkedEntity.getEntityData().getMaxHP()) * BAR_WIDTH * tileWidthPixel);
		hpBarBackgroundImage.setWidth(BAR_WIDTH * tileWidthPixel);

		hpBarImage.setHeight(BAR_HEIGHT * tileHeightPixel);
		hpBarBackgroundImage.setHeight(BAR_HEIGHT * tileHeightPixel);
	}

	private void updatePos() {
		hpBarImage.setPosition((linkedEntity.getCurrentPosition().getCameraX()), (linkedEntity.getCurrentPosition().getCameraY()) + offsetY);
		hpBarBackgroundImage.setPosition((linkedEntity.getCurrentPosition().getCameraX()), (linkedEntity.getCurrentPosition().getCameraY()) + offsetY);
	}

	public WidgetGroup getHpBarGroup() {
		return group;
	}

}
