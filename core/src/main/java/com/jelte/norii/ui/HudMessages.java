package com.jelte.norii.ui;

import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class HudMessages {
	public static final float POPUP_WIDTH_FACTOR = 2f;
	public static final float POPUP_HEIGHT_FACTOR = 2f;
	public static final float POPUP_MESSAGE_FADE_IN_OUT_DURATION = 2f;

	private HashMap<HudMessageTypes, HudMessagePopup> messageTypesToPopups;
	private HashMap<HudMessageTypes, HudMessageWindow> messageTypesToWindows;

	private final Stage stage;
	private final int mapWidth;
	private final int mapHeight;
	private final float tilePixelWidth;
	private final float tilePixelHeight;
	private final boolean isTutorial;

	public HudMessages(Stage stage, int mapWidth, int mapHeight, float tilePixelWidth, float tilePixelHeight, boolean isTutorial) {
		this.stage = stage;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.tilePixelWidth = tilePixelWidth;
		this.tilePixelHeight = tilePixelHeight;
		this.isTutorial = isTutorial;
		messageTypesToPopups = new HashMap<>();
		messageTypesToWindows = new HashMap<>();
		createInfoMessages(isTutorial);
	}

	private void createInfoMessages(boolean isTutorial) {
		messageTypesToPopups.put(HudMessageTypes.INVALID_ATTACK_TARGET, new HudMessagePopup("Invalid Attack Target", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToPopups.put(HudMessageTypes.INVALID_MOVE, new HudMessagePopup("Invalid Move", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToPopups.put(HudMessageTypes.INVALID_SPAWN_POINT, new HudMessagePopup("Invalid Spawn Point", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToPopups.put(HudMessageTypes.INVALID_SPELL_TARGET, new HudMessagePopup("Invalid Spell Target", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToPopups.put(HudMessageTypes.NOT_ENOUGH_AP, new HudMessagePopup("Not enough AP", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToPopups.put(HudMessageTypes.AI_VICTORY, new HudMessagePopup("You Lose!", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToPopups.put(HudMessageTypes.PLAYER_VICTORY, new HudMessagePopup("You Win!", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));

		messageTypesToWindows.put(HudMessageTypes.DEPLOY_UNITS_INFO, new HudMessageWindow("Deploy your units \n on one of the \n blue squares", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToWindows.put(HudMessageTypes.NUMBER_OF_UNITS_DEPLOYED, new HudMessageWindow("", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToWindows.put(HudMessageTypes.SELECT_UNIT_INFO, new HudMessageWindow("Click once on a \n unit to select it.", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToWindows.put(HudMessageTypes.EXPLAIN_ACTION_INFO, new HudMessageWindow("Possible actions are, move, attack, \n cast an ability or skip your turn.", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToWindows.put(HudMessageTypes.EXPLAIN_TURNING_INFO, new HudMessageWindow("You can change the direction your character is facing, \n by pressing W, A, S, D.", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToWindows.put(HudMessageTypes.EXPLAIN_LOCKING_INFO,
				new HudMessageWindow("After an action is performed, that character is locked and no other character can be used for that turn.", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToWindows.put(HudMessageTypes.EXPLAIN_END_TURN_INFO, new HudMessageWindow("Press skip to end your turn.", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToWindows.put(HudMessageTypes.EXPLAIN_CANCEL_INFO, new HudMessageWindow("Right click on the mouse to cancel any action.", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));
		messageTypesToWindows.put(HudMessageTypes.EXPLAIN_WIN_LOSE_INFO, new HudMessageWindow("Once all your enemies are defeated, you win the round", tilePixelWidth, tilePixelHeight, mapWidth, mapHeight));

		messageTypesToWindows.get(HudMessageTypes.DEPLOY_UNITS_INFO).setVisible(isTutorial);

		for (HudMessageWindow window : messageTypesToWindows.values()) {
			stage.addActor(window.getTextWindow());
		}

		for (HudMessagePopup popup : messageTypesToPopups.values()) {
			stage.addActor(popup.getPopupMessage());
		}
	}

	public void showPopup(HudMessageTypes type) {
		messageTypesToPopups.get(type).getPopupMessage().setVisible(true);
		messageTypesToPopups.get(type).getPopupMessage().addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(POPUP_MESSAGE_FADE_IN_OUT_DURATION), Actions.fadeOut(POPUP_MESSAGE_FADE_IN_OUT_DURATION)));
	}

	public void showInfoWindow(HudMessageTypes type) {
		messageTypesToWindows.get(type).getTextWindow().setVisible(true);
	}

	public void showPopupTutorial(HudMessageTypes type) {
		messageTypesToPopups.get(type).getPopupMessage().setVisible(isTutorial);
		messageTypesToPopups.get(type).getPopupMessage().addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(POPUP_MESSAGE_FADE_IN_OUT_DURATION), Actions.fadeOut(POPUP_MESSAGE_FADE_IN_OUT_DURATION)));
	}

	public void showInfoWindowTutorial(HudMessageTypes type) {
		messageTypesToWindows.get(type).getTextWindow().setVisible(isTutorial);
	}

	public void hideInfoWindow(HudMessageTypes type) {
		messageTypesToWindows.get(type).getTextWindow().setVisible(false);
	}

	public void updateNumberOfDeployedUnits(int number, int max) {
		messageTypesToWindows.get(HudMessageTypes.NUMBER_OF_UNITS_DEPLOYED).getTextWindow().setVisible(true);
		messageTypesToWindows.get(HudMessageTypes.NUMBER_OF_UNITS_DEPLOYED).getTextLabel().setText("deployed " + number + " / " + max + " units");
	}
}
