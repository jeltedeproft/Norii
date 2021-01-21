package com.jelte.norii.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.TiledMapPosition;

public class ActionsUi extends Window {
	private static final int MOVE_AMOUNT_OUT_OF_BOUND_X = 32;
	private static final int MOVE_AMOUNT_OUT_OF_BOUND_Y = 32;
	private static final int HEIGHT_TILES = 2;
	private static final int WIDTH_TILES = 5;
	private static final float ICON_PADDING = 2f;
	private static final float MAIN_WINDOW_PADDING = 0f;
	private static final String MOVE_BUTTON_SPRITE_NAME = "move";
	private static final String ATTACK_BUTTON_SPRITE_NAME = "attack";
	private static final String SKIP_BUTTON_SPRITE_NAME = "skip";

	private final int mapWidth;
	private final int mapHeight;

	private final float tilePixelWidth;
	private final float tilePixelHeight;

	private MoveActionUIButton moveActionUIButton;
	private AttackActionUIButton attackActionUIButton;
	private SkipActionUIButton skipActionUIButton;

	private ArrayList<ActionUIButton> buttons;
	private ArrayList<ActionInfoUiWindow> popUps;

	public ActionsUi(final Entity entity, int mapWidth, int mapHeight, Hud hud) {
		super("", AssetManagerUtility.getSkin());

		this.mapHeight = mapHeight;
		this.mapWidth = mapWidth;
		setBackground(AssetManagerUtility.getSkin().getDrawable("window-noborder"));

		tilePixelWidth = Hud.UI_VIEWPORT_WIDTH / mapWidth;
		tilePixelHeight = Hud.UI_VIEWPORT_HEIGHT / mapHeight;

		configureMainWindow();
		initVariables(entity);
		createWidgets(entity, hud);
		addWidgets(entity, hud);
		initPopUps();

		setSize(tilePixelWidth * WIDTH_TILES, tilePixelHeight * HEIGHT_TILES);
		pad(MAIN_WINDOW_PADDING);
	}

	private void configureMainWindow() {
		setVisible(false);
		setKeepWithinStage(false);
	}

	private void initVariables(final Entity entity) {
		buttons = new ArrayList<>();
	}

	private void createWidgets(Entity entity, Hud hud) {
		createButtons(entity, hud);
		storeButtons();
	}

	private void createButtons(Entity entity, Hud hud) {
		final int id = entity.getEntityID();
		moveActionUIButton = new MoveActionUIButton(MOVE_BUTTON_SPRITE_NAME, id, mapWidth, mapHeight, hud);
		attackActionUIButton = new AttackActionUIButton(ATTACK_BUTTON_SPRITE_NAME, id, mapWidth, mapHeight, hud);
		skipActionUIButton = new SkipActionUIButton(SKIP_BUTTON_SPRITE_NAME, id, mapWidth, mapHeight, hud);
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

	private void addWidgets(Entity entity, Hud hud) {
		addButtons();
		addSpells(entity, hud);
	}

	private void addButtons() {
		add(attackActionUIButton.getButton()).size(tilePixelWidth, tilePixelHeight).pad(ICON_PADDING);
		add(moveActionUIButton.getButton()).size(tilePixelWidth, tilePixelHeight).pad(ICON_PADDING);
		add(skipActionUIButton.getButton()).size(tilePixelWidth, tilePixelHeight).pad(ICON_PADDING);
	}

	private void addSpells(Entity entity, Hud hud) {
		if (!entity.getAbilities().isEmpty()) {
			for (final Ability ability : entity.getAbilities()) {
				final SpellActionUIButton spellActionUIButton = new SpellActionUIButton(ability.getSpellData().getIconSpriteName(), entity.getEntityID(), ability, mapWidth, mapHeight, hud);
				buttons.add(spellActionUIButton);
				add(spellActionUIButton.getButton()).size(tilePixelWidth, tilePixelHeight).pad(ICON_PADDING);
			}
		}
	}

	public void update(Entity entity) {
		if (this.isVisible()) {
			updatePos(entity);
		}
	}

	public void updatePos(Entity entity) {
		final TiledMapPosition pos = entity.getCurrentPosition();
		this.setPosition((pos.getTileX() * tilePixelWidth) + tilePixelWidth, ((pos.getTileY() * tilePixelHeight) + tilePixelHeight));
		adjustPosition();
		adjustPopUps();
		setHovering(entity);
	}

	private void adjustPopUps() {
		for (final ActionInfoUiWindow popUp : popUps) {
			popUp.setPosition(getX(), getY() + (tilePixelHeight * 2f));
		}
	}

	private void adjustPosition() {
		final Boolean right = getX() > Hud.UI_VIEWPORT_WIDTH;
		final Boolean up = getY() > Hud.UI_VIEWPORT_HEIGHT;
		final Boolean left = getX() < 0;
		final Boolean down = getY() < 0;

		if (Boolean.TRUE.equals(right)) {
			setX(getX() - (MOVE_AMOUNT_OUT_OF_BOUND_X));
		}

		if (Boolean.TRUE.equals(left)) {
			setX(getX() + (MOVE_AMOUNT_OUT_OF_BOUND_X));
		}

		if (Boolean.TRUE.equals(up)) {
			setY(getY() - MOVE_AMOUNT_OUT_OF_BOUND_Y);
		}

		if (Boolean.TRUE.equals(down)) {
			setY(getY() + MOVE_AMOUNT_OUT_OF_BOUND_Y);
		}
	}

	public List<ActionInfoUiWindow> getPopUps() {
		return popUps;
	}

	private void setHovering(Entity entity) {
		for (final ActionUIButton button : buttons) {
			if (button.isHovering() && button.entered) {
				entity.getEntityactor().setActionsHovering(true);
			}

			if (button.exited) {
				entity.getEntityactor().setActionsHovering(false);
				button.exited = false;
			}
		}
	}
}
