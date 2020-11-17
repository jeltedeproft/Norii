package com.jelte.norii.ui;

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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.utility.AssetManagerUtility;

public class CharacterHud extends Window {
	private static final String UNKNOWN_HERO_IMAGE = "nochar";

	private int heroHP;
	private int heroAP;

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
	private static final int HP_LABEL_WIDTH = 1;
	private static final int HUD_BORDER_HEIGHT = 3;
	private static final int HUD_BORDER_WIDTH = 8;

	public CharacterHud(final List<Entity> allUnits) {
		super("", AssetManagerUtility.getSkin());
		linkUnitsToMenu(allUnits);
		initElementsForUI();
		populateHeroImage();
	}

	private void linkUnitsToMenu(final List<Entity> allUnits) {
		// allUnits.forEach(entity -> entity.setbottomMenu(this));
	}

	private void initElementsForUI() {
		initWindow();
		createFont();
		initPortrait();
		initStatsMenu();
	}

	private void initWindow() {
		final WindowStyle styleTransparent = AssetManagerUtility.getSkin().get("transparent", WindowStyle.class);
		setStyle(styleTransparent);
		pad(0);
		setPosition(0, 25);
		setTransform(true);
		setClip(false);
		setMovable(true);
		padTop(1);
		padLeft(1);
		this.setDebug(true);
		updateContainer();
		updateSizeElements();
	}

	private void initPortrait() {
		heroImageButton = new ImageButton(AssetManagerUtility.getSkin().get("Portrait", ImageButtonStyle.class));
		heroImageButton.setPosition(0, 0);
		heroImageButton.setSize(2, 2);
		heroImageButton.getImage().setFillParent(true);
	}

	private void createFont() {
		final BitmapFont font = AssetManagerUtility.getFreeTypeFontAsset("04_fonts/sporty.ttf");
		labelStyle = new LabelStyle();
		labelStyle.font = font;
	}

	private void initStatsMenu() {
		final Skin statusUISkin = AssetManagerUtility.getSkin();
		statsGroup = new Window("", statusUISkin.get("default", WindowStyle.class));
		statsGroup.setResizable(true);
		statsGroup.top();

		heroNameLabel = new Label("", labelStyle);
		hpLabel = new Label(" hp:", labelStyle);
		hp = new Label("", labelStyle);
		apLabel = new Label(" ap:", labelStyle);
		ap = new Label("", labelStyle);
		heroNameLabel.setSize(1, 1);
		hpLabel.setSize(1, 1);
		hp.setSize(1, 1);
		apLabel.setSize(1, 1);
		ap.setSize(1, 1);
		addLabelsToStatsGroup();
	}

	private void addLabelsToStatsGroup() {
		statsGroup.add(heroNameLabel).align(Align.topLeft).colspan(3).size(1);
		statsGroup.row();

		statsGroup.add(hpLabel).align(Align.topLeft).expandX().width(HP_LABEL_WIDTH).size(1);
		statsGroup.add(hp).align(Align.topLeft).padRight(STATS_MENU_ELEMENT_PADDING).expandX().size(1);
		statsGroup.row();

		statsGroup.add(apLabel).align(Align.left).expandX().size(1);
		statsGroup.add(ap).align(Align.left).padRight(STATS_MENU_ELEMENT_PADDING).expandX().size(1);
	}

	private void changeHeroImage(final String heroImageName) {
		final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(heroImageName));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		final ImageButtonStyle oldStyle = heroImageButton.getStyle();
//		final Image heroImage = new Image(trd);
//		heroImage.setSize(1, 1);
//		heroImage.getDrawable().setMinHeight(1);
//		heroImage.getDrawable().setMinWidth(1);
		oldStyle.imageUp = trd;
		heroImageButton.setStyle(oldStyle);
		heroImageButton.getImage().setFillParent(true);
	}

	private void changeHeroImage() {
		changeHeroImage(UNKNOWN_HERO_IMAGE);
	}

	private void populateHeroImage() {
		horizontalGroup = new HorizontalGroup();
		horizontalGroup.addActor(heroImageButton);
		horizontalGroup.padTop(1);
		horizontalGroup.fill();
		horizontalGroup.setSize(2, 2);

		horizontalGroup.addActor(statsGroup);
		statsGroup.setSize(3, 3);
		add(horizontalGroup).expand().left().size(5);
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
				setVisible(true);
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

	private void updateSizeElements() {

	}

	private void updateContainer() {
		setSize(HUD_BORDER_WIDTH, HUD_BORDER_HEIGHT);
	}
}
