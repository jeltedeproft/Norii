package com.jelte.norii.ui;

import java.util.EnumMap;
import java.util.Map.Entry;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class HudMessages {
	public static final float POPUP_WIDTH_FACTOR = 2f;
	public static final float POPUP_HEIGHT_FACTOR = 2f;
	public static final float POPUP_MESSAGE_FADE_IN_OUT_DURATION = 2f;

	public static final String INVALID_ATTACK_TARGET_MESSAGE = "Invalid Attack Target";
	public static final String INVALID_MOVE_MESSAGE = "Invalid Move";
	public static final String INVALID_SPAWN_POINT_MESSAGE = "Invalid Spawn Point";
	public static final String INVALID_SPELL_TARGET_MESSAGE = "Invalid Spell Target";
	public static final String NOT_ENOUGH_AP_MESSAGE = "Not enough AP";
	public static final String AI_VICTORY_MESSAGE = "You Lose!";
	public static final String PLAYER_VICTORY_MESSAGE = "You Win!";
	public static final String DEPLOY_UNITS_MESSAGE = "Deploy your units \n on one of the \n blue squares";
	public static final String NUMBER_OF_UNITS_DEPLOYED_MESSAGE = "Deployed 0/0 units";
	public static final String SELECT_UNIT_MESSAGE = "Click once on a \n unit to select it.";
	public static final String EXPLAIN_ACTION_MESSAGE = "Possible actions are, move, attack, \n cast an ability or skip your turn.";
	public static final String EXPLAIN_TURNING_MESSAGE = "You can change the direction your character is facing, \n by pressing W, A, S, D.";
	public static final String EXPLAIN_END_TURN_MESSAGE = "Press skip to end your turn.";
	public static final String EXPLAIN_CANCEL_MESSAGE = "Right click on the mouse to cancel any action.";
	public static final String EXPLAIN_WIN_LOSE_MESSAGE = "Once all your enemies are defeated\n, you win the round";
	public static final String PLAYER_TURN_MESSAGE = "Player turn";
	public static final String ENEMY_TURN_MESSAGE = "Enemy turn";

	private final EnumMap<HudMessageTypes, HudMessagePopup> messageTypesToPopups;
	private final EnumMap<HudMessageTypes, HudMessageWindow> tutorialMessageTypesToWindows;
	private final EnumMap<HudMessageTypes, HudMessageWindow> messageTypesToWindows;
	private final EnumMap<HudMessageTypes, Boolean> tutorialMessageTypesDisplayed;

	private final Stage stage;
	private final boolean isTutorial;

	public HudMessages(Stage stage, boolean isTutorial) {
		this.stage = stage;
		this.isTutorial = isTutorial;
		messageTypesToPopups = new EnumMap<>(HudMessageTypes.class);
		tutorialMessageTypesToWindows = new EnumMap<>(HudMessageTypes.class);
		messageTypesToWindows = new EnumMap<>(HudMessageTypes.class);
		tutorialMessageTypesDisplayed = new EnumMap<>(HudMessageTypes.class);
		createInfoMessages(isTutorial);
	}

	private void createInfoMessages(boolean isTutorial) {
		messageTypesToPopups.put(HudMessageTypes.INVALID_ATTACK_TARGET, new HudMessagePopup(INVALID_ATTACK_TARGET_MESSAGE));
		messageTypesToPopups.put(HudMessageTypes.INVALID_MOVE, new HudMessagePopup(INVALID_MOVE_MESSAGE));
		messageTypesToPopups.put(HudMessageTypes.INVALID_SPAWN_POINT, new HudMessagePopup(INVALID_SPAWN_POINT_MESSAGE));
		messageTypesToPopups.put(HudMessageTypes.INVALID_SPELL_TARGET, new HudMessagePopup(INVALID_SPELL_TARGET_MESSAGE));
		messageTypesToPopups.put(HudMessageTypes.NOT_ENOUGH_AP, new HudMessagePopup(NOT_ENOUGH_AP_MESSAGE));
		messageTypesToPopups.put(HudMessageTypes.AI_VICTORY, new HudMessagePopup(AI_VICTORY_MESSAGE));
		messageTypesToPopups.put(HudMessageTypes.PLAYER_VICTORY, new HudMessagePopup(PLAYER_VICTORY_MESSAGE));
		messageTypesToPopups.put(HudMessageTypes.NUMBER_OF_UNITS_DEPLOYED, new HudMessagePopup(NUMBER_OF_UNITS_DEPLOYED_MESSAGE));
		messageTypesToPopups.put(HudMessageTypes.PLAYER_TURN, new HudMessagePopup(PLAYER_TURN_MESSAGE));
		messageTypesToPopups.put(HudMessageTypes.ENEMY_TURN, new HudMessagePopup(ENEMY_TURN_MESSAGE));

		tutorialMessageTypesToWindows.put(HudMessageTypes.DEPLOY_UNITS_INFO, new HudMessageWindow(DEPLOY_UNITS_MESSAGE));
		tutorialMessageTypesToWindows.put(HudMessageTypes.SELECT_UNIT_INFO, new HudMessageWindow(SELECT_UNIT_MESSAGE));
		tutorialMessageTypesToWindows.put(HudMessageTypes.EXPLAIN_ACTION_INFO, new HudMessageWindow(EXPLAIN_ACTION_MESSAGE));
		tutorialMessageTypesToWindows.put(HudMessageTypes.EXPLAIN_TURNING_INFO, new HudMessageWindow(EXPLAIN_TURNING_MESSAGE));
		tutorialMessageTypesToWindows.put(HudMessageTypes.EXPLAIN_END_TURN_INFO, new HudMessageWindow(EXPLAIN_END_TURN_MESSAGE));
		tutorialMessageTypesToWindows.put(HudMessageTypes.EXPLAIN_CANCEL_INFO, new HudMessageWindow(EXPLAIN_CANCEL_MESSAGE));
		tutorialMessageTypesToWindows.put(HudMessageTypes.EXPLAIN_WIN_LOSE_INFO, new HudMessageWindow(EXPLAIN_WIN_LOSE_MESSAGE));

		tutorialMessageTypesToWindows.get(HudMessageTypes.DEPLOY_UNITS_INFO).setVisible(isTutorial);

		for (final HudMessageWindow window : tutorialMessageTypesToWindows.values()) {
			stage.addActor(window.getTextWindow());
		}

		for (final HudMessageWindow window : messageTypesToWindows.values()) {
			stage.addActor(window.getTextWindow());
		}

		for (final HudMessagePopup popup : messageTypesToPopups.values()) {
			stage.addActor(popup.getPopupMessage());
		}

		for (final HudMessageTypes type : tutorialMessageTypesToWindows.keySet()) {
			tutorialMessageTypesDisplayed.put(type, false);
		}

		tutorialMessageTypesDisplayed.put(HudMessageTypes.DEPLOY_UNITS_INFO, true);
	}

	public void showPopup(HudMessageTypes type) {
		if (messageTypesToPopups.containsKey(type)) {
			messageTypesToPopups.get(type).getPopupMessage().setVisible(true);
			messageTypesToPopups.get(type).getPopupMessage().addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(POPUP_MESSAGE_FADE_IN_OUT_DURATION), Actions.fadeOut(POPUP_MESSAGE_FADE_IN_OUT_DURATION)));
		}
	}

	public void showInfoWindow(HudMessageTypes type) {
		if (messageTypesToWindows.containsKey(type)) {
			messageTypesToWindows.get(type).getTextWindow().setVisible(true);
		}
	}

	public void showNextTutorialMessage() {
		for (final Entry<HudMessageTypes, HudMessageWindow> type : tutorialMessageTypesToWindows.entrySet()) {
			type.getValue().getTextWindow().setVisible(false);
			if (Boolean.FALSE.equals(tutorialMessageTypesDisplayed.get(type.getKey()))) {
				type.getValue().getTextWindow().setVisible(isTutorial);
				tutorialMessageTypesDisplayed.put(type.getKey(), true);
				break;
			}
		}
	}

	public void hideInfoWindow(HudMessageTypes type) {
		if (messageTypesToWindows.containsKey(type)) {
			messageTypesToWindows.get(type).getTextWindow().setVisible(false);
		}
	}

	public void updateNumberOfDeployedUnits(int number, int max) {
		messageTypesToPopups.get(HudMessageTypes.NUMBER_OF_UNITS_DEPLOYED).getPopupMessage().setText("deployed " + number + " / " + max + " units");
		showPopup(HudMessageTypes.NUMBER_OF_UNITS_DEPLOYED);
	}
}
