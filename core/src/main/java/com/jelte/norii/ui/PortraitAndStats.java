package com.jelte.norii.ui;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.utility.AssetManagerUtility;

public class PortraitAndStats {
	private static final String UNKNOWN_HERO_IMAGE = "nochar";
	private static final String FONT_FILENAME = "15_fonts/sporty.ttf";
	private static final int NUMBER_OF_STATS_SHOWN = 3;
	private static final int PORTRAIT_WIDTH_PADDING = 5;
	private static final int PORTRAIT_HEIGHT_PADDING = 5;
	private static final int WINDOW_PADDING = 0;
	private static final int HERO_NAME_LABEL_HEIGHT = 5;
	private static final int HERO_NAME_LABEL_WIDTH = 75;
	private static final int STATS_WIDTH = 20;

	private int heroHP;
	private int heroAP;
	private final float tilePixelWidth;
	private final float tilePixelHeight;

	private ImageButton heroImageButton;
	private Table table;
	private Entity linkedEntity;

	private Label heroNameLabel;
	private Label hpLabel;
	private Label hp;
	private Label apLabel;
	private Label ap;

	public PortraitAndStats(final List<Entity> allUnits, int mapWidth, int mapHeight) {
		tilePixelWidth = Hud.UI_VIEWPORT_WIDTH / mapWidth;
		tilePixelHeight = Hud.UI_VIEWPORT_HEIGHT / mapHeight;

		linkUnitsToMenu(allUnits);
		initElementsForUI();
		populateHeroImage();
	}

	private void linkUnitsToMenu(final List<Entity> allUnits) {
		allUnits.forEach(entity -> entity.setbottomMenu(this));
	}

	private void initElementsForUI() {
		initPortrait();
		initStatsMenu();
	}

	private void initPortrait() {
		final ImageButtonStyle imageButtonStyle = AssetManagerUtility.getSkin().get("Portrait", ImageButtonStyle.class);
		imageButtonStyle.imageUp.setMinHeight(tilePixelHeight * NUMBER_OF_STATS_SHOWN);
		imageButtonStyle.imageUp.setMinWidth(tilePixelWidth * NUMBER_OF_STATS_SHOWN);
		heroImageButton = new ImageButton(imageButtonStyle);
	}

	private void initStatsMenu() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();
		heroNameLabel = new Label("", statusUISkin);
		hpLabel = new Label(" hp:", statusUISkin);
		hp = new Label("", statusUISkin);
		apLabel = new Label(" ap:", statusUISkin);
		ap = new Label("", statusUISkin);
	}

	private void changeHeroImage(final String heroImageName) {
		final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(heroImageName));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		final ImageButtonStyle oldStyle = heroImageButton.getStyle();
		oldStyle.imageUp = trd;
		oldStyle.imageUp.setMinHeight(tilePixelHeight * NUMBER_OF_STATS_SHOWN);
		oldStyle.imageUp.setMinWidth(tilePixelWidth * NUMBER_OF_STATS_SHOWN);
		heroImageButton.setStyle(oldStyle);
	}

	private void changeHeroImage() {
		changeHeroImage(UNKNOWN_HERO_IMAGE);
	}

	private void populateHeroImage() {
		table = new Table();
		table.setPosition(tilePixelWidth, Hud.UI_VIEWPORT_HEIGHT - (tilePixelHeight * NUMBER_OF_STATS_SHOWN) - tilePixelHeight);
		table.add(heroImageButton).width((tilePixelWidth * NUMBER_OF_STATS_SHOWN) + PORTRAIT_WIDTH_PADDING).height((tilePixelHeight * NUMBER_OF_STATS_SHOWN) + PORTRAIT_HEIGHT_PADDING);
		final Table subtable = new Table();
		subtable.pad(WINDOW_PADDING);
		subtable.add(heroNameLabel).height(HERO_NAME_LABEL_HEIGHT).align(Align.topLeft).width(HERO_NAME_LABEL_WIDTH);
		subtable.row();

		subtable.add(hpLabel).align(Align.left).expandX().width(STATS_WIDTH);
		subtable.add(hp).align(Align.left).expandX();
		subtable.row();

		subtable.add(apLabel).align(Align.left).expandX().width(STATS_WIDTH);
		subtable.add(ap).align(Align.left).expandX();
		subtable.setBackground(AssetManagerUtility.getSkin().getDrawable("window-noborder"));
		table.add(subtable).height((tilePixelHeight * NUMBER_OF_STATS_SHOWN) + PORTRAIT_HEIGHT_PADDING);

		table.validate();

		table.invalidateHierarchy();
		table.pack();

	}

	public void setHero(final Entity entity) {
		if (entity != null) {
			if (!entity.getEntityData().getName().equalsIgnoreCase(heroNameLabel.getText().toString())) {
				linkedEntity = entity;
				initiateHeroStats();
				populateElementsForUI(entity);
			}
		} else {
			resetStats();
		}
	}

	private void initiateHeroStats() {
		heroHP = linkedEntity.getHp();
		heroAP = linkedEntity.getAp();
	}

	private void populateElementsForUI(final Entity entity) {
		heroNameLabel.setText(entity.getEntityData().getName());
		changeHeroImage(entity.getEntityData().getPortraitSpritePath());
	}

	private void resetStats() {
		heroNameLabel.setText("");
		changeHeroImage();
	}

	public void update() {
		updateStats();
		updateLabels();
	}

	private void updateStats() {
		if (linkedEntity != null) {
			heroHP = linkedEntity.getHp();
			heroAP = linkedEntity.getAp();

			if (Boolean.TRUE.equals(linkedEntity.getEntityactor().getIsHovering())) {
				table.setVisible(true);
			}
		}
	}

	private void updateLabels() {
		hp.setText(String.valueOf(heroHP));
		ap.setText(String.valueOf(heroAP));

		if (heroAP == 0) {
			ap.setColor(Color.RED);
		} else {
			ap.setColor(Color.WHITE);
		}
	}

	public Table getTable() {
		return table;
	}

}
