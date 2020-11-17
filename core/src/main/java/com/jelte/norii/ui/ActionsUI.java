package com.jelte.norii.ui;

import java.util.ArrayList;
import java.util.List;

import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.screen.BattleScreen;
import com.jelte.norii.testUI.ActionUIButton;

public class ActionsUI extends UIWindow {
	private static final int Y_OFFSET = 1;
	private static final int WINDOW_HEIGHT = 2;
	private static final int BUTTON_WIDTH = 1;
	private static final int BUTTON_HEIGHT = 1;
	private static final float ICON_PADDING = 0.2f;
	private static final float MAIN_WINDOW_PADDING = 0.2f;
	private static final int MAX_NUMBER_ICONS = 5;
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
		super("", MAX_NUMBER_ICONS + 1f, WINDOW_HEIGHT);
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
		// entity.setActionsui(this);
	}

	@Override
	protected void createWidgets() {
		createButtons();
		storeButtons();
	}

	private void createButtons() {
//		moveActionUIButton = new MoveActionUIButton(MOVE_BUTTON_SPRITE_NAME, linkedEntity);
//		attackActionUIButton = new AttackActionUIButton(ATTACK_BUTTON_SPRITE_NAME, linkedEntity);
		// skipActionUIButton = new SkipActionUIButton(this, SKIP_BUTTON_SPRITE_NAME,
		// linkedEntity);
	}

	private void storeButtons() {
		buttons.add(moveActionUIButton);
		buttons.add(attackActionUIButton);
		buttons.add(skipActionUIButton);
	}

	private void initPopUps() {
		popUps = new ArrayList<>();
		for (final ActionUIButton button : buttons) {
			// popUps.add(button.getPopUp());
		}
	}

	@Override
	protected void addWidgets() {
		addButtons();
		addSpells();
	}

	private void addButtons() {
		final float buttonWidth = BUTTON_WIDTH;
		final float buttonHeight = BUTTON_HEIGHT;

		this.add(attackActionUIButton.getButton()).size(buttonWidth, buttonHeight).pad(ICON_PADDING);
		this.add(moveActionUIButton.getButton()).size(buttonWidth, buttonHeight).pad(ICON_PADDING);
		this.add(skipActionUIButton.getButton()).size(buttonWidth, buttonHeight).pad(ICON_PADDING);
	}

	private void addSpells() {
		final float buttonWidth = BUTTON_WIDTH;
		final float buttonHeight = BUTTON_HEIGHT;

		for (final Ability ability : linkedEntity.getAbilities()) {
			// final SpellActionUIButton spellActionUIButton = new
			// SpellActionUIButton(ability.getSpellData().getIconSpriteName(), linkedEntity,
			// ability);
			// buttons.add(spellActionUIButton);
			// this.add(spellActionUIButton.getButton()).size(buttonWidth,
			// buttonHeight).pad(ICON_PADDING);
		}
	}

	@Override
	public void updatePos() {
		this.setPosition(linkedEntity.getCurrentPosition().getTileX(), linkedEntity.getCurrentPosition().getTileY() + Y_OFFSET);
		adjustPosition();
		adjustPopUps();
	}

	private void adjustPopUps() {
		final float buttonHeight = BUTTON_HEIGHT * 2f;
		for (final ActionInfoUIWindow popUp : popUps) {
			popUp.setPosition(this.getX(), this.getY() + buttonHeight);
		}
	}

	private void adjustPosition() {
		final float x = linkedEntity.getCurrentPosition().getTileX();
		final float y = linkedEntity.getCurrentPosition().getTileY();
		final float offsetX = 1;
		final float offsetY = WINDOW_HEIGHT + Y_OFFSET;
		final Boolean right = x > (BattleScreen.VISIBLE_WIDTH / 3);
		final Boolean up = y > (BattleScreen.VISIBLE_HEIGHT / 3);

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

}
