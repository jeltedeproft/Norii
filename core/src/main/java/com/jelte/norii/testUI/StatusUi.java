package com.jelte.norii.testUI;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.utility.AssetManagerUtility;

public class StatusUi extends Window {
	private int levelVal;
	private int hpVal;
	private int maxHpVal;
	private int apVal;
	private int maxApVal;
	private int xpVal;
	private int maxXpVal;

	private final int mapWidth;
	private final int mapHeight;

	private Label heroName;
	private Label hp;
	private Label ap;
	private Label xp;
	private Label levelValLabel;

	private LabelStyle labelStyle;
	private Label hpLabel;
	private Label apLabel;
	private Label xpLabel;
	private Label levelLabel;

	private Entity linkedEntity;
	private boolean actionsUIIsHovering = false;

	private static final float WIDTH_TILES = 8;
	private static final float HEIGHT_TILES = 7;

	public StatusUi(final Entity entity, int mapWidth, int mapHeight) {
		super("", AssetManagerUtility.getSkin());
		this.mapHeight = mapHeight;
		this.mapWidth = mapWidth;
		initVariables(entity);
		configureMainWindow();
		createWidgets();
		addWidgets();
		this.setSize((NewHud.UI_VIEWPORT_WIDTH / mapWidth) * WIDTH_TILES, (NewHud.UI_VIEWPORT_HEIGHT / mapHeight) * HEIGHT_TILES);
		this.pad(0);
	}

	private void configureMainWindow() {
		setVisible(false);
		setResizable(true);
	}

	private void initVariables(final Entity entity) {
		linkedEntity = entity;
		entity.setStatusui(this);
		updateStats();
	}

	private void createWidgets() {
		createFont();
		createLabels();
		createGroups();
	}

	private void createFont() {
		final BitmapFont font = AssetManagerUtility.getFreeTypeFontAsset("15_fonts/sporty.ttf");
		labelStyle = new LabelStyle();
		labelStyle.font = font;
	}

	private void createLabels() {
		heroName = new Label(linkedEntity.getEntityData().getName(), labelStyle);
		hpLabel = new Label(" hp:", labelStyle);
		hp = new Label(String.valueOf(hpVal) + "/" + maxHpVal, labelStyle);
		apLabel = new Label(" ap:", labelStyle);
		ap = new Label(String.valueOf(apVal) + "/" + maxApVal, labelStyle);
		xpLabel = new Label(" xp:", labelStyle);
		xp = new Label(String.valueOf(xpVal) + "/" + maxXpVal, labelStyle);
		levelLabel = new Label(" lv:", labelStyle);
		levelValLabel = new Label(String.valueOf(levelVal), labelStyle);
	}

	private void createGroups() {
		final WidgetGroup group = new WidgetGroup();
		final WidgetGroup group2 = new WidgetGroup();
		group.setFillParent(true);
		group2.setFillParent(true);

		defaults().expand().fill();
	}

	private void addWidgets() {
		this.add(heroName).colspan(3);
		row();

		this.add(hpLabel).align(Align.left).expandX();
		this.add(hp).align(Align.left);
		row();

		this.add(apLabel).align(Align.left).expandX();
		this.add(ap).align(Align.left);
		row();

		this.add(levelLabel).align(Align.left).expandX();
		this.add(levelValLabel).align(Align.left);
		row();

		this.add(xpLabel).align(Align.left).expandX();
		this.add(xp).align(Align.left);
		row();

		pack();

	}

	public void update() {
		updateStats();
		updateLabels();

		final boolean isHovering = linkedEntity.getEntityactor().getIsHovering();

		if (Boolean.TRUE.equals(isHovering)) {
			setVisible(true);
		}

		if (actionsUIIsHovering) {
			// setVisible(false);
		}

		updatePos();
	}

	private void updatePos() {
		this.setPosition((linkedEntity.getCurrentPosition().getTileX() * (NewHud.UI_VIEWPORT_WIDTH / mapWidth)) + 16, ((linkedEntity.getCurrentPosition().getTileY() * (NewHud.UI_VIEWPORT_HEIGHT / mapHeight)) + 16));
	}

	private void updateStats() {
		levelVal = linkedEntity.getEntityData().getLevel();
		hpVal = linkedEntity.getHp();
		maxHpVal = linkedEntity.getEntityData().getMaxHP();
		apVal = linkedEntity.getAp();
		maxApVal = linkedEntity.getEntityData().getMaxAP();
		xpVal = linkedEntity.getEntityData().getXp();
		maxXpVal = linkedEntity.getEntityData().getMaxXP();
	}

	private void updateLabels() {
		hp.setText(String.valueOf(hpVal) + "/" + maxHpVal);
		ap.setText(String.valueOf(apVal) + "/" + maxApVal);
		xp.setText(String.valueOf(xpVal) + "/" + maxXpVal);
		levelValLabel.setText(String.valueOf(levelVal));
	}

	public void setActionsUIHovering(boolean actionsUIHovering) {
		this.actionsUIIsHovering = actionsUIHovering;
	}
}
