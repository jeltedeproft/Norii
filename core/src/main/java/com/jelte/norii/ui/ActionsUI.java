package com.jelte.norii.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.screen.BattleScreen;

public class ActionsUI extends UIWindow {
	private static final float Y_OFFSET = 5f;
	private static final float WINDOW_WIDTH = 5f;
	private static final float WINDOW_HEIGHT = 1f;
	private static final int BUTTON_WIDTH = 1;
	private static final int BUTTON_HEIGHT = 1;
	private static final int ICON_PADDING = 10;
	private static final int MAIN_WINDOW_PADDING = 5;
	private static final float EXTRA_WINDOW_SIZE = (MAIN_WINDOW_PADDING + ICON_PADDING) * 0.0625f;
	private static final String MOVE_BUTTON_SPRITE_NAME = "move";
	private static final String ATTACK_BUTTON_SPRITE_NAME = "attack";
	private static final String SKIP_BUTTON_SPRITE_NAME = "skip";

	private MoveActionUIButton moveActionUIButton;
	private AttackActionUIButton attackActionUIButton;
	private SkipActionUIButton skipActionUIButton;

	private ArrayList<ActionUIButton> buttons;
	private ArrayList<ActionInfoUIWindow> popUps;
	private PlayerEntity linkedEntity;

	public ActionsUI(final PlayerEntity entity) {
		super("", WINDOW_WIDTH + EXTRA_WINDOW_SIZE, WINDOW_HEIGHT + EXTRA_WINDOW_SIZE);
		configureMainWindow();
		initVariables(entity);
		createWidgets();
		addWidgets();
		initPopUps();
	}

	@Override
	protected void configureMainWindow() {
		setVisible(false);
		this.pad(MAIN_WINDOW_PADDING);
		setKeepWithinStage(false);
	}

	private void initVariables(final PlayerEntity entity) {
		buttons = new ArrayList<>();
		linkedEntity = entity;
		entity.setActionsui(this);
	}

	@Override
	protected void createWidgets() {
		createButtons();
		storeButtons();
	}

	private void createButtons() {
		moveActionUIButton = new MoveActionUIButton(MOVE_BUTTON_SPRITE_NAME, linkedEntity);
		attackActionUIButton = new AttackActionUIButton(ATTACK_BUTTON_SPRITE_NAME, linkedEntity);
		skipActionUIButton = new SkipActionUIButton(this, SKIP_BUTTON_SPRITE_NAME, linkedEntity);
	}

	private void storeButtons() {
		buttons.add(moveActionUIButton);
		buttons.add(attackActionUIButton);
		buttons.add(skipActionUIButton);
	}

	private void initPopUps() {
		popUps = new ArrayList<>();
		for (final ActionUIButton button : buttons) {
			popUps.add(button.getPopUp());
		}
	}

	@Override
	protected void addWidgets() {
		addButtons();
		addSpells();
	}

	private void addButtons() {
		final float buttonWidth = BUTTON_WIDTH * tileWidthPixel;
		final float buttonHeight = BUTTON_HEIGHT * tileHeightPixel;

		this.add(attackActionUIButton.getButton()).size(buttonWidth, buttonHeight).pad(ICON_PADDING);
		this.add(moveActionUIButton.getButton()).size(buttonWidth, buttonHeight).pad(ICON_PADDING);
		this.add(skipActionUIButton.getButton()).size(buttonWidth, buttonHeight).pad(ICON_PADDING);
	}

	private void addSpells() {
		final float buttonWidth = BUTTON_WIDTH * tileWidthPixel;
		final float buttonHeight = BUTTON_HEIGHT * tileHeightPixel;

		for (final Ability ability : linkedEntity.getAbilities()) {
			final SpellActionUIButton spellActionUIButton = new SpellActionUIButton(
					ability.getSpellData().getIconSpriteName(), linkedEntity, ability);
			buttons.add(spellActionUIButton);
			this.add(spellActionUIButton.getButton()).size(buttonWidth, buttonHeight).pad(ICON_PADDING);
		}
	}

	@Override
	public void updatePos() {
		this.setPosition(linkedEntity.getCurrentPosition().getCameraX(),
				linkedEntity.getCurrentPosition().getCameraY() + Y_OFFSET);
		adjustPosition();
		adjustPopUps();
		setHovering();
	}

	private void adjustPopUps() {
		final float buttonHeight = BUTTON_HEIGHT * tileHeightPixel * 2;
		for (final ActionInfoUIWindow popUp : popUps) {
			popUp.setPosition(this.getX(), this.getY() + buttonHeight);
		}
	}

	private void adjustPosition() {
		final float x = linkedEntity.getCurrentPosition().getCameraX();
		final float y = linkedEntity.getCurrentPosition().getCameraY();
		final float offsetX = Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH;
		final float offsetY = (WINDOW_HEIGHT * tileHeightPixel) + Y_OFFSET;
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

	public List<ActionInfoUIWindow> getPopUps() {
		return popUps;
	}

	private void setHovering() {
		for (final ActionUIButton button : buttons) {
			if (button.isHovering() && button.entered) {
				linkedEntity.getEntityactor().setActionsHovering(true);
			}

			if (button.exited) {
				linkedEntity.getEntityactor().setActionsHovering(false);
				button.exited = false;
			}
		}
	}
}
