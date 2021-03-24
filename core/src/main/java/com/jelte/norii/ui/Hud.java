package com.jelte.norii.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.profile.ProfileManager;
import com.jelte.norii.profile.ProfileObserver;
import com.jelte.norii.screen.BattleScreen;
import com.jelte.norii.utility.AssetManagerUtility;

public class Hud implements ProfileObserver {
	private Stage stage;
	private Image onTileHover;
	private Label playerVictoryMessage;
	private Label aiVictoryMessage;
	private Label invalidAttackTargetMessage;
	private Label invalidMoveMessage;
	private Label invalidSpawnPointMessage;
	private Label invalidSpellTargetMessage;
	private Label notEnoughAPMessage;
	private Label numberOfDeployedUnitsMessage;
	private Label deployUnitsMessage;
	private Label explainActionsMessage;
	private Label explainWinConditionsMessage;
	private Window deployUnitsMessageWindow;
	private Window explainActionsMessageWindow;
	private Window explainWinConditionsMessageWindow;
	private HashMap<Integer, HpBar> entityIdWithHpBar;
	private HashMap<Integer, StatusUi> entityIdWithStatusUi;
	private HashMap<Integer, ActionsUi> entityIdWithActionUi;
	private HashMap<Integer, List<ActionInfoUiWindow>> entityIdWithActionInfoUiWindows;
	private PortraitAndStats portraitAndStats;
	private BattleScreen battleScreen;

	private int mapWidth;
	private int mapHeight;

	private float tilePixelWidth;
	private float tilePixelHeight;

	private boolean locked;

	public static final float UI_VIEWPORT_WIDTH = 400f;
	public static final float UI_VIEWPORT_HEIGHT = 400f;
	public static final float POPUP_MESSAGE_FADE_IN_OUT_DURATION = 2f;
	public static final float POPUP_WIDTH_FACTOR = 2f;
	public static final float POPUP_HEIGHT_FACTOR = 1.2f;

	public Hud(List<Entity> playerUnits, List<Entity> aiUnits, SpriteBatch spriteBatch, int mapWidth, int mapHeight, BattleScreen battleScreen) {
		final List<Entity> allUnits = Stream.concat(playerUnits.stream(), aiUnits.stream()).collect(Collectors.toList());
		initVariables(spriteBatch, mapWidth, mapHeight, battleScreen);
		createEndGameMessages();
		createInfoMessages();
		createTileHoverParticle();
		createCharacterHUD();
		for (final Entity entity : allUnits) {
			addUnit(entity);
		}
	}

	private void initVariables(SpriteBatch spriteBatch, int mapWidth, int mapHeight, BattleScreen battleScreen) {
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.battleScreen = battleScreen;
		tilePixelWidth = UI_VIEWPORT_WIDTH / mapWidth;
		tilePixelHeight = UI_VIEWPORT_HEIGHT / mapHeight;
		entityIdWithHpBar = new HashMap<>();
		entityIdWithStatusUi = new HashMap<>();
		entityIdWithActionUi = new HashMap<>();
		entityIdWithActionInfoUiWindows = new HashMap<>();
		stage = new Stage(new FitViewport(UI_VIEWPORT_WIDTH, UI_VIEWPORT_HEIGHT), spriteBatch);
	}

