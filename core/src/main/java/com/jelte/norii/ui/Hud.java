package com.jelte.norii.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jelte.norii.entities.AiEntity;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.profile.ProfileManager;
import com.jelte.norii.profile.ProfileObserver;
import com.jelte.norii.utility.AssetManagerUtility;

public class Hud implements ProfileObserver {
	private Stage stage;
	private Image onTileHover;
	private Label playerVictoryMessage;
	private Label AiVictoryMessage;
	private ArrayList<HpBar> hpBars;
	private PortraitAndStats portraitAndStats;
	private ArrayList<StatusUi> statusUIs;
	private ArrayList<ActionInfoUiWindow> actionInfoUIWindows;
	private ArrayList<ActionsUi> actionUIs;

	private int mapWidth;
	private int mapHeight;

	private float tilePixelWidth;
	private float tilePixelHeight;

	public static final float UI_VIEWPORT_WIDTH = 400f;
	public static final float UI_VIEWPORT_HEIGHT = 400f;
	public static final int HEALTHBAR_Y_OFFSET = 12;

	public Hud(List<PlayerEntity> playerUnits, List<AiEntity> aiUnits, SpriteBatch spriteBatch, int mapWidth, int mapHeight) {
		final List<Entity> allUnits = Stream.concat(playerUnits.stream(), aiUnits.stream()).collect(Collectors.toList());
		initVariables(spriteBatch, mapWidth, mapHeight);
		createEndGameMessages();
		createTileHoverParticle();
		createCharacterHUD();
		for (Entity entity : allUnits) {
			addUnit(entity);
		}
	}

	private void initVariables(SpriteBatch spriteBatch, int mapWidth, int mapHeight) {
		hpBars = new ArrayList<>();
		statusUIs = new ArrayList<>();
		actionUIs = new ArrayList<>();
		actionInfoUIWindows = new ArrayList<>();
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		tilePixelWidth = UI_VIEWPORT_WIDTH / mapWidth;
		tilePixelHeight = UI_VIEWPORT_HEIGHT / mapHeight;
		stage = new Stage(new FitViewport(UI_VIEWPORT_WIDTH, UI_VIEWPORT_HEIGHT), spriteBatch);
	}

	private void createEndGameMessages() {
		playerVictoryMessage = new Label("You Win!", AssetManagerUtility.getSkin(), "bigFont");
		AiVictoryMessage = new Label("You Lose!", AssetManagerUtility.getSkin(), "bigFont");

		playerVictoryMessage.setPosition((mapWidth / 8.0f) * tilePixelWidth, (mapHeight / 2.0f) * tilePixelHeight);
		AiVictoryMessage.setPosition((mapWidth / 8.0f) * tilePixelWidth, (mapHeight / 2.0f) * tilePixelHeight);

		playerVictoryMessage.setVisible(false);
		AiVictoryMessage.setVisible(false);

		stage.addActor(playerVictoryMessage);
		stage.addActor(AiVictoryMessage);
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
		portraitAndStats.linkUnit(entity);
		createStatusUI(entity);
		if (entity.isPlayerUnit()) {
			createActionUI((PlayerEntity) entity);
		}
	}

	private void createHpBar(Entity entity) {
		final HpBar hpBar = new HpBar(entity, mapWidth, mapHeight);
		stage.addActor(hpBar.getHealthBar());
		hpBars.add(hpBar);
	}

	private void createCharacterHUD() {
		portraitAndStats = new PortraitAndStats(mapWidth, mapHeight);
		stage.addActor(portraitAndStats.getTable());
	}

	private void createStatusUI(Entity entity) {
		final StatusUi statusui = new StatusUi(entity, mapWidth, mapHeight);
		statusUIs.add(statusui);

		statusui.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		stage.addActor(statusui);
	}

	private void createActionUI(PlayerEntity playerUnit) {
		final ActionsUi actionui = new ActionsUi(playerUnit, mapWidth, mapHeight);
		actionUIs.add(actionui);

		actionui.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		stage.addActor(actionui);

		for (final ActionInfoUiWindow popUp : actionui.getPopUps()) {
			actionInfoUIWindows.add(popUp);
			stage.addActor(popUp);
		}
	}

	public void setPositionTileHover(int tileX, int tileY) {
		onTileHover.setPosition(tileX * tilePixelWidth, tileY * tilePixelHeight);
	}

	public void update() {
		for (final HpBar bar : hpBars) {
			bar.getHealthBar().setPosition(bar.getEntity().getCurrentPosition().getTileX() * tilePixelWidth, ((bar.getEntity().getCurrentPosition().getTileY() * tilePixelHeight) + HEALTHBAR_Y_OFFSET));
			bar.getHealthBar().setValue(bar.getEntity().getHp());
			if (bar.getHealthBar().getValue() == 0) {
				bar.getHealthBar().setVisible(false);
			}
		}

		for (final StatusUi statusUI : statusUIs) {
			statusUI.update();
		}

		for (final ActionsUi actionsUi : actionUIs) {
			actionsUi.update();
		}

		for (final ActionInfoUiWindow popUp : actionInfoUIWindows) {
			popUp.update();
		}
	}

	public void showPlayerWin() {
		playerVictoryMessage.setVisible(true);
	}

	public void showAiWin() {
		AiVictoryMessage.setVisible(true);
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

	public List<ActionsUi> getActionUIs() {
		return actionUIs;
	}

	public List<StatusUi> getStatusUIs() {
		return statusUIs;
	}

}
