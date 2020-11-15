package com.jelte.norii.testUI;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.utility.AssetManagerUtility;

public class PortraitAndStats {
	private static final String UNKNOWN_HERO_IMAGE = "nochar";

	private int heroHP;
	private int heroAP;
	private final int mapWidth;
	private final int mapHeight;

	private ImageButton heroImageButton;
	private HorizontalGroup horizontalGroup;
	private Window statsGroup;

	private Entity linkedEntity;

	private Label heroNameLabel;
	private Label hpLabel;
	private Label hp;
	private Label apLabel;
	private Label ap;
	private LabelStyle labelStyle;

	private static final int STATS_MENU_ELEMENT_PADDING = 1;
	private static final int HP_LABEL_WIDTH = 4;

	public PortraitAndStats(final List<Entity> allUnits, int mapWidth, int mapHeight) {
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		linkUnitsToMenu(allUnits);
		initElementsForUI();
		populateHeroImage();
	}

	private void linkUnitsToMenu(final List<Entity> allUnits) {
		allUnits.forEach(entity -> entity.setbottomMenu(this));
	}

	private void initElementsForUI() {
		createFont();
		initPortrait();
		initStatsMenu();
	}

	private void createFont() {
		final BitmapFont font = AssetManagerUtility.getFreeTypeFontAsset("08_fonts/sporty.ttf");
		labelStyle = new LabelStyle();
		labelStyle.font = font;
	}

	private void initPortrait() {
		heroImageButton = new ImageButton(AssetManagerUtility.getSkin().get("Portrait", ImageButtonStyle.class));
		heroImageButton.setFillParent(true);
		heroImageButton.debugAll();
		heroImageButton.align(Align.bottomLeft);
		// heroImageButton.setScale(0.5f);
		heroImageButton.pack();
	}

	private void initStatsMenu() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();
		statsGroup = new Window("", statusUISkin.get("default", WindowStyle.class));
		heroNameLabel = new Label("", labelStyle);
		hpLabel = new Label(" hp:", labelStyle);
		hp = new Label("", labelStyle);
		apLabel = new Label(" ap:", labelStyle);
		ap = new Label("", labelStyle);
		addLabelsToStatsGroup();
	}

	private void addLabelsToStatsGroup() {
		statsGroup.add(heroNameLabel).align(Align.topLeft).colspan(3);
		statsGroup.row();

		statsGroup.add(hpLabel).align(Align.topLeft).expandX().width(HP_LABEL_WIDTH);
		statsGroup.add(hp).align(Align.topLeft).padRight(STATS_MENU_ELEMENT_PADDING).expandX();
		statsGroup.row();

		statsGroup.add(apLabel).align(Align.left).expandX();
		statsGroup.add(ap).align(Align.left).padRight(STATS_MENU_ELEMENT_PADDING).expandX();
		statsGroup.pack();
	}

	private void changeHeroImage(final String heroImageName) {
		final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(heroImageName));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		final ImageButtonStyle oldStyle = heroImageButton.getStyle();
		oldStyle.imageUp = trd;
		oldStyle.imageUp.setMinHeight(8);
		oldStyle.imageUp.setMinWidth(8);
		;
		oldStyle.up.setMinHeight(8);
		oldStyle.up.setMinWidth(8);
		heroImageButton.setStyle(oldStyle);
		heroImageButton.getBackground().setMinHeight(8);
		heroImageButton.getBackground().setMinWidth(8);
		// heroImageButton.getImage().setFillParent(true);
		heroImageButton.getImage().setAlign(Align.bottomLeft);
	}

	private void changeHeroImage() {
		changeHeroImage(UNKNOWN_HERO_IMAGE);
	}

	private void populateHeroImage() {
		horizontalGroup = new HorizontalGroup();
		horizontalGroup.addActor(heroImageButton);
		horizontalGroup.fill();
		horizontalGroup.setSize(16, 16);
		horizontalGroup.setPosition(0, 100);
		horizontalGroup.layout();
		horizontalGroup.debugAll();

		horizontalGroup.addActor(statsGroup);
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
				horizontalGroup.setVisible(true);
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

	public HorizontalGroup getHorizontalGroup() {
		return horizontalGroup;
	}

}