	private void createInfoMessages() {
		invalidAttackTargetMessage = new Label("Invalid Attack Target", AssetManagerUtility.getSkin());
		invalidMoveMessage = new Label("Invalid Move", AssetManagerUtility.getSkin());
		invalidSpawnPointMessage = new Label("Invalid Spawn Point", AssetManagerUtility.getSkin());
		invalidSpellTargetMessage = new Label("Invalid Spell Target", AssetManagerUtility.getSkin());
		notEnoughAPMessage = new Label("Not enough AP", AssetManagerUtility.getSkin());
		numberOfDeployedUnitsMessage = new Label("", AssetManagerUtility.getSkin());
		deployUnitsMessageWindow = new Window("Info", AssetManagerUtility.getSkin());

		deployUnitsMessage = new Label("Deploy your units \n on one of the \n blue squares", AssetManagerUtility.getSkin());
		deployUnitsMessageWindow.add(deployUnitsMessage);

		explainActionsMessageWindow = new Window("Info", AssetManagerUtility.getSkin());
		explainActionsMessage = new Label("Click once on a \n unit to select it. \n An action bar will appear, \n with one of 4 possible actions, \n move, attack, ability or skip. \n Once an action is performed, \n the unit is locked an no other \n unit can be selected.",
				AssetManagerUtility.getSkin());
		explainActionsMessageWindow.add(explainActionsMessage);

		explainWinConditionsMessageWindow = new Window("Info", AssetManagerUtility.getSkin());
		explainWinConditionsMessage = new Label("Once all your enemies are defeated, you win the round", AssetManagerUtility.getSkin());
		explainWinConditionsMessageWindow.add(explainWinConditionsMessage);

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
		deployUnitsMessageWindow.setVisible(false);
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
		invalidAttackTargetMessage.setVisible(true);
		invalidAttackTargetMessage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(POPUP_MESSAGE_FADE_IN_OUT_DURATION), Actions.fadeOut(POPUP_MESSAGE_FADE_IN_OUT_DURATION)));
	}

	public void showInvalidMoveMessage() {
		invalidMoveMessage.setVisible(true);
		invalidMoveMessage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(POPUP_MESSAGE_FADE_IN_OUT_DURATION), Actions.fadeOut(POPUP_MESSAGE_FADE_IN_OUT_DURATION)));
	}

	public void showInvalidSpawnPointMessage() {
		invalidSpawnPointMessage.setVisible(true);
		invalidSpawnPointMessage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(POPUP_MESSAGE_FADE_IN_OUT_DURATION), Actions.fadeOut(POPUP_MESSAGE_FADE_IN_OUT_DURATION)));
	}

	public void showInvalidSpellTargetMessage() {
		invalidSpellTargetMessage.setVisible(true);
		invalidSpellTargetMessage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(POPUP_MESSAGE_FADE_IN_OUT_DURATION), Actions.fadeOut(POPUP_MESSAGE_FADE_IN_OUT_DURATION)));
	}

	public void showNotEnoughAPMessage() {
		notEnoughAPMessage.setVisible(true);
		notEnoughAPMessage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(POPUP_MESSAGE_FADE_IN_OUT_DURATION), Actions.fadeOut(POPUP_MESSAGE_FADE_IN_OUT_DURATION)));
	}

	public void showDeployUnitsMessage() {
		deployUnitsMessageWindow.setVisible(true);
	}

	public void hideDeployUnitsMessage() {
		deployUnitsMessageWindow.setVisible(false);
	}

	public void showExplainActionsMessage() {
		explainActionsMessageWindow.setVisible(true);
	}

	public void hideExplainActionsMessage() {
		explainActionsMessageWindow.setVisible(false);
	}

	public void showExplainWinConditionsMessage() {
		explainWinConditionsMessageWindow.setVisible(true);
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

	private void createTileHoverParticle() {
		final TextureRegion tr = new TextureRegion(AssetManagerUtility.getSprite(AssetManagerUtility.TILE_HOVER_IMAGE));
		final TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		onTileHover = new Image(trd);
		onTileHover.setPosition(-1, -1);
		onTileHover.setSize(tilePixelWidth, tilePixelHeight);
		onTileHover.getDrawable().setMinHeight(tilePixelHeight);
		onTileHover.getDrawable().setMinWidth(tilePixelWidth);

		stage.addActor(onTileHover);
	}

	public void addUnit(Entity entity) {
		createHpBar(entity);
		portraitAndStats.setHero(entity);
		createStatusUI(entity);
		if (entity.isPlayerUnit()) {
			createActionUI(entity);
		}
	}

	public void removeUnit(Entity entity) {
		final Integer id = entity.getEntityID();
		if (entityIdWithStatusUi.containsKey(id)) {
			entityIdWithStatusUi.get(id).setVisible(false);
			entityIdWithStatusUi.get(id).remove();
			entityIdWithStatusUi.remove(id);
		}

		if (entityIdWithHpBar.containsKey(id)) {
			entityIdWithHpBar.get(id).getHealthBar().setVisible(false);
			entityIdWithHpBar.get(id).getHealthBar().remove();
			entityIdWithHpBar.remove(id);
		}

		if (entityIdWithActionUi.containsKey(id)) {
			for (final ActionUIButton button : entityIdWithActionUi.get(id).getButtons()) {
				button.hide();
			}

			for (final ActionInfoUiWindow popup : entityIdWithActionUi.get(id).getPopUps()) {
				popup.setVisible(false);
			}
			entityIdWithActionUi.get(id).setVisible(false);
			entityIdWithActionUi.get(id).remove();
			entityIdWithActionUi.remove(id);
		}
	}

	private void createHpBar(Entity entity) {
		final HpBar hpBar = new HpBar(entity, mapWidth, mapHeight);
		stage.addActor(hpBar.getHealthBar());
		entityIdWithHpBar.put(entity.getEntityID(), hpBar);
	}

	private void createCharacterHUD() {
		portraitAndStats = new PortraitAndStats(mapWidth, mapHeight);
		stage.addActor(portraitAndStats.getTable());
	}

	private void createStatusUI(Entity entity) {
		final StatusUi statusui = new StatusUi(entity, mapWidth, mapHeight);
		entityIdWithStatusUi.put(entity.getEntityID(), statusui);

		statusui.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		stage.addActor(statusui);
	}

	private void createActionUI(Entity playerUnit) {
		final ActionsUi actionui = new ActionsUi(playerUnit, mapWidth, mapHeight, this);
		entityIdWithActionUi.put(playerUnit.getEntityID(), actionui);

		actionui.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		stage.addActor(actionui);
		entityIdWithActionInfoUiWindows.put(playerUnit.getEntityID(), actionui.getPopUps());
		for (final ActionInfoUiWindow popUp : actionui.getPopUps()) {
			stage.addActor(popUp);
		}
	}

	public void setPositionTileHover(int tileX, int tileY) {
		onTileHover.setPosition(tileX * tilePixelWidth, tileY * tilePixelHeight);
	}

	public void update(List<Entity> units) {
		for (final Entity entity : units) {
			final int id = entity.getEntityID();
			final HpBar bar = entityIdWithHpBar.get(id);
			final StatusUi status = entityIdWithStatusUi.get(id);
			final ActionsUi action = entityIdWithActionUi.get(id);
			final List<ActionInfoUiWindow> infos = entityIdWithActionInfoUiWindows.get(id);

			if (bar != null) {
				bar.update(entity);
			}
			if (status != null) {
				status.update(entity);
			}
			if (action != null) {
				action.update(entity);
			}
			if (infos != null) {
				for (final ActionInfoUiWindow info : infos) {
					info.update();
				}
			}

			if (entity.isStatsChanged()) {
				portraitAndStats.update(entity);
			}
		}
	}

	public void showPlayerWin() {
		playerVictoryMessage.setVisible(true);
	}

	public void showAiWin() {
		aiVictoryMessage.setVisible(true);
	}

	public Stage getStage() {
		return stage;
	}

	public Image getTileHoverImage() {
		return onTileHover;
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	public void render(float delta) {
		stage.act(delta);
		stage.draw();
	}

	public void dispose() {
		stage.dispose();
	}

	@Override
	public void onNotify(ProfileManager profileManager, ProfileEvent event) {
		// no-op
	}

	public Collection<ActionsUi> getActionUIs() {
		return entityIdWithActionUi.values();
	}

	public Collection<StatusUi> getStatusUIs() {
		return entityIdWithStatusUi.values();
	}

	public Map<Integer, HpBar> getEntityIdWithHpBar() {
		return entityIdWithHpBar;
	}

	public Map<Integer, StatusUi> getEntityIdWithStatusUi() {
		return entityIdWithStatusUi;
	}

	public Map<Integer, ActionsUi> getEntityIdWithActionUi() {
		return entityIdWithActionUi;
	}

	public Map<Integer, List<ActionInfoUiWindow>> getEntityIdWithActionInfoUiWindows() {
		return entityIdWithActionInfoUiWindows;
	}

	public void sendMessage(MessageToBattleScreen message, int entityID, Ability ability) {
		battleScreen.messageFromUi(message, entityID, ability);
	}

	public PortraitAndStats getPortraitAndStats() {
		return portraitAndStats;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void showActions(boolean show, Entity entity) {
		final int id = entity.getEntityID();
		final ActionsUi action = entityIdWithActionUi.get(id);
		action.setVisible(show);
	}

	public void hideDeployedUnits() {
		numberOfDeployedUnitsMessage.setVisible(false);
	}
}
