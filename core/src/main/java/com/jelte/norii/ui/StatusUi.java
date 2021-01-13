package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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

	private final float tilePixelWidth;
	private final float tilePixelHeight;

	private Label heroName;
	private Label hp;
	private Label ap;
	private Label xp;
	private Label levelValLabel;

	private Label hpLabel;
	private Label apLabel;
	private Label xpLabel;
	private Label levelLabel;

	private boolean actionsUIIsHovering = false;

	private static final float WIDTH_TILES = 8;
	private static final float HEIGHT_TILES = 7;
	private static final float WINDOW_PADDING = 0;
	private static final int HERO_NAME_COLSPAN = 3;
	private static final int STATS_WIDTH = 20;
	private static final int STATS_HEIGHT = 20;
	private static final String SEPARATOR = "/";
	private static final String FONT_NAME_LABEL_STYLE = "smallFont";

	public StatusUi(final Entity entity, int mapWidth, int mapHeight) {
		super("", AssetManagerUtility.getSkin());

		tilePixelWidth = Hud.UI_VIEWPORT_WIDTH / mapWidth;
		tilePixelHeight = Hud.UI_VIEWPORT_HEIGHT / mapHeight;

		initVariables(entity);
		configureMainWindow();
		createWidgets(entity);
		addWidgets();

		setSize(tilePixelWidth * WIDTH_TILES, tilePixelHeight * HEIGHT_TILES);
		pad(WINDOW_PADDING);
		padLeft(10);
		setTransform(false);
	}

	private void configureMainWindow() {
		setVisible(false);
		setResizable(true);
	}

	private void initVariables(final Entity entity) {
		updateStats(entity);
	}

	private void createWidgets(Entity entity) {
		createLabels(entity);
		createGroups();
	}

	private void createLabels(Entity entity) {
		final Skin statusUISkin = AssetManagerUtility.getSkin();
		heroName = new Label(entity.getEntityData().getName(), statusUISkin, FONT_NAME_LABEL_STYLE);
		hpLabel = new Label(" hp:", statusUISkin, FONT_NAME_LABEL_STYLE);
		hp = new Label(String.valueOf(hpVal) + SEPARATOR + maxHpVal, statusUISkin, FONT_NAME_LABEL_STYLE);
		apLabel = new Label(" ap:", statusUISkin, FONT_NAME_LABEL_STYLE);
		ap = new Label(String.valueOf(apVal) + SEPARATOR + maxApVal, statusUISkin, FONT_NAME_LABEL_STYLE);
		xpLabel = new Label(" xp:", statusUISkin, FONT_NAME_LABEL_STYLE);
		xp = new Label(String.valueOf(xpVal) + SEPARATOR + Entity.MAX_XP, statusUISkin, FONT_NAME_LABEL_STYLE);
		levelLabel = new Label(" lv:", statusUISkin, FONT_NAME_LABEL_STYLE);
		levelValLabel = new Label(String.valueOf(levelVal), statusUISkin, FONT_NAME_LABEL_STYLE);
	}

	private void createGroups() {
		final WidgetGroup group = new WidgetGroup();
		final WidgetGroup group2 = new WidgetGroup();
		group.setFillParent(true);
		group2.setFillParent(true);

		defaults().expand().fill();
	}

	private void addWidgets() {
		this.add(heroName).colspan(HERO_NAME_COLSPAN).height(30);
		row();

		this.add(hpLabel).align(Align.left).expandX().width(STATS_WIDTH).height(STATS_HEIGHT);
		this.add(hp).align(Align.left).width(STATS_WIDTH).height(STATS_HEIGHT);
		row();

		this.add(apLabel).align(Align.left).expandX().width(STATS_WIDTH).height(STATS_HEIGHT);
		this.add(ap).align(Align.left).width(STATS_WIDTH).height(STATS_HEIGHT);
		row();

		this.add(levelLabel).align(Align.left).expandX().width(STATS_WIDTH).height(STATS_HEIGHT);
		this.add(levelValLabel).align(Align.left).width(STATS_WIDTH).height(STATS_HEIGHT);
		row();

		this.add(xpLabel).align(Align.left).expandX().width(STATS_WIDTH).height(STATS_HEIGHT);
		this.add(xp).align(Align.left).width(STATS_WIDTH).height(STATS_HEIGHT);
		row();

		pack();

	}

	public void update(Entity unit) {
		updateStats(unit);
		updateLabels();

		final boolean isHovering = unit.getEntityactor().getIsHovering();

		if (Boolean.TRUE.equals(isHovering)) {
			setVisible(true);
		}

		if (actionsUIIsHovering) {
			// setVisible(false);
		}

		updatePos(unit);
	}

	private void updatePos(Entity unit) {
		this.setPosition((unit.getCurrentPosition().getTileX() * tilePixelWidth) + tilePixelWidth, ((unit.getCurrentPosition().getTileY() * tilePixelHeight) + tilePixelHeight));
	}

	private void updateStats(Entity unit) {
		levelVal = unit.getEntityData().getLevel();
		hpVal = unit.getHp();
		maxHpVal = unit.getEntityData().getMaxHP();
		apVal = unit.getAp();
		maxApVal = unit.getEntityData().getMaxAP();
		xpVal = unit.getXp();
	}

	private void updateLabels() {
		hp.setText(String.valueOf(hpVal) + SEPARATOR + maxHpVal);
		ap.setText(String.valueOf(apVal) + SEPARATOR + maxApVal);
		xp.setText(String.valueOf(xpVal) + SEPARATOR + Entity.MAX_XP);
		levelValLabel.setText(String.valueOf(levelVal));
	}

	public void setActionsUIHovering(boolean actionsUIHovering) {
		this.actionsUIIsHovering = actionsUIHovering;
	}
}
