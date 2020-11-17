package com.jelte.norii.testUI;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
	private Table table;

	private Entity linkedEntity;

	private Label heroNameLabel;
	private Label hpLabel;
	private Label hp;
	private Label apLabel;
	private Label ap;
	private LabelStyle labelStyle;

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
		final BitmapFont font = AssetManagerUtility.getFreeTypeFontAsset("15_fonts/sporty.ttf");
		labelStyle = new LabelStyle();
		labelStyle.font = font;
	}

	private void initPortrait() {
		final ImageButtonStyle imageButtonStyle = AssetManagerUtility.getSkin().get("Portrait", ImageButtonStyle.class);
		imageButtonStyle.imageUp.setMinHeight(45);
		imageButtonStyle.imageUp.setMinWidth(45);
		heroImageButton = new ImageButton(imageButtonStyle);
	}

	private void initStatsMenu() {
		heroNameLabel = new Label("", labelStyle);
		hpLabel = new Label(" hp:", labelStyle);
		hp = new Label("", labelStyle);
		apLabel = new Label(" ap:", labelStyle);
		ap = new Label("", labelStyle);
	}

	private void changeHeroImage(final String heroImageName) {
		final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(heroImageName));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		final ImageButtonStyle oldStyle = heroImageButton.getStyle();
		oldStyle.imageUp = trd;
		oldStyle.imageUp.setMinHeight(45);
		oldStyle.imageUp.setMinWidth(45);
		heroImageButton.setStyle(oldStyle);
	}

	private void changeHeroImage() {
		changeHeroImage(UNKNOWN_HERO_IMAGE);
	}

	private void populateHeroImage() {
		table = new Table();
		table.setPosition(20, 330);
		table.add(heroImageButton).size(50);
		final Table subtable = new Table();
		subtable.pad(0);
		subtable.add(heroNameLabel).height(5).align(Align.topLeft).width(75);
		subtable.row();

		subtable.add(hpLabel).align(Align.left).expandX().width(20);
		subtable.add(hp).align(Align.left).expandX();
		subtable.row();

		subtable.add(apLabel).align(Align.left).expandX().width(20);
		subtable.add(ap).align(Align.left).expandX();
		subtable.setBackground(AssetManagerUtility.getSkin().getDrawable("window-noborder"));
		table.add(subtable).height(50);

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
