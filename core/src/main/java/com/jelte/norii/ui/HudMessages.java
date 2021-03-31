package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jelte.norii.utility.AssetManagerUtility;

public class HudMessages {
	public static final float POPUP_WIDTH_FACTOR = 2f;
	public static final float POPUP_HEIGHT_FACTOR = 2f;
	public static final float POPUP_MESSAGE_FADE_IN_OUT_DURATION = 2f;

	private Label playerVictoryMessage;
	private Label aiVictoryMessage;
	private Label invalidAttackTargetMessage;
	private Label invalidMoveMessage;
	private Label invalidSpawnPointMessage;
	private Label invalidSpellTargetMessage;
	private Label notEnoughAPMessage;
	private Label numberOfDeployedUnitsMessage;
	private Window deployUnitsMessageWindow;
	private Window explainActionsMessageWindow;
	private Window explainWinConditionsMessageWindow;
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
		createInfoMessages(isTutorial);
		createEndGameMessages();
	}

	private void createInfoMessages(boolean isTutorial) {
		invalidAttackTargetMessage = new Label("Invalid Attack Target", AssetManagerUtility.getSkin());
		invalidMoveMessage = new Label("Invalid Move", AssetManagerUtility.getSkin());
		invalidSpawnPointMessage = new Label("Invalid Spawn Point", AssetManagerUtility.getSkin());
		invalidSpellTargetMessage = new Label("Invalid Spell Target", AssetManagerUtility.getSkin());
		notEnoughAPMessage = new Label("Not enough AP", AssetManagerUtility.getSkin());
		numberOfDeployedUnitsMessage = new Label("", AssetManagerUtility.getSkin());

		deployUnitsMessageWindow = new Window("Info", AssetManagerUtility.getSkin());
		final ImageButtonStyle imageButtonStyle = AssetManagerUtility.getSkin().get("closebutton", ImageButtonStyle.class);
		final ImageButton deployUnitsCloseButton = new ImageButton(imageButtonStyle);
		final Label deployUnitsMessage = new Label("Deploy your units \n on one of the \n blue squares", AssetManagerUtility.getSkin());
		deployUnitsMessageWindow.add(deployUnitsMessage);
		deployUnitsMessageWindow.getTitleTable().add(deployUnitsCloseButton).size(20).padBottom(5);

		deployUnitsCloseButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				deployUnitsMessageWindow.setVisible(false);
			}
		});

		explainActionsMessageWindow = new Window("Info", AssetManagerUtility.getSkin());
		final ImageButton explainActionsCloseButton = new ImageButton(AssetManagerUtility.getSkin(), "closebutton");
		explainActionsMessageWindow.getTitleTable().add(explainActionsCloseButton).size(20).padBottom(5);

		explainActionsCloseButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				explainActionsMessageWindow.setVisible(false);
			}
		});

		final Label explainActionsMessage = new Label(
				"Click once on a \n unit to select it. \n An action bar will appear, \n with one of 4 possible actions, \n move, attack, ability or skip. \n Once an action is performed, \n the unit is locked an no other \n unit can be selected.", AssetManagerUtility.getSkin());
		explainActionsMessageWindow.add(explainActionsMessage);
		explainActionsMessageWindow.pack();
		explainActionsMessageWindow.setMovable(true);

		explainWinConditionsMessageWindow = new Window("Info", AssetManagerUtility.getSkin());
		final ImageButton explainWinConditionsCloseButton = new ImageButton(AssetManagerUtility.getSkin(), "closebutton");
		explainWinConditionsMessageWindow.getTitleTable().add(explainWinConditionsCloseButton).size(20).padBottom(5);

		explainWinConditionsCloseButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				explainWinConditionsMessageWindow.setVisible(false);
			}
		});

		final Label explainWinConditionsMessage = new Label("Once all your enemies are defeated, you win the round", AssetManagerUtility.getSkin());
		explainWinConditionsMessageWindow.add(explainWinConditionsMessage);
		explainWinConditionsMessageWindow.pack();
		explainWinConditionsMessageWindow.setMovable(true);

		invalidAttackTargetMessage.setPosition((mapWidth / POPUP_WIDTH_FACTOR) * tilePixelWidth, (mapHeight / POPUP_HEIGHT_FACTOR) * tilePixelHeight);
		invalidMoveMessage.setPosition((mapWidth / POPUP_WIDTH_FACTOR) * tilePixelWidth, (mapHeight / POPUP_HEIGHT_FACTOR) * tilePixelHeight);
		invalidSpawnPointMessage.setPosition((mapWidth / POPUP_WIDTH_FACTOR) * tilePixelWidth, (mapHeight / POPUP_HEIGHT_FACTOR) * tilePixelHeight);
		invalidSpellTargetMessage.setPosition((mapWidth / POPUP_WIDTH_FACTOR) * tilePixelWidth, (mapHeight / POPUP_HEIGHT_FACTOR) * tilePixelHeight);
		notEnoughAPMessage.setPosition((mapWidth / POPUP_WIDTH_FACTOR) * tilePixelWidth, (mapHeight / POPUP_HEIGHT_FACTOR) * tilePixelHeight);
		numberOfDeployedUnitsMessage.setPosition((mapWidth / POPUP_WIDTH_FACTOR) * tilePixelWidth, (mapHeight / POPUP_HEIGHT_FACTOR) * tilePixelHeight);
		deployUnitsMessageWindow.setPosition((mapWidth / POPUP_WIDTH_FACTOR) * tilePixelWidth, (mapHeight / POPUP_HEIGHT_FACTOR) * tilePixelHeight);
		explainActionsMessageWindow.setPosition((mapWidth / POPUP_WIDTH_FACTOR) * tilePixelWidth, (mapHeight / POPUP_HEIGHT_FACTOR) * tilePixelHeight);
		explainWinConditionsMessageWindow.setPosition((mapWidth / POPUP_WIDTH_FACTOR) * tilePixelWidth, (mapHeight / POPUP_HEIGHT_FACTOR) * tilePixelHeight);

		invalidAttackTargetMessage.setVisible(false);
		invalidMoveMessage.setVisible(false);
		invalidSpawnPointMessage.setVisible(false);
		invalidSpellTargetMessage.setVisible(false);
		notEnoughAPMessage.setVisible(false);
		numberOfDeployedUnitsMessage.setVisible(false);
		deployUnitsMessageWindow.setVisible(isTutorial);
		explainActionsMessageWindow.setVisible(false);
		explainWinConditionsMessageWindow.setVisible(false);

		stage.addActor(invalidAttackTargetMessage);
		stage.addActor(invalidMoveMessage);
		stage.addActor(invalidSpawnPointMessage);
		stage.addActor(invalidSpellTargetMessage);
		stage.addActor(notEnoughAPMessage);
		stage.addActor(numberOfDeployedUnitsMessage);
		stage.addActor(deployUnitsMessageWindow);
		stage.addActor(explainActionsMessageWindow);
		stage.addActor(explainWinConditionsMessageWindow);
	}

	public void showInvalidAttackMessage() {
		invalidAttackTargetMessage.setVisible(isTutorial);
		invalidAttackTargetMessage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(POPUP_MESSAGE_FADE_IN_OUT_DURATION), Actions.fadeOut(POPUP_MESSAGE_FADE_IN_OUT_DURATION)));
	}

	public void showInvalidMoveMessage() {
		invalidMoveMessage.setVisible(isTutorial);
		invalidMoveMessage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(POPUP_MESSAGE_FADE_IN_OUT_DURATION), Actions.fadeOut(POPUP_MESSAGE_FADE_IN_OUT_DURATION)));
	}

	public void showInvalidSpawnPointMessage() {
		invalidSpawnPointMessage.setVisible(isTutorial);
		invalidSpawnPointMessage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(POPUP_MESSAGE_FADE_IN_OUT_DURATION), Actions.fadeOut(POPUP_MESSAGE_FADE_IN_OUT_DURATION)));
	}

	public void showInvalidSpellTargetMessage() {
		invalidSpellTargetMessage.setVisible(isTutorial);
		invalidSpellTargetMessage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(POPUP_MESSAGE_FADE_IN_OUT_DURATION), Actions.fadeOut(POPUP_MESSAGE_FADE_IN_OUT_DURATION)));
	}

	public void showNotEnoughAPMessage() {
		notEnoughAPMessage.setVisible(isTutorial);
		notEnoughAPMessage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(POPUP_MESSAGE_FADE_IN_OUT_DURATION), Actions.fadeOut(POPUP_MESSAGE_FADE_IN_OUT_DURATION)));
	}

	public void showDeployUnitsMessage() {
		deployUnitsMessageWindow.setVisible(isTutorial);
	}

	public void hideDeployUnitsMessage() {
		deployUnitsMessageWindow.setVisible(false);
	}

	public void showExplainActionsMessage() {
		explainActionsMessageWindow.setVisible(isTutorial);
	}

	public void hideExplainActionsMessage() {
		explainActionsMessageWindow.setVisible(false);
	}

	public void showExplainWinConditionsMessage() {
		explainWinConditionsMessageWindow.setVisible(isTutorial);
	}

	public void hideExplainWinConditionsMessage() {
		explainWinConditionsMessageWindow.setVisible(false);
	}

	public void updateNumberOfDeployedUnits(int number, int max) {
		numberOfDeployedUnitsMessage.setVisible(true);
		numberOfDeployedUnitsMessage.setText("deployed " + number + " / " + max + " units");
	}

	private void createEndGameMessages() {
		playerVictoryMessage = new Label("You Win!", AssetManagerUtility.getSkin(), "bigFont");
		aiVictoryMessage = new Label("You Lose!", AssetManagerUtility.getSkin(), "bigFont");

		playerVictoryMessage.setPosition((mapWidth / 8.0f) * tilePixelWidth, (mapHeight / 2.0f) * tilePixelHeight);
		aiVictoryMessage.setPosition((mapWidth / 8.0f) * tilePixelWidth, (mapHeight / 2.0f) * tilePixelHeight);

		playerVictoryMessage.setVisible(false);
		aiVictoryMessage.setVisible(false);

		stage.addActor(playerVictoryMessage);
		stage.addActor(aiVictoryMessage);
	}

	public void showPlayerWin() {
		playerVictoryMessage.setVisible(true);
	}

	public void showAiWin() {
		aiVictoryMessage.setVisible(true);
	}

	public void hideDeployedUnits() {
		numberOfDeployedUnitsMessage.setVisible(false);
	}

}
