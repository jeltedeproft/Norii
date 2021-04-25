package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.utility.AssetManagerUtility;

public class StatusUi extends Window {
	private int phyDefVal;
	private int hpVal;
	private int maxHpVal;
	private int apVal;
	private int maxApVal;
	private int magDefVal;

	private final float tilePixelWidth;
	private final float tilePixelHeight;

	private Label heroName;
	private Label hp;
	private Label ap;
	private Label magDef;
	private Label phyDef;

	private Label hpLabel;
	private Label apLabel;
	private Label magDefLabel;
	private Label phyDefLabel;

	private boolean actionsUIIsHovering = false;

	private static final float WIDTH_TILES = 10;
	private static final float HEIGHT_TILES = 5.5f;// for this class, change this value, it recalculates around window size
	private static final float WINDOW_PADDING = 0;
	private static final int HERO_NAME_COLSPAN = 3;
	private static final int STATS_WIDTH = 20;
	private static final int STATS_HEIGHT = 5;
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
		hpLabel = new Label("hp:", statusUISkin, FONT_NAME_LABEL_STYLE);
		hp = new Label(String.valueOf(hpVal) + SEPARATOR + maxHpVal, statusUISkin, FONT_NAME_LABEL_STYLE);
		apLabel = new Label("ap:", statusUISkin, FONT_NAME_LABEL_STYLE);
		ap = new Label(String.valueOf(apVal) + SEPARATOR + maxApVal, statusUISkin, FONT_NAME_LABEL_STYLE);
		magDefLabel = new Label("mag def:", statusUISkin, FONT_NAME_LABEL_STYLE);
		magDef = new Label(String.valueOf(magDefVal), statusUISkin, FONT_NAME_LABEL_STYLE);
		phyDefLabel = new Label("phy def:", statusUISkin, FONT_NAME_LABEL_STYLE);
		phyDef = new Label(String.valueOf(phyDefVal), statusUISkin, FONT_NAME_LABEL_STYLE);
	}

	private void createGroups() {
		final WidgetGroup group = new WidgetGroup();
		final WidgetGroup group2 = new WidgetGroup();
		group.setFillParent(true);
		group2.setFillParent(true);

		defaults().expand().fill();
	}

	private void addWidgets() {
		this.add(heroName).colspan(HERO_NAME_COLSPAN);
		row();

		this.add(hpLabel).align(Align.left).expandX().width(STATS_WIDTH).height(STATS_HEIGHT);
		this.add(hp).align(Align.left).width(STATS_WIDTH).height(STATS_HEIGHT);
		row();

		this.add(apLabel).align(Align.left).expandX().width(STATS_WIDTH).height(STATS_HEIGHT);
		this.add(ap).align(Align.left).width(STATS_WIDTH).height(STATS_HEIGHT);
		row();

		this.add(phyDefLabel).align(Align.left).expandX().width(STATS_WIDTH).height(STATS_HEIGHT);
		this.add(phyDef).align(Align.left).width(STATS_WIDTH).height(STATS_HEIGHT);
		row();

		this.add(magDefLabel).align(Align.left).expandX().width(STATS_WIDTH).height(STATS_HEIGHT);
		this.add(magDef).align(Align.left).width(STATS_WIDTH).height(STATS_HEIGHT);
		row();

		pack();

	}

	public void update(Entity unit) {
		updateStats(unit);
		updateLabels();

		final boolean isHovering = unit.getVisualComponent().isHovering();

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
		phyDefVal = unit.getEntityData().getPhysicalDefense();
		hpVal = unit.getHp();
		maxHpVal = unit.getEntityData().getMaxHP();
		apVal = unit.getAp();
		maxApVal = unit.getEntityData().getMaxAP();
		magDefVal = unit.getEntityData().getMagicalDefense();
	}

	private void updateLabels() {
		hp.setText(String.valueOf(hpVal) + SEPARATOR + maxHpVal);
		ap.setText(String.valueOf(apVal) + SEPARATOR + maxApVal);
		magDef.setText(String.valueOf(magDefVal) + "%");
		phyDef.setText(String.valueOf(phyDefVal) + "%");
	}

	public void setActionsUIHovering(boolean actionsUIHovering) {
		this.actionsUIIsHovering = actionsUIHovering;
	}
}
