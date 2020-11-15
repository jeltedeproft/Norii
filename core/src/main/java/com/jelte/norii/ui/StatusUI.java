package com.jelte.norii.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.utility.AssetManagerUtility;

public class StatusUI extends UIWindow {
	private int levelVal;
	private int hpVal;
	private int maxHpVal;
	private int apVal;
	private int maxApVal;
	private int xpVal;
	private int maxXpVal;

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

	private static final float WIDTH_TILES = 10;
	private static final float HEIGHT_TILES = 3;

	public StatusUI(final Entity entity) {
		super("", WIDTH_TILES, HEIGHT_TILES);
		initVariables(entity);
		configureMainWindow();
		createWidgets();
		addWidgets();
		this.setBounds(10, 10, WIDTH_TILES, HEIGHT_TILES);

	}

	@Override
	protected void configureMainWindow() {
		// setVisible(false);
		setResizable(true);
	}

	private void initVariables(final Entity entity) {
		linkedEntity = entity;
		entity.setStatusui(this);
		updateStats();
	}

	@Override
	protected void createWidgets() {
		createFont();
		createLabels();
		createGroups();
	}

	private void createFont() {
		final BitmapFont font = AssetManagerUtility.getFreeTypeFontAsset("24_fonts/sporty.ttf");
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

	@Override
	protected void addWidgets() {
		this.add(new Image(AssetManagerUtility.getSprite("SnakeQueenIdleDown")));
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

	@Override
	public void update() {
		super.update();

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

	@Override
	protected void updatePos() {
		this.setPosition((linkedEntity.getCurrentPosition().getTileX()) + 1, (linkedEntity.getCurrentPosition().getTileY()) + 1);
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
