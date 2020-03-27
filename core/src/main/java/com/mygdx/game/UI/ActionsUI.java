package com.mygdx.game.UI;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Magic.Ability;
import com.mygdx.game.Screen.BattleScreen;

import Utility.Utility;

public class ActionsUI extends UIWindow {
	private static final float WINDOW_WIDTH = 2.5f;
	private static final float WINDOW_HEIGHT = 3.3f;
	private static final int BUTTON_WIDTH = 1;
	private static final int BUTTON_HEIGHT = 1;
	private static final int ICON_PADDING = 10;
	private static final int MAIN_WINDOW_PADDING = 5;
	private static final float EXTRA_WINDOW_SIZE = (MAIN_WINDOW_PADDING + ICON_PADDING) * 0.0625f;
	private static final String MOVE_BUTTON_SPRITEPATH = "sprites/gui/move.png";
	private static final String ATTACK_BUTTON_SPRITEPATH = "sprites/gui/attack.png";
	private static final String SKIP_BUTTON_SPRITEPATH = "sprites/gui/skip.png";

	private MoveActionUIButton moveActionUIButton;
	private AttackActionUIButton attackActionUIButton;
	private SkipActionUIButton skipActionUIButton;
	private ArrayList<SpellActionUIButton> SpellActionUIButtons;

	private Label moveLabel;
	private Label attackLabel;
	private Label skipLabel;

	private ArrayList<ActionUIButton> buttons;
	private Entity linkedEntity;

	public ActionsUI(final Entity entity) {
		super("", WINDOW_WIDTH + EXTRA_WINDOW_SIZE, WINDOW_HEIGHT + EXTRA_WINDOW_SIZE);
		configureMainWindow();
		initVariables(entity);
		createWidgets();
		addWidgets();
	}

	protected void configureMainWindow() {
		setVisible(false);
		this.pad(MAIN_WINDOW_PADDING);
		setKeepWithinStage(false);
	}

	private void initVariables(final Entity entity) {
		buttons = new ArrayList<ActionUIButton>();
		SpellActionUIButtons = new ArrayList<SpellActionUIButton>();
		linkedEntity = entity;
		entity.setActionsui(this);
	}

	protected void createWidgets() {
		createButtons();
		createLabels();
		storeButtons();
		addSpells();
	}

	private void createButtons() {
		moveActionUIButton = new MoveActionUIButton(MOVE_BUTTON_SPRITEPATH, linkedEntity);
		attackActionUIButton = new AttackActionUIButton(ATTACK_BUTTON_SPRITEPATH, linkedEntity);
		skipActionUIButton = new SkipActionUIButton(this, SKIP_BUTTON_SPRITEPATH, linkedEntity);
	}

	private void createLabels() {
		moveLabel = new Label("move", Utility.getStatusUISkin());
		attackLabel = new Label("attack", Utility.getStatusUISkin());
		skipLabel = new Label("skip", Utility.getStatusUISkin());
	}

	private void storeButtons() {
		buttons.add(moveActionUIButton);
		buttons.add(attackActionUIButton);
		buttons.add(skipActionUIButton);
	}

	protected void addWidgets() {
		addButtons();
	}

	private void addButtons() {
		final float buttonWidth = BUTTON_WIDTH * tileWidthPixel;
		final float buttonHeight = BUTTON_HEIGHT * tileHeightPixel;

		this.add(moveActionUIButton.getButton()).size(buttonWidth, buttonHeight).pad(ICON_PADDING);
		this.add(moveLabel).expand().fill();
		row();

		this.add(attackActionUIButton.getButton()).size(buttonWidth, buttonHeight).pad(ICON_PADDING);
		this.add(attackLabel).expand().fill();
		row();

		this.add(skipActionUIButton.getButton()).size(buttonWidth, buttonHeight).pad(ICON_PADDING);
		this.add(skipLabel).expand().fill();
		this.add();
	}

	private void addSpells() {
		final float buttonWidth = BUTTON_WIDTH * tileWidthPixel;
		final float buttonHeight = BUTTON_HEIGHT * tileHeightPixel;

		for (final Ability ability : linkedEntity.getAbilities()) {
			final SpellActionUIButton spellActionUIButton = new SpellActionUIButton(ability.getSpellData().getIconSpritePath(), linkedEntity, ability);
			buttons.add(spellActionUIButton);
			this.add(spellActionUIButton.getButton()).size(buttonWidth, buttonHeight).pad(ICON_PADDING);
			this.add(new Label(ability.getName(), Utility.getStatusUISkin())).expand().fill();
			row();
		}
	}

	public void updatePos() {
		this.setPosition((linkedEntity.getCurrentPosition().getCameraX()), (linkedEntity.getCurrentPosition().getCameraY()));
		adjustPosition();
	}

	private void adjustPosition() {
		final float x = linkedEntity.getCurrentPosition().getCameraX();
		final float y = linkedEntity.getCurrentPosition().getCameraY();
		final float offsetX = Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH;
		final float offsetY = WINDOW_HEIGHT * tileHeightPixel;
		final Boolean right = x > (Gdx.graphics.getWidth() / 3);
		final Boolean up = y > (Gdx.graphics.getHeight() / 3);

		if (Boolean.TRUE.equals(right)) {
			this.setX(x - (offsetX * 2));
		} else {
			this.setX(x + offsetX);
		}

		if (Boolean.TRUE.equals(up)) {
			this.setY(y - (offsetY));
		} else {
			this.setY(y + (offsetY / WINDOW_HEIGHT));
		}
	}
}
